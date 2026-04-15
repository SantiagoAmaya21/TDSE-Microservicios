import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { createPost } from '../services/api';

const CreatePost = () => {
  const [content, setContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { getAccessTokenSilently } = useAuth0();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setError('Post content cannot be empty');
      return;
    }

    if (content.length > 140) {
      setError('Post content cannot exceed 140 characters');
      return;
    }

    setIsSubmitting(true);
    setError('');
    setSuccess('');

    try {
      const token = await getAccessTokenSilently();
      await createPost({ content }, token);
      setSuccess('Post created successfully!');
      setContent('');
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to create post. Please try again.');
      console.error('Error creating post:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getCharacterCountClass = () => {
    const remaining = 140 - content.length;
    if (remaining <= 0) return 'danger';
    if (remaining <= 20) return 'warning';
    return '';
  };

  return (
    <div className="create-post-form">
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}
      
      <form onSubmit={handleSubmit}>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What's happening?"
          className="post-textarea"
          rows="3"
          maxLength="140"
          disabled={isSubmitting}
        />
        
        <div className="flex justify-between items-center mt-3">
          <div className={`character-count ${getCharacterCountClass()}`}>
            {content.length}/140
          </div>
          
          <button
            type="submit"
            className="post-button"
            disabled={isSubmitting || !content.trim() || content.length > 140}
          >
            {isSubmitting ? 'Posting...' : 'Post'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreatePost;
