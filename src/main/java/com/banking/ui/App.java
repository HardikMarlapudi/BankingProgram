package com.banking.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    // ---------- Simple domain model ----------
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
        public void setBalance(int balance) { this.balance = balance; } // needed to restore from JSON
        public void deposit(int amount) { balance += amount; }
        public boolean withdraw(int amount) {
            if (amount <= balance) { balance -= amount; return true; }
            return false;
        }
    }

    // ---------- JSON DTO ----------
    static class AccountRecord {
        public String username;
        public int accountNumber;
        public int balance;

        public AccountRecord() {} // Jackson needs it
        public AccountRecord(String username, int accountNumber, int balance) {
            this.username = username;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }
    }

    // ---------- Persistence helper (JSON at ~/.banking/bankinginfo.json) ----------
    static class AccountStore {
        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final Path DIR   = Paths.get(System.getProperty("user.home"), ".banking");
        private static final Path FILE  = DIR.resolve("bankinginfo.json");

        private static List<AccountRecord> readAll() {
            try {
                if (Files.notExists(FILE)) return new ArrayList<>();
                return MAPPER.readValue(Files.readAllBytes(FILE),
                        new TypeReference<List<AccountRecord>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        private static void writeAll(List<AccountRecord> records) {
            try {
                if (Files.notExists(DIR)) Files.createDirectories(DIR);
                byte[] json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(records);
                Files.write(FILE, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static AccountRecord findByAccount(int accountNumber) {
            return readAll().stream()
                    .filter(r -> r.accountNumber == accountNumber)
                    .findFirst().orElse(null);
        }

        static void upsert(AccountRecord rec) {
            List<AccountRecord> all = readAll();
            all.removeIf(r -> r.accountNumber == rec.accountNumber);
            all.add(rec);
            writeAll(all);
        }
    }

    private Stage stage;
    private BankAccount session;
    private Label statusBar;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Banking GUI");
        stage.setScene(buildLoginScene());
        stage.show();
    }

    // --------------------------- LOGIN ---------------------------
    private Scene buildLoginScene() {
        Label title = new Label("Welcome to Your Bank");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(260);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(260);

        TextField acctField = new TextField();
        acctField.setPromptText("Account Number");
        acctField.setMaxWidth(260);

        Label error = new Label();
        error.setStyle("-fx-text-fill: #c0392b;");

        Button loginBtn = new Button("Log in");
        loginBtn.setDefaultButton(true);
        loginBtn.setMaxWidth(260);

        loginBtn.setOnAction(e -> {
            error.setText("");
            String u = userField.getText().trim();
            String p = passField.getText();            // not used here (demo)
            String a = acctField.getText().trim();
            if (u.isEmpty() || p.isEmpty() || a.isEmpty()) { error.setText("Please fill in all fields."); return; }

            int acctNum;
            try { acctNum = Integer.parseInt(a); }
            catch (NumberFormatException ex) { error.setText("Account number must be numeric."); return; }

            // Create session and restore balance if account exists
            session = new BankAccount(u, acctNum);
            AccountRecord existing = AccountStore.findByAccount(acctNum);
            if (existing != null) {
                // If username differs, keep the stored username (or enforce match—up to you)
                if (!Objects.equals(existing.username, u)) {
                    // choose policy: we will prefer stored username
                    session = new BankAccount(existing.username, acctNum);
                }
                session.setBalance(existing.balance);
            }

            stage.setScene(buildDashboardScene());
        });

        VBox form = new VBox(12, title, userField, passField, acctField, loginBtn, error);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setCenter(form);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 560, 420);
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/css/app.css")).toExternalForm()
        );
        return scene;
    }

    // ------------------------ DASHBOARD -------------------------
    private Scene buildDashboardScene() {
        Label welcome = new Label("Welcome, " + session.getUsername());
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label balanceValue = new Label("$" + session.getBalance());
        balanceValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> balanceValue.setText("$" + session.getBalance()));

        HBox balanceRow = new HBox(10, new Label("Current Balance:"), balanceValue, refresh);
        balanceRow.setAlignment(Pos.CENTER_LEFT);

        TextField depositField = new TextField();
        depositField.setPromptText("Amount");
        Button depositBtn = new Button("Deposit");
        depositBtn.setOnAction(e -> {
            Integer amt = parsePositiveInt(depositField.getText());
            if (amt == null) { setStatus("Enter a positive deposit amount."); return; }
            session.deposit(amt);
            depositField.clear();
            balanceValue.setText("$" + session.getBalance());
            persistSession(); // <-- save to JSON
            setStatus("Deposited $" + amt);
        });

        HBox depositRow = new HBox(10, new Label("Deposit:"), depositField, depositBtn);
        depositRow.setAlignment(Pos.CENTER_LEFT);

        TextField withdrawField = new TextField();
        withdrawField.setPromptText("Amount");
        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setOnAction(e -> {
            Integer amt = parsePositiveInt(withdrawField.getText());
            if (amt == null) { setStatus("Enter a positive withdraw amount."); return; }
            if (session.withdraw(amt)) {
                withdrawField.clear();
                balanceValue.setText("$" + session.getBalance());
                persistSession(); // <-- save to JSON
                setStatus("Withdrew $" + amt);
            } else setStatus("Insufficient funds.");
        });

        HBox withdrawRow = new HBox(10, new Label("Withdraw:"), withdrawField, withdrawBtn);
        withdrawRow.setAlignment(Pos.CENTER_LEFT);

        Button logout = new Button("Log out");
        logout.setOnAction(e -> {
            persistSession();   // <-- save when leaving
            session = null;
            stage.setScene(buildLoginScene());
        });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(10, spacer, logout);
        footer.setAlignment(Pos.CENTER_RIGHT);

        statusBar = new Label("Ready.");
        statusBar.setStyle("-fx-text-fill:#2c3e50;");
        BorderPane bottom = new BorderPane();
        bottom.setLeft(statusBar);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        VBox content = new VBox(14, welcome, new Separator(), balanceRow,
                depositRow, withdrawRow, new Separator(), footer);
        content.setPadding(new Insets(16));
        content.setFillWidth(true);

        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 680, 440);
        scene.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm());
        return scene;
    }
    // ----------------------- Helpers -----------------------
    private void persistSession() {
        if (session == null) return;
        AccountStore.upsert(new AccountRecord(
                session.getUsername(),
                session.getAccountNumber(),
                session.getBalance()
        ));
    }

    private Integer parsePositiveInt(String s) {
        try { int v = Integer.parseInt(s.trim()); return v > 0 ? v : null; }
        catch (Exception e) { return null; }
    }

    private void setStatus(String msg) {
        if (statusBar != null) statusBar.setText(msg);
    }

    // inline CSS so no external files required
    private String inlineCss() {
        String css = """
            .root { -fx-font-family: "Inter","Segoe UI",Arial; }
            .text-field, .password-field { -fx-pref-width: 260; }
            .button { -fx-cursor: hand; }
            .separator { -fx-padding: 6 0 6 0; }
        """;
        return "data:text/css," + css.replace("\n","%0A").replace(" ","%20").replace("\"","%22");
    }

    public static void main(String[] args) { launch(args); }
}
