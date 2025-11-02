package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import rmi.Account;
import rmi.BankInterface;

/**
 * Giao diện UI cho ứng dụng ngân hàng RMI - Phiên bản cải tiến
 * - Quản lý kết nối tốt hơn
 * - Cleanup resources khi đóng
 * - Validation đầu vào
 * - Progress indicators
 */
public class BankClientUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private BankInterface bankService;
    private boolean isConnected = false;
    
    // Components
    private JTextField txtServerHost;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JLabel lblStatus;
    private JProgressBar progressBar;
    
    private JComboBox<String> cboFromAccount;
    private JComboBox<String> cboToAccount;
    private JTextField txtAmount;
    private JButton btnTransfer;
    private JButton btnRefresh;
    
    private JTable tableAccounts;
    private DefaultTableModel tableModel;
    
    private JTextField txtNewAccountNumber;
    private JTextField txtNewAccountName;
    private JTextField txtNewBalance;
    private JButton btnCreateAccount;
    
    private NumberFormat currencyFormat;
    
    public BankClientUI() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        initComponents();
        setupWindowListener();
    }
    
    /**
     * Cleanup khi đóng cửa sổ
     */
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }
    
    /**
     * Dọn dẹp tài nguyên trước khi đóng
     */
    private void cleanup() {
        if (isConnected) {
            int choice = JOptionPane.showConfirmDialog(
                this, 
                "Bạn đang kết nối đến server. Ngắt kết nối và thoát?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                disconnect();
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }
    
    private void initComponents() {
        setTitle("Hệ thống chuyển khoản RMI - Bank System v2.0");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Custom close handler
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Connection panel
        mainPanel.add(createConnectionPanel(), BorderLayout.NORTH);
        
        // Center panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Chuyển khoản", createTransferPanel());
        tabbedPane.addTab("Danh sách tài khoản", createAccountListPanel());
        tabbedPane.addTab("Tạo tài khoản", createNewAccountPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Kết nối Server"));
        
        // Top panel - Connection controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Server Host:"));
        txtServerHost = new JTextField("localhost", 20);
        txtServerHost.setToolTipText("Nhập IP hoặc hostname của RMI Server");
        topPanel.add(txtServerHost);
        
        btnConnect = new JButton("🔌 Kết nối");
        btnConnect.addActionListener(e -> connectToServer());
        btnConnect.setBackground(new Color(76, 175, 80));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.setFocusPainted(false);
        topPanel.add(btnConnect);
        
        btnDisconnect = new JButton("🔌 Ngắt kết nối");
        btnDisconnect.addActionListener(e -> disconnect());
        btnDisconnect.setEnabled(false);
        btnDisconnect.setBackground(new Color(244, 67, 54));
        btnDisconnect.setForeground(Color.WHITE);
        btnDisconnect.setFocusPainted(false);
        topPanel.add(btnDisconnect);
        
        lblStatus = new JLabel("⚫ Chưa kết nối");
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(lblStatus);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // From Account
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ tài khoản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        cboFromAccount = new JComboBox<>();
        cboFromAccount.setPreferredSize(new Dimension(300, 25));
        panel.add(cboFromAccount, gbc);
        
        // To Account
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Đến tài khoản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        cboToAccount = new JComboBox<>();
        cboToAccount.setPreferredSize(new Dimension(300, 25));
        panel.add(cboToAccount, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Số tiền:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        txtAmount = new JTextField(20);
        panel.add(txtAmount, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnTransfer = new JButton("Chuyển khoản");
        btnTransfer.setEnabled(false);
        btnTransfer.addActionListener(e -> performTransfer());
        buttonPanel.add(btnTransfer);
        
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(e -> loadAccounts());
        buttonPanel.add(btnRefresh);
        
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createAccountListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"Số tài khoản", "Tên chủ tài khoản", "Số dư (VND)"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableAccounts = new JTable(tableModel);
        tableAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAccounts.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tableAccounts);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNewAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số tài khoản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtNewAccountNumber = new JTextField(20);
        panel.add(txtNewAccountNumber, gbc);
        
        // Account Name
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Tên chủ tài khoản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtNewAccountName = new JTextField(20);
        panel.add(txtNewAccountName, gbc);
        
        // Balance
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Số dư ban đầu:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        txtNewBalance = new JTextField(20);
        txtNewBalance.setText("0");
        panel.add(txtNewBalance, gbc);
        
        // Button
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnCreateAccount = new JButton("Tạo tài khoản");
        btnCreateAccount.setEnabled(false);
        btnCreateAccount.addActionListener(e -> createNewAccount());
        panel.add(btnCreateAccount, gbc);
        
        return panel;
    }
    
    private void connectToServer() {
        // Validate input
        String host = txtServerHost.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập địa chỉ server!\n\nVí dụ: localhost hoặc 192.168.1.100", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtServerHost.requestFocus();
            return;
        }
        
        // Show progress
        showProgress(true, "Đang kết nối đến " + host + "...");
        setControlsEnabled(false);
        
        // Connect in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String errorMessage = "";
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Registry registry = LocateRegistry.getRegistry(host, 1099);
                    bankService = (BankInterface) registry.lookup("BankService");
                    
                    // Test connection
                    bankService.getAllAccounts();
                    
                    return true;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                showProgress(false, "");
                
                try {
                    if (get()) {
                        // Success
                        isConnected = true;
                        lblStatus.setText("🟢 Đã kết nối: " + host);
                        lblStatus.setForeground(new Color(0, 150, 0));
                        
                        btnConnect.setEnabled(false);
                        btnDisconnect.setEnabled(true);
                        txtServerHost.setEnabled(false);
                        
                        btnTransfer.setEnabled(true);
                        btnRefresh.setEnabled(true);
                        btnCreateAccount.setEnabled(true);
                        
                        loadAccounts();
                        
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            "✓ Kết nối server thành công!\n\n" +
                            "Server: " + host + ":1099\n" +
                            "Service: BankService",
                            "Kết nối thành công", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Failure
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            "❌ Không thể kết nối đến server!\n\n" +
                            "Server: " + host + ":1099\n" +
                            "Lỗi: " + errorMessage + "\n\n" +
                            "Vui lòng kiểm tra:\n" +
                            "• Server đã chạy chưa?\n" +
                            "• Địa chỉ IP có đúng không?\n" +
                            "• Firewall đã mở port 1099 chưa?",
                            "Lỗi kết nối", 
                            JOptionPane.ERROR_MESSAGE);
                        
                        setControlsEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setControlsEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Ngắt kết nối khỏi server
     */
    private void disconnect() {
        if (!isConnected) {
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn ngắt kết nối khỏi server?",
            "Xác nhận ngắt kết nối",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            bankService = null;
            isConnected = false;
            
            lblStatus.setText("⚫ Chưa kết nối");
            lblStatus.setForeground(Color.RED);
            
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            txtServerHost.setEnabled(true);
            
            btnTransfer.setEnabled(false);
            btnRefresh.setEnabled(false);
            btnCreateAccount.setEnabled(false);
            
            // Clear data
            tableModel.setRowCount(0);
            cboFromAccount.removeAllItems();
            cboToAccount.removeAllItems();
            
            JOptionPane.showMessageDialog(this,
                "Đã ngắt kết nối khỏi server.",
                "Ngắt kết nối",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Hiển thị/ẩn progress bar
     */
    private void showProgress(boolean show, String message) {
        progressBar.setVisible(show);
        progressBar.setIndeterminate(show);
        progressBar.setString(message);
    }
    
    /**
     * Enable/Disable controls
     */
    private void setControlsEnabled(boolean enabled) {
        btnConnect.setEnabled(enabled);
        txtServerHost.setEnabled(enabled);
    }
    
    private void loadAccounts() {
        try {
            List<Account> accounts = bankService.getAllAccounts();
            
            // Update table
            tableModel.setRowCount(0);
            cboFromAccount.removeAllItems();
            cboToAccount.removeAllItems();
            
            for (Account account : accounts) {
                Object[] row = {
                    account.getAccountNumber(),
                    account.getAccountName(),
                    currencyFormat.format(account.getBalance())
                };
                tableModel.addRow(row);
                
                String displayText = String.format("%s - %s", 
                        account.getAccountNumber(), account.getAccountName());
                cboFromAccount.addItem(displayText);
                cboToAccount.addItem(displayText);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách tài khoản: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void performTransfer() {
        // Validate input
        if (cboFromAccount.getSelectedItem() == null || cboToAccount.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vui lòng chọn tài khoản nguồn và đích!", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (txtAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vui lòng nhập số tiền cần chuyển!", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtAmount.requestFocus();
            return;
        }
        
        String fromAccountText = (String) cboFromAccount.getSelectedItem();
        String toAccountText = (String) cboToAccount.getSelectedItem();
        
        String fromAccount = fromAccountText.split(" - ")[0];
        String toAccount = toAccountText.split(" - ")[0];
        
        if (fromAccount.equals(toAccount)) {
            JOptionPane.showMessageDialog(this, 
                "❌ Không thể chuyển khoản cho chính mình!", 
                "Lỗi giao dịch", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Số tiền phải lớn hơn 0!", 
                    "Lỗi nhập liệu", 
                    JOptionPane.ERROR_MESSAGE);
                txtAmount.requestFocus();
                return;
            }
            
            if (amount > 1000000000) { // 1 billion
                JOptionPane.showMessageDialog(this,
                    "❌ Số tiền quá lớn! Tối đa 1,000,000,000 VND",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Số tiền không hợp lệ!\n\nVui lòng nhập số (ví dụ: 1000000)", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtAmount.requestFocus();
            return;
        }
        
        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this, 
            String.format(
                "Xác nhận chuyển khoản:\n\n" +
                "Từ: %s\n" +
                "Đến: %s\n" +
                "Số tiền: %,.0f VND\n\n" +
                "Tiếp tục?",
                fromAccountText, toAccountText, amount
            ),
            "⚠️ Xác nhận giao dịch", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Show progress
        final double finalAmount = amount;
        final String finalFromAccount = fromAccount;
        final String finalToAccount = toAccount;
        
        showProgress(true, "Đang xử lý giao dịch...");
        btnTransfer.setEnabled(false);
        btnRefresh.setEnabled(false);
        
        // Perform transfer in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String errorMsg = "";
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return bankService.transfer(finalFromAccount, finalToAccount, finalAmount);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                showProgress(false, "");
                btnTransfer.setEnabled(true);
                btnRefresh.setEnabled(true);
                
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            String.format(
                                "✅ Chuyển khoản thành công!\n\n" +
                                "Số tiền: %,.0f VND\n" +
                                "Từ: %s\n" +
                                "Đến: %s\n\n" +
                                "✓ Đã cập nhật đồng bộ trên 2 database.",
                                finalAmount, finalFromAccount, finalToAccount
                            ),
                            "Giao dịch thành công", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        txtAmount.setText("");
                        loadAccounts();
                    } else {
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            "❌ Chuyển khoản thất bại!\n\n" +
                            "Nguyên nhân có thể:\n" +
                            "• Số dư không đủ\n" +
                            "• Tài khoản không tồn tại\n" +
                            "• Lỗi kết nối database\n\n" +
                            "Chi tiết: " + errorMsg,
                            "Giao dịch thất bại", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BankClientUI.this,
                        "❌ Lỗi: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void createNewAccount() {
        // Validate input
        String accountNumber = txtNewAccountNumber.getText().trim();
        String accountName = txtNewAccountName.getText().trim();
        String balanceText = txtNewBalance.getText().trim();
        
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vui lòng nhập số tài khoản!", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtNewAccountNumber.requestFocus();
            return;
        }
        
        if (accountName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vui lòng nhập tên chủ tài khoản!", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtNewAccountName.requestFocus();
            return;
        }
        
        if (balanceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vui lòng nhập số dư ban đầu!", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtNewBalance.requestFocus();
            return;
        }
        
        double balance;
        try {
            balance = Double.parseDouble(balanceText.replace(",", ""));
            
            if (balance < 0) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Số dư không thể âm!", 
                    "Lỗi nhập liệu", 
                    JOptionPane.ERROR_MESSAGE);
                txtNewBalance.requestFocus();
                return;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Số dư không hợp lệ!\n\nVui lòng nhập số (ví dụ: 1000000)", 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            txtNewBalance.requestFocus();
            return;
        }
        
        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format(
                "Xác nhận tạo tài khoản mới:\n\n" +
                "Số TK: %s\n" +
                "Tên: %s\n" +
                "Số dư: %,.0f VND\n\n" +
                "Tiếp tục?",
                accountNumber, accountName, balance
            ),
            "⚠️ Xác nhận tạo tài khoản",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Show progress
        final double finalBalance = balance;
        showProgress(true, "Đang tạo tài khoản...");
        btnCreateAccount.setEnabled(false);
        
        // Create account in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String errorMsg = "";
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return bankService.createAccount(accountNumber, accountName, finalBalance);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                showProgress(false, "");
                btnCreateAccount.setEnabled(true);
                
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            String.format(
                                "✅ Tạo tài khoản thành công!\n\n" +
                                "Số TK: %s\n" +
                                "Tên: %s\n" +
                                "Số dư: %,.0f VND\n\n" +
                                "✓ Đã cập nhật đồng bộ trên 2 database.",
                                accountNumber, accountName, finalBalance
                            ),
                            "Tạo tài khoản thành công", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        txtNewAccountNumber.setText("");
                        txtNewAccountName.setText("");
                        txtNewBalance.setText("0");
                        loadAccounts();
                    } else {
                        JOptionPane.showMessageDialog(BankClientUI.this, 
                            "❌ Tạo tài khoản thất bại!\n\n" +
                            "Nguyên nhân có thể:\n" +
                            "• Số tài khoản đã tồn tại\n" +
                            "• Lỗi kết nối database\n\n" +
                            "Chi tiết: " + errorMsg,
                            "Tạo tài khoản thất bại", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BankClientUI.this,
                        "❌ Lỗi: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankClientUI ui = new BankClientUI();
            ui.setVisible(true);
        });
    }
}
