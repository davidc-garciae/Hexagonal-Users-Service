-- insert-test-data.sql - Insert test data for specific scenarios
-- Insert base test users for testing scenarios

-- Test Owner
INSERT INTO users (id, first_name, last_name, document, phone, birth_date, email, password, role, active, restaurant_id)
VALUES (101, 'Test', 'Owner', '12345678', '+573001234567', '1990-01-01', 'test.owner@test.com',
        '$2a$10$encoded.password.hash', 'OWNER', true, null)
ON CONFLICT (email) DO NOTHING;

-- Test Employee
INSERT INTO users (id, first_name, last_name, document, phone, birth_date, email, password, role, active, restaurant_id)
VALUES (102, 'Test', 'Employee', '87654321', '+573007654321', '1995-05-15', 'test.employee@test.com',
        '$2a$10$encoded.password.hash', 'EMPLOYEE', true, 1)
ON CONFLICT (email) DO NOTHING;

-- Test Customer
INSERT INTO users (id, first_name, last_name, document, phone, birth_date, email, password, role, active, restaurant_id)
VALUES (103, 'Test', 'Customer', '11111111', '+573001111111', '1992-08-20', 'test.customer@test.com',
        '$2a$10$encoded.password.hash', 'CUSTOMER', true, null)
ON CONFLICT (email) DO NOTHING;
