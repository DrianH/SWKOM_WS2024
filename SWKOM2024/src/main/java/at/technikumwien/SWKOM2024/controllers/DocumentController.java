package at.technikumwien.SWKOM2024.controllers;

import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/files")
public class DocumentController {
    private String uploadDir = "/uploads";

    @Operation(summary = "Upload a file")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE /*"multipart/form-data"*/)
    public ResponseEntity<String> uploadFile(
            @RequestParam MultipartFile file) {
        try {
            // Create the directory if it doesn't exist
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the specified directory
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);

            System.out.println("Received file: " + file.getOriginalFilename());

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "List all uploaded files")
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        File directory = new File(uploadDir);
        String[] files = directory.list();
        return ResponseEntity.ok(Arrays.asList(files));
    }
}