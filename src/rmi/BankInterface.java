package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Interface định nghĩa các phương thức từ xa cho ứng dụng ngân hàng
 */
public interface BankInterface extends Remote {
    
    /**
     * Chuyển khoản giữa 2 tài khoản
     * @param fromAccount Tài khoản nguồn
     * @param toAccount Tài khoản đích
     * @param amount Số tiền chuyển
     * @return true nếu chuyển thành công, false nếu thất bại
     * @throws RemoteException
     */
    boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException;
    
    /**
     * Lấy số dư tài khoản
     * @param accountNumber Số tài khoản
     * @return Số dư hiện tại
     * @throws RemoteException
     */
    double getBalance(String accountNumber) throws RemoteException;
    
    /**
     * Lấy danh sách tất cả tài khoản
     * @return Danh sách tài khoản
     * @throws RemoteException
     */
    List<Account> getAllAccounts() throws RemoteException;
    
    /**
     * Tạo tài khoản mới
     * @param accountNumber Số tài khoản
     * @param accountName Tên chủ tài khoản
     * @param balance Số dư ban đầu
     * @return true nếu tạo thành công
     * @throws RemoteException
     */
    boolean createAccount(String accountNumber, String accountName, double balance) throws RemoteException;
}
