import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    private String accountName;
    private long accountNumber;
    private LocalDate date;
    private LocalTime time;
    private String currency;
    private double amount;
    private String type;
    private double currentBalance;

    public Transaction(String name, long accountNumber, LocalDate date, LocalTime time, String currency, double amount, String type, double currentBalance) {
        this.accountName = name;
        this.accountNumber = accountNumber;
        this.date = date;
        this.time = time;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.currentBalance = currentBalance;
    }

    public String getAccountName() {
        return accountName;
    }
    public long getAccountNumber() {
        return accountNumber;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getTime() {
        return time;
    }
    public String getCurrency() {
        return currency;
    }
    public double getAmount() {
        return amount;
    }
    public String getType() {
        return type;
    }
    public double getCurrentBalance() {
        return currentBalance;
    }
}
