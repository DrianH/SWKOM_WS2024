package at.technikumwien.SWKOM2024.services;

import at.technikumwien.SWKOM2024.entities.Document;
import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MinioClient minioClient;

    @Override
    public Document saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String bucketName = "documents";
        String fileName = file.getOriginalFilename();

        // Ensure the bucket exists in MinIO
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new IOException("Failed to create or verify bucket in MinIO", e);
        }

        // Upload the file to MinIO
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }

        // Construct the MinIO file URL manually
        String fileUrl = "http://minio:9000/" + bucketName + "/" + fileName;

        // Save metadata and file content to PostgreSQL
        Document document = new Document();
        document.setName(fileName); // File name
        document.setType(file.getContentType()); // MIME type
        document.setSize(file.getSize()); // File size
        document.setContent(file.getBytes()); // File content as byte[] for database storage

        try {
            document = documentRepository.save(document); // Persist to database
        } catch (Exception e) {
            throw new IOException("Failed to save file metadata and content to the database", e);
        }

        String message = document.getName() + ":" + fileUrl;

        // Send the MinIO file URL to RabbitMQ
        try {
            rabbitTemplate.convertAndSend("file-uploads", message);
        } catch (Exception e) {
            throw new IOException("Failed to send name:message to RabbitMQ", e);
        }

        return document;
    }

    @Override
    public List<Map<String, Object>> listAllFiles() {
        // Fetch all documents and return their ID and name as a map
        return documentRepository.findAll().stream()
                .map(document -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", document.getId());
                    map.put("name", document.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Optional<Document> getFileById(Long id) {
        // Fetch the document by ID
        return documentRepository.findById(id);
    }

    @Override
    public void deleteFileById(Long id) {
        documentRepository.deleteById(id);
    }

}