CREATE DATABASE bankingDB;
GRANT ALL PRIVILEGES ON bankingDB.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

DROP TABLE IF EXISTS users;

-- Create users table with proper constraints and indexes
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email),
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add comments to the table and columns
ALTER TABLE users COMMENT 'User accounts table';
ALTER TABLE users MODIFY COLUMN id BIGINT AUTO_INCREMENT COMMENT 'Primary key';
ALTER TABLE users MODIFY COLUMN username VARCHAR(50) NOT NULL COMMENT 'Unique username';
ALTER TABLE users MODIFY COLUMN email VARCHAR(100) NOT NULL COMMENT 'Unique email address';
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NOT NULL COMMENT 'Encrypted password';
ALTER TABLE users MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Account creation timestamp';
ALTER TABLE users MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp'; 