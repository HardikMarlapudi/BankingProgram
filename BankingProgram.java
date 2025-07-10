import java.util.Scanner;

public class BankingProgram {
    
        private static int total;
        private static int balance;
        private static int inputNum;
        private static int deposit;
        private static int withdrawl;
                    
        public BankingProgram(int total, int balance, int deposit, int withdrawl) {
            this.total = total;
            this.balance = balance;
            this.deposit = deposit;
            this.withdrawl = withdrawl;
        }

        public int inputNum() {
            return inputNum;
        }

        public void total() {
            total = balance + inputNum;
        }

        public int getBalance() {
            return balance;
        }
                    
        public int deposit() {
            return deposit;
        }
                        
        public int withdrawl() {
            return withdrawl;
        }
                    
        public static void main(String[] args) {
                    
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i <= 10; i++) {
            balance = 0; // The balance starts of with $0 by default
        
            System.out.println("Welcome to your online banking app!!!");
            System.out.println("Enter your choice: Check Balance, Deposit, Withdrawl, or exit");
                    
            String choice = scanner.nextLine();
        
        if (choice.equalsIgnoreCase("Check Balance")) {
            System.out.println("How much money would you like to enter in your account? ");
            scanner.nextLine();
            System.out.println("Your current balance is: " + total);
        } else if (choice.equalsIgnoreCase("Deposit")) {
            System.out.println("How much money would like to deposit?");
            deposit = Integer.parseInt(scanner.nextLine());
            balance += deposit;
            System.out.println("You deposited $" + deposit);
            System.out.println(balance);
        } else if (choice.equalsIgnoreCase("Withdrawl")) {
            System.out.println("How much money would you like to withdrawl? ");
            withdrawl = Integer.parseInt(scanner.nextLine());
            if (withdrawl <= balance) {
            balance -= withdrawl;
            scanner.nextLine();
            System.out.println("You withdrew $" + withdrawl);
            System.out.println(balance);
            } else {
                System.out.println("Insufficient funds!");
            }
        } else if (choice.equals("exit")) {
            System.out.println("Thank you for using our app!");
            System.exit(0);
        } else {
            System.out.println("Invalid Choice!!!");
            }
        }
    }
}
