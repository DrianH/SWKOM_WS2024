package at.technikumwien.SWKOM2024.services;

import at.technikumwien.SWKOM2024.controllers.DocumentController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class DocumentProcessingListener {

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    @RabbitListener(queues = "file-uploads")
    public void receiveMessage(String message) {
        logger.info("Received message from RabbitMQ: {}", message);
        // Process the file as needed, such as storing metadata or notifying another service.
    }
}