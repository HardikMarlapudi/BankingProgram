package src.main.service;

import src.main.dto.LoginRequest;
import src.main.dto.LoginResponse;

public class AuthService {
    private final ApiClient api = new ApiClient();

    /** @return JWT token or null if invalid */
    public String login(String username, String password, int accountNumber) {
        LoginResponse resp = api.post("/auth/login", null,
                new LoginRequest(username, password, accountNumber), LoginResponse.class);
        return (resp != null) ? resp.token() : null;
    }
}
