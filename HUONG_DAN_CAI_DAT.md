# HƯỚNG DẪN CÀI ĐẶT CHI TIẾT

## Bước 1: Cài đặt MySQL trên 2 máy

### Máy 1 (Server chính - ví dụ: 192.168.1.100)
```bash
# Windows: Tải MySQL Installer từ https://dev.mysql.com/downloads/installer/
# Linux: 
sudo apt-get update
sudo apt-get install mysql-server
```

### Máy 2 (Server dự phòng - ví dụ: 192.168.1.101)
- Làm tương tự như Máy 1

## Bước 2: Cấu hình MySQL cho phép kết nối từ xa

### Trên Windows:
1. Mở file: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
2. Tìm dòng `bind-address` và sửa thành:
```ini
bind-address = 0.0.0.0
```
3. Restart MySQL Service:
   - Win + R → services.msc
   - Tìm "MySQL" → Click chuột phải → Restart

### Trên Linux:
1. Sửa file cấu hình:
```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

2. Sửa dòng:
```ini
bind-address = 0.0.0.0
```

3. Restart MySQL:
```bash
sudo service mysql restart
```

## Bước 3: Tạo database và cấp quyền

### Chạy trên CẢ 2 máy:

```sql
-- 1. Đăng nhập MySQL
mysql -u root -p

-- 2. Tạo database
CREATE DATABASE bank_db;

-- 3. Cấp quyền truy cập từ xa
GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;

-- 4. Sử dụng database
USE bank_db;

-- 5. Tạo bảng accounts
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. Insert dữ liệu mẫu
INSERT INTO accounts (account_number, account_name, balance) VALUES
('ACC001', 'Nguyen Van A', 10000000.00),
('ACC002', 'Tran Thi B', 5000000.00),
('ACC003', 'Le Van C', 8000000.00),
('ACC004', 'Pham Thi D', 15000000.00),
('ACC005', 'Hoang Van E', 3000000.00);

-- 7. Kiểm tra
SELECT * FROM accounts;
```

## Bước 4: Kiểm tra Firewall

### Windows:
```powershell
# Mở PowerShell với quyền Administrator
# Cho phép port MySQL (3306)
New-NetFirewallRule -DisplayName "MySQL" -Direction Inbound -LocalPort 3306 -Protocol TCP -Action Allow

# Cho phép port RMI (1099)
New-NetFirewallRule -DisplayName "RMI" -Direction Inbound -LocalPort 1099 -Protocol TCP -Action Allow
```

### Linux:
```bash
# UFW Firewall
sudo ufw allow 3306/tcp
sudo ufw allow 1099/tcp
sudo ufw reload

# Hoặc firewalld
sudo firewall-cmd --permanent --add-port=3306/tcp
sudo firewall-cmd --permanent --add-port=1099/tcp
sudo firewall-cmd --reload
```

## Bước 5: Kiểm tra kết nối MySQL từ xa

### Từ máy Client, test kết nối:
```bash
# Test kết nối đến Máy 1
mysql -h 192.168.1.100 -u root -p

# Test kết nối đến Máy 2
mysql -h 192.168.1.101 -u root -p
```

Nếu kết nối thành công, bạn sẽ thấy MySQL prompt.

## Bước 6: Tải MySQL Connector/J

1. Tải từ: https://dev.mysql.com/downloads/connector/j/
2. Chọn "Platform Independent" → Download ZIP
3. Giải nén và lấy file `mysql-connector-java-8.x.xx.jar`

## Bước 7: Import project vào Eclipse

1. Mở Eclipse
2. File → Import → General → Existing Projects into Workspace
3. Chọn thư mục `RMI_App`
4. Click Finish

## Bước 8: Thêm MySQL JDBC Driver vào project

1. Click chuột phải vào project `RMI_App`
2. Properties → Java Build Path → Libraries
3. Click "Add External JARs..."
4. Chọn file `mysql-connector-java-8.x.xx.jar` đã tải ở Bước 6
5. Click Apply and Close

## Bước 9: Cấu hình địa chỉ Database

Mở file `src/database/DatabaseConfig.java` và sửa:

```java
// Database 1 - Máy chủ chính
public static final String DB1_HOST = "192.168.1.100"; // ← Sửa thành IP máy 1
public static final String DB1_USER = "root";
public static final String DB1_PASSWORD = "root";      // ← Sửa password

