package debug;

import database.DatabaseConfig;

/**
 * Class để debug và kiểm tra giá trị DatabaseConfig
 * Chạy class này để xem config đang dùng
 */
public class CheckDatabaseConfig {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  DATABASE CONFIGURATION CHECK");
        System.out.println("========================================");
        System.out.println();
        
        System.out.println("DATABASE 1 (Server chính):");
        System.out.println("  Host: " + DatabaseConfig.DB1_HOST);
        System.out.println("  Port: " + DatabaseConfig.DB1_PORT);
        System.out.println("  Database: " + DatabaseConfig.DB1_NAME);
        System.out.println("  User: " + DatabaseConfig.DB1_USER);
        System.out.println("  Password: " + DatabaseConfig.DB1_PASSWORD);
        System.out.println("  URL: " + DatabaseConfig.getDB1Url());
        System.out.println();
        
        System.out.println("DATABASE 2 (Server dự phòng):");
        System.out.println("  Host: " + DatabaseConfig.DB2_HOST);
        System.out.println("  Port: " + DatabaseConfig.DB2_PORT);
        System.out.println("  Database: " + DatabaseConfig.DB2_NAME);
        System.out.println("  User: " + DatabaseConfig.DB2_USER);
        System.out.println("  Password: " + DatabaseConfig.DB2_PASSWORD);
        System.out.println("  URL: " + DatabaseConfig.getDB2Url());
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("  KIỂM TRA");
        System.out.println("========================================");
        
        boolean hasError = false;
        
        // Check DB1
        if (DatabaseConfig.DB1_USER == null || DatabaseConfig.DB1_USER.isEmpty()) {
            System.out.println("❌ DB1_USER đang RỖNG!");
            hasError = true;
        }
        if (DatabaseConfig.DB1_PASSWORD == null || DatabaseConfig.DB1_PASSWORD.isEmpty()) {
            System.out.println("❌ DB1_PASSWORD đang RỖNG!");
            hasError = true;
        }
        
        // Check DB2
        if (DatabaseConfig.DB2_USER == null || DatabaseConfig.DB2_USER.isEmpty()) {
            System.out.println("❌ DB2_USER đang RỖNG!");
            hasError = true;
        } else if (!DatabaseConfig.DB2_USER.equals("rmiuser1")) {
            System.out.println("⚠️  DB2_USER KHÔNG PHẢI 'rmiuser1': " + DatabaseConfig.DB2_USER);
            System.out.println("   Sửa lại trong DatabaseConfig.java!");
            hasError = true;
        }
        
        if (DatabaseConfig.DB2_PASSWORD == null || DatabaseConfig.DB2_PASSWORD.isEmpty()) {
            System.out.println("❌ DB2_PASSWORD đang RỖNG!");
            hasError = true;
        } else if (!DatabaseConfig.DB2_PASSWORD.equals("rmi1")) {
            System.out.println("⚠️  DB2_PASSWORD KHÔNG PHẢI 'rmi1': " + DatabaseConfig.DB2_PASSWORD);
            System.out.println("   Sửa lại trong DatabaseConfig.java!");
            hasError = true;
        }
        
        if (!hasError) {
            System.out.println("✓ Tất cả cấu hình đều OK!");
            System.out.println();
            System.out.println("Nếu vẫn bị lỗi 'Access denied':");
            System.out.println("  1. Kiểm tra user 'rmiuser1' đã tạo trong MySQL chưa");
            System.out.println("  2. Chạy: mysql -u rmiuser1 -prmi1 bank_db");
            System.out.println("  3. Nếu lỗi → Tạo user bằng FIX_DATABASE_USER.sql");
        } else {
            System.out.println();
            System.out.println("❌ CÓ LỖI TRONG CẤU HÌNH!");
            System.out.println();
            System.out.println("Sửa file: src/database/DatabaseConfig.java");
            System.out.println("Sau đó:");
            System.out.println("  1. SAVE file (Ctrl+S)");
            System.out.println("  2. Clean project (Project → Clean...)");
            System.out.println("  3. Chạy lại CheckDatabaseConfig này để verify");
        }
        
        System.out.println("========================================");
    }
}
