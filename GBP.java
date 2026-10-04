public class GBP extends Currency{
    public GBP(){
        super("GBP", 82.80, 1.32, 207.43, 1, 1.17, 8.87);
    }

    @Override
    public double convertToPHP(double amount){
        return super.getPHP()*amount;
    }
    @Override
    public double convertToUSD(double amount){
        return super.getUSD()*amount;
    }
    @Override
    public double convertToJPY(double amount){
        return super.getJPY()*amount;
    }
    @Override
    public double convertToGBP(double amount){
        return super.getGBP()*amount;
    }
    @Override
    public double convertToEUR(double amount){
        return super.getEUR()*amount;
    }
    @Override
    public double convertToCNY(double amount){
        return super.getCNY()*amount;
    }
}
