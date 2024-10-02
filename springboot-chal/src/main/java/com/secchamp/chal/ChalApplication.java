package com.secchamp.chal;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class ChalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChalApplication.class, args);
	}

}
