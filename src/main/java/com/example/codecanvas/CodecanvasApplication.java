package com.example.codecanvas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodecanvasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodecanvasApplication.class, args);
	}

}
