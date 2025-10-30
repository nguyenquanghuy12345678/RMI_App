/**
 * Module cho ứng dụng RMI Banking
 */
module RMI_App {
    // Các module Java SE cần thiết
    requires java.rmi;          // Hỗ trợ RMI
    requires java.sql;          // Hỗ trợ JDBC/Database
    requires java.desktop;      // Hỗ trợ Swing UI
    
    // Export các package để RMI có thể truy cập
    exports rmi;
    exports server;
    exports client;
    exports database;
}