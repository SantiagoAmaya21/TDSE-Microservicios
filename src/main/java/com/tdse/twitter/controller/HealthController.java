package com.tdse.twitter.controller;

import com.tdse.twitter.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = "Check if the application is running and healthy"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "TDSE Twitter-like Application");
        healthInfo.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("Application is healthy", healthInfo));
    }
    
    @GetMapping("/info")
    @Operation(
        summary = "Application information",
        description = "Get detailed information about the application"
    )
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "TDSE Twitter-like Application");
        info.put("description", "A secure Twitter-like application with microservices and Auth0");
        info.put("version", "1.0.0");
        info.put("buildTime", LocalDateTime.now());
        info.put("features", new String[]{
            "Post Creation (140 chars max)",
            "Public Stream",
            "Auth0 Authentication",
            "RESTful API",
            "Swagger Documentation",
            "Microservices Architecture"
        });
        
        return ResponseEntity.ok(info);
    }
}
