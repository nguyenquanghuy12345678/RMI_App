# 🪟 HƯỚNG DẪN CÀI ĐẶT TRÊN WINDOWS VỚI XAMPP

## 📋 Tổng quan
- **OS**: Windows 10/11
- **Database**: MySQL (đi kèm XAMPP)
- **Tool**: XAMPP Control Panel
- **Port**: 3306 (MySQL)

---

## 📥 BƯỚC 1: TẢI VÀ CÀI ĐẶT XAMPP

### Download XAMPP
1. Truy cập: https://www.apachefriends.org/download.html
2. Tải phiên bản Windows (khuyến nghị: XAMPP 8.2.x hoặc mới hơn)
3. File tải về: `xampp-windows-x64-8.2.x-installer.exe`

### Cài đặt
```
1. Double-click file installer
2. Click "Next"
3. Chọn components (chỉ cần):
   ☑ MySQL
   ☑ phpMyAdmin
   ☐ Apache (không bắt buộc cho dự án này)
   
4. Chọn thư mục cài đặt: C:\xampp (mặc định)
5. Click "Next" → "Next" → "Finish"
```

### Kết quả
```
C:\xampp\
├── mysql\           ← MySQL Server
├── phpMyAdmin\      ← Web interface
└── xampp-control.exe ← Control Panel
```

---

## ▶️ BƯỚC 2: KHỞI ĐỘNG MYSQL

### Mở XAMPP Control Panel
1. Chạy: `C:\xampp\xampp-control.exe`
2. Click nút **"Start"** bên cạnh MySQL
3. Trạng thái sẽ đổi thành màu xanh: **Running**

### Kiểm tra
```
✓ MySQL Running
✓ Port: 3306
```

**Lưu ý**: Nếu port 3306 bị chiếm:
- Click "Config" → "my.ini"
- Tìm `port = 3306` → Đổi thành `port = 3307`
- Save và Restart MySQL

---

## 🗄️ BƯỚC 3: TẠO DATABASE

### Cách 1: Dùng phpMyAdmin (Giao diện Web)

1. **Mở phpMyAdmin**
   - Truy cập: http://localhost/phpmyadmin
   - Username: `root`
   - Password: (để trống)

2. **Tạo Database**
   ```
   Click "New" (bên trái)
   Database name: bank_db
   Collation: utf8mb4_general_ci
   Click "Create"
   ```

3. **Import SQL Script**
   ```
   1. Click vào database "bank_db"
   2. Tab "Import"
   3. Click "Choose File" → Chọn file database_setup.sql
   4. Click "Go"
   5. Xem kết quả: "Import has been successfully finished"
   ```

4. **Kiểm tra**
   ```
   Click "bank_db" → "accounts"
   Tab "Browse" → Phải thấy 5 tài khoản mẫu
   ```

### Cách 2: Dùng MySQL Command Line

1. **Mở Command Prompt**
   ```cmd
   Win + R → cmd → Enter
   ```

2. **Di chuyển đến thư mục MySQL**
   ```cmd
   cd C:\xampp\mysql\bin
   ```

3. **Đăng nhập MySQL**
   ```cmd
   mysql.exe -u root -p
   ```
   (Nhấn Enter khi hỏi password - mặc định không có password)

4. **Tạo database và import**
   ```sql
   CREATE DATABASE bank_db;
   USE bank_db;
   SOURCE D:/eclipse-workspace/RMI_App/database_setup.sql;
   ```

5. **Kiểm tra**
   ```sql
   SHOW TABLES;
   SELECT * FROM accounts;
   ```
   → Phải thấy 5 dòng dữ liệu

6. **Thoát**
   ```sql
   EXIT;
   ```

---

## 🔐 BƯỚC 4: CẤU HÌNH BẢO MẬT (Tuỳ chọn nhưng khuyến nghị)

### Đặt password cho root

1. **Mở phpMyAdmin**
   - http://localhost/phpmyadmin

2. **Thay đổi password**
   ```
   Tab "User accounts"
   Click "Edit privileges" cho user "root"
   Tab "Change password"
   Nhập password mới (ví dụ: "root123")
   Click "Go"
   ```

3. **Cập nhật config phpMyAdmin**
   - Mở file: `C:\xampp\phpMyAdmin\config.inc.php`
   - Tìm dòng:
     ```php
     $cfg['Servers'][$i]['password'] = '';
     ```
   - Sửa thành:
     ```php
     $cfg['Servers'][$i]['password'] = 'root123';
     ```
   - Save file

4. **Cập nhật DatabaseConfig.java**
   ```java
   public static final String DB1_PASSWORD = "root123";
   ```

---

## 🌐 BƯỚC 5: CHO PHÉP KẾT NỐI TỪ XA

### Sửa file cấu hình my.ini

1. **Mở XAMPP Control Panel**
   - Click nút "Config" bên cạnh MySQL
   - Chọn "my.ini"

