# 🔗 KẾT NỐI 2 MÁY: WINDOWS (XAMPP) + LINUX (MariaDB)

## 📋 Tổng quan kiến trúc

```
┌─────────────────────────────────┐
│   MÁY 1: WINDOWS + XAMPP        │
│   IP: 192.168.1.100             │
│   Database: MySQL (XAMPP)       │
│   Port: 3306                    │
│   ┌──────────────────────────┐  │
│   │  bank_db                 │  │
│   │  ├── accounts (5 rows)   │  │
│   │  └── ...                 │  │
│   └──────────────────────────┘  │
└─────────────────────────────────┘
            ▲
            │ Network
            │ (LAN/WiFi)
            ▼
┌─────────────────────────────────┐
│   MÁY 2: LINUX + MariaDB        │
│   IP: 192.168.1.101             │
│   Database: MariaDB             │
│   Port: 3306                    │
│   ┌──────────────────────────┐  │
│   │  bank_db                 │  │
│   │  ├── accounts (5 rows)   │  │
│   │  └── ...                 │  │
│   └──────────────────────────┘  │
└─────────────────────────────────┘
```

---

## ✅ YÊU CẦU TRƯỚC KHI BẮT ĐẦU

### Máy 1 (Windows)
- [x] Đã cài XAMPP
- [x] MySQL đang chạy trong XAMPP Control Panel
- [x] Đã tạo database 'bank_db'
- [x] Đã import dữ liệu (5 accounts)
- [x] Đã cấu hình bind-address = 0.0.0.0
- [x] Đã cấp quyền remote (root@'%')
- [x] Đã mở port 3306 trong Firewall
- [x] Biết IP máy Windows: _______________

### Máy 2 (Linux)
- [x] Đã cài MariaDB
- [x] MariaDB đang chạy (systemctl status)
- [x] Đã tạo database 'bank_db'
- [x] Đã import dữ liệu (5 accounts)
- [x] Đã cấu hình bind-address = 0.0.0.0
- [x] Đã cấp quyền remote (root@'%')
- [x] Đã mở port 3306 trong UFW/Firewalld
- [x] Biết IP máy Linux: _______________

**Nếu chưa hoàn thành, đọc:**
- Windows: `SETUP_WINDOWS_XAMPP.md`
- Linux: `SETUP_LINUX_MARIADB.md`

---

## 🔍 BƯỚC 1: XÁC ĐỊNH ĐỊA CHỈ IP

### Trên Windows (XAMPP)
```powershell
# Mở PowerShell
ipconfig | findstr IPv4
```

Kết quả (ví dụ):
```
IPv4 Address. . . . . . . . . . . : 192.168.1.100
```

### Trên Linux (MariaDB)
```bash
# Lấy IP
hostname -I
# Hoặc
ip addr show | grep "inet " | grep -v 127.0.0.1
```

Kết quả (ví dụ):
```
192.168.1.101
```

### Ghi nhớ
```
Máy Windows (XAMPP):  192.168.1.100
Máy Linux (MariaDB):  192.168.1.101
```

---

## 🧪 BƯỚC 2: TEST KẾT NỐI MẠNG

### Test từ Windows → Linux

**Ping test:**
```powershell
ping 192.168.1.101
```

Kết quả tốt:
```
Reply from 192.168.1.101: bytes=32 time=1ms TTL=64
Reply from 192.168.1.101: bytes=32 time=1ms TTL=64
```

**Telnet test (port 3306):**
```powershell
telnet 192.168.1.101 3306
```

Nếu kết nối thành công → Màn hình đen hoặc ký tự lạ (OK)
Nếu lỗi → Firewall chặn hoặc MariaDB chưa mở port

### Test từ Linux → Windows

**Ping test:**
```bash
ping 192.168.1.100 -c 4
```

**Telnet test:**
```bash
telnet 192.168.1.100 3306
```

Hoặc dùng nc (netcat):
```bash
nc -zv 192.168.1.100 3306
```

Kết quả tốt:
```
Connection to 192.168.1.100 3306 port [tcp/mysql] succeeded!
```

---

## 🔐 BƯỚC 3: TEST KẾT NỐI DATABASE

### Từ Windows → Linux MariaDB

**Mở Command Prompt:**
```cmd
cd C:\xampp\mysql\bin
mysql.exe -u root -p -h 192.168.1.101
```

Nhập password: `root123`

**Test queries:**
```sql
SHOW DATABASES;
USE bank_db;
SELECT * FROM accounts;
EXIT;
```

### Từ Linux → Windows MySQL

