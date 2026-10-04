public abstract class Currency {
    private String name;
    private double inPHP;
    private double inUSD;
    private double inJPY;
    private double inGBP;
    private double inEUR;
    private double inCNY;

    public Currency(String name, double p, double u, double j, double g, double e, double c) {
        this.name = name;
        this.inPHP = p;
        this.inUSD = u;
        this.inJPY = j;
        this.inGBP = g;
        this.inEUR = e;
        this.inCNY = c;
    }

    public double getPHP(){
        return this.inPHP;
    }
    public double getUSD(){
        return this.inUSD;
    }
    public double getJPY(){
        return this.inJPY;
    }
    public double getGBP(){
        return this.inGBP;
    }
    public double getEUR(){
        return this.inEUR;
    }
    public double getCNY(){
        return this.inCNY;
    }

    public abstract double convertToPHP(double amount);
    public abstract double convertToUSD(double amount);
    public abstract double convertToJPY(double amount);
    public abstract double convertToGBP(double amount);
    public abstract double convertToEUR(double amount);
    public abstract double convertToCNY(double amount);
}