// Database 2 - Máy chủ dự phòng
public static final String DB2_HOST = "192.168.1.101"; // ← Sửa thành IP máy 2
public static final String DB2_USER = "root";
public static final String DB2_PASSWORD = "root";      // ← Sửa password
```

## Bước 10: Chạy ứng dụng

### A. Khởi động Server (chạy trên 1 máy bất kỳ)

1. Trong Eclipse, mở file `src/server/BankServer.java`
2. Click chuột phải → Run As → Java Application
3. Kiểm tra Console, phải thấy:
```
=================================
RMI Bank Server đã khởi động!
Server đang lắng nghe trên port 1099
Service name: BankService
=================================
```

### B. Khởi động Client (có thể chạy trên nhiều máy)

1. Trong Eclipse, mở file `src/client/BankClientUI.java`
2. Click chuột phải → Run As → Java Application
3. Giao diện UI sẽ hiện ra

### C. Sử dụng

1. **Kết nối Server**:
   - Nhập IP máy chạy Server (ví dụ: 192.168.1.100)
   - Nếu chạy cùng máy, để "localhost"
   - Click "Kết nối"

2. **Chuyển khoản**:
   - Tab "Chuyển khoản"
   - Chọn tài khoản nguồn và đích
   - Nhập số tiền
   - Click "Chuyển khoản"
   - Hệ thống sẽ tự động cập nhật lên 2 database

3. **Xem danh sách tài khoản**:
   - Tab "Danh sách tài khoản"
   - Click "Làm mới" để cập nhật

4. **Tạo tài khoản mới**:
   - Tab "Tạo tài khoản"
   - Điền thông tin
   - Click "Tạo tài khoản"

## Kiểm tra đồng bộ database

### Sau khi chuyển khoản, kiểm tra trên 2 máy:

```sql
-- Chạy trên Máy 1
mysql -u root -p
USE bank_db;
SELECT * FROM accounts;

-- Chạy trên Máy 2
mysql -u root -p
USE bank_db;
SELECT * FROM accounts;
```

Dữ liệu trên 2 máy phải giống nhau!

## Xử lý lỗi thường gặp

### Lỗi 1: "Connection refused" khi kết nối MySQL
**Nguyên nhân**: Firewall chặn hoặc MySQL không cho phép remote
**Giải pháp**: 
- Kiểm tra lại Bước 2 và Bước 4
- Thử tắt firewall tạm thời để test

### Lỗi 2: "Access denied for user 'root'@'...' "
**Nguyên nhân**: Sai username/password hoặc chưa cấp quyền
**Giải pháp**: Kiểm tra lại Bước 3

### Lỗi 3: RMI "Connection refused to host: ..."
**Nguyên nhân**: Server chưa chạy hoặc firewall chặn port 1099
**Giải pháp**: 
- Kiểm tra Server đã chạy chưa
- Mở port 1099 trong firewall (Bước 4)

### Lỗi 4: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Nguyên nhân**: Chưa add MySQL JDBC Driver vào project
**Giải pháp**: Làm lại Bước 8

### Lỗi 5: Module errors
**Nguyên nhân**: Java 9+ sử dụng module system
**Giải pháp**: File `module-info.java` đã được cấu hình sẵn

## Mô hình triển khai

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│  Client UI      │         │   RMI Server     │         │  MySQL Server 1 │
│  (Swing)        │◄────────┤   (Port 1099)    │◄────────┤  (192.168.1.100)│
│                 │  RMI    │                  │  JDBC   │                 │
└─────────────────┘         │                  │         └─────────────────┘
                            │                  │
                            │                  │         ┌─────────────────┐
                            │                  │◄────────┤  MySQL Server 2 │
                            └──────────────────┘  JDBC   │  (192.168.1.101)│
                                                          └─────────────────┘
```

## Ghi chú quan trọng

1. **Transaction**: Hệ thống sử dụng transaction để đảm bảo tính nhất quán - nếu cập nhật thất bại trên 1 trong 2 database, cả 2 sẽ được rollback.

2. **Performance**: Với hệ thống production, nên sử dụng Connection Pooling (HikariCP, C3P0) thay vì tạo connection mới mỗi lần.

3. **Security**: 
   - Đổi password mặc định 'root'
   - Sử dụng SSL cho kết nối database
   - Mã hóa dữ liệu nhạy cảm

4. **Scalability**: Có thể thêm nhiều database server và sử dụng kỹ thuật replication tự động.

## Liên hệ hỗ trợ
Nếu gặp vấn đề, hãy kiểm tra lại từng bước và đảm bảo:
- MySQL đã chạy trên cả 2 máy
- Firewall đã mở port 3306 và 1099
- Địa chỉ IP và password đã đúng
- MySQL JDBC Driver đã được thêm vào project
