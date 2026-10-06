/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: Kotlin
Paradigm(s): Functional 
********************
 */

fun main(){
    // Main menu
    println("Select Transaction:")
    println("[1] Register Account Name")
    println("[2] Deposit Amount")
    println("[3] Withdraw Amount")
    println("[4] Currency Exchange")
    println("[5] Record Exchange Rates")
    println("[6] Show Interest Amount")
    println()
    print("Choice: ")
   
    val choice = readln().toInt()
    println()
    println("***")
    println("Choice = $choice")
    
    // Register Account Name
    println()
    println("Register Account Name")
    print("Account Name: ")
    
    val accountname = readln()
    println()
    println("***")
    println("Account Name = $accountname")
    
    // Deposit Amount
    
    println("\nDeposit Amount")
    print("Account Name: ")
    val depaccountname = readln()
    println("Current Balance: 1000.00")
    println("Currency: PHP\n")
    
    print("Deposit Amount: ")
    val depamt = readln().toDouble()
    println("\n***")
    println("Account Name = $depaccountname")
    println("Deposit Amount = %.2f".format(depamt))
    
    
    // Withdraw Amount
    
    println("\nWithdraw Amount")
    print("Account Name: ")
    val withdrawaccountname = readln()
    println("Current Balance: 1000.00")
    println("Currency: PHP\n")
    
    print("Withdraw Amount: ")
    val withdrawamt = readln().toDouble()
    println("\n***")
    println("Account Name = $withdrawaccountname")
    println("Withdraw Amount = %.2f".format(withdrawamt))
    
    // Currency Exchange
    
    println("\nForeign Currency Exchange")
    print("Source Amount (PHP): ")
    val sourceamt = readln().toDouble()
    
    println("\nExchanged Currency")
    println("[1] Philippine Peso (PHP) = %.2f".format(sourceamt))
    println("[2] United States Dollar (USD) = %.2f".format(sourceamt * 62.00))
    println("[3] Japanese Yen (JPY) = %.2f".format(sourceamt * 0.40))
    println("[4] British Pound Sterling (GBP) = %.2f".format(sourceamt * 84.00))
    println("[5] Euro (EUR) = %.2f".format(sourceamt * 72.00))
    println("[6] Chinese Yuan Renminni (CNY) = %.2f".format(sourceamt * 9.00))
    
    println("\n***")
    println("Source Currency = Philippine Peso (PHP)")
    println("Source Amount (PHP): = %.2f".format(sourceamt))
    
    // Record Exchange Rate
    
    println("\nRecord Exchange Rate\n")
    
    println("[1] Philippine Peso (PHP)")
    println("[2] United States Dollar (USD)")
    println("[3] Japanese Yen (JPY)")
    println("[4] British Pound Sterling (GBP)")
    println("[5] Euro (EUR)")
    println("[6] Chinese Yuan Renminni (CNY)")
    
    print("\nSelect Foreign Currency: ")
    val selectedcurr = readln()
    print("Exchange Rate: ")
    val exchangerate = readln().toDouble()
    println("\n***")
    println("Select Foreign Currency = $selectedcurr")
    println("Exchange Rate = %.2f".format(exchangerate))
    
    
    
}