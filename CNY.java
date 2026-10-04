public class CNY extends Currency{
    public CNY(){
        super("CNY", 9.34, 0.15, 23.38, 0.11, 0.13, 1);
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
