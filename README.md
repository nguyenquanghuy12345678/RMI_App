# Ứng dụng chuyển khoản RMI với đồng bộ 2 Database

## Mô tả
Ứng dụng ngân hàng sử dụng Java RMI (Remote Method Invocation) với giao diện Swing UI, hỗ trợ chuyển khoản và cập nhật đồng bộ dữ liệu lên 2 database MySQL trên 2 máy khác nhau.

## Tính năng
- ✅ Kết nối RMI Server từ xa
- ✅ Chuyển khoản giữa các tài khoản
- ✅ Cập nhật đồng bộ lên 2 database MySQL (transaction-based)
- ✅ Xem danh sách tài khoản và số dư
- ✅ Tạo tài khoản mới
- ✅ Giao diện UI thân thiện với người dùng

## Cấu trúc dự án
```
RMI_App/
├── src/
│   ├── rmi/
│   │   ├── BankInterface.java      # RMI Interface
│   │   └── Account.java            # Model tài khoản
│   ├── database/
│   │   ├── DatabaseConfig.java     # Cấu hình kết nối DB
│   │   └── DatabaseHandler.java    # Xử lý database operations
│   ├── server/
│   │   └── BankServer.java         # RMI Server
│   └── client/
│       └── BankClientUI.java       # UI Client
├── database_setup.sql              # Script tạo database
└── README.md
```

## Yêu cầu hệ thống
- Java JDK 8 trở lên
- MySQL Server 5.7+ (cài trên 2 máy khác nhau)
- MySQL Connector/J (JDBC Driver)
- Eclipse IDE hoặc IDE Java tương tự

## Cài đặt

### 1. Cấu hình MySQL (trên cả 2 máy)

#### Máy 1 và Máy 2:
```sql
-- Chạy script database_setup.sql trên cả 2 máy
mysql -u root -p < database_setup.sql
```

#### Cho phép kết nối từ xa (nếu cần):
```sql
-- Trong MySQL console
GRANT ALL PRIVILEGES ON bank_db.* TO 'root'@'%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

#### Cấu hình MySQL Server cho phép remote connection:
```ini
# File: /etc/mysql/mysql.conf.d/mysqld.cnf (Linux)
# hoặc: C:\ProgramData\MySQL\MySQL Server X.X\my.ini (Windows)

[mysqld]
bind-address = 0.0.0.0  # Cho phép kết nối từ mọi IP
```

Sau đó restart MySQL service.

### 2. Thêm MySQL JDBC Driver vào project

#### Tải MySQL Connector/J:
- Tải từ: https://dev.mysql.com/downloads/connector/j/
- Hoặc sử dụng Maven/Gradle

#### Thêm vào Eclipse:
1. Click chuột phải vào project → Properties
2. Chọn "Java Build Path" → Tab "Libraries"
3. Click "Add External JARs..."
4. Chọn file `mysql-connector-java-x.x.xx.jar`
5. Click Apply and Close

### 3. Cấu hình kết nối Database

Mở file `src/database/DatabaseConfig.java` và sửa thông tin:

```java
// Database 1 - Máy chủ chính
public static final String DB1_HOST = "192.168.1.100"; // IP máy 1
public static final String DB1_PORT = "3306";
public static final String DB1_NAME = "bank_db";
public static final String DB1_USER = "root";
public static final String DB1_PASSWORD = "your_password";

// Database 2 - Máy chủ dự phòng  
public static final String DB2_HOST = "192.168.1.101"; // IP máy 2
public static final String DB2_PORT = "3306";
public static final String DB2_NAME = "bank_db";
public static final String DB2_USER = "root";
public static final String DB2_PASSWORD = "your_password";
```

## Cách chạy

### 1. Khởi động RMI Server (chạy trên máy server)

```bash
# Trong Eclipse:
# Click chuột phải vào BankServer.java → Run As → Java Application

# Hoặc dùng command line:
cd bin
java server.BankServer
```

Kết quả:
```
=================================
RMI Bank Server đã khởi động!
Server đang lắng nghe trên port 1099
Service name: BankService
=================================
```

### 2. Khởi động Client UI (chạy trên máy client)

```bash
# Trong Eclipse:
# Click chuột phải vào BankClientUI.java → Run As → Java Application

# Hoặc dùng command line:
cd bin
java client.BankClientUI
```

### 3. Sử dụng ứng dụng

1. **Kết nối Server**: Nhập địa chỉ IP của máy server (hoặc localhost nếu chạy cùng máy) → Click "Kết nối"
2. **Chuyển khoản**: 
   - Chọn tài khoản nguồn và đích
   - Nhập số tiền
   - Click "Chuyển khoản"
3. **Xem danh sách**: Tab "Danh sách tài khoản"
4. **Tạo tài khoản mới**: Tab "Tạo tài khoản"

## Cơ chế đồng bộ

Ứng dụng sử dụng **Two-Phase Commit** để đảm bảo tính nhất quán:

1. Bắt đầu transaction trên cả 2 database
2. Thực hiện cập nhật trên database 1
3. Thực hiện cập nhật trên database 2
4. Nếu cả 2 thành công → COMMIT cả 2
5. Nếu có lỗi → ROLLBACK cả 2

```java
conn1.setAutoCommit(false);
conn2.setAutoCommit(false);

// Update both databases
boolean db1Success = updateBalance(conn1, ...);
boolean db2Success = updateBalance(conn2, ...);

if (db1Success && db2Success) {
    conn1.commit();
    conn2.commit();
} else {
    conn1.rollback();
    conn2.rollback();
}
```

## Xử lý lỗi

### Server không khởi động được:
- Kiểm tra port 1099 có bị chiếm không
- Kiểm tra firewall

### Không kết nối được database:
- Kiểm tra MySQL đã chạy chưa
- Kiểm tra thông tin kết nối trong DatabaseConfig.java
- Kiểm tra firewall cho phép port 3306

### Client không kết nối được Server:
- Kiểm tra Server đã chạy chưa
- Kiểm tra địa chỉ IP và port
- Kiểm tra firewall

## Mở rộng

### Thêm tính năng:
- Lịch sử giao dịch
- Xác thực người dùng
- Mã hóa dữ liệu
- Load balancing
- Replication tự động

### Cải thiện hiệu năng:
- Connection pooling
- Caching
- Async replication

## Tác giả
Hệ thống ngân hàng RMI với đồng bộ database

## License
Educational Purpose
