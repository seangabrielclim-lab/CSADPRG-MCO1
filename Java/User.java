public class User {
    private String name;
    private double amount;
    private static double rate = 0.05f;
    private String currency;
    private long id;
    private String tier;
    private double adb;
    private long accountAge;

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

    public String getTier() {
        return tier;
    }

    public String getCurrency() {
        return currency;
    }

    public long getAccountAge() {
        return accountAge;
    }

    public long getId() {
        return id;
    }

    public double getAdb() {return adb;}

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setAdb(double adb) {this.adb = adb;}

    public void setAccountAge(long accountAge) {this.accountAge = accountAge;}
}
