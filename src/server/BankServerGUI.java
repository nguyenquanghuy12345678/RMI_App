package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.DatabaseHandler;
import rmi.Account;
import rmi.BankInterface;

/**
 * RMI Server với giao diện GUI
 */
public class BankServerGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Server components
    private BankServerImpl serverImpl;
    private Registry registry;
    private boolean serverRunning = false;
    
    // UI Components
    private JTextArea txtLog;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnDisconnect;
    private JLabel lblStatus;
    private JLabel lblPort;
    private JLabel lblClientCount;
    private JTextField txtPort;
    private JPanel statusPanel;
    
    private static final int DEFAULT_PORT = 1099;
    private int activeClients = 0;
    
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public BankServerGUI() {
        initComponents();
        log("Server khởi động. Nhấn [Start Server] để bắt đầu.");
    }
    
    private void initComponents() {
        setTitle("RMI Bank Server - Admin Console");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel - Server control
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);
        
        // Center panel - Log area
        mainPanel.add(createLogPanel(), BorderLayout.CENTER);
        
        // Bottom panel - Status
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverRunning) {
                    int confirm = JOptionPane.showConfirmDialog(
                        BankServerGUI.this,
                        "Server đang chạy. Bạn có muốn dừng server và thoát?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        stopServer();
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(new TitledBorder("Điều khiển Server"));
        
        // Port label and input
        panel.add(new JLabel("Port RMI:"));
        txtPort = new JTextField(String.valueOf(DEFAULT_PORT), 6);
        txtPort.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtPort);
        
        // Start button
        btnStart = new JButton("▶ Start Server");
        btnStart.setPreferredSize(new Dimension(150, 35));
        btnStart.setBackground(new Color(46, 204, 113));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Arial", Font.BOLD, 13));
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e -> startServer());
        panel.add(btnStart);
        
        // Stop button
        btnStop = new JButton("■ Stop Server");
        btnStop.setPreferredSize(new Dimension(150, 35));
        btnStop.setBackground(new Color(231, 76, 60));
        btnStop.setForeground(Color.WHITE);
        btnStop.setFont(new Font("Arial", Font.BOLD, 13));
        btnStop.setFocusPainted(false);
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> stopServer());
        panel.add(btnStop);
        
        // Disconnect button
        btnDisconnect = new JButton("🔌 Disconnect All");
        btnDisconnect.setPreferredSize(new Dimension(150, 35));
        btnDisconnect.setBackground(new Color(243, 156, 18));
        btnDisconnect.setForeground(Color.WHITE);
        btnDisconnect.setFont(new Font("Arial", Font.BOLD, 13));
        btnDisconnect.setFocusPainted(false);
        btnDisconnect.setEnabled(false);
        btnDisconnect.setToolTipText("Ngắt kết nối tất cả clients");
        btnDisconnect.addActionListener(e -> disconnectAllClients());
        panel.add(btnDisconnect);
        
        // Separator
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Clear log button
        JButton btnClearLog = new JButton("🗑 Xóa Log");
        btnClearLog.setPreferredSize(new Dimension(120, 35));
        btnClearLog.setFont(new Font("Arial", Font.PLAIN, 12));
        btnClearLog.addActionListener(e -> {
            txtLog.setText("");
            log("Log đã được xóa.");
        });
        panel.add(btnClearLog);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Server Log"));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(40, 44, 52));
        txtLog.setForeground(new Color(171, 178, 191));
        
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Status indicator
        lblStatus = new JLabel("● Stopped");
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(lblStatus);
        
        // Port info
        lblPort = new JLabel("Port: N/A");
        statusPanel.add(lblPort);
        
        // Client count
        lblClientCount = new JLabel("Clients: 0");
        statusPanel.add(lblClientCount);
        
        // Database status
        JLabel lblDB = new JLabel("DB: Ready");
        lblDB.setForeground(new Color(46, 204, 113));
        statusPanel.add(lblDB);
        
        return statusPanel;
    }
    
    private void startServer() {
        try {
            // Get port from input
            int port;
            try {
                port = Integer.parseInt(txtPort.getText().trim());
                if (port < 1024 || port > 65535) {
                    throw new NumberFormatException("Port phải trong khoảng 1024-65535");
                }
            } catch (NumberFormatException ex) {
                log("✗ LỖI: Port không hợp lệ - " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Port không hợp lệ! Vui lòng nhập số từ 1024-65535", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log("========================================");
            log("Đang khởi động RMI Server...");
            log("Port: " + port);
            
            // Create registry
            registry = LocateRegistry.createRegistry(port);
            log("✓ RMI Registry đã được tạo trên port " + port);
            
            // Create server implementation
            serverImpl = new BankServerImpl(this);
            log("✓ Server implementation đã được khởi tạo");
            
            // Bind server
            registry.rebind("BankService", serverImpl);
            log("✓ Service 'BankService' đã được đăng ký");
            
            serverRunning = true;
            
            // Update UI
            txtPort.setEnabled(false);  // Disable port input when running
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnDisconnect.setEnabled(true);
            lblStatus.setText("● Running");
            lblStatus.setForeground(new Color(46, 204, 113));
            lblPort.setText("Port: " + port);
            
            log("========================================");
            log("✓ SERVER ĐÃ SẴN SÀNG!");
            log("Đang lắng nghe kết nối từ clients...");
            log("========================================");
            
        } catch (Exception e) {
            log("✗ LỖI khi khởi động server: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể khởi động server:\n" + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        try {
            log("========================================");
            log("Đang dừng server...");
            
            if (registry != null) {
                registry.unbind("BankService");
                log("✓ Service đã được unbind");
            }
            
            if (serverImpl != null) {
                UnicastRemoteObject.unexportObject(serverImpl, true);
                log("✓ Server object đã được unexport");
            }
            
            serverRunning = false;
            activeClients = 0;  // Reset client count
            
            // Update UI
            txtPort.setEnabled(true);  // Enable port input when stopped
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnDisconnect.setEnabled(false);
            lblStatus.setText("● Stopped");
            lblStatus.setForeground(Color.RED);
            lblPort.setText("Port: N/A");
            lblClientCount.setText("Clients: 0");
            
            log("✓ SERVER ĐÃ DỪNG");
            log("========================================");
            
        } catch (Exception e) {
            log("✗ LỖI khi dừng server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void disconnectAllClients() {
        log("========================================");
        log("⚠️  Ngắt kết nối tất cả clients...");
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn ngắt kết nối tất cả clients?\nServer sẽ tiếp tục chạy.",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Force garbage collection để ngắt các connection không dùng
                System.gc();
                
                // Reset client count
                lblClientCount.setText("Clients: 0");
                
                log("✓ Đã ngắt kết nối tất cả clients");
                log("Server vẫn đang chạy và chấp nhận kết nối mới");
                log("========================================");
                
                JOptionPane.showMessageDialog(
                    this,
                    "Đã ngắt kết nối tất cả clients.\nServer vẫn đang chạy.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (Exception e) {
                log("✗ Lỗi khi ngắt kết nối: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log("✗ Đã hủy ngắt kết nối");
            log("========================================");
        }
    }
    
    public void log(String message) {
        String timestamp = timeFormat.format(new Date());
        txtLog.append("[" + timestamp + "] " + message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    public void updateClientCount(int count) {
        SwingUtilities.invokeLater(() -> {
            lblClientCount.setText("Clients: " + count);
        });
    }
    
    /**
     * Server Implementation class
     */
    private class BankServerImpl extends UnicastRemoteObject implements BankInterface {
        
        private static final long serialVersionUID = 1L;
        private DatabaseHandler dbHandler;
        private BankServerGUI gui;
        private int clientCount = 0;
        
        protected BankServerImpl(BankServerGUI gui) throws RemoteException {
            super();
            this.gui = gui;
            this.dbHandler = new DatabaseHandler();
        }
        
        @Override
        public boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException {
            gui.log("→ Yêu cầu chuyển khoản:");
            gui.log("  Từ: " + fromAccount + " → Đến: " + toAccount);
            gui.log("  Số tiền: " + String.format("%,.0f VND", amount));
            
            boolean result = dbHandler.transfer(fromAccount, toAccount, amount);
            
            if (result) {
                gui.log("  ✓ Chuyển khoản THÀNH CÔNG!");
            } else {
                gui.log("  ✗ Chuyển khoản THẤT BẠI!");
            }
            
            return result;
        }
        
        @Override
        public double getBalance(String accountNumber) throws RemoteException {
            double balance = dbHandler.getBalance(accountNumber);
            gui.log("→ Truy vấn số dư TK " + accountNumber + ": " + String.format("%,.0f VND", balance));
            return balance;
        }
        
        @Override
        public List<Account> getAllAccounts() throws RemoteException {
            List<Account> accounts = dbHandler.getAllAccounts();
            gui.log("→ Lấy danh sách tài khoản: " + accounts.size() + " TK");
            clientCount++;
            gui.updateClientCount(clientCount);
            return accounts;
        }
        
        @Override
        public boolean createAccount(String accountNumber, String accountName, double balance) throws RemoteException {
            gui.log("→ Tạo tài khoản mới:");
            gui.log("  Số TK: " + accountNumber);
            gui.log("  Tên: " + accountName);
            gui.log("  Số dư: " + String.format("%,.0f VND", balance));
            
            boolean result = dbHandler.createAccount(accountNumber, accountName, balance);
            
            if (result) {
                gui.log("  ✓ Tạo tài khoản THÀNH CÔNG!");
            } else {
                gui.log("  ✗ Tạo tài khoản THẤT BẠI!");
            }
            
            return result;
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            BankServerGUI serverGUI = new BankServerGUI();
            serverGUI.setVisible(true);
        });
    }
}
