package at.technikumwien.SWKOM2024.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue fileUploadsQueue() {
        return new Queue("file-uploads", true);
    }

    @Bean
    public Queue resultQueue(){
        return new Queue("result", true);
    }
}