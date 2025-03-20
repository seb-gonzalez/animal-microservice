package com.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// Spring Boot requires a main class to bootstrap the application and launch the microservice.

@SpringBootApplication
public class AnimalMicroserviceApplication {
	
    public static void main(String[] args) {
    	
        SpringApplication.run(AnimalMicroserviceApplication.class, args);
    }
}