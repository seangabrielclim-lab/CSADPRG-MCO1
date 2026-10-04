public class EUR extends Currency{
    public EUR(){
        super("EUR", 70.94, 1.13, 177.61, 0.86, 1, 7.60);
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
