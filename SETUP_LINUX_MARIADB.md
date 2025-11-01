# 🐧 HƯỚNG DẪN CÀI ĐẶT TRÊN LINUX VỚI MARIADB

## 📋 Tổng quan
- **OS**: Ubuntu 20.04/22.04/24.04 (hoặc Debian-based)
- **Database**: MariaDB 10.x
- **Lý do chọn MariaDB**:
  - ✅ Nhẹ hơn MySQL
  - ✅ Hiệu năng cao hơn
  - ✅ Ít lỗi hơn
  - ✅ Tương thích 100% với MySQL
  - ✅ Mã nguồn mở hoàn toàn
  - ✅ Được cộng đồng hỗ trợ tốt

---

## 🔄 TẠI SAO CHỌN MARIADB THAY VÌ MYSQL?

### So sánh MariaDB vs MySQL vs XAMPP Linux

| Tiêu chí | MariaDB | MySQL | XAMPP Linux |
|----------|---------|-------|-------------|
| Dung lượng | ~200MB | ~400MB | ~600MB |
| RAM sử dụng | 256MB | 512MB | 800MB+ |
| Hiệu năng | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Ổn định | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Cài đặt | Rất dễ | Dễ | Phức tạp |
| Tương thích MySQL | 100% | 100% | 100% |
| Mã nguồn | Mở hoàn toàn | Có giới hạn | Mở |
| **KHUYẾN NGHỊ** | ✓✓✓ | ✓✓ | ✓ |

**Kết luận**: MariaDB là lựa chọn tốt nhất cho Linux!

---

## 📥 BƯỚC 1: CÀI ĐẶT MARIADB

### Update hệ thống
```bash
sudo apt update
sudo apt upgrade -y
```

### Cài đặt MariaDB Server
```bash
sudo apt install mariadb-server mariadb-client -y
```

### Kiểm tra cài đặt
```bash
# Kiểm tra version
mariadb --version
# Kết quả: mariadb Ver 15.1 Distrib 10.x.x-MariaDB

# Kiểm tra service
sudo systemctl status mariadb
# Kết quả: ● mariadb.service - MariaDB 10.x database server
#          Active: active (running)
```

### Khởi động và enable auto-start
```bash
# Khởi động MariaDB
sudo systemctl start mariadb

# Enable auto-start khi boot
sudo systemctl enable mariadb

# Kiểm tra
sudo systemctl is-enabled mariadb
# Kết quả: enabled
```

---

## 🔐 BƯỚC 2: BẢO MẬT MARIADB

### Chạy script bảo mật
```bash
sudo mysql_secure_installation
```

### Trả lời các câu hỏi:

```
1. Enter current password for root (enter for none): 
   → Nhấn ENTER (chưa có password)

2. Switch to unix_socket authentication [Y/n]
   → Nhập: n (giữ password authentication)

3. Change the root password? [Y/n]
   → Nhập: Y
   
4. New password: 
   → Nhập: root123 (hoặc password bạn muốn)
   
5. Re-enter new password: 
   → Nhập lại: root123

6. Remove anonymous users? [Y/n]
   → Nhập: Y

7. Disallow root login remotely? [Y/n]
   → Nhập: n (cho phép remote - QUAN TRỌNG!)

8. Remove test database and access to it? [Y/n]
   → Nhập: Y

9. Reload privilege tables now? [Y/n]
   → Nhập: Y
```

### Kết quả
```
All done!  If you've completed all of the above steps, your MariaDB
installation should now be secure.

Thanks for using MariaDB!
```

---

## 🗄️ BƯỚC 3: TẠO DATABASE

### Đăng nhập MariaDB
```bash
sudo mariadb -u root -p
# Nhập password: root123
```

### Tạo database
```sql
-- Tạo database
CREATE DATABASE bank_db;

-- Sử dụng database
USE bank_db;

-- Tạo bảng accounts
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo index
CREATE INDEX idx_account_number ON accounts(account_number);

-- Insert dữ liệu mẫu
INSERT INTO accounts (account_number, account_name, balance) VALUES
('ACC001', 'Nguyen Van A', 10000000.00),
('ACC002', 'Tran Thi B', 5000000.00),
('ACC003', 'Le Van C', 8000000.00),
('ACC004', 'Pham Thi D', 15000000.00),
('ACC005', 'Hoang Van E', 3000000.00);

-- Kiểm tra
SELECT * FROM accounts;
```

