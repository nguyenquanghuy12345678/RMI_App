# ✅ CHECKLIST CÀI ĐẶT RMI BANKING SYSTEM

## 📋 TRƯỚC KHI BẮT ĐẦU

- [ ] Đã đọc file INDEX.txt
- [ ] Đã đọc QUICK_START.md
- [ ] Có ít nhất 2 máy trong cùng mạng LAN (hoặc 1 máy để test)
- [ ] Biết địa chỉ IP của các máy

---

## 🗄️ BƯỚC 1: CÀI ĐẶT MYSQL (Trên 2 máy)

### Máy 1 (Server chính):
- [ ] Đã cài MySQL Server
- [ ] MySQL đang chạy (kiểm tra: `mysql -u root -p`)
- [ ] Ghi nhớ IP máy này: ________________

### Máy 2 (Server dự phòng):
- [ ] Đã cài MySQL Server
- [ ] MySQL đang chạy
- [ ] Ghi nhớ IP máy này: ________________

---

## 🛠️ BƯỚC 2: CẤU HÌNH MYSQL

### Trên MỖI máy MySQL, làm theo:

- [ ] Đã sửa file cấu hình cho phép remote (bind-address = 0.0.0.0)
- [ ] Đã restart MySQL service
- [ ] Đã chạy script database_setup.sql
  ```sql
  mysql -u root -p < database_setup.sql
  ```
- [ ] Đã tạo database 'bank_db'
- [ ] Đã tạo bảng 'accounts'
- [ ] Đã insert 5 tài khoản mẫu
- [ ] Kiểm tra dữ liệu:
  ```sql
  SELECT * FROM bank_db.accounts;
  ```
  Kết quả: Có 5 dòng ✓

---

## 🔓 BƯỚC 3: CẤP QUYỀN MYSQL

### Trên MỖI máy MySQL:

- [ ] Đã cấp quyền remote:
  ```sql
  GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root';
  FLUSH PRIVILEGES;
  ```

---

## 🔥 BƯỚC 4: CẤU HÌNH FIREWALL

### Trên MỖI máy (Server MySQL + Server RMI):

- [ ] Đã mở port 3306 (MySQL)
- [ ] Đã mở port 1099 (RMI)
- [ ] Nếu Windows: Đã chạy `setup_firewall.ps1` (as Admin)
- [ ] Nếu Linux: Đã chạy lệnh ufw/firewalld

---

## 🧪 BƯỚC 5: TEST KẾT NỐI MYSQL

### Từ máy sẽ chạy RMI Server:

- [ ] Test kết nối đến MySQL 1:
  ```bash
  mysql -h <IP_May_1> -u root -p
  ```
  Kết quả: Đăng nhập thành công ✓

- [ ] Test kết nối đến MySQL 2:
  ```bash
  mysql -h <IP_May_2> -u root -p
  ```
  Kết quả: Đăng nhập thành công ✓

---

## 💻 BƯỚC 6: SETUP PROJECT

### Cài đặt Java & IDE:

- [ ] Đã cài Java JDK 8+
- [ ] Kiểm tra: `java -version`
- [ ] Đã cài Eclipse IDE (hoặc IntelliJ)

### Download MySQL JDBC Driver:

- [ ] Đã tải MySQL Connector/J từ: https://dev.mysql.com/downloads/connector/j/
- [ ] File: mysql-connector-java-x.x.xx.jar
- [ ] Ghi nhớ vị trí file: ________________

### Import Project:

- [ ] Đã mở Eclipse
- [ ] Đã import project RMI_App vào workspace
- [ ] Project hiển thị không lỗi trong Package Explorer

### Thêm JDBC Driver:

- [ ] Right-click project → Properties → Java Build Path
- [ ] Tab Libraries → Add External JARs
- [ ] Đã chọn file mysql-connector-java-x.x.xx.jar
- [ ] Apply and Close
- [ ] Không còn lỗi compile ✓

---

## ⚙️ BƯỚC 7: CẤU HÌNH DATABASE CONNECTION

### Sửa file DatabaseConfig.java:

- [ ] Đã mở file: `src/database/DatabaseConfig.java`
- [ ] Đã sửa DB1_HOST = "_______________" (IP máy MySQL 1)
- [ ] Đã sửa DB2_HOST = "_______________" (IP máy MySQL 2)
- [ ] Đã sửa DB1_PASSWORD = "_______________"
- [ ] Đã sửa DB2_PASSWORD = "_______________"
- [ ] Đã Save file (Ctrl+S)

---

## 🔍 BƯỚC 8: TEST KẾT NỐI (Tuỳ chọn nhưng khuyến nghị)

- [ ] Đã chạy script `check_connection.ps1`
- [ ] Test MySQL 1: PASS ✓
- [ ] Test MySQL 2: PASS ✓
- [ ] (RMI Server chưa chạy nên test này sẽ FAIL - OK)

---

## 🚀 BƯỚC 9: CHẠY RMI SERVER

### Khởi động Server:

- [ ] Đã mở file `src/server/BankServer.java` trong Eclipse
- [ ] Right-click → Run As → Java Application
- [ ] Console hiển thị:
  ```
  =================================
  RMI Bank Server đã khởi động!
  Server đang lắng nghe trên port 1099
  Service name: BankService
  =================================
  ```
- [ ] Server đang chạy, KHÔNG đóng cửa sổ này ✓

---

