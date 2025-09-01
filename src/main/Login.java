import java.util.Scanner;

public class Login {

    public static String getBANKING_INFO() {
        return BANKING_INFO;
    }

    public static void setBANKING_INFO(String BANKING_INFO) {
        Login.BANKING_INFO = BANKING_INFO;
    }
    
    private String username;
    private String password;

    private static String BANKING_INFO = "resources/jsonFolder/bankinginfo.json";

    public static void main(String[] args) {
        Scanner key = new Scanner(System.in);

        System.out.println("Welcome to your online banking app!");

        System.out.print("Enter your username: ");
        String username = key.nextLine();

        System.out.print("Enter your password: ");
        String password = key.nextLine();

        System.out.print("Enter your account number: ");
        int accountNumber = key.nextInt();

        Login user = null;
        try {
            System.out.println("User successfully saved!!!");
        } catch (Exception e) {
            System.out.println("Sorry, cannot create a user!");
            return;
        }
        System.out.println("Welcome, " + user.getusername() + "!");
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
