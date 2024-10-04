package at.technikumwien.SWKOM2024.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
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

    /*@Operation(summary = "Download a file")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        try {
            File file = new File(uploadDir + "/" + filename);
            Path path = file.toPath();
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Delete a file")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename) {
        File file = new File(uploadDir + "/" + filename);
        if (file.exists() && file.delete()) {
            return ResponseEntity.ok("File deleted successfully: " + filename);
        } else {
            return ResponseEntity.status(404).body("File not found or unable to delete: " + filename);
        }
    }*/
}