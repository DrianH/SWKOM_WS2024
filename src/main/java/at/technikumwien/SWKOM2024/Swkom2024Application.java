package at.technikumwien.SWKOM2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "at.technikumwien.SWKOM2024.repositories")
public class Swkom2024Application {

	public static void main(String[] args) {
		SpringApplication.run(Swkom2024Application.class, args);
	}

}
