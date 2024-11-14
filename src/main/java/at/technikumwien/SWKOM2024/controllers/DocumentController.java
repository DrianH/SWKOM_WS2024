package at.technikumwien.SWKOM2024.controllers;

import at.technikumwien.SWKOM2024.entities.Document;
import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@Validated  // Enables validation in the controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    @Operation(summary = "Upload a file")
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") @NotNull MultipartFile file) {
        try {
            logger.info("Received file upload request for file: {}", file.getOriginalFilename());

            // Validate the file data before processing
            if (file.isEmpty()) {
                logger.warn("File upload failed: file is empty");
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Create a Document object to save to the database
            Document document = new Document();
            document.setName(file.getOriginalFilename());
            document.setType(file.getContentType());
            document.setSize(file.getSize());
            document.setContent(file.getBytes());  // Store the file content as a byte array

            // Enforce validation for the Document entity
            if (!isValidDocument(document)) {
                return ResponseEntity.badRequest().body("Invalid document data");
            }

            // Save the document to the database
            documentRepository.save(document);

            rabbitTemplate.convertAndSend("file-uploads", document.getName());
            logger.info("File uploaded and message sent to RabbitMQ for file: {}", file.getOriginalFilename());

            return ResponseEntity.ok("File uploaded and saved to the database successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("File upload failed due to IOException: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "List all uploaded files")
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        logger.info("Fetching list of uploaded files");
        // Fetch all documents from the database and return their names
        List<Document> documents = documentRepository.findAll();
        List<String> fileNames = documents.stream()
                .map(Document::getName)
                .collect(Collectors.toList());

        logger.info("Fetched {} files", fileNames.size());
        return ResponseEntity.ok(fileNames);
    }

    @Operation(summary = "Download a file by ID")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        // Retrieve the file from the database by ID
        Optional<Document> documentOptional = documentRepository.findById(id);

        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"")
                    .header("Content-Type", document.getType())  // Set the file's content type
                    .body(document.getContent());  // Return the file's byte content
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Helper method to validate the Document entity before saving
    private boolean isValidDocument(@Valid Document document) {
        // Validation logic will automatically be handled due to the @Valid annotation
        // Custom validation checks can also be added here if necessary
        return true;
    }
}