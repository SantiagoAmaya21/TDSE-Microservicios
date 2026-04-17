package com.tdse.twitter.service;

import com.tdse.twitter.dto.UserDto;
import com.tdse.twitter.model.User;
import com.tdse.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setAuth0Id("auth0|123456");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setDisplayName("Test User");
        testUser.setPicture("https://example.com/avatar.jpg");
        testUser.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void getOrCreateUser_ShouldReturnExistingUser_WhenUserExists() {
        // Arrange
        String auth0Id = "auth0|123456";
        String email = "test@example.com";
        String username = "testuser";
        String displayName = "Test User";
        String picture = "https://example.com/avatar.jpg";
        
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(testUser));
        
        // Act
        UserDto result = userService.getOrCreateUser(auth0Id, email, username, displayName, picture);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(auth0Id, result.getAuth0Id());
        assertEquals(email, result.getEmail());
        assertEquals(username, result.getUsername());
        assertEquals(displayName, result.getDisplayName());
        assertEquals(picture, result.getPicture());
        
        verify(userRepository).findByAuth0Id(auth0Id);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getOrCreateUser_ShouldCreateNewUser_WhenUserNotExists() {
        // Arrange
        String auth0Id = "auth0|newuser";
        String email = "newuser@example.com";
        String username = "newuser";
        String displayName = "New User";
        String picture = "https://example.com/newavatar.jpg";
        
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        
        // Act
        UserDto result = userService.getOrCreateUser(auth0Id, email, username, displayName, picture);
        
        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(auth0Id, result.getAuth0Id());
        assertEquals(email, result.getEmail());
        assertEquals(username, result.getUsername());
        assertEquals(displayName, result.getDisplayName());
        assertEquals(picture, result.getPicture());
        
        verify(userRepository).findByAuth0Id(auth0Id);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<UserDto> result = userService.findByUsername(username);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(username, result.get().getUsername());
        
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserDto> result = userService.findByUsername(username);
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setAuth0Id("auth0|789012");
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setDisplayName("User Two");
        user2.setPicture("https://example.com/avatar2.jpg");
        user2.setCreatedAt(LocalDateTime.now());
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        List<UserDto> result = userService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        
        verify(userRepository).findAll();
    }
    
    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<UserDto> result = userService.findByEmail(email);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserDto> result = userService.findByEmail(email);
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void existsByAuth0Id_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        String auth0Id = "auth0|123456";
        when(userRepository.existsByAuth0Id(auth0Id)).thenReturn(true);
        
        // Act
        boolean result = userService.existsByAuth0Id(auth0Id);
        
        // Assert
        assertTrue(result);
        verify(userRepository).existsByAuth0Id(auth0Id);
    }
    
    @Test
    void existsByAuth0Id_ShouldReturnFalse_WhenUserNotExists() {
        // Arrange
        String auth0Id = "auth0|nonexistent";
        when(userRepository.existsByAuth0Id(auth0Id)).thenReturn(false);
        
        // Act
        boolean result = userService.existsByAuth0Id(auth0Id);
        
        // Assert
        assertFalse(result);
        verify(userRepository).existsByAuth0Id(auth0Id);
    }
    
    @Test
    void findByAuth0Id_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String auth0Id = "auth0|123456";
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<UserDto> result = userService.findByAuth0Id(auth0Id);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(auth0Id, result.get().getAuth0Id());
        
        verify(userRepository).findByAuth0Id(auth0Id);
    }
    
    @Test
    void findByAuth0Id_ShouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        String auth0Id = "auth0|nonexistent";
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserDto> result = userService.findByAuth0Id(auth0Id);
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByAuth0Id(auth0Id);
    }
}
