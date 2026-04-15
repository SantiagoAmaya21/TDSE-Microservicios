import React from 'react';

const PostCard = ({ post }) => {
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    if (diffInMinutes < 10080) return `${Math.floor(diffInMinutes / 1440)}d ago`;
    
    return date.toLocaleDateString();
  };

  return (
    <div className="post-card">
      <div className="user-info">
        {post.user?.picture && (
          <img
            src={post.user.picture}
            alt={post.user.displayName || post.user.username}
            className="user-avatar"
          />
        )}
        <div className="user-details">
          <div className="username">
            @{post.user?.username}
          </div>
          <div className="post-time">
            {formatDate(post.createdAt)}
          </div>
        </div>
      </div>
      
      <div className="post-content">
        {post.content}
      </div>
      
      <div className="post-meta">
        Posted by {post.user?.displayName || post.user?.username}
      </div>
    </div>
  );
};

export default PostCard;