2. **Tìm và sửa dòng bind-address**
   ```ini
   # Tìm dòng này (có thể có dấu # ở đầu)
   # bind-address = 127.0.0.1
   
   # Sửa thành (bỏ dấu # nếu có)
   bind-address = 0.0.0.0
   ```

3. **Lưu file và Restart MySQL**
   - Save file my.ini
   - Trong XAMPP Control Panel: Stop → Start MySQL

### Cấp quyền remote access

1. **Mở MySQL Command Line**
   ```cmd
   cd C:\xampp\mysql\bin
   mysql.exe -u root -p
   ```

2. **Chạy lệnh cấp quyền**
   ```sql
   -- Cấp quyền cho root từ mọi IP
   GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root123';
   FLUSH PRIVILEGES;
   
   -- Kiểm tra
   SELECT user, host FROM mysql.user WHERE user='root';
   ```
   
   Kết quả phải có:
   ```
   +------+-----------+
   | user | host      |
   +------+-----------+
   | root | %         |
   | root | localhost |
   +------+-----------+
   ```

3. **Thoát**
   ```sql
   EXIT;
   ```

---

## 🔥 BƯỚC 6: CẤU HÌNH FIREWALL

### Mở PowerShell với quyền Administrator
```
Win + X → "Windows PowerShell (Admin)"
```

### Chạy lệnh mở port MySQL
```powershell
# Mở port 3306
New-NetFirewallRule -DisplayName "MySQL XAMPP" `
                    -Direction Inbound `
                    -LocalPort 3306 `
                    -Protocol TCP `
                    -Action Allow

# Kiểm tra
Get-NetFirewallRule -DisplayName "MySQL XAMPP"
```

### Hoặc dùng script có sẵn
```powershell
cd D:\eclipse-workspace\RMI_App
.\setup_firewall.ps1
```

---

## 🧪 BƯỚC 7: KIỂM TRA KẾT NỐI

### Test local connection
```cmd
cd C:\xampp\mysql\bin
mysql.exe -u root -p -h localhost
```

### Test remote connection (từ máy khác)
```cmd
mysql.exe -u root -p -h <IP_MAY_WINDOWS>
```

### Test bằng script
```powershell
cd D:\eclipse-workspace\RMI_App
.\check_connection.ps1
```

### Lấy địa chỉ IP của máy Windows
```powershell
ipconfig | findstr IPv4
```
Ghi nhớ IP (ví dụ: 192.168.1.100)

---

## ⚙️ BƯỚC 8: CẤU HÌNH PROJECT

### Sửa file DatabaseConfig.java

```java
// File: src/database/DatabaseConfig.java

// Database 1 - Máy Windows (XAMPP)
public static final String DB1_HOST = "localhost";  // Hoặc IP máy này
public static final String DB1_PORT = "3306";
public static final String DB1_NAME = "bank_db";
public static final String DB1_USER = "root";
public static final String DB1_PASSWORD = "root123";  // Password bạn đã đặt

// Database 2 - Máy Linux (sẽ setup ở bước sau)
public static final String DB2_HOST = "192.168.1.101";  // IP máy Linux
public static final String DB2_PORT = "3306";
public static final String DB2_NAME = "bank_db";
public static final String DB2_USER = "root";
public static final String DB2_PASSWORD = "root123";
```

---

## 🚀 BƯỚC 9: TEST XAMPP MYSQL

### Test 1: Kết nối cơ bản
```sql
1. Mở phpMyAdmin: http://localhost/phpmyadmin
2. Click "bank_db"
3. Click table "accounts"
4. Tab "Browse" → Thấy 5 tài khoản ✓
```

### Test 2: Query
```sql
-- Trong phpMyAdmin → Tab "SQL"
SELECT * FROM accounts WHERE account_number = 'ACC001';
```
Kết quả: 
```
ACC001 | Nguyen Van A | 10000000.00
```

### Test 3: Insert
```sql
INSERT INTO accounts (account_number, account_name, balance) 
VALUES ('TEST001', 'Test User', 1000000);

SELECT * FROM accounts WHERE account_number = 'TEST001';
```

### Test 4: Update
```sql
UPDATE accounts SET balance = 999000 WHERE account_number = 'TEST001';

SELECT * FROM accounts WHERE account_number = 'TEST001';
```

### Test 5: Delete test data
```sql
DELETE FROM accounts WHERE account_number = 'TEST001';
```

---

## 🔧 XỬ LÝ LỖI THƯỜNG GẶP

### ❌ Lỗi 1: MySQL không start được

**Nguyên nhân**: Port 3306 bị chiếm
**Giải pháp**:
```
1. Mở XAMPP Control Panel
2. Click "Netstat" → Tìm port 3306
3. Nếu bị chiếm, có 2 cách:
   
   Cách A: Tắt ứng dụng đang chiếm port
   Cách B: Đổi port MySQL:
   - Config → my.ini
   - Tìm: port = 3306
   - Đổi: port = 3307
   - Save và Start lại
```

