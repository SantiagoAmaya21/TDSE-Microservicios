package com.tdse.twitter.controller;

import com.tdse.twitter.model.Stream;
import com.tdse.twitter.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "*")
public class StreamController {
    
    private final PostService postService;
    
    public StreamController(PostService postService) {
        this.postService = postService;
    }
    
    @GetMapping
    @Operation(
        summary = "Get public stream",
        description = "Retrieve the global public stream of all posts (same as GET /api/posts)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the public stream",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Stream.class)
            )
        )
    })
    public ResponseEntity<Stream> getPublicStream() {
        Stream stream = postService.getPublicStream();
        return ResponseEntity.ok(stream);
    }
}
