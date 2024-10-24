package at.technikumwien.SWKOM2024.controllers;

import at.technikumwien.SWKOM2024.entities.Document;
import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Operation(summary = "Upload a file")
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        try {
            // Create a Document object to save to the database
            Document document = new Document();
            document.setName(file.getOriginalFilename());
            document.setType(file.getContentType());
            document.setSize(file.getSize());
            document.setContent(file.getBytes());  // Store the file content as a byte array

            // Save the document to the database
            documentRepository.save(document);

            System.out.println("Received file: " + file.getOriginalFilename());

            return ResponseEntity.ok("File uploaded and saved to the database successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "List all uploaded files")
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        // Fetch all documents from the database and return their names
        List<Document> documents = documentRepository.findAll();
        List<String> fileNames = documents.stream()
                .map(Document::getName)
                .collect(Collectors.toList());

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
                    .body(document.getContent());  // Return the file's byte content
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}