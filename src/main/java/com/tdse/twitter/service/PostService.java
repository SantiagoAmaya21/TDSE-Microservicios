package com.tdse.twitter.service;

import com.tdse.twitter.dto.CreatePostRequest;
import com.tdse.twitter.dto.PostDto;
import com.tdse.twitter.dto.UserDto;
import com.tdse.twitter.model.Post;
import com.tdse.twitter.model.Stream;
import com.tdse.twitter.model.User;
import com.tdse.twitter.repository.PostRepository;
import com.tdse.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    
    public PostDto createPost(CreatePostRequest request, String auth0Id) {
        Optional<User> userOptional = userRepository.findByAuth0Id(auth0Id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with Auth0 ID: " + auth0Id);
        }
        
        Post post = new Post();
        post.setContent(request.getContent());
        post.setUser(userOptional.get());
        
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }
    
    public Stream getPublicStream() {
        List<Post> posts = postRepository.findAllWithUserOrderByCreatedAtDesc();
        List<PostDto> postDtos = posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new Stream(postDtos);
    }
    
    public List<PostDto> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserIdWithUser(userId);
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<PostDto> findPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public void deletePost(Long id, String auth0Id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new RuntimeException("Post not found with ID: " + id);
        }
        
        Post post = postOptional.get();
        if (!post.getUser().getAuth0Id().equals(auth0Id)) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.delete(post);
    }
    
    public long getPostCountByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }
    
    private PostDto convertToDto(Post post) {
        UserDto userDto = new UserDto(
                post.getUser().getId(),
                post.getUser().getAuth0Id(),
                post.getUser().getEmail(),
                post.getUser().getUsername(),
                post.getUser().getDisplayName(),
                post.getUser().getPicture(),
                post.getUser().getCreatedAt()
        );
        
        return new PostDto(
                post.getId(),
                post.getContent(),
                userDto,
                post.getCreatedAt()
        );
    }
}