### Kết quả
```
+----+----------------+---------------+-------------+---------------------+---------------------+
| id | account_number | account_name  | balance     | created_at          | updated_at          |
+----+----------------+---------------+-------------+---------------------+---------------------+
|  1 | ACC001         | Nguyen Van A  | 10000000.00 | 2025-11-02 10:00:00 | 2025-11-02 10:00:00 |
|  2 | ACC002         | Tran Thi B    |  5000000.00 | 2025-11-02 10:00:00 | 2025-11-02 10:00:00 |
|  3 | ACC003         | Le Van C      |  8000000.00 | 2025-11-02 10:00:00 | 2025-11-02 10:00:00 |
|  4 | ACC004         | Pham Thi D    | 15000000.00 | 2025-11-02 10:00:00 | 2025-11-02 10:00:00 |
|  5 | ACC005         | Hoang Van E   |  3000000.00 | 2025-11-02 10:00:00 | 2025-11-02 10:00:00 |
+----+----------------+---------------+-------------+---------------------+---------------------+
5 rows in set (0.001 sec)
```

### Hoặc import từ file SQL
```bash
# Thoát MariaDB
EXIT;

# Copy file SQL từ Windows sang Linux (dùng WinSCP, FileZilla, hoặc scp)
# Giả sử file đã được copy vào /tmp/

# Import file
sudo mariadb -u root -p bank_db < /tmp/database_setup.sql

# Kiểm tra
sudo mariadb -u root -p -e "SELECT * FROM bank_db.accounts;"
```

---

## 🌐 BƯỚC 4: CẤU HÌNH REMOTE ACCESS

### Cấp quyền remote cho root
```bash
# Đăng nhập MariaDB
sudo mariadb -u root -p
```

```sql
-- Cấp quyền cho root từ mọi IP
GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root123';
FLUSH PRIVILEGES;

-- Kiểm tra
SELECT user, host FROM mysql.user WHERE user='root';
```

Kết quả:
```
+------+-----------+
| user | host      |
+------+-----------+
| root | %         |
| root | localhost |
+------+-----------+
```

```sql
-- Thoát
EXIT;
```

### Sửa file cấu hình MariaDB
```bash
# Mở file cấu hình
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
```

### Tìm và sửa dòng bind-address
```ini
# Tìm dòng này (thường ở dòng 27-30):
bind-address = 127.0.0.1

# Sửa thành:
bind-address = 0.0.0.0
```

### Lưu file
```
Ctrl + O → Enter (Save)
Ctrl + X (Exit)
```

### Restart MariaDB
```bash
sudo systemctl restart mariadb

# Kiểm tra
sudo systemctl status mariadb
# Phải thấy: Active: active (running)
```

---

## 🔥 BƯỚC 5: CẤU HÌNH FIREWALL

### Kiểm tra firewall đang dùng
```bash
# UFW (Ubuntu/Debian)
sudo ufw status

# Nếu inactive:
sudo ufw enable
```

### Mở port 3306
```bash
# Cho phép port 3306
sudo ufw allow 3306/tcp

# Kiểm tra
sudo ufw status numbered
```

Kết quả:
```
Status: active

     To                         Action      From
     --                         ------      ----
[ 1] 22/tcp                     ALLOW IN    Anywhere
[ 2] 3306/tcp                   ALLOW IN    Anywhere
```

### Nếu dùng firewalld (CentOS/RHEL)
```bash
sudo firewall-cmd --permanent --add-port=3306/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports
```

---

## 🧪 BƯỚC 6: KIỂM TRA KẾT NỐI

### Lấy địa chỉ IP của máy Linux
```bash
ip addr show | grep inet
# Hoặc
hostname -I
```
Kết quả: `192.168.1.101` (ghi nhớ IP này)

### Test kết nối local
```bash
mariadb -u root -p -h localhost
# Nhập password: root123
```

```sql
USE bank_db;
SELECT * FROM accounts;
EXIT;
```

### Test kết nối từ máy Windows

**Trên máy Windows**, mở CMD:
```cmd
cd C:\xampp\mysql\bin
mysql.exe -u root -p -h 192.168.1.101
```

Nhập password: `root123`

Nếu kết nối thành công:
```sql
SHOW DATABASES;
USE bank_db;
SELECT * FROM accounts;
```

### Test bằng telnet (từ Windows)
```cmd
telnet 192.168.1.101 3306
```
Nếu kết nối được → Port đã mở ✓

---

## 📊 BƯỚC 7: CÀI ĐẶT CÔNG CỤ QUẢN LÝ (Tuỳ chọn)

### Option 1: phpMyAdmin (Giao diện Web)

```bash
# Cài Apache và PHP
sudo apt install apache2 php php-mysql -y

# Cài phpMyAdmin
sudo apt install phpmyadmin -y
```

Trong quá trình cài:
```
1. Web server: [*] apache2
2. Configure database: Yes
3. Password: root123
```

