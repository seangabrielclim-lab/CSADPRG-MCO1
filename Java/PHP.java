public class PHP extends Currency{
    public PHP(){
        super("PHP", 1f, 0.016f, 2.50f, 0.012f, 0.014f, 0.11f);
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
