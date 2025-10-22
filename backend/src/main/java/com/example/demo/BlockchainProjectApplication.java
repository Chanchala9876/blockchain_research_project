package com.example.demo;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BlockchainProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainProjectApplication.class, args);
	}

	@PostConstruct
    public void init() {
        // Create upload directory if it doesn't exist
        File uploadDir = new File("uploads/papers");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
}
