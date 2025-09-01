package src.main;

import java.util.Scanner;

public class BankingProgram {

    private String username;
    private String password;
    private int accountNumber;
    private int balance;

    public BankingProgram(String username, String password, int accountNumber) {
        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
        this.balance = 0; // Starts with $0 by default
    }

    public BankingProgram(int accountNumber, String password, String username) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.username = username;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    // How much money the user would like to withdrawl from their bank account

    public boolean withdraw(int amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    private static String USERS_FILE = "resources/jsonFolder/bankinginfo.json";

    public static String getUSERS_FILE() {
        return USERS_FILE;
    }

    public static void main(String[] args) {
        Scanner key = new Scanner(System.in);

        System.out.println("Welcome to your online banking app!");

        System.out.print("Enter your username: ");
        String username = key.nextLine();

        System.out.print("Enter your password: ");
        String password = key.nextLine();

        System.out.print("Enter your account number: ");
        int accountNumber = Integer.parseInt(key.nextLine());

        BankingProgram user = new BankingProgram(username, password, accountNumber);

        while (true) {
            System.out.println("\nEnter your choice: Check Balance, Deposit, Withdraw, or Exit");
            String choice = key.nextLine();

            switch (choice.toLowerCase()) {
                case "check balance":
                    System.out.println("Your balance is: $" + user.getBalance());
                    break;

                case "deposit":
                    System.out.print("Enter the amount you want to deposit: ");
                    int depositAmount = Integer.parseInt(key.nextLine());
                    user.deposit(depositAmount);
                    System.out.println("Your new balance is: $" + user.getBalance());
                    break;

                case "withdraw":
                    System.out.print("Enter the amount you want to withdraw: ");
                    int withdrawAmount = Integer.parseInt(key.nextLine());
                    if (user.withdraw(withdrawAmount)) {
                        System.out.println("Your new balance is: $" + user.getBalance());
                    } else {
                        System.out.println("Insufficient funds. Your balance is: $" + user.getBalance());
                    }
                    break;

                case "exit":
                    System.out.println("Thank you for using our banking app!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }
}
