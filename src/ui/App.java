package src.main.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    // --- Simple in-memory model (replace with your services later) ---
    static class BankAccount {
        private final String username;
        private final int accountNumber;
        private int balance = 0;

        BankAccount(String username, int accountNumber) {
            this.username = username;
            this.accountNumber = accountNumber;
        }
        public String getUsername() { return username; }
        public int getAccountNumber() { return accountNumber; }
        public int getBalance() { return balance; }
        public void deposit(int amount) { balance += amount; }
        public boolean withdraw(int amount) {
            if (amount <= balance) { balance -= amount; return true; }
            return false;
        }
    }

    private Stage stage;                 // primary window
    private BankAccount sessionAccount;  // "logged in" account

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Banking GUI");
        stage.setScene(buildLoginScene());  // start on Login
        stage.show();
    }

    // ---------------------- LOGIN SCENE ----------------------
    private Scene buildLoginScene() {
        Label title = new Label("Welcome to Your Bank");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcome = new Label("Welcome, " + sessionAccount.getUsername());
        welcome.getStyleClass().add("title-label");

        TextField userField = new TextField();
        userField.setPromptText("Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        TextField acctField = new TextField();
        acctField.setPromptText("Account Number");

        Label error = new Label();
        error.setStyle("-fx-text-fill: #c0392b;");

        Button loginBtn = new Button("Log in");
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            error.setText("");

            String u = userField.getText().trim();
            String p = passField.getText(); // not used in demo
            String a = acctField.getText().trim();

            if (u.isEmpty() || p.isEmpty() || a.isEmpty()) {
                error.setText("Please fill in all fields.");
                return;
            }
            int acctNum;
            try {
                acctNum = Integer.parseInt(a);
            } catch (NumberFormatException ex) {
                error.setText("Account number must be numeric.");
                return;
            }

            // Simulate authentication; in real app, call AuthService.login(...)
            sessionAccount = new BankAccount(u, acctNum);
            stage.setScene(buildDashboardScene());
        });

        VBox root = new VBox(12, title, userField, passField, acctField, loginBtn, error);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(420);
        root.setPrefHeight(360);

        return new Scene(root);
    }

    // -------------------- DASHBOARD SCENE --------------------
    private Scene buildDashboardScene() {
        // Header
        Label welcome = new Label("Welcome, " + sessionAccount.getUsername());
        welcome.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Balance row
        Label balanceLabel = new Label("$" + sessionAccount.getBalance());
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> balanceLabel.setText("$" + sessionAccount.getBalance()));

        HBox balanceRow = new HBox(10, new Label("Current Balance:"), balanceLabel, refreshBtn);
        balanceRow.setAlignment(Pos.CENTER_LEFT);

        // Deposit
        TextField depositField = new TextField();
        depositField.setPromptText("Amount");
        Button depositBtn = new Button("Deposit");
        depositBtn.setOnAction(e -> {
            String txt = depositField.getText().trim();
            Integer amt = parsePositiveInt(txt);
            if (amt == null) {
                showToast("Enter a valid, positive deposit amount.");
                return;
            }
            sessionAccount.deposit(amt);
            depositField.clear();
            balanceLabel.setText("$" + sessionAccount.getBalance());
            showToast("Deposited $" + amt);
        });
        HBox depositRow = new HBox(10, new Label("Deposit:"), depositField, depositBtn);
        depositRow.setAlignment(Pos.CENTER_LEFT);

        // Withdraw
        TextField withdrawField = new TextField();
        withdrawField.setPromptText("Amount");
        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setOnAction(e -> {
            String txt = withdrawField.getText().trim();
            Integer amt = parsePositiveInt(txt);
            if (amt == null) {
                showToast("Enter a valid, positive withdraw amount.");
                return;
            }
            if (sessionAccount.withdraw(amt)) {
                withdrawField.clear();
                balanceLabel.setText("$" + sessionAccount.getBalance());
                showToast("Withdrew $" + amt);
            } else {
                showToast("Insufficient funds.");
            }
        });
        HBox withdrawRow = new HBox(10, new Label("Withdraw:"), withdrawField, withdrawBtn);
        withdrawRow.setAlignment(Pos.CENTER_LEFT);

        // Footer buttons
        Button logoutBtn = new Button("Log out");
        logoutBtn.setOnAction(e -> {
            sessionAccount = null;
            stage.setScene(buildLoginScene());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(10, spacer, logoutBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);

        // Status bar
        Label status = new Label("Ready.");
        status.setStyle("-fx-text-fill: #2c3e50;");
        BorderPane statusBar = new BorderPane();
        statusBar.setLeft(status);
        statusBar.setPadding(new Insets(8, 0, 0, 0));

        // Wrap everything
        VBox content = new VBox(14, welcome, new Separator(), balanceRow, depositRow, withdrawRow, new Separator(), footer);
        content.setPadding(new Insets(16));
        content.setFillWidth(true);

        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.setBottom(statusBar);
        root.setPadding(new Insets(8, 16, 12, 16));

        // Tiny helper to update status text
        this.toastTarget = status;

        Scene scene = new Scene(root, 560, 380);
        // Optional: basic styling
        scene.getStylesheets().add(inlineBaseCss());
        return scene;
    }

    // ------------------ Utilities & styling ------------------
    private Label toastTarget;

    private void showToast(String message) {
        if (toastTarget != null) toastTarget.setText(message);
    }

    private Integer parsePositiveInt(String s) {
        try {
            int v = Integer.parseInt(s);
            return (v > 0) ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Small inline stylesheet (you can move to styles.css later) */
    private String inlineBaseCss() {
        // Creates a data: URL so we don't need an external CSS file yet.
        String css = """
            .root { -fx-font-family: "Inter", "Segoe UI", Arial; }
            .label { -fx-font-size: 13px; }
            .button { -fx-cursor: hand; }
        """;
        return "data:text/css," + css.replace("\n", "%0A").replace(" ", "%20").replace("\"", "%22");
    }

    public static void main(String[] args) { launch(args); }
}
