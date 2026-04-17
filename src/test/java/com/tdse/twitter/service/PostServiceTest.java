package com.tdse.twitter.service;

import com.tdse.twitter.dto.CreatePostRequest;
import com.tdse.twitter.dto.PostDto;
import com.tdse.twitter.model.Post;
import com.tdse.twitter.model.Stream;
import com.tdse.twitter.model.User;
import com.tdse.twitter.repository.PostRepository;
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
public class PostServiceTest {
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private PostService postService;
    
    private User testUser;
    private Post testPost;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setAuth0Id("auth0|123456");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setDisplayName("Test User");
        testUser.setCreatedAt(LocalDateTime.now());
        
        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("Test post content");
        testPost.setUser(testUser);
        testPost.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void createPost_ShouldReturnPostDto_WhenUserExists() {
        // Arrange
        CreatePostRequest request = new CreatePostRequest("Test post content");
        String auth0Id = "auth0|123456";
        
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        
        // Act
        PostDto result = postService.createPost(request, auth0Id);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test post content", result.getContent());
        assertEquals(testUser.getId(), result.getUser().getId());
        
        verify(userRepository).findByAuth0Id(auth0Id);
        verify(postRepository).save(any(Post.class));
    }
    
    @Test
    void createPost_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        CreatePostRequest request = new CreatePostRequest("Test post content");
        String auth0Id = "nonexistent|user";
        
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createPost(request, auth0Id);
        });
        
        assertEquals("User not found with Auth0 ID: nonexistent|user", exception.getMessage());
        verify(userRepository).findByAuth0Id(auth0Id);
        verify(postRepository, never()).save(any(Post.class));
    }
    
    @Test
    void getPublicStream_ShouldReturnStreamWithPosts() {
        // Arrange
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findAllWithUserOrderByCreatedAtDesc()).thenReturn(posts);
        
        // Act
        Stream result = postService.getPublicStream();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPosts().size());
        assertEquals("Test post content", result.getPosts().get(0).getContent());
        
        verify(postRepository).findAllWithUserOrderByCreatedAtDesc();
    }
    
    @Test
    void getPublicStream_ShouldReturnEmptyStream_WhenNoPosts() {
        // Arrange
        when(postRepository.findAllWithUserOrderByCreatedAtDesc()).thenReturn(Arrays.asList());
        
        // Act
        Stream result = postService.getPublicStream();
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.getPosts().size());
        
        verify(postRepository).findAllWithUserOrderByCreatedAtDesc();
    }
    
    @Test
    void findPostById_ShouldReturnPostDto_WhenPostExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        
        // Act
        Optional<PostDto> result = postService.findPostById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test post content", result.get().getContent());
        
        verify(postRepository).findById(1L);
    }
    
    @Test
    void findPostById_ShouldReturnEmpty_WhenPostNotFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<PostDto> result = postService.findPostById(1L);
        
        // Assert
        assertFalse(result.isPresent());
        verify(postRepository).findById(1L);
    }
    
    @Test
    void deletePost_ShouldDeletePost_WhenUserIsAuthor() {
        // Arrange
        String auth0Id = "auth0|123456";
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        
        // Act
        postService.deletePost(1L, auth0Id);
        
        // Assert
        verify(postRepository).findById(1L);
        verify(postRepository).delete(testPost);
    }
    
    @Test
    void deletePost_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        String auth0Id = "auth0|123456";
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.deletePost(1L, auth0Id);
        });
        
        assertEquals("Post not found with ID: 1", exception.getMessage());
        verify(postRepository).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }
    
    @Test
    void deletePost_ShouldThrowException_WhenUserIsNotAuthor() {
        // Arrange
        String differentAuth0Id = "auth0|789012";
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.deletePost(1L, differentAuth0Id);
        });
        
        assertEquals("You can only delete your own posts", exception.getMessage());
        verify(postRepository).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }
    
    @Test
    void getPostsByUserId_ShouldReturnUserPosts() {
        // Arrange
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByUserIdWithUser(1L)).thenReturn(posts);
        
        // Act
        List<PostDto> result = postService.getPostsByUserId(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test post content", result.get(0).getContent());
        
        verify(postRepository).findByUserIdWithUser(1L);
    }
    
    @Test
    void getPostCountByUserId_ShouldReturnCount() {
        // Arrange
        when(postRepository.countByUserId(1L)).thenReturn(5L);
        
        // Act
        long result = postService.getPostCountByUserId(1L);
        
        // Assert
        assertEquals(5L, result);
        verify(postRepository).countByUserId(1L);
    }
}
