package com.example.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssignmentApplication {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}

}
