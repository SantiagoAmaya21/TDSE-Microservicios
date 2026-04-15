package com.tdse.twitter.controller;

import com.tdse.twitter.dto.CreatePostRequest;
import com.tdse.twitter.dto.PostDto;
import com.tdse.twitter.model.Stream;
import com.tdse.twitter.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PostService postService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void getPublicStream_ShouldReturnStream() throws Exception {
        Stream stream = new Stream(Arrays.asList());
        when(postService.getPublicStream()).thenReturn(stream);
        
        mockMvc.perform(get("/api/posts")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    @WithMockUser(roles = {"USER"})
    public void createPost_ShouldReturnCreatedPost() throws Exception {
        CreatePostRequest request = new CreatePostRequest("Hello, world!");
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Hello, world!");
        
        when(postService.createPost(any(CreatePostRequest.class), any(String.class)))
                .thenReturn(postDto);
        
        mockMvc.perform(post("/api/posts")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Hello, world!"));
    }
    
    @Test
    public void getPostById_ShouldReturnPost() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Test post");
        
        when(postService.findPostById(1L)).thenReturn(Optional.of(postDto));
        
        mockMvc.perform(get("/api/posts/1")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test post"));
    }
    
    @Test
    public void getPostById_ShouldReturnNotFound() throws Exception {
        when(postService.findPostById(1L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/posts/1")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }
}