```bash
mariadb -u root -p -h 192.168.1.100
```

Nhập password: `root123`

**Test queries:**
```sql
SHOW DATABASES;
USE bank_db;
SELECT * FROM accounts;
EXIT;
```

### Kết quả mong đợi
✅ Kết nối thành công  
✅ Thấy database 'bank_db'  
✅ Thấy 5 tài khoản mẫu  

---

## ⚙️ BƯỚC 4: CẤU HÌNH PROJECT JAVA

### Sửa file DatabaseConfig.java

Mở file: `src/database/DatabaseConfig.java`

```java
package database;

/**
 * Cấu hình kết nối database cho 2 máy
 */
public class DatabaseConfig {
    
    // ========================================
    // Database 1 - Máy Windows (XAMPP MySQL)
    // ========================================
    public static final String DB1_HOST = "192.168.1.100";  // ← IP máy Windows
    public static final String DB1_PORT = "3306";
    public static final String DB1_NAME = "bank_db";
    public static final String DB1_USER = "root";
    public static final String DB1_PASSWORD = "root123";     // ← Password bạn đặt
    
    // ========================================
    // Database 2 - Máy Linux (MariaDB)
    // ========================================
    public static final String DB2_HOST = "192.168.1.101";  // ← IP máy Linux
    public static final String DB2_PORT = "3306";
    public static final String DB2_NAME = "bank_db";
    public static final String DB2_USER = "root";
    public static final String DB2_PASSWORD = "root123";     // ← Password bạn đặt
    
    // URL builders
    public static String getDB1Url() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", 
                DB1_HOST, DB1_PORT, DB1_NAME);
    }
    
    public static String getDB2Url() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", 
                DB2_HOST, DB2_PORT, DB2_NAME);
    }
}
```

**Lưu ý quan trọng:**
- Đổi IP cho đúng với IP thực tế của 2 máy
- Password phải giống với password đã đặt
- Port mặc định là 3306 (nếu không đổi)

---

## 🚀 BƯỚC 5: CHẠY VÀ TEST ỨNG DỤNG

### 1. Khởi động RMI Server

**Chọn máy chạy Server** (có thể Windows hoặc Linux, ví dụ: Windows)

Trong Eclipse:
```
1. Right-click BankServer.java
2. Run As → Java Application
3. Kiểm tra Console
```

Kết quả:
```
=================================
RMI Bank Server đã khởi động!
Server đang lắng nghe trên port 1099
Service name: BankService
=================================
```

### 2. Khởi động Client UI

Trong Eclipse:
```
1. Right-click BankClientUI.java
2. Run As → Java Application
3. Giao diện UI hiển thị
```

### 3. Kết nối và Test

**Trong UI:**
```
1. Server Host: localhost (nếu cùng máy) hoặc IP máy chạy Server
2. Click "Kết nối"
3. Status: "Đã kết nối" (màu xanh)
```

### 4. Test chuyển khoản

```
Tab "Chuyển khoản":
- Từ: ACC001 - Nguyen Van A
- Đến: ACC002 - Tran Thi B
- Số tiền: 1000000
- Click "Chuyển khoản"
- Confirm: Yes
```

### 5. Kiểm tra Console

```
Server Console:
Nhận yêu cầu chuyển khoản: ACC001 -> ACC002, số tiền: 1000000.00
Chuyển khoản thành công trên cả 2 database!
```

---

## ✅ BƯỚC 6: XÁC MINH ĐỒNG BỘ

### Kiểm tra trên Windows (XAMPP)

**Mở phpMyAdmin:**
```
http://localhost/phpmyadmin
→ Click "bank_db"
→ Click table "accounts"
→ Tab "Browse"
```

Hoặc dùng MySQL command:
```cmd
cd C:\xampp\mysql\bin
mysql.exe -u root -p
```

```sql
USE bank_db;
SELECT account_number, account_name, balance 
FROM accounts 
WHERE account_number IN ('ACC001', 'ACC002');
```

Kết quả (sau khi chuyển 1 triệu):
```
+----------------+---------------+-------------+
| account_number | account_name  | balance     |
+----------------+---------------+-------------+
| ACC001         | Nguyen Van A  | 9000000.00  |  ← Giảm 1M
| ACC002         | Tran Thi B    | 6000000.00  |  ← Tăng 1M
+----------------+---------------+-------------+
```

### Kiểm tra trên Linux (MariaDB)

```bash
mariadb -u root -p
```

```sql
USE bank_db;
SELECT account_number, account_name, balance 
FROM accounts 
WHERE account_number IN ('ACC001', 'ACC002');
```

