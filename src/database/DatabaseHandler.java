package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import rmi.Account;

/**
 * Xử lý kết nối và thao tác với database
 */
public class DatabaseHandler {
    
    /**
     * Lấy kết nối đến database
     */
    private Connection getConnection(String url, String user, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Cập nhật số dư tài khoản trên 1 database
     */
    private boolean updateBalance(Connection conn, String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Chuyển khoản - cập nhật đồng bộ 2 database
     */
    public boolean transfer(String fromAccount, String toAccount, double amount) {
        Connection conn1 = null;
        Connection conn2 = null;
        
        try {
            // Kết nối 2 database
            conn1 = getConnection(DatabaseConfig.getDB1Url(), 
                    DatabaseConfig.DB1_USER, DatabaseConfig.DB1_PASSWORD);
            conn2 = getConnection(DatabaseConfig.getDB2Url(), 
                    DatabaseConfig.DB2_USER, DatabaseConfig.DB2_PASSWORD);
            
            // Bắt đầu transaction trên cả 2 database
            conn1.setAutoCommit(false);
            conn2.setAutoCommit(false);
            
            // Lấy số dư hiện tại
            double fromBalance = getBalance(conn1, fromAccount);
            double toBalance = getBalance(conn1, toAccount);
            
            // Kiểm tra số dư
            if (fromBalance < amount) {
                System.out.println("Số dư không đủ!");
                return false;
            }
            
            // Tính số dư mới
            double newFromBalance = fromBalance - amount;
            double newToBalance = toBalance + amount;
            
            // Cập nhật trên database 1
            boolean db1Success = updateBalance(conn1, fromAccount, newFromBalance) &&
                                updateBalance(conn1, toAccount, newToBalance);
            
            // Cập nhật trên database 2
            boolean db2Success = updateBalance(conn2, fromAccount, newFromBalance) &&
                                updateBalance(conn2, toAccount, newToBalance);
            
            if (db1Success && db2Success) {
                // Commit cả 2 database
                conn1.commit();
                conn2.commit();
                System.out.println("Chuyển khoản thành công trên cả 2 database!");
                return true;
            } else {
                // Rollback nếu có lỗi
                conn1.rollback();
                conn2.rollback();
                System.out.println("Lỗi cập nhật, đã rollback!");
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback nếu có exception
            try {
                if (conn1 != null) conn1.rollback();
                if (conn2 != null) conn2.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            // Đóng kết nối
            try {
                if (conn1 != null) conn1.close();
                if (conn2 != null) conn2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Lấy số dư từ database
     */
    public double getBalance(String accountNumber) {
        try (Connection conn = getConnection(DatabaseConfig.getDB1Url(), 
                DatabaseConfig.DB1_USER, DatabaseConfig.DB1_PASSWORD)) {
            return getBalance(conn, accountNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private double getBalance(Connection conn, String accountNumber) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }
    
    /**
     * Lấy danh sách tất cả tài khoản
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        
        try (Connection conn = getConnection(DatabaseConfig.getDB1Url(), 
                DatabaseConfig.DB1_USER, DatabaseConfig.DB1_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Account account = new Account(
                    rs.getString("account_number"),
                    rs.getString("account_name"),
                    rs.getDouble("balance")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Tạo tài khoản mới - đồng bộ trên 2 database
     */
    public boolean createAccount(String accountNumber, String accountName, double balance) {
        Connection conn1 = null;
        Connection conn2 = null;
        
        try {
            conn1 = getConnection(DatabaseConfig.getDB1Url(), 
                    DatabaseConfig.DB1_USER, DatabaseConfig.DB1_PASSWORD);
            conn2 = getConnection(DatabaseConfig.getDB2Url(), 
                    DatabaseConfig.DB2_USER, DatabaseConfig.DB2_PASSWORD);
            
            String sql = "INSERT INTO accounts (account_number, account_name, balance) VALUES (?, ?, ?)";
            
            // Insert vào database 1
            PreparedStatement pstmt1 = conn1.prepareStatement(sql);
            pstmt1.setString(1, accountNumber);
            pstmt1.setString(2, accountName);
            pstmt1.setDouble(3, balance);
            
            // Insert vào database 2
            PreparedStatement pstmt2 = conn2.prepareStatement(sql);
            pstmt2.setString(1, accountNumber);
            pstmt2.setString(2, accountName);
            pstmt2.setDouble(3, balance);
            
            boolean result = pstmt1.executeUpdate() > 0 && pstmt2.executeUpdate() > 0;
            
            pstmt1.close();
            pstmt2.close();
            
            return result;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn1 != null) conn1.close();
                if (conn2 != null) conn2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
