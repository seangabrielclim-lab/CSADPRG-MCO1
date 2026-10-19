class ExchangeRateSet {
    
    private val rates = doubleArrayOf(1.0, 62.0, 0.4, 84.0, 72.0, 9.0)

    fun setRate(currencyNum: Int, rate: Double){
        rates[currencyNum-1] = rate
    }
    fun convertCurrency(sourceCurr: Int, targetCurr: Int, amount: Double): Double {
        return (amount * rates[sourceCurr-1]) / rates[targetCurr-1]
    }

}