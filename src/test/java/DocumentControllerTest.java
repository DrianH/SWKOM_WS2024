import at.technikumwien.SWKOM2024.controllers.DocumentController;
import at.technikumwien.SWKOM2024.entities.Document;
import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentControllerTest {

    @InjectMocks
    private DocumentController documentController;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        // Mock input file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );

        // Mock Document
        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setType(file.getContentType());
        document.setSize(file.getSize());
        document.setContent(file.getBytes());

        // Mock repository save
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        // Call method and verify response
        ResponseEntity<String> response = documentController.uploadFile(file);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("successfully"));

        // Verify repository and RabbitMQ interactions
        verify(documentRepository, times(1)).save(any(Document.class));
        verify(rabbitTemplate, times(1)).convertAndSend("file-uploads", document.getName());
    }

    @Test
    void testUploadFileEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        ResponseEntity<String> response = documentController.uploadFile(file);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("File is empty", response.getBody());
    }

    @Test
    void testListFiles() {
        // Mock data
        Document document1 = new Document();
        document1.setName("file1.pdf");
        Document document2 = new Document();
        document2.setName("file2.pdf");

        when(documentRepository.findAll()).thenReturn(Arrays.asList(document1, document2));

        // Call method and verify response
        ResponseEntity<List<String>> response = documentController.listFiles();
        assertEquals(200, response.getStatusCodeValue());
        List<String> fileNames = response.getBody();
        assertNotNull(fileNames);
        assertEquals(2, fileNames.size());
        assertTrue(fileNames.contains("file1.pdf"));
        assertTrue(fileNames.contains("file2.pdf"));
    }

    @Test
    void testDownloadFileFound() {
        // Mock data
        Document document = new Document();
        document.setId(1L);
        document.setName("file1.pdf");
        document.setContent("Dummy content".getBytes());

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        // Call method and verify response
        ResponseEntity<byte[]> response = documentController.downloadFile(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertArrayEquals("Dummy content".getBytes(), response.getBody());
    }

    @Test
    void testDownloadFileNotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = documentController.downloadFile(1L);
        assertEquals(404, response.getStatusCodeValue());
    }
}