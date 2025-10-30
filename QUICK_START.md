# HƯỚNG DẪN NHANH - RMI Banking System

## 📋 Tổng quan
Ứng dụng chuyển khoản ngân hàng sử dụng Java RMI, cập nhật đồng bộ lên 2 database MySQL.

## 🚀 Khởi động nhanh (Quick Start)

### Bước 1: Chuẩn bị
- ✅ Cài Java JDK 8+
- ✅ Cài MySQL trên 2 máy
- ✅ Tải MySQL Connector/J
- ✅ Mở Eclipse IDE

### Bước 2: Setup Database (trên CẢ 2 máy MySQL)
```sql
-- Đăng nhập MySQL
mysql -u root -p

-- Chạy script
source database_setup.sql

-- Hoặc copy-paste lệnh từ file database_setup.sql
```

### Bước 3: Cấu hình Project

1. **Import project vào Eclipse**
   - File → Import → Existing Projects → Chọn thư mục RMI_App

2. **Thêm MySQL JDBC Driver**
   - Right-click project → Properties → Java Build Path → Libraries
   - Add External JARs → Chọn mysql-connector-java-x.x.xx.jar

3. **Sửa file DatabaseConfig.java**
   ```java
   DB1_HOST = "192.168.1.100"  // IP máy MySQL 1
   DB2_HOST = "192.168.1.101"  // IP máy MySQL 2
   DB1_PASSWORD = "your_password"
   DB2_PASSWORD = "your_password"
   ```

### Bước 4: Mở Firewall (Windows - chạy PowerShell as Admin)
```powershell
# Chạy script tự động
.\setup_firewall.ps1

# Hoặc thủ công:
New-NetFirewallRule -DisplayName "MySQL" -Direction Inbound -LocalPort 3306 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "RMI" -Direction Inbound -LocalPort 1099 -Protocol TCP -Action Allow
```

### Bước 5: Kiểm tra kết nối
```powershell
.\check_connection.ps1
```

### Bước 6: Chạy ứng dụng

**Cách 1: Trong Eclipse**
- Chạy Server: Right-click `BankServer.java` → Run As → Java Application
- Chạy Client: Right-click `BankClientUI.java` → Run As → Java Application

**Cách 2: Dùng file .bat**
- Double-click `run_server.bat`
- Double-click `run_client.bat`

## 🎯 Sử dụng

1. **Kết nối Server**
   - Nhập IP Server (hoặc localhost)
   - Click "Kết nối"

2. **Chuyển khoản**
   - Chọn tài khoản nguồn
   - Chọn tài khoản đích
   - Nhập số tiền
   - Click "Chuyển khoản"

3. **Kiểm tra đồng bộ**
   ```sql
   -- Trên máy 1 và máy 2
   SELECT * FROM bank_db.accounts;
   ```
   → Dữ liệu phải giống nhau!

## 📁 Cấu trúc File

```
RMI_App/
├── src/
│   ├── rmi/                    # RMI Interface & Models
│   ├── database/               # Database handlers
│   ├── server/                 # RMI Server
│   └── client/                 # UI Client
├── database_setup.sql          # Script tạo DB
├── setup_firewall.ps1          # Script mở firewall
├── check_connection.ps1        # Script test kết nối
├── run_server.bat              # Chạy server
├── run_client.bat              # Chạy client
├── README.md                   # Tài liệu đầy đủ
├── HUONG_DAN_CAI_DAT.md       # Hướng dẫn chi tiết
└── QUICK_START.md             # File này
```

## 🔧 Xử lý lỗi nhanh

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-------------|-----------|
| Connection refused MySQL | Firewall chặn | Chạy setup_firewall.ps1 |
| Access denied | Sai password | Sửa DatabaseConfig.java |
| RMI connection failed | Server chưa chạy | Khởi động BankServer |
| ClassNotFoundException | Thiếu JDBC Driver | Add mysql-connector-j.jar |

## 📊 Demo Data

Tài khoản mẫu:
- ACC001 - Nguyen Van A - 10,000,000 VND
- ACC002 - Tran Thi B - 5,000,000 VND
- ACC003 - Le Van C - 8,000,000 VND
- ACC004 - Pham Thi D - 15,000,000 VND
- ACC005 - Hoang Van E - 3,000,000 VND

## 🌟 Tính năng chính

✅ Chuyển khoản real-time
✅ Đồng bộ 2 database (transaction-based)
✅ Rollback tự động nếu lỗi
✅ Giao diện UI thân thiện
✅ Tạo tài khoản mới
✅ Xem danh sách tài khoản

## 📞 Hỗ trợ

Nếu gặp vấn đề:
1. Đọc file HUONG_DAN_CAI_DAT.md
2. Chạy check_connection.ps1 để test
3. Kiểm tra Console log

---
**Phiên bản:** 1.0
**Ngày cập nhật:** 2025
