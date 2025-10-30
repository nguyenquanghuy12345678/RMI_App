-- Script tạo database và table cho hệ thống ngân hàng RMI
-- Chạy script này trên CẢ 2 máy chủ MySQL

-- Tạo database
CREATE DATABASE IF NOT EXISTS bank_db;
USE bank_db;

-- Tạo bảng accounts
CREATE TABLE IF NOT EXISTS accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo index cho truy vấn nhanh hơn
CREATE INDEX idx_account_number ON accounts(account_number);

-- Insert dữ liệu mẫu
INSERT INTO accounts (account_number, account_name, balance) VALUES
('ACC001', 'Nguyen Van A', 10000000.00),
('ACC002', 'Tran Thi B', 5000000.00),
('ACC003', 'Le Van C', 8000000.00),
('ACC004', 'Pham Thi D', 15000000.00),
('ACC005', 'Hoang Van E', 3000000.00);

-- Xem dữ liệu
SELECT * FROM accounts;

-- Cấp quyền cho user (tuỳ chọn)
-- GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root';
-- FLUSH PRIVILEGES;
