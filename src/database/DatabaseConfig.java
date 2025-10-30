package database;

/**
 * Cấu hình kết nối database
 */
public class DatabaseConfig {
    
    // Database 1 - Máy chủ chính
    public static final String DB1_HOST = "localhost"; // Thay bằng IP máy 1
    public static final String DB1_PORT = "3306";
    public static final String DB1_NAME = "bank_db";
    public static final String DB1_USER = "root";
    public static final String DB1_PASSWORD = "root";
    
    // Database 2 - Máy chủ dự phòng
    public static final String DB2_HOST = "localhost"; // Thay bằng IP máy 2
    public static final String DB2_PORT = "3306";
    public static final String DB2_NAME = "bank_db";
    public static final String DB2_USER = "root";
    public static final String DB2_PASSWORD = "root";
    
    public static String getDB1Url() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", 
                DB1_HOST, DB1_PORT, DB1_NAME);
    }
    
    public static String getDB2Url() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", 
                DB2_HOST, DB2_PORT, DB2_NAME);
    }
}
