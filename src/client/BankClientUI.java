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
 * Giao diện UI cho ứng dụng ngân hàng RMI
 */
public class BankClientUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private BankInterface bankService;
    
    // Components
    private JTextField txtServerHost;
    private JButton btnConnect;
    private JLabel lblStatus;
    
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
    }
    
    private void initComponents() {
        setTitle("Hệ thống chuyển khoản RMI");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Kết nối Server"));
        
        panel.add(new JLabel("Server Host:"));
        txtServerHost = new JTextField("localhost", 15);
        panel.add(txtServerHost);
        
        btnConnect = new JButton("Kết nối");
        btnConnect.addActionListener(e -> connectToServer());
        panel.add(btnConnect);
        
        lblStatus = new JLabel("Chưa kết nối");
        lblStatus.setForeground(Color.RED);
        panel.add(lblStatus);
        
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
        try {
            String host = txtServerHost.getText().trim();
            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ server!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            bankService = (BankInterface) registry.lookup("BankService");
            
            lblStatus.setText("Đã kết nối");
            lblStatus.setForeground(new Color(0, 150, 0));
            btnConnect.setEnabled(false);
            btnTransfer.setEnabled(true);
            btnRefresh.setEnabled(true);
            btnCreateAccount.setEnabled(true);
            
            loadAccounts();
            
            JOptionPane.showMessageDialog(this, "Kết nối server thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối server: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
        try {
            // Validate input
            if (cboFromAccount.getSelectedItem() == null || cboToAccount.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (txtAmount.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String fromAccountText = (String) cboFromAccount.getSelectedItem();
            String toAccountText = (String) cboToAccount.getSelectedItem();
            
            String fromAccount = fromAccountText.split(" - ")[0];
            String toAccount = toAccountText.split(" - ")[0];
            
            if (fromAccount.equals(toAccount)) {
                JOptionPane.showMessageDialog(this, "Không thể chuyển cho chính mình!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(txtAmount.getText().trim());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirm
            int confirm = JOptionPane.showConfirmDialog(this, 
                    String.format("Xác nhận chuyển %.2f VND từ %s đến %s?", 
                            amount, fromAccountText, toAccountText),
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Perform transfer
            boolean success = bankService.transfer(fromAccount, toAccount, amount);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Chuyển khoản thành công!\nĐã cập nhật đồng bộ trên 2 database.", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtAmount.setText("");
                loadAccounts();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Chuyển khoản thất bại! Có thể do số dư không đủ.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void createNewAccount() {
        try {
            String accountNumber = txtNewAccountNumber.getText().trim();
            String accountName = txtNewAccountName.getText().trim();
            String balanceText = txtNewBalance.getText().trim();
            
            if (accountNumber.isEmpty() || accountName.isEmpty() || balanceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double balance = Double.parseDouble(balanceText);
            
            if (balance < 0) {
                JOptionPane.showMessageDialog(this, "Số dư không thể âm!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = bankService.createAccount(accountNumber, accountName, balance);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Tạo tài khoản thành công!\nĐã cập nhật đồng bộ trên 2 database.", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtNewAccountNumber.setText("");
                txtNewAccountName.setText("");
                txtNewBalance.setText("0");
                loadAccounts();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số dư không hợp lệ!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankClientUI ui = new BankClientUI();
            ui.setVisible(true);
        });
    }
}