Kết quả (phải GIỐNG HỆT Windows):
```
+----------------+---------------+-------------+
| account_number | account_name  | balance     |
+----------------+---------------+-------------+
| ACC001         | Nguyen Van A  | 9000000.00  |  ← Giảm 1M
| ACC002         | Tran Thi B    | 6000000.00  |  ← Tăng 1M
+----------------+---------------+-------------+
```

### ✅ XÁC NHẬN
**Nếu 2 database có dữ liệu GIỐNG NHAU → ĐỒNG BỘ THÀNH CÔNG!** 🎉

---

## 🔄 BƯỚC 7: TEST CÁC TÌNH HUỐNG

### Test 1: Số dư không đủ

```
Chuyển 100,000,000 từ ACC005 (chỉ có 3 triệu)
Kết quả: "Số dư không đủ!"
Kiểm tra DB: Dữ liệu KHÔNG thay đổi ✓
```

### Test 2: Tạo tài khoản mới

```
Tab "Tạo tài khoản":
- Số TK: ACC999
- Tên: Test User
- Số dư: 5000000
- Click "Tạo tài khoản"

Kiểm tra cả 2 DB:
SELECT * FROM accounts WHERE account_number = 'ACC999';
→ Phải có trên CẢ 2 database ✓
```

### Test 3: Ngắt kết nối 1 database

**Trên Linux, tạm dừng MariaDB:**
```bash
sudo systemctl stop mariadb
```

**Thử chuyển khoản:**
```
Kết quả: Lỗi kết nối
→ Hệ thống sẽ rollback (không cập nhật database nào)
```

**Khởi động lại MariaDB:**
```bash
sudo systemctl start mariadb
```

### Test 4: Đồng thời nhiều client

```
1. Chạy 2 Client UI cùng lúc (trên 2 máy khác nhau)
2. Cả 2 đều kết nối Server
3. Thực hiện chuyển khoản song song
4. Kiểm tra: Dữ liệu vẫn nhất quán ✓
```

---

## 📊 MONITORING VÀ DEBUGGING

### Xem log trên Windows (XAMPP)

```
File: C:\xampp\mysql\data\mysql_error.log
```

### Xem log trên Linux (MariaDB)

```bash
sudo tail -f /var/log/mysql/error.log
```

### Xem kết nối hiện tại

**Windows:**
```sql
SHOW PROCESSLIST;
```

**Linux:**
```bash
mariadb -u root -p -e "SHOW PROCESSLIST;"
```

### Monitor network traffic

**Windows:**
```powershell
netstat -an | findstr 3306
```

**Linux:**
```bash
sudo netstat -tulpn | grep 3306
# Hoặc
sudo ss -tulpn | grep 3306
```

---

## 🔧 XỬ LÝ LỖI

### ❌ Lỗi 1: Communications link failure

**Nguyên nhân**: Không kết nối được database
**Kiểm tra:**
```
1. Database có đang chạy không?
2. Firewall có mở port 3306 không?
3. IP trong DatabaseConfig.java có đúng không?
4. Password có đúng không?
```

**Debug:**
```java
// Thêm vào DatabaseHandler.java để debug
System.out.println("DB1 URL: " + DatabaseConfig.getDB1Url());
System.out.println("DB2 URL: " + DatabaseConfig.getDB2Url());
```

### ❌ Lỗi 2: Data không đồng bộ

**Nguyên nhân**: Transaction bị lỗi giữa chừng
**Kiểm tra:**
```sql
-- Trên cả 2 database:
SELECT * FROM accounts ORDER BY account_number;
```

**Fix:** 
```
1. Backup database tốt
2. Restore trên database bị lỗi
3. Kiểm tra lại code transaction
```

### ❌ Lỗi 3: Server RMI không start

**Nguyên nhân**: Port 1099 bị chiếm
**Kiểm tra:**
```powershell
netstat -ano | findstr 1099
```

**Fix:**
```
1. Tắt ứng dụng đang chiếm port
2. Hoặc đổi port RMI trong code
```

### ❌ Lỗi 4: Client không kết nối được Server

**Kiểm tra:**
```
1. Server đã chạy chưa?
2. IP Server có đúng không?
3. Firewall có mở port 1099 không?
```

**Test:**
```powershell
telnet <IP_SERVER> 1099
```

---

## 📈 TỐI ƯU HÓA

### Connection Pooling (Nâng cao)

Thay vì tạo connection mỗi lần, dùng connection pool:

**Thêm HikariCP vào project:**
```xml
<!-- pom.xml nếu dùng Maven -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

**Sửa DatabaseHandler.java:**
```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

