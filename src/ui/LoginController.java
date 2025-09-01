package src.main.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import src.main.service.AuthService;

import java.util.concurrent.CompletableFuture;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField accountField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void onLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String acct = accountField.getText().trim();

        errorLabel.setText("");
        if (user.isEmpty() || pass.isEmpty() || acct.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        int accountNumber;
        try { accountNumber = Integer.parseInt(acct); }
        catch (NumberFormatException e) { errorLabel.setText("Account number must be numeric."); return; }

        // Avoid blocking UI
        CompletableFuture
            .supplyAsync(() -> authService.login(user, pass, accountNumber))
            .thenAccept(token -> {
                if (token == null) {
                    Platform.runLater(() -> errorLabel.setText("Invalid credentials."));
                } else {
                    Platform.runLater(() -> openDashboard(user, accountNumber, token));
                }
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> errorLabel.setText("Login failed: " + ex.getMessage()));
                return null;
            });
    }

    private void openDashboard(String username, int accountNumber, String jwtToken) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/ui/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 520, 520);
            DashboardController controller = loader.getController();
            controller.init(username, accountNumber, jwtToken);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            errorLabel.setText("Unable to open dashboard: " + e.getMessage());
        }
    }
}
