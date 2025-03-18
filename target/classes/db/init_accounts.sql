-- Create ACCOUNTS table if it doesn't exist
CREATE TABLE IF NOT EXISTS ACCOUNTS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    EMAIL TEXT NOT NULL UNIQUE,
    PASSWORD TEXT NOT NULL,
    ROLE TEXT NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert or update demo admin user
-- Password is 'admin123'
INSERT OR REPLACE INTO ACCOUNTS (username, password, userID) 
VALUES (
    'admin@ums.com',
    'uni123',
    'ADMIN_001'
); 