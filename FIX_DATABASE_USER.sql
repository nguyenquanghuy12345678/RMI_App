-- =====================================================
-- FIX LỖI: Access denied for user
-- Chạy script này trên CẢ 2 máy (Windows XAMPP + máy kia)
-- =====================================================

-- BƯỚC 1: Tạo user rmiuser1 nếu chưa có
CREATE USER IF NOT EXISTS 'rmiuser1'@'%' IDENTIFIED BY 'rmi1';
CREATE USER IF NOT EXISTS 'rmiuser1'@'localhost' IDENTIFIED BY 'rmi1';

-- BƯỚC 2: Cấp quyền đầy đủ
GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'%';
GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'localhost';

-- BƯỚC 3: Flush privileges
FLUSH PRIVILEGES;

-- BƯỚC 4: Kiểm tra user đã tạo
SELECT User, Host FROM mysql.user WHERE User = 'rmiuser1';

-- BƯỚC 5: Kiểm tra quyền
SHOW GRANTS FOR 'rmiuser1'@'%';
SHOW GRANTS FOR 'rmiuser1'@'localhost';

-- =====================================================
-- Kết quả mong đợi:
-- User: rmiuser1 với Host: % và localhost
-- Grants: ALL PRIVILEGES on bank_db.*
-- =====================================================