Truy cập: `http://192.168.1.101/phpmyadmin`

### Option 2: MySQL Workbench (Từ Windows)

1. Tải MySQL Workbench trên Windows
2. Tạo connection mới:
   - Hostname: 192.168.1.101
   - Port: 3306
   - Username: root
   - Password: root123
3. Test Connection → Connect

### Option 3: DBeaver (Cross-platform)

Tải từ: https://dbeaver.io/download/

---

## ⚙️ BƯỚC 8: TỐI ƯU HÓA CHO HIỆU NĂNG

### Sửa file cấu hình MariaDB
```bash
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
```

### Thêm/sửa các dòng sau:
```ini
[mysqld]
# Basic Settings
bind-address = 0.0.0.0
port = 3306
max_connections = 100

# InnoDB Settings (cho hiệu năng tốt)
innodb_buffer_pool_size = 256M
innodb_log_file_size = 64M
innodb_flush_log_at_trx_commit = 2

# Query Cache
query_cache_type = 1
query_cache_size = 32M
query_cache_limit = 2M

# Connection Settings
max_connect_errors = 1000000
wait_timeout = 600
interactive_timeout = 600

# Character Set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

### Lưu và restart
```bash
# Ctrl + O, Enter, Ctrl + X

sudo systemctl restart mariadb
```

---

## 🔍 BƯỚC 9: MONITORING VÀ MAINTENANCE

### Kiểm tra trạng thái MariaDB
```bash
# Status
sudo systemctl status mariadb

# Check port
sudo netstat -tulpn | grep 3306
# Hoặc
sudo ss -tulpn | grep 3306
```

### Xem log
```bash
# Error log
sudo tail -f /var/log/mysql/error.log

# Query log (nếu enable)
sudo tail -f /var/log/mysql/mysql.log
```

### Xem các kết nối hiện tại
```sql
mariadb -u root -p -e "SHOW PROCESSLIST;"
```

### Xem dung lượng database
```bash
mariadb -u root -p -e "
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'bank_db'
GROUP BY table_schema;
"
```

---

## 💾 BACKUP VÀ RESTORE

### Backup database
```bash
# Backup toàn bộ database
sudo mysqldump -u root -p bank_db > /tmp/bank_db_backup_$(date +%Y%m%d).sql

# Backup với nén
sudo mysqldump -u root -p bank_db | gzip > /tmp/bank_db_backup_$(date +%Y%m%d).sql.gz
```

### Restore database
```bash
# Từ file SQL
sudo mariadb -u root -p bank_db < /tmp/bank_db_backup_20251102.sql

# Từ file nén
gunzip < /tmp/bank_db_backup_20251102.sql.gz | sudo mariadb -u root -p bank_db
```

### Tự động backup (Cron job)
```bash
# Tạo script backup
sudo nano /usr/local/bin/backup_mariadb.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/var/backups/mariadb"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

mysqldump -u root -proot123 bank_db | gzip > $BACKUP_DIR/bank_db_$DATE.sql.gz

# Xóa backup cũ hơn 7 ngày
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
```

```bash
# Cấp quyền thực thi
sudo chmod +x /usr/local/bin/backup_mariadb.sh

# Thêm vào crontab (chạy hàng ngày lúc 2h sáng)
sudo crontab -e
```

Thêm dòng:
```
0 2 * * * /usr/local/bin/backup_mariadb.sh >> /var/log/mariadb_backup.log 2>&1
```

---

## 🔧 XỬ LÝ LỖI THƯỜNG GẶP

### ❌ Lỗi 1: Can't connect to MariaDB server

**Nguyên nhân**: Service chưa chạy
**Giải pháp**:
```bash
sudo systemctl start mariadb
sudo systemctl status mariadb
```

### ❌ Lỗi 2: Access denied for user 'root'@'IP'

**Nguyên nhân**: Chưa cấp quyền remote
**Giải pháp**: Làm lại BƯỚC 4

### ❌ Lỗi 3: Connection timeout

**Nguyên nhân**: Firewall chặn
**Giải pháp**: Làm lại BƯỚC 5

### ❌ Lỗi 4: Bind address 0.0.0.0 không hoạt động

**Kiểm tra**:
```bash
sudo netstat -tulpn | grep 3306
```

Phải thấy:
```
tcp    0   0 0.0.0.0:3306    0.0.0.0:*    LISTEN    1234/mariadbd
```

Nếu thấy `127.0.0.1:3306` → Chưa sửa đúng file config

### ❌ Lỗi 5: MariaDB dùng quá nhiều RAM

**Giải pháp**: Giảm `innodb_buffer_pool_size`
```bash
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
```

```ini
# Giảm xuống 128M cho máy có ít RAM
innodb_buffer_pool_size = 128M
```

```bash
sudo systemctl restart mariadb
```

---

## 📊 THÔNG TIN HỆ THỐNG

### Đường dẫn quan trọng
```
Config file:     /etc/mysql/mariadb.conf.d/50-server.cnf
Data directory:  /var/lib/mysql/
Error log:       /var/log/mysql/error.log
Socket:          /var/run/mysqld/mysqld.sock
PID file:        /var/run/mysqld/mysqld.pid
```

### Thông tin kết nối
```
Host:     192.168.1.101 (IP máy Linux)
Port:     3306
Database: bank_db
Username: root
Password: root123
```

### Lệnh quản lý service
```bash
# Start
sudo systemctl start mariadb

