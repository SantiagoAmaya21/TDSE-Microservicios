package com.tdse.twitter.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataInitializer {
    
    @Bean
    CommandLineRunner init() {
        return args -> {
            System.out.println("TDSE Twitter-like Application started successfully!");
            System.out.println("Swagger UI available at: http://localhost:8080/swagger-ui.html");
            System.out.println("H2 Console available at: http://localhost:8080/h2-console");
        };
    }
}
