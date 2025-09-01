package src.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import src.main.service.BankService;

import java.util.concurrent.CompletableFuture;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label balanceLevel;
    @FXML private TextField depositField;
    @FXML private TextField withdrawField;
    @FXML private Label statusLabel;

    private final BankService bankService = new BankService();
    private String token;
    private int accountNumber;
    private String username;

    public void init(String username, int accountNumber, String token) {
        this.username = username;
        this.accountNumber = accountNumber;
        this.token = token;
        welcomeLabel.setText("Welcome, " + username + "!");
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        CompletableFuture.runAsync(() -> {
            try {
                double balance = bankService.getBalance(accountNumber, token);
                Platform.runLater(() -> balanceLevel.setText("Balance: $" + balance));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Failed to fetch balance: " + e.getMessage()));
            }
        });
    }

    @FXML
    public void onDeposit() {
        String amt = depositField.getText().trim();
        if (amt.isEmpty()) { statusLabel.setText("Enter deposit amount."); return; }
        int amount;
        try { amount = Integer.parseInt(amt); } catch (NumberFormatException e) { statusLabel.setText("Amount must be numeric."); return; }
        statusLabel.setText("Depositing...");
        CompletableFuture
            .runAsync(() -> bankService.deposit(token, accountNumber, amount))
            .thenRun(() -> Platform.runLater(() -> { depositField.clear(); onRefresh(); }))
            .exceptionally(ex -> { Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage())); return null;});
    }

    @FXML
    public void onWithdraw() {
        String amt = withdrawField.getText().trim();
        if (amt.isEmpty()) { statusLabel.setText("Enter withdraw amount."); return; }
        int amount;
        try { amount = Integer.parseInt(amt); } catch (NumberFormatException e) { statusLabel.setText("Amount must be numeric."); return; }
        statusLabel.setText("Withdrawing...");
        CompletableFuture
            .runAsync(() -> bankService.withdraw(token, accountNumber, amount))
            .thenRun(() -> Platform.runLater(() -> { withdrawField.clear(); onRefresh(); }))
            .exceptionally(ex -> { Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage())); return null;});
    }
}
