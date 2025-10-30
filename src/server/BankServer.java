package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import database.DatabaseHandler;
import rmi.Account;
import rmi.BankInterface;

/**
 * RMI Server - Xử lý các yêu cầu từ client
 */
public class BankServer extends UnicastRemoteObject implements BankInterface {
    
    private static final long serialVersionUID = 1L;
    private DatabaseHandler dbHandler;
    
    protected BankServer() throws RemoteException {
        super();
        dbHandler = new DatabaseHandler();
    }
    
    @Override
    public boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException {
        System.out.println(String.format("Nhận yêu cầu chuyển khoản: %s -> %s, số tiền: %.2f", 
                fromAccount, toAccount, amount));
        return dbHandler.transfer(fromAccount, toAccount, amount);
    }
    
    @Override
    public double getBalance(String accountNumber) throws RemoteException {
        return dbHandler.getBalance(accountNumber);
    }
    
    @Override
    public List<Account> getAllAccounts() throws RemoteException {
        return dbHandler.getAllAccounts();
    }
    
    @Override
    public boolean createAccount(String accountNumber, String accountName, double balance) throws RemoteException {
        System.out.println(String.format("Tạo tài khoản mới: %s - %s", accountNumber, accountName));
        return dbHandler.createAccount(accountNumber, accountName, balance);
    }
    
    public static void main(String[] args) {
        try {
            // Tạo RMI Registry trên port 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Tạo đối tượng server
            BankServer server = new BankServer();
            
            // Đăng ký server với tên "BankService"
            registry.rebind("BankService", server);
            
            System.out.println("=================================");
            System.out.println("RMI Bank Server đã khởi động!");
            System.out.println("Server đang lắng nghe trên port 1099");
            System.out.println("Service name: BankService");
            System.out.println("=================================");
            
        } catch (Exception e) {
            System.err.println("Lỗi khởi động server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