private static HikariDataSource dataSource1;
private static HikariDataSource dataSource2;

static {
    // Pool cho DB1
    HikariConfig config1 = new HikariConfig();
    config1.setJdbcUrl(DatabaseConfig.getDB1Url());
    config1.setUsername(DatabaseConfig.DB1_USER);
    config1.setPassword(DatabaseConfig.DB1_PASSWORD);
    dataSource1 = new HikariDataSource(config1);
    
    // Pool cho DB2
    HikariConfig config2 = new HikariConfig();
    config2.setJdbcUrl(DatabaseConfig.getDB2Url());
    config2.setUsername(DatabaseConfig.DB2_USER);
    config2.setPassword(DatabaseConfig.DB2_PASSWORD);
    dataSource2 = new HikariDataSource(config2);
}

private Connection getConnection(int dbNum) throws SQLException {
    return (dbNum == 1) ? dataSource1.getConnection() 
                        : dataSource2.getConnection();
}
```

---

## 🎯 CHECKLIST TỔNG HỢP

### Chuẩn bị
- [ ] Windows: XAMPP MySQL đang chạy
- [ ] Linux: MariaDB đang chạy
- [ ] Cả 2 database đã có 'bank_db' với dữ liệu
- [ ] Firewall đã mở port 3306 trên cả 2 máy
- [ ] Đã biết IP của cả 2 máy

### Kiểm tra kết nối
- [ ] Ping từ Windows → Linux thành công
- [ ] Ping từ Linux → Windows thành công
- [ ] Telnet port 3306 từ Windows → Linux OK
- [ ] Telnet port 3306 từ Linux → Windows OK
- [ ] MySQL client từ Windows → Linux OK
- [ ] MySQL client từ Linux → Windows OK

### Cấu hình project
- [ ] Đã sửa DatabaseConfig.java với IP đúng
- [ ] Password đúng
- [ ] Đã thêm MySQL JDBC Driver vào project
- [ ] Không có compile error

### Test chức năng
- [ ] RMI Server start thành công
- [ ] Client UI kết nối được Server
- [ ] Chuyển khoản thành công
- [ ] Dữ liệu đồng bộ trên 2 database
- [ ] Tạo tài khoản mới thành công
- [ ] Test số dư không đủ → Rollback OK

---

## 💡 BEST PRACTICES

### 1. Backup thường xuyên
```bash
# Windows (XAMPP)
cd C:\xampp\mysql\bin
mysqldump.exe -u root -p bank_db > backup.sql

# Linux (MariaDB)
mysqldump -u root -p bank_db > backup.sql
```

### 2. Monitor log
```bash
# Theo dõi log real-time
# Linux:
sudo tail -f /var/log/mysql/error.log

# Windows:
Get-Content C:\xampp\mysql\data\mysql_error.log -Wait
```

### 3. Kiểm tra định kỳ
```sql
-- Chạy trên cả 2 database để đảm bảo đồng bộ
SELECT COUNT(*) FROM accounts;
SELECT SUM(balance) FROM accounts;
```

### 4. Security
```sql
-- Đổi password mạnh hơn
ALTER USER 'root'@'%' IDENTIFIED BY 'P@ssw0rd!2024';

-- Tạo user riêng cho app (không dùng root)
CREATE USER 'bankapp'@'%' IDENTIFIED BY 'BankApp@123';
GRANT ALL ON bank_db.* TO 'bankapp'@'%';
FLUSH PRIVILEGES;
```

---

## 🎓 TỔNG KẾT

### Bạn đã có:
✅ 2 database server độc lập (Windows + Linux)  
✅ Kết nối network giữa 2 máy  
✅ Đồng bộ dữ liệu real-time  
✅ Transaction rollback tự động  
✅ RMI Banking System hoạt động hoàn chỉnh  

### Kiến trúc hoàn chỉnh:
```
[Client UI] → [RMI Server] → [Database Handler]
                                    ├─→ [Windows MySQL]
                                    └─→ [Linux MariaDB]
```

### Ưu điểm:
⭐ High Availability (1 DB down, vẫn có thể chuyển sang manual)  
⭐ Data Consistency (Two-Phase Commit)  
⭐ Cross-platform (Windows ↔ Linux)  
⭐ Real-time Sync  

---

**🎉 HỆ THỐNG ĐÃ SẴN SÀNG VẬN HÀNH!**

═══════════════════════════════════════════════════════════
2-Database Sync Setup | Windows XAMPP + Linux MariaDB
═══════════════════════════════════════════════════════════
