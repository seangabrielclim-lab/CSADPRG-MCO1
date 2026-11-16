public class User {
    private String name;
    private double amount;
    private static double rate = 0.05f;
    private String currency;
    private long id;

    public User(String name, double amount, long id) {
        this.name = name;
        this.amount = amount;
        this.currency = "PHP";
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public String getCurrency() {
        return currency;
    }

    public long getId() {
        return id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
