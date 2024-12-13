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
import java.util.Map;
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

}