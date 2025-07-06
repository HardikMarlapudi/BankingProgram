import java.util.Scanner;

public class BankingProgram {
    
        private String choice;
        private static int balance;
        private static int deposit;
        private static int withdrawl;
                    
        public BankingProgram(String choice, int balance, int deposit, int withdrawl) {
            this.choice = choice;
            this.balance = balance;
            this.deposit = deposit;
            this.withdrawl = withdrawl;
        }
        
        public void setChoice(String choice) {
            this.choice = choice;
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
        
            System.out.println("Welcome to your online banking app!!!");
            System.out.println("Enter your choice: Check Balance, Deposit, Withdrawl, or Exit");
                    
            String choice = scanner.nextLine();
        
        if(choice.equals("Check Balance")) {
            System.out.println("How much money would you like to add into your account?");
            scanner.nextLine();
            System.out.println("Your current balance is: " + balance);
        
        } else if (choice.equals("Deposit")) {
            System.out.println("How much money would like to deposit?");
            scanner.nextLine();
            System.out.println("You deposited $ " + deposit);
        } else if (choice.equals("Withdrawl")) {
            System.out.println("How much money would you like to withdrawl?");
            scanner.nextLine();
            System.out.println("You withdrew $ " + withdrawl);
        } else {
            System.out.println("Thank you for using our online banking app!!");
            System.exit(0);
        }
    }
}
