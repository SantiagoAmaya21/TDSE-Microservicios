package com.tdse.twitter.model;

import com.tdse.twitter.dto.PostDto;
import java.time.LocalDateTime;
import java.util.List;

public class Stream {
    
    private List<PostDto> posts;
    private LocalDateTime lastUpdated;
    
    public Stream() {}
    
    public Stream(List<PostDto> posts) {
        this.posts = posts;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public List<PostDto> getPosts() {
        return posts;
    }
    
    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