## 🖥️ BƯỚC 10: CHẠY CLIENT UI

### Khởi động Client:

- [ ] Đã mở file `src/client/BankClientUI.java` trong Eclipse
- [ ] Right-click → Run As → Java Application
- [ ] Cửa sổ UI hiển thị ra
- [ ] Title: "Hệ thống chuyển khoản RMI" ✓

---

## 🔌 BƯỚC 11: KẾT NỐI SERVER

### Trong UI Client:

- [ ] Đã nhập Server Host: _______________
  - Nếu Server cùng máy: localhost
  - Nếu Server khác máy: nhập IP máy Server

- [ ] Đã click nút "Kết nối"
- [ ] Trạng thái hiển thị: "Đã kết nối" (màu xanh) ✓
- [ ] Các combo box đã load tài khoản
- [ ] Các nút đã enable

---

## 💰 BƯỚC 12: TEST CHUYỂN KHOẢN

### Thực hiện chuyển khoản test:

- [ ] Tab "Chuyển khoản"
- [ ] Chọn "Từ tài khoản": ACC001 - Nguyen Van A
- [ ] Chọn "Đến tài khoản": ACC002 - Tran Thi B
- [ ] Nhập số tiền: 1000000 (1 triệu)
- [ ] Click "Chuyển khoản"
- [ ] Confirm: YES
- [ ] Kết quả: "Chuyển khoản thành công!" ✓
- [ ] Tab "Danh sách tài khoản" → Click "Làm mới"
- [ ] Số dư ACC001 giảm 1,000,000 ✓
- [ ] Số dư ACC002 tăng 1,000,000 ✓

---

## 🔄 BƯỚC 13: KIỂM TRA ĐỒNG BỘ DATABASE

### Trên máy MySQL 1:

- [ ] Đăng nhập MySQL
- [ ] Chạy query:
  ```sql
  SELECT * FROM bank_db.accounts WHERE account_number IN ('ACC001', 'ACC002');
  ```
- [ ] Ghi kết quả:
  - ACC001 balance: _______________
  - ACC002 balance: _______________

### Trên máy MySQL 2:

- [ ] Đăng nhập MySQL
- [ ] Chạy query:
  ```sql
  SELECT * FROM bank_db.accounts WHERE account_number IN ('ACC001', 'ACC002');
  ```
- [ ] Ghi kết quả:
  - ACC001 balance: _______________
  - ACC002 balance: _______________

### So sánh:

- [ ] Số liệu trên 2 database GIỐNG NHAU ✓✓✓
- [ ] ĐÃ ĐỒNG BỘ THÀNH CÔNG! 🎉

---

## 🎯 BƯỚC 14: TEST CÁC CHỨC NĂNG KHÁC

### Tạo tài khoản mới:

- [ ] Tab "Tạo tài khoản"
- [ ] Nhập Số TK: ACC999
- [ ] Nhập Tên: Test User
- [ ] Nhập Số dư: 5000000
- [ ] Click "Tạo tài khoản"
- [ ] Kết quả: Thành công ✓
- [ ] Kiểm tra trên cả 2 DB: ACC999 đã tồn tại ✓

### Kiểm tra số dư không đủ:

- [ ] Thử chuyển 100,000,000 từ ACC005 (chỉ có 3 triệu)
- [ ] Kết quả: Báo lỗi "Số dư không đủ" ✓

---

## ✅ HOÀN THÀNH!

### Tất cả các bước đã xong:

- [ ] MySQL Server 1 & 2 đang chạy
- [ ] Database đã được tạo và đồng bộ
- [ ] RMI Server đang chạy
- [ ] Client UI kết nối thành công
- [ ] Chuyển khoản hoạt động
- [ ] Dữ liệu đồng bộ trên 2 database
- [ ] Tất cả tính năng hoạt động tốt

---

## 🎓 GHI CHÚ QUAN TRỌNG

### Khi gặp lỗi:

1. ❌ "Connection refused MySQL"
   → Kiểm tra lại Bước 4 (Firewall)

2. ❌ "Access denied"
   → Kiểm tra lại Bước 3 (Quyền MySQL) và Bước 7 (Password)

3. ❌ "Cannot connect to RMI Server"
   → Kiểm tra Bước 9 (Server đã chạy chưa)

4. ❌ "ClassNotFoundException JDBC Driver"
   → Kiểm tra lại Bước 6 (Thêm JDBC jar)

### Tips:

- ⭐ Luôn chạy Server TRƯỚC, Client SAU
- ⭐ Kiểm tra Console log để debug
- ⭐ Dùng `check_connection.ps1` để test nhanh
- ⭐ Đọc HUONG_DAN_CAI_DAT.md nếu cần chi tiết hơn

---

## 📊 TRACKING

Ngày bắt đầu: _______________
Ngày hoàn thành: _______________
Thời gian tổng: _______________

Vấn đề gặp phải:
- _______________________________________________
- _______________________________________________
- _______________________________________________

Cách giải quyết:
- _______________________________________________
- _______________________________________________
- _______________________________________________

---

**🎉 CHÚC MỪNG BẠN ĐÃ HOÀN THÀNH CÀI ĐẶT! 🎉**

Bây giờ bạn có một hệ thống chuyển khoản RMI với đồng bộ 2 database hoạt động hoàn hảo!

═══════════════════════════════════════════════════════════
RMI Banking System v1.0 | Checklist for Installation
═══════════════════════════════════════════════════════════
