package at.technikumwien.SWKOM2024.services;

import at.technikumwien.SWKOM2024.entities.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentService {
    Document saveFile(MultipartFile file) throws IOException;
    List<Map<String, Object>> listAllFiles(); // Updated return type
    Optional<Document> getFileById(Long id);
    void deleteFileById(Long id);
}