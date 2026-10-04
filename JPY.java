public class JPY extends Currency{
    public JPY(){
        super("JPY", 0.40, 0.0064, 1, 0.0048, 0.0056, 0.043);
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
