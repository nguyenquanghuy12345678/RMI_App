# ❌ FIX LỖI: Access denied for user

## 🔴 Lỗi gặp phải:

```
java.sql.SQLException: Access denied for user ''@'LAPTOP-K0QCF3N7' (using password: NO)
```

## 🔍 Nguyên nhân:

Lỗi này có nghĩa là:
1. ❌ Username đang **RỖNG** (`''`)
2. ❌ Password đang **KHÔNG được sử dụng** (`using password: NO`)
3. ❌ User `rmiuser1` chưa được tạo trên **XAMPP localhost**

## ✅ GIẢI PHÁP - Làm theo thứ tự:

---

### BƯỚC 1: Kiểm tra file DatabaseConfig.java đã sửa

File `src/database/DatabaseConfig.java` phải có:

```java
// Database 1 - Máy chủ chính
public static final String DB1_HOST = "192.168.1.7";
public static final String DB1_USER = "rmiuser1";
public static final String DB1_PASSWORD = "rmi1";

// Database 2 - Máy chủ dự phòng (XAMPP localhost)
public static final String DB2_HOST = "localhost";
public static final String DB2_USER = "rmiuser1";
public static final String DB2_PASSWORD = "rmi1";
```

✅ **ĐÃ SỬA XONG** - Cả 2 database đều dùng `rmiuser1/rmi1`

---

### BƯỚC 2: Tạo user trên XAMPP (máy Windows này)

#### Cách 1: Dùng phpMyAdmin (DỄ NHẤT)

1. Mở trình duyệt: `http://localhost/phpmyadmin`

2. Click tab **"User accounts"** (Tài khoản người dùng)

3. Click **"Add user account"** (Thêm tài khoản)

4. Điền thông tin:
   ```
   User name: rmiuser1
   Host name: % (chọn Any host từ dropdown)
   Password: rmi1
   Re-type: rmi1
   ```

5. Trong phần **"Database for user account"**:
   - ✅ Chọn: "Grant all privileges on database bank_db"

6. Tick vào: **"Grant all privileges on wildcard name (username\_%)."**

7. Click **"Go"** (Thực hiện)

8. Lặp lại để tạo thêm user với Host: **localhost**
   ```
   User name: rmiuser1
   Host name: localhost
   Password: rmi1
   ```

#### Cách 2: Dùng MySQL Command Line (NHANH HƠN)

1. Mở **XAMPP Control Panel**

2. Click **"Shell"** button

3. Chạy lệnh:
   ```bash
   mysql -u root -p
   ```
   
4. Nếu hỏi password, nhấn **Enter** (XAMPP mặc định không có password)

5. Chạy script:
   ```bash
   source D:/eclipse-workspace/RMI_App/FIX_DATABASE_USER.sql
   ```
   
   Hoặc copy-paste từng lệnh:
   ```sql
   CREATE USER IF NOT EXISTS 'rmiuser1'@'%' IDENTIFIED BY 'rmi1';
   CREATE USER IF NOT EXISTS 'rmiuser1'@'localhost' IDENTIFIED BY 'rmi1';
   
   GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'%';
   GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'localhost';
   
   FLUSH PRIVILEGES;
   ```

6. Kiểm tra:
   ```sql
   SELECT User, Host FROM mysql.user WHERE User = 'rmiuser1';
   ```
   
   Kết quả phải có 2 dòng:
   ```
   rmiuser1    %
   rmiuser1    localhost
   ```

---

### BƯỚC 3: Kiểm tra kết nối

Chạy script test:

```powershell
.\check_connection.ps1
```

Hoặc test thủ công:

```bash
# Trong XAMPP Shell
mysql -h localhost -u rmiuser1 -prmi1 bank_db

# Nếu thành công, bạn sẽ thấy:
mysql> 
```

---

### BƯỚC 4: Restart Server RMI

1. **ĐÓNG** RMI Server đang chạy (nếu có)

2. **Clean & Build** project trong Eclipse:
   - Right-click project → Clean...
   - Project → Clean... → OK

3. **Chạy lại Server**:
   - Right-click `BankServer.java` → Run As → Java Application

