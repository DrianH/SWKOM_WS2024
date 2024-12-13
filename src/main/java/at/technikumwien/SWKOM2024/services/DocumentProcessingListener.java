package at.technikumwien.SWKOM2024.services;

import at.technikumwien.SWKOM2024.controllers.DocumentController;
import at.technikumwien.SWKOM2024.repositories.DocumentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class DocumentProcessingListener {

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    @Autowired
    DocumentRepository documentRepository;

    @RabbitListener(queues = "result")
    public void receiveMessage(String message) {
        logger.info("Received message from RabbitMQ: {}", message);

        try {
            // Split the message into parts for testing (e.g., "id:ocrResult")
            String[] parts = message.split(":", 2);
            if (parts.length < 2) {
                logger.error("Invalid message format. Expected 'id:ocrResult'. Received: {}", message);
                return;
            }

            Long documentId = Long.parseLong(parts[0]); // Extract the document ID
            String ocrResult = parts[1]; // Extract the OCR result

            // Log the extracted values for testing
            logger.info("Extracted Document ID: {}, OCR Result: {}", documentId, ocrResult);

            // For now, just print the message for verification
            System.out.println("Document ID: " + documentId + ", OCR Result: " + ocrResult);
        } catch (Exception e) {
            logger.error("Error processing message from RabbitMQ: {}", message, e);
        }
    }
}