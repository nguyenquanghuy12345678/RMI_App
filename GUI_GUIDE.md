# 🎨 GIAO DIỆN MỚI - HƯỚNG DẪN SỬ DỤNG

## 📦 ĐÃ CẬP NHẬT

### ✅ 1. Server GUI (BankServerGUI.java)

**Tính năng mới:**
- ✨ Giao diện đồ họa đầy đủ cho Server
- 🚀 Nút Start/Stop server dễ dàng
- 📊 Log realtime các hoạt động
- 👥 Hiển thị số client đang kết nối
- 🔌 Quản lý port RMI
- ⚠️ Cảnh báo khi đóng server đang chạy
- 🎨 Giao diện modern, dễ nhìn

### ✅ 2. Client GUI (BankClientUI.java)

**Đã có sẵn:**
- 🔌 Nút "Ngắt kết nối"
- ⚠️ Cảnh báo khi đóng có kết nối
- 🔄 Refresh dữ liệu
- ✅ Validation đầu vào

---

## 🚀 CÁCH SỬ DỤNG

### **A. CHẠY SERVER MỚI (Với GUI)**

#### Cách 1: Trong Eclipse
```
1. Mở file: src/server/BankServerGUI.java
2. Right-click → Run As → Java Application
3. Cửa sổ Server GUI hiện ra
4. Nhấn nút [Start Server]
5. Server bắt đầu lắng nghe!
```

#### Cách 2: Command Line
```bash
cd bin
java --module-path . --module RMI_App/server.BankServerGUI
```

#### Giao diện Server:
```
┌─────────────────────────────────────────────────────┐
│ RMI Bank Server - Admin Console              ☐ ❐ ✕ │
├─────────────────────────────────────────────────────┤
│ ┌─ Điều khiển Server ──────────────────────────┐   │
│ │ Port RMI: [1099    ] [Start Server] [Xóa Log]│   │
│ └──────────────────────────────────────────────┘   │
│ ┌─ Server Log ─────────────────────────────────┐   │
│ │ [12:34:56] Server khởi động...               │   │
│ │ [12:34:57] ✓ RMI Registry đã tạo             │   │
│ │ [12:34:58] → Yêu cầu chuyển khoản:           │   │
│ │   Từ: ACC001 → Đến: ACC002                  │   │
│ │   Số tiền: 1,000,000 VND                    │   │
│ │   ✓ Chuyển khoản THÀNH CÔNG!                │   │
│ └──────────────────────────────────────────────┘   │
│ ● Running  |  Port: 1099  |  Clients: 2  |  DB: Ready │
└─────────────────────────────────────────────────────┘
```

**Chức năng:**
- 🟢 **Nút Start Server**: Khởi động server
- 🔴 **Nút Stop Server**: Dừng server an toàn
- 🗑️ **Nút Xóa Log**: Xóa log để dễ đọc
- 📋 **Log Area**: Hiển thị tất cả hoạt động realtime
- 📊 **Status Bar**: Trạng thái, port, số client

---

### **B. CHẠY CLIENT**

```
1. Mở file: src/client/BankClientUI.java
2. Right-click → Run As → Java Application
3. Nhập Server Host (localhost hoặc IP)
4. Nhấn [Kết nối]
5. Sử dụng các chức năng
6. Nhấn [🔌 Ngắt kết nối] khi xong
```

---

## 🎯 WORKFLOW CHUẨN

### **1. Khởi động hệ thống:**

```
Bước 1: Chạy BankServerGUI.java
        ↓
Bước 2: Nhấn [Start Server]
        ↓
Bước 3: Chạy BankClientUI.java
        ↓
Bước 4: Nhập host → [Kết nối]
        ↓
Bước 5: Sử dụng các chức năng
```

### **2. Đóng hệ thống đúng cách:**

```
Bước 1: Trong Client → [🔌 Ngắt kết nối]
        ↓
Bước 2: Đóng cửa sổ Client (×)
        ↓
Bước 3: Trong Server → [Stop Server]
        ↓
Bước 4: Đóng cửa sổ Server (×)
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

### **✅ ĐÚNG:**
1. ✓ Chạy **Server TRƯỚC**, Client sau
2. ✓ Nhấn **[Start Server]** trước khi client kết nối
3. ✓ Nhấn **[Ngắt kết nối]** trước khi đóng client
4. ✓ Nhấn **[Stop Server]** trước khi đóng server
5. ✓ Đợi server log "✓ SERVER ĐÃ SẴN SÀNG!"

### **❌ SAI:**
1. ✗ Đóng cửa sổ server khi đang có client kết nối
2. ✗ Không nhấn Stop Server trước khi thoát
3. ✗ Chạy client trước server
4. ✗ Kết nối khi server chưa Start

---

## 🔧 XỬ LÝ LỖI

### **Lỗi 1: "Port already in use"**
**Nguyên nhân:** Server cũ vẫn đang chạy ở background

**Giải pháp:**
```powershell
# Kill process Java
Get-Process -Name "java" | Stop-Process -Force

# Hoặc trong Server GUI: Nhấn [Stop Server]
```

### **Lỗi 2: "Connection refused"**
**Nguyên nhân:** Server chưa Start hoặc sai port

**Giải pháp:**
1. Kiểm tra Server GUI → Phải thấy "● Running"
2. Kiểm tra port trong Server (mặc định: 1099)
3. Nhập đúng port trong Client

### **Lỗi 3: Client không ngắt kết nối được**
**Giải pháp:**
1. Nhấn nút [🔌 Ngắt kết nối]
2. Nếu không được → Đóng cửa sổ trực tiếp
3. Server sẽ tự động cleanup

---

## 📸 SCREENSHOTS

### Server GUI - Stopped
```
Status: ● Stopped (đỏ)
Nút: [Start Server] (xanh lá)
Port: N/A
Clients: 0
```

### Server GUI - Running
```
Status: ● Running (xanh lá)
Nút: [Stop Server] (đỏ)
Port: 1099
Clients: 2
Log: Các hoạt động realtime
```

### Client GUI
```
┌─ Kết nối Server ──────────────────────┐
│ Host: [localhost] [Kết nối] [🔌 Ngắt] │
│ Status: ● Đã kết nối (xanh)            │
└────────────────────────────────────────┘
```

---

## 💡 MẸO SỬ DỤNG

### **1. Chạy nhiều Client:**
- Có thể chạy nhiều BankClientUI.java cùng lúc
- Mỗi client là 1 instance riêng
- Server sẽ track số client đang kết nối

### **2. Theo dõi hoạt động:**
- Nhìn vào Server Log để debug
- Mọi thao tác đều được log realtime
- Nút [Xóa Log] để xóa log cũ

### **3. Thay đổi Port:**
- Có thể đổi port khác 1099
- Phải đổi TRƯỚC khi Start Server
- Client phải nhập đúng port khi kết nối

### **4. Quản lý Server:**
- Nhấn [Stop Server] để dừng an toàn
- Nếu đóng cửa sổ → Hệ thống tự hỏi confirm
- Chọn YES → Server tự stop và cleanup

---

## 🎨 GIAO DIỆN ĐẸP HƠN

### **Màu sắc:**
- 🟢 Xanh lá: Start, Kết nối thành công
- 🔴 Đỏ: Stop, Ngắt kết nối, Lỗi
- 🔵 Xanh dương: Info, Status
- ⚪ Xám: Disabled, Inactive

### **Font:**
- Server Log: Consolas (monospace)
- UI Text: Arial, System Font
- Dễ đọc trên mọi độ phân giải

---

## 🔄 SO SÁNH SERVER CŨ VÀ MỚI

| Tính năng | Server Cũ (BankServer) | Server Mới (BankServerGUI) |
|-----------|------------------------|----------------------------|
| Giao diện | ❌ Console only | ✅ GUI đầy đủ |
| Start/Stop | ❌ Chỉ có Start | ✅ Cả Start và Stop |
| Log | ❌ Console text | ✅ GUI Text Area |
| Port config | ❌ Hard-coded | ✅ Có thể đổi |
| Client count | ❌ Không hiển thị | ✅ Realtime tracking |
| Cleanup | ❌ Khó | ✅ Tự động |

---

## 📝 CHECKLIST SỬ DỤNG

### **Lần đầu setup:**
- [ ] Đã tạo database trên 2 máy
- [ ] Đã tạo user `rmiuser1`
- [ ] Đã sửa `DatabaseConfig.java`
- [ ] Đã test kết nối MySQL

### **Mỗi lần sử dụng:**
- [ ] Chạy `BankServerGUI.java`
- [ ] Nhấn `[Start Server]`
- [ ] Đợi log "✓ SERVER ĐÃ SẴN SÀNG!"
- [ ] Chạy `BankClientUI.java`
- [ ] Nhập host → `[Kết nối]`
- [ ] Thực hiện các thao tác
- [ ] Nhấn `[🔌 Ngắt kết nối]`
- [ ] Nhấn `[Stop Server]`
- [ ] Đóng cả 2 cửa sổ

---

## 🎉 HOÀN THÀNH!

Bây giờ bạn có:
- ✅ Server với giao diện GUI đầy đủ
- ✅ Client với quản lý kết nối tốt
- ✅ Log realtime mọi hoạt động
- ✅ Start/Stop an toàn
- ✅ Cleanup tự động
- ✅ Giao diện đẹp, dễ dùng

**Hãy thử ngay!** 🚀

═══════════════════════════════════════════════════════════
RMI Banking System v2.0 - GUI Edition
═══════════════════════════════════════════════════════════