4. **Chạy lại Client**:
   - Right-click `BankClientUI.java` → Run As → Java Application

---

### BƯỚC 5: Test lại tạo tài khoản

Trong Client UI:
1. Tab "Tạo tài khoản"
2. Số TK: **0404040404**
3. Tên: **Nguyen Quang Huy**
4. Số dư: **0** (hoặc số bất kỳ)
5. Click **"Tạo tài khoản"**

✅ Kết quả: **"Tạo tài khoản thành công!"**

---

## 🔧 XỬ LÝ NẾU VẪN LỖI

### Lỗi 1: "User 'rmiuser1'@'localhost' already exists"
**Giải pháp:** User đã tồn tại, chỉ cần cấp quyền lại:
```sql
GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'localhost';
FLUSH PRIVILEGES;
```

### Lỗi 2: "Access denied for user 'rmiuser1'@'localhost'"
**Giải pháp:** Đổi lại password:
```sql
ALTER USER 'rmiuser1'@'localhost' IDENTIFIED BY 'rmi1';
ALTER USER 'rmiuser1'@'%' IDENTIFIED BY 'rmi1';
FLUSH PRIVILEGES;
```

### Lỗi 3: Vẫn hiển thị user rỗng ''
**Giải pháp:** 
1. Kiểm tra lại file `DatabaseConfig.java` đã **Save** chưa (Ctrl+S)
2. **Clean project** trong Eclipse
3. **Restart** RMI Server

### Lỗi 4: "Unknown database 'bank_db'"
**Giải pháp:** Chạy lại script tạo database:
```bash
mysql -u rmiuser1 -prmi1 < database_setup.sql
```

---

## 📋 CHECKLIST KIỂM TRA

- [ ] File `DatabaseConfig.java` đã sửa DB2_USER = "rmiuser1"
- [ ] File `DatabaseConfig.java` đã sửa DB2_PASSWORD = "rmi1"
- [ ] Đã **Save** file (Ctrl+S)
- [ ] Đã tạo user `rmiuser1` trên XAMPP
- [ ] User có quyền trên database `bank_db`
- [ ] Test kết nối thành công: `mysql -u rmiuser1 -prmi1`
- [ ] Đã **Restart** RMI Server
- [ ] Đã **Restart** Client UI
- [ ] Test tạo tài khoản thành công

---

## 🎯 TÓM TẮT NHANH

```bash
# 1. XAMPP Shell - Tạo user
mysql -u root -p
> CREATE USER 'rmiuser1'@'%' IDENTIFIED BY 'rmi1';
> CREATE USER 'rmiuser1'@'localhost' IDENTIFIED BY 'rmi1';
> GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'%';
> GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'localhost';
> FLUSH PRIVILEGES;
> exit;

# 2. Test kết nối
mysql -u rmiuser1 -prmi1 bank_db

# 3. Trong Eclipse
# - Clean project
# - Restart Server
# - Restart Client
# - Test tạo tài khoản
```

---

## ✅ SAU KHI FIX XONG

Bạn sẽ thấy trong Console:

```
Tạo tài khoản mới: 0404040404 - Nguyen Quang Huy
```

Và trong UI:
```
✓ Tạo tài khoản thành công!
  Đã cập nhật đồng bộ trên 2 database.
```

Kiểm tra trong MySQL:
```sql
SELECT * FROM bank_db.accounts WHERE account_number = '0404040404';
```

Kết quả:
```
0404040404 | Nguyen Quang Huy | 0.00
```

---

## 💡 LƯU Ý QUAN TRỌNG

1. ⭐ **CẢ 2 máy** đều phải có user `rmiuser1` với password `rmi1`
2. ⭐ Phải **RESTART Server** sau khi sửa `DatabaseConfig.java`
3. ⭐ Nhớ **Save file** trước khi chạy lại (Ctrl+S)
4. ⭐ Nếu dùng XAMPP, mặc định root **KHÔNG có password**

---

**🎉 HOÀN THÀNH!** 

Database đã sẵn sàng với user `rmiuser1` trên cả 2 máy!

═══════════════════════════════════════════════════════════
RMI Banking System - Database User Fix Guide v1.0
═══════════════════════════════════════════════════════════
