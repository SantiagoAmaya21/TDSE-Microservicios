package com.tdse.twitter.controller;

import com.tdse.twitter.dto.UserDto;
import com.tdse.twitter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    @WithMockUser
    public void getCurrentUser_ShouldReturnUser() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAuth0Id("auth0|123456");
        userDto.setEmail("test@example.com");
        userDto.setUsername("testuser");
        userDto.setDisplayName("Test User");
        
        when(userService.getOrCreateUser(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(userDto);
        
        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                .with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.auth0Id").value("auth0|123456"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.displayName").value("Test User"));
    }
    
    @Test
    public void getCurrentUser_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void getUserByUsername_ShouldReturnUser() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setDisplayName("Test User");
        
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(userDto));
        
        // Act & Assert
        mockMvc.perform(get("/api/users/testuser")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.displayName").value("Test User"));
    }
    
    @Test
    public void getUserByUsername_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Arrange
        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/users/nonexistent")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Arrange
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("user1");
        
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user2");
        
        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));
        
        // Act & Assert
        mockMvc.perform(get("/api/users")
                .with(request -> {
                    request.setRemoteAddr("127.0.0.1");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }
}