### ❌ Lỗi 2: Access denied for user 'root'@'%'

**Nguyên nhân**: Chưa cấp quyền remote
**Giải pháp**: Làm lại BƯỚC 5

### ❌ Lỗi 3: Can't connect to MySQL server on 'IP'

**Nguyên nhân**: Firewall chặn
**Giải pháp**: Làm lại BƯỚC 6

### ❌ Lỗi 4: Table 'bank_db.accounts' doesn't exist

**Nguyên nhân**: Chưa import SQL script
**Giải pháp**: Làm lại BƯỚC 3

---

## 📊 THÔNG TIN HỆ THỐNG

### Đường dẫn quan trọng
```
XAMPP Installation: C:\xampp\
MySQL Config:       C:\xampp\mysql\bin\my.ini
MySQL Data:         C:\xampp\mysql\data\
MySQL Bin:          C:\xampp\mysql\bin\
phpMyAdmin:         C:\xampp\phpMyAdmin\
Error Log:          C:\xampp\mysql\data\mysql_error.log
```

### Thông tin kết nối
```
Host:     localhost (hoặc 192.168.1.x)
Port:     3306
Database: bank_db
Username: root
Password: root123 (hoặc để trống nếu chưa đặt)
```

### Dịch vụ
```
MySQL Service: Chạy qua XAMPP Control Panel
Auto Start:    Config → Checkmark "MySQL" trong "Autostart of modules"
```

---

## 🎯 CHECKLIST HOÀN THÀNH

- [ ] Đã cài XAMPP thành công
- [ ] MySQL chạy được trong XAMPP Control Panel
- [ ] Đã tạo database 'bank_db'
- [ ] Đã import file database_setup.sql
- [ ] Có 5 tài khoản mẫu trong bảng accounts
- [ ] Đã đặt password cho root (tuỳ chọn)
- [ ] Đã cấu hình bind-address = 0.0.0.0
- [ ] Đã cấp quyền remote access
- [ ] Đã mở port 3306 trong Firewall
- [ ] Test kết nối local thành công
- [ ] Test kết nối remote thành công (nếu có máy khác)
- [ ] Đã lấy địa chỉ IP máy Windows
- [ ] Đã cập nhật DatabaseConfig.java

---

## 💡 TIPS VÀ LƯU Ý

### Auto Start MySQL khi khởi động Windows
```
1. Mở XAMPP Control Panel
2. Click "Config" (góc trên bên phải)
3. Checkmark "MySQL" trong "Autostart of modules"
4. Save
```

### Backup Database
```sql
-- Trong phpMyAdmin:
1. Click "bank_db"
2. Tab "Export"
3. Method: Quick
4. Format: SQL
5. Click "Go"
→ File backup được tải về
```

### Restore Database
```sql
-- Trong phpMyAdmin:
1. Click "bank_db"
2. Tab "Import"
3. Choose File → Chọn file backup .sql
4. Click "Go"
```

### Xem Log lỗi
```
C:\xampp\mysql\data\mysql_error.log
```

### Tối ưu hiệu năng (cho dự án lớn)
```ini
# File: C:\xampp\mysql\bin\my.ini
# Thêm/sửa các dòng sau:

innodb_buffer_pool_size = 256M
max_connections = 100
query_cache_size = 32M
```

---

## 🆚 SO SÁNH: XAMPP vs MySQL Standalone

| Tiêu chí | XAMPP | MySQL Standalone |
|----------|-------|------------------|
| Cài đặt | Rất dễ (1-click) | Phức tạp hơn |
| Quản lý | XAMPP Control Panel | Services.msc |
| phpMyAdmin | ✓ Có sẵn | Phải cài riêng |
| Auto Start | Tuỳ chọn | Service tự động |
| Gỡ cài đặt | Dễ dàng | Phức tạp hơn |
| **Khuyến nghị** | ✓ Cho dev/test | Cho production |

---

## 🎓 TỔNG KẾT

### Bạn đã có:
✅ MySQL Server chạy trên Windows (XAMPP)  
✅ Database 'bank_db' với dữ liệu mẫu  
✅ phpMyAdmin để quản lý  
✅ Remote access được cấu hình  
✅ Firewall đã mở port 3306  

### Bước tiếp theo:
👉 Setup máy thứ 2 (Linux) - Xem file `SETUP_LINUX_MARIADB.md`  
👉 Cấu hình `DatabaseConfig.java` với IP 2 máy  
👉 Chạy RMI Server và Client  

---

**🎉 XAMPP MySQL trên Windows đã sẵn sàng!**

═══════════════════════════════════════════════════════════
XAMPP MySQL Setup for RMI Banking System | Windows Guide
═══════════════════════════════════════════════════════════
