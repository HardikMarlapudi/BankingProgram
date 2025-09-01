package src.main.service;

import src.main.dto.BalanceResponse;
import src.main.dto.TransactionRequest;

public class BankService {
    private final ApiClient api = new ApiClient();

    public Integer getBalance(String token, int accountNumber) {
        BalanceResponse resp = api.get("/accounts/" + accountNumber + "/balance", token, BalanceResponse.class);
        return (resp != null) ? resp.balance() : null;
    }

    public void deposit(String token, int accountNumber, int amount) {
        api.post("/accounts/" + accountNumber + "/deposit", token,
                new TransactionRequest(amount), Void.class);
    }

    public void withdraw(String token, int accountNumber, int amount) {
        api.post("/accounts/" + accountNumber + "/withdraw", token,
                new TransactionRequest(amount), Void.class);
    }
}
