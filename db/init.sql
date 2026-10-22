-- Run these statements in MySQL as a database administrator.
CREATE DATABASE IF NOT EXISTS employee_management
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'employee_app'@'localhost'
    IDENTIFIED BY 'replace-with-a-strong-password';

GRANT ALL PRIVILEGES ON employee_management.* TO 'employee_app'@'localhost';
FLUSH PRIVILEGES;
