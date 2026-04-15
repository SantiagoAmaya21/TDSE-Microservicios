import React, { useState, useEffect } from 'react';
import { getPosts } from '../services/api';
import PostCard from './PostCard';

const Stream = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await getPosts();
      setPosts(data.posts || []);
    } catch (err) {
      setError('Failed to load posts. Please try again.');
      console.error('Error fetching posts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleNewPost = (newPost) => {
    setPosts(prevPosts => [newPost, ...prevPosts]);
  };

  if (loading) {
    return (
      <div className="loading">
        <p>Loading posts...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error">
        {error}
        <button 
          onClick={fetchPosts}
          className="ml-4 text-blue-500 hover:underline"
        >
          Retry
        </button>
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <p>No posts yet. Be the first to share something!</p>
      </div>
    );
  }

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4 text-gray-800">Public Stream</h2>
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
};

export default Stream;
