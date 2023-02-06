package com.jovanfunda.Faculty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.jovanfunda.*")
@EntityScan("com.jovanfunda.*")   
@ComponentScan("com.jovanfunda.*")
public class FacultyApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacultyApplication.class, args);
	}
}
