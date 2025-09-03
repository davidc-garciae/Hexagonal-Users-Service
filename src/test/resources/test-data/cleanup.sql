-- cleanup.sql - Reset database for testing
DELETE FROM users WHERE email LIKE '%test.com' OR email LIKE '%@test%';
DELETE FROM users WHERE id > 10; -- Preserve seed data

-- Reset sequences if needed
-- ALTER SEQUENCE users_id_seq RESTART WITH 11;
