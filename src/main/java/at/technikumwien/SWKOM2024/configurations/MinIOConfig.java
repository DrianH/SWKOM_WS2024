package at.technikumwien.SWKOM2024.configurations;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://minio:9000")  // Use "minio" as host since it's in the same Docker network
                .credentials("minioadmin", "minioadmin")
                .build();
    }
}