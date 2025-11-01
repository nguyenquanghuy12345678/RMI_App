# 🎯 START HERE - BẮT ĐẦU TỪ ĐÂY!

Chào mừng bạn đến với **RMI Banking System**!

---

## 📌 BẠN CÓ 2 MÁY:

```
┌─────────────────┐         ┌─────────────────┐
│  MÁY 1          │         │  MÁY 2          │
│  WINDOWS        │ ◄─────► │  LINUX          │
│  + XAMPP        │         │  + MariaDB      │
└─────────────────┘         └─────────────────┘
```

---

## ⚡ HƯỚNG DẪN NHANH 3 BƯỚC

### 🔹 BƯỚC 1: Chọn Database cho Linux (MÁY 2)

**Đọc ngay:** `COMPARISON_DATABASE.md`

**Khuyến nghị:** ⭐ **MariaDB** (nhẹ, nhanh, ổn định)

**Lý do:**
- ✅ Nhẹ: ~200MB (vs ~600MB XAMPP)
- ✅ Nhanh: < 2 giây khởi động
- ✅ Ổn định: Ít lỗi hơn
- ✅ Tương thích 100% MySQL

---

### 🔹 BƯỚC 2: Cài đặt Database

#### Trên MÁY 1 (Windows):

**📖 Đọc:** `SETUP_WINDOWS_XAMPP.md`

**Tóm tắt:**
```
1. Tải XAMPP: https://www.apachefriends.org/
2. Cài đặt (chọn MySQL)
3. Start MySQL trong XAMPP Control Panel
4. Chạy script: database_setup.sql
5. Cấu hình remote access
6. Mở firewall port 3306
```

#### Trên MÁY 2 (Linux):

**📖 Đọc:** `SETUP_LINUX_MARIADB.md`

**Tóm tắt:**
```bash
# Cài MariaDB (1 lệnh!)
sudo apt install mariadb-server -y

# Bảo mật
sudo mysql_secure_installation

# Tạo database
sudo mariadb -u root -p < database_setup.sql

# Cấu hình remote
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
# Sửa: bind-address = 0.0.0.0

# Mở firewall
sudo ufw allow 3306/tcp
```

---

### 🔹 BƯỚC 3: Kết nối 2 máy

**📖 Đọc:** `SETUP_2_MACHINES.md`

**Tóm tắt:**
```
1. Lấy IP 2 máy (ipconfig / hostname -I)
2. Test ping giữa 2 máy
3. Test kết nối database từ xa
4. Sửa file DatabaseConfig.java:
   - DB1_HOST = IP máy Windows
   - DB2_HOST = IP máy Linux
5. Chạy RMI Server
6. Chạy Client UI
7. Test chuyển khoản
8. Verify đồng bộ trên 2 database
```

---

## 📚 TÀI LIỆU CHI TIẾT

| File | Nội dung | Khi nào đọc? |
|------|----------|--------------|
| **COMPARISON_DATABASE.md** | So sánh MySQL/MariaDB/XAMPP | ⭐ Đọc đầu tiên |
| **SETUP_WINDOWS_XAMPP.md** | Setup XAMPP trên Windows | Khi setup máy 1 |
| **SETUP_LINUX_MARIADB.md** | Setup MariaDB trên Linux | Khi setup máy 2 |
| **SETUP_2_MACHINES.md** | Kết nối & test 2 máy | Sau khi setup xong |
| **QUICK_START.md** | Hướng dẫn tổng quan | Xem tổng quan |
| **HUONG_DAN_CAI_DAT.md** | Chi tiết đầy đủ | Khi gặp vấn đề |
| **CHECKLIST.md** | Theo dõi tiến độ | Track progress |

---

## ✅ CHECKLIST TỔNG HỢP

### Máy 1 (Windows)
- [ ] Đã cài XAMPP
- [ ] MySQL chạy trong XAMPP Control Panel
- [ ] Đã tạo database 'bank_db'
- [ ] Có 5 tài khoản mẫu
- [ ] Đã cấu hình remote access
- [ ] Đã mở firewall port 3306
- [ ] IP máy 1: _______________

### Máy 2 (Linux)
- [ ] Đã cài MariaDB
- [ ] MariaDB đang chạy
- [ ] Đã tạo database 'bank_db'
- [ ] Có 5 tài khoản mẫu
- [ ] Đã cấu hình remote access
- [ ] Đã mở firewall port 3306
- [ ] IP máy 2: _______________

### Kết nối
- [ ] Ping từ máy 1 → máy 2 thành công
- [ ] Ping từ máy 2 → máy 1 thành công
- [ ] Test MySQL từ Windows → Linux OK
- [ ] Test MySQL từ Linux → Windows OK

### Project
- [ ] Đã sửa DatabaseConfig.java với IP đúng
- [ ] Đã thêm MySQL JDBC Driver
- [ ] RMI Server chạy được
- [ ] Client UI kết nối được
- [ ] Chuyển khoản thành công
- [ ] Dữ liệu đồng bộ trên 2 database ✓

---

## 🎯 LỘ TRÌNH HỌC TẬP

```
Ngày 1: Setup Database
├─ Sáng:  Setup máy Windows (XAMPP)
└─ Chiều: Setup máy Linux (MariaDB)

Ngày 2: Kết nối & Test
├─ Sáng:  Test kết nối giữa 2 máy
├─ Chiều: Cấu hình project Java
└─ Tối:   Chạy và test ứng dụng

Ngày 3: Tối ưu & Deploy
├─ Sáng:  Tối ưu hiệu năng
├─ Chiều: Backup & Security
└─ Tối:   Hoàn thiện tài liệu
```

---

## 💡 TIPS QUAN TRỌNG

### ⭐ Lựa chọn database:
```
Windows → XAMPP MySQL (đơn giản, có GUI)
Linux   → MariaDB (nhẹ, nhanh, ổn định)
```

### ⭐ File quan trọng phải sửa:
```java
// src/database/DatabaseConfig.java
DB1_HOST = "192.168.1.100"  // IP máy Windows
DB2_HOST = "192.168.1.101"  // IP máy Linux
DB1_PASSWORD = "root123"
DB2_PASSWORD = "root123"
```

### ⭐ Port cần mở:
```
Port 3306: MySQL/MariaDB
Port 1099: RMI Server
```

### ⭐ Test scripts:
```powershell
# Test firewall & kết nối
.\setup_firewall.ps1
.\check_connection.ps1
```

---

## 🔧 XỬ LÝ LỖI NHANH

| Lỗi | Giải pháp |
|-----|-----------|
| ❌ MySQL không start | Kiểm tra port 3306 có bị chiếm không |
| ❌ Không kết nối được database | Kiểm tra firewall, IP, password |
| ❌ RMI Server lỗi | Đọc Console log, check port 1099 |
| ❌ Data không đồng bộ | Kiểm tra Console log, test từng DB |

---

## 📞 HỖ TRỢ

### Đọc tài liệu theo thứ tự:
1. `COMPARISON_DATABASE.md` - Chọn database
2. `SETUP_WINDOWS_XAMPP.md` - Setup máy 1
3. `SETUP_LINUX_MARIADB.md` - Setup máy 2
4. `SETUP_2_MACHINES.md` - Kết nối 2 máy
5. `CHECKLIST.md` - Track progress

### Scripts hỗ trợ:
- `setup_firewall.ps1` - Mở firewall tự động
- `check_connection.ps1` - Test kết nối
- `database_setup.sql` - Tạo database

---

## 🎓 KẾT QUẢ MONG ĐỢI

Sau khi hoàn thành, bạn sẽ có:

✅ Hệ thống RMI Banking hoạt động hoàn chỉnh  
✅ 2 database đồng bộ real-time (Windows + Linux)  
✅ Transaction-based với auto rollback  
✅ Giao diện UI thân thiện  
✅ Kiến thức về:
   - Java RMI
   - Database sync
   - Cross-platform deployment
   - Network programming

---

## 🚀 BẮT ĐẦU NGAY!

### Bước đầu tiên:

```
1. Đọc COMPARISON_DATABASE.md
   → Hiểu tại sao chọn MariaDB cho Linux

2. Theo hướng dẫn setup:
   → Windows: SETUP_WINDOWS_XAMPP.md
   → Linux: SETUP_LINUX_MARIADB.md

3. Kết nối 2 máy:
   → SETUP_2_MACHINES.md
```

---

## 📊 KIẾN TRÚC TỔNG QUAN

```
┌──────────────────────────────────────────────────────┐
│              CLIENT LAYER (Swing UI)                 │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐    │
│  │ Client 1   │  │ Client 2   │  │ Client N   │    │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘    │
└────────┼────────────────┼────────────────┼───────────┘
         │                │                │
         └────────────────┼────────────────┘
                          │ RMI (port 1099)
         ┌────────────────┴────────────────┐
         │                                  │
┌────────▼────────────────────────────────────────────┐
│           SERVER LAYER (RMI Server)                 │
│  ┌────────────────────────────────────────────┐    │
│  │  BankServer (Transaction Handler)          │    │
│  └───────────────────┬────────────────────────┘    │
└──────────────────────┼─────────────────────────────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
         │ JDBC                      │ JDBC
         │                           │
┌────────▼────────┐         ┌────────▼────────┐
│  DATABASE 1     │         │  DATABASE 2     │
│  Windows        │  SYNC   │  Linux          │
│  XAMPP MySQL    │◄───────►│  MariaDB        │
│  192.168.1.100  │         │  192.168.1.101  │
└─────────────────┘         └─────────────────┘
```

---

**🎉 Chúc bạn thành công! Bắt đầu với COMPARISON_DATABASE.md!**

═══════════════════════════════════════════════════════════
START HERE Guide | RMI Banking System
═══════════════════════════════════════════════════════════
