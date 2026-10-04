public class User {
    private static int nextUserID = 0;
    private String name;
    private double amount;
    private static double rate = 0.05f;
    private String currency;
    private int id;

    public User(String name) {
        this.name = name;
        this.amount = 0;
        this.currency = "PHP";
        id = this.nextUserID++;
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

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
