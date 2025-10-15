package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the CRUD Wiki Spring Boot application.
 * <p>
 * Bootstraps the application and starts the embedded server.
 * </p>
 *
 */

@SpringBootApplication
public class CrudWikiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudWikiApplication.class, args);
	}

}