# Stop
sudo systemctl stop mariadb

# Restart
sudo systemctl restart mariadb

# Status
sudo systemctl status mariadb

# Enable auto-start
sudo systemctl enable mariadb

# Disable auto-start
sudo systemctl disable mariadb
```

---

## 🎯 CHECKLIST HOÀN THÀNH

- [ ] Đã cài MariaDB Server thành công
- [ ] MariaDB đang chạy (systemctl status)
- [ ] Đã chạy mysql_secure_installation
- [ ] Đã đặt password root: root123
- [ ] Đã tạo database 'bank_db'
- [ ] Đã tạo bảng 'accounts' và insert dữ liệu
- [ ] Có 5 tài khoản mẫu trong bảng
- [ ] Đã cấu hình bind-address = 0.0.0.0
- [ ] Đã cấp quyền remote cho root@'%'
- [ ] Đã mở port 3306 trong firewall (ufw/firewalld)
- [ ] Test kết nối local thành công
- [ ] Test kết nối từ Windows thành công
- [ ] Đã lấy địa chỉ IP máy Linux: _______________
- [ ] MariaDB auto-start khi boot (enable)

---

## 💡 TIPS VÀ BEST PRACTICES

### Bảo mật
```bash
# Chỉ cho phép kết nối từ IP cụ thể (thay vì %)
# Trong MariaDB:
GRANT ALL ON bank_db.* TO 'root'@'192.168.1.100' IDENTIFIED BY 'root123';
FLUSH PRIVILEGES;
```

### Performance Tuning
```bash
# Xem các biến cấu hình hiện tại
mariadb -u root -p -e "SHOW VARIABLES LIKE '%buffer%';"

# Xem status
mariadb -u root -p -e "SHOW STATUS LIKE '%Threads%';"
```

### Monitoring
```bash
# Cài htop để monitor
sudo apt install htop -y
htop

# Xem MariaDB process
ps aux | grep mariadb
```

---

## 🆚 SO SÁNH CHI TIẾT

### MariaDB vs MySQL vs XAMPP (Linux)

#### 1. Dung lượng cài đặt
```
MariaDB:      ~200-300 MB
MySQL:        ~400-500 MB
XAMPP Linux:  ~600-800 MB
```

#### 2. RAM Usage (idle)
```
MariaDB:      ~100-150 MB
MySQL:        ~200-300 MB
XAMPP:        ~300-500 MB
```

#### 3. Startup Time
```
MariaDB:      < 2 seconds
MySQL:        2-3 seconds
XAMPP:        5-8 seconds
```

#### 4. Hiệu năng (TPS - Transactions Per Second)
```
MariaDB:      ~2000 TPS
MySQL:        ~1800 TPS
XAMPP:        ~1500 TPS
```

---

## 🎓 TỔNG KẾT

### Bạn đã có:
✅ MariaDB Server chạy trên Linux  
✅ Database 'bank_db' với dữ liệu mẫu  
✅ Remote access đã được cấu hình  
✅ Firewall đã mở port 3306  
✅ Auto-start khi boot  
✅ Backup script (tuỳ chọn)  

### Ưu điểm MariaDB:
⭐ Nhẹ (~200MB vs ~600MB XAMPP)  
⭐ Nhanh (startup < 2s)  
⭐ Ổn định (ít lỗi)  
⭐ Tương thích 100% MySQL  
⭐ Dễ quản lý  

### Bước tiếp theo:
👉 Cấu hình `DatabaseConfig.java` với IP 2 máy  
👉 Test kết nối giữa Windows ↔ Linux  
👉 Chạy RMI Banking System  

---

**🎉 MariaDB trên Linux đã sẵn sàng!**

═══════════════════════════════════════════════════════════
MariaDB Setup for RMI Banking System | Linux Guide
═══════════════════════════════════════════════════════════
