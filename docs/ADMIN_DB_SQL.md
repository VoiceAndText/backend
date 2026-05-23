-- Add role and status columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'USER' NOT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;

-- Assign admin role to an existing user (replace email)
UPDATE users SET role = 'ADMIN' WHERE email = '관리자이메일@example.com';
