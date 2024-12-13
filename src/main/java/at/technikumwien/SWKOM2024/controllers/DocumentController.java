package at.technikumwien.SWKOM2024.controllers;

import at.technikumwien.SWKOM2024.entities.Document;
import at.technikumwien.SWKOM2024.services.DocumentService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/files")
@Validated
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Operation(summary = "Upload a file")
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") @NotNull MultipartFile file) {
        try {
            logger.info("Received file upload request for file: {}", file.getOriginalFilename());
            Document document = documentService.saveFile(file);
            logger.info("File uploaded and saved to database: {}", document.getName());
            return ResponseEntity.ok("File uploaded and saved to the database successfully: " + file.getOriginalFilename());
        } catch (IllegalArgumentException e) {
            logger.warn("File upload failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            logger.error("File upload failed due to IOException: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "List all uploaded files")
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listFiles() {
        logger.info("Fetching list of uploaded files");
        List<Map<String, Object>> files = documentService.listAllFiles();
        logger.info("Fetched {} files", files.size());
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "Download a file by ID")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        logger.info("Fetching file with ID: {}", id);
        Optional<Document> documentOptional = documentService.getFileById(id);

        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"")
                    .header("Content-Type", document.getType())
                    .body(document.getContent());
        } else {
            logger.warn("File with ID {} not found", id);
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Delete a file by ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        Optional<Document> documentOptional = documentService.getFileById(id);

        if (documentOptional.isPresent()) {
            documentService.deleteFileById(id);
            logger.info("Deleted file with ID: {}", id);
            return ResponseEntity.ok("File deleted successfully");
        } else {
            logger.warn("File with ID {} not found", id);
            return ResponseEntity.status(404).body("File not found");
        }
    }
}