package rmi;

import java.io.Serializable;

/**
 * Class đại diện cho tài khoản ngân hàng
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String accountNumber;
    private String accountName;
    private double balance;
    
    public Account() {
    }
    
    public Account(String accountNumber, String accountName, double balance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s: %.2f VND", accountNumber, accountName, balance);
    }
}
