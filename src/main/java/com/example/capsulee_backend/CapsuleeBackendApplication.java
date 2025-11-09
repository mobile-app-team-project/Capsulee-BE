package com.example.capsulee_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CapsuleeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapsuleeBackendApplication.class, args);
	}

}
