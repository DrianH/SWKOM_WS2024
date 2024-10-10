package at.technikumwien.SWKOM2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"controllers", "entities", "repositories"})
public class Swkom2024Application {

	public static void main(String[] args) {
		SpringApplication.run(Swkom2024Application.class, args);
	}

}
