-- ==============================================================
-- Placement Management System Seed Data
-- Passwords below are BCrypt hashes of 'Password@123'
-- Hash: $2a$10$7Q2V8pGsqpM8R6YmI9qJreKqfXfDq.lW0C7nL5qQhR1iXk0N4e2s6
-- ==============================================================

USE placement_db;

-- Standard Skills
INSERT IGNORE INTO skills (name, category) VALUES
('Java', 'Backend'),
('Spring Boot', 'Backend'),
('Python', 'Backend/AI'),
('React.js', 'Frontend'),
('JavaScript', 'Frontend'),
('HTML/CSS', 'Frontend'),
('MySQL', 'Database'),
('PostgreSQL', 'Database'),
('Docker', 'DevOps'),
('Git', 'Tools'),
('RESTful APIs', 'Backend'),
('Machine Learning', 'AI'),
('Data Structures', 'Core CS'),
('Algorithms', 'Core CS'),
('Cloud Computing', 'Cloud');

-- Companies
INSERT IGNORE INTO companies (id, name, website, industry, location, description, is_active) VALUES
(1, 'TechCorp Solutions', 'https://techcorp.example.com', 'Information Technology', 'Bangalore, India', 'Leading IT and enterprise cloud solutions provider.', TRUE),
(2, 'FinTech Global', 'https://fintechglobal.example.com', 'Financial Services', 'Mumbai, India', 'Innovator in automated payments and banking tech.', TRUE),
(3, 'CloudNative Labs', 'https://cloudnative.example.com', 'Software Products', 'Hyderabad, India', 'Specialized in microservices, distributed systems and AI platforms.', TRUE);

-- Announcements
INSERT INTO announcements (title, content, priority, target_role) VALUES
('Campus Placement Season 2026 Commences', 'The official placement drive for the 2026 graduating batch is now open. Update your profiles and resumes before applying.', 'HIGH', 'ALL'),
('TechCorp Solutions Pre-Placement Talk', 'TechCorp will conduct a virtual PPT on Oct 25th at 10:00 AM. Attendance is mandatory for registered students.', 'MEDIUM', 'STUDENT');
