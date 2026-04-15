# Database Initialization Script for PostgreSQL

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    auth0_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(200),
    picture TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Posts Table
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL CHECK (LENGTH(content) <= 140),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_users_auth0_id ON users(auth0_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(user_id);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_user_created ON posts(user_id, created_at DESC);

-- Create Sequence for Post IDs (if needed)
CREATE SEQUENCE IF NOT EXISTS posts_seq START 1;

-- Insert Sample Data (Optional - for testing)
INSERT INTO users (auth0_id, email, username, display_name) VALUES 
('auth0|sample1', 'user1@example.com', 'user1', 'Sample User 1'),
('auth0|sample2', 'user2@example.com', 'user2', 'Sample User 2')
ON CONFLICT (auth0_id) DO NOTHING;

-- Insert Sample Posts (Optional - for testing)
INSERT INTO posts (content, user_id) VALUES 
('Welcome to TDSE Twitter-like Application!', 1),
('This is a sample post for testing purposes.', 1),
('Another sample post from a different user.', 2)
ON CONFLICT DO NOTHING;

-- Grant Permissions (adjust as needed)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
