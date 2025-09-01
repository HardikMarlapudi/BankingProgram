package src.main.dto;

public record BalanceResponse(int balance) {
    public BalanceResponse(String balance) {
        this(Integer.parseInt(balance));
    }
}
