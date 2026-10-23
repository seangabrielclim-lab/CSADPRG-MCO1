// ********************
// Last names: Chan, Cheng, Jugno, Lim
// Language: R
// Paradigm(s): Imperative, Object-Oriented
// ********************

// Compile command: kotlinc Main.kt User.kt UserDB.kt ExchangeRateSet.kt -include-runtime -d Main.jar
// Run command:     java -jar Main.jar

fun printMainMenu(){
    println("Select Transaction:")
    println("[1] Register Account Name")
    println("[2] Deposit Amount")
    println("[3] Withdraw Amount")
    println("[4] Currency Exchange")
    println("[5] Record Exchange Rates")
    println("[6] Show Interest Amount\n")
}

// Sub-menus
fun registerAccount(db: UserDB){
    var doneRegistering = false
    while (!doneRegistering) {
        println("Register Account Name")
        print("Account Name: ")
        val name = readln()
        if (name.isEmpty() || db.findUserByName(name) != null) {
            println("\nAn account with this name is already registered.\n") // ERROR!!!
        }
        else {
            db.addUser(User(name))
            doneRegistering = true
        }
    }
    return
}
fun deposit(db: UserDB, rates: ExchangeRateSet){
    val user = getUser("Deposit Amount", db)
    printAccountInfo(user)
    println()
    println("[1] Philippine Peso (PHP)")
    println("[2] United States Dollar (USD)")
    println("[3] Japanese Yen (JPY)")
    println("[4] British Pound Sterling (GBP)")
    println("[5] Euro (EUR)")
    println("[6] Chinese Yuan Renminni (CNY)\n")
    val depositCurr = getChoice("Transaction Currency: ")
    var amount = getAmt("Deposit Amount: ")
    amount = rates.convertCurrency(depositCurr, 1, amount)
    println()
    if (depositCurr != 1) println("Equivalent in PHP: ${"%.2f".format(amount)} (Rate = ${"%.2f".format(rates.getRate(depositCurr))})")
    db.deposit(user, amount)
    println("Updated Balance: ${"%.2f".format(user.balance)}")
}
fun withdraw(db: UserDB, rates: ExchangeRateSet){
    val user = getUser("Withdraw Amount", db)
    printAccountInfo(user)
    println()
    println("[1] Philippine Peso (PHP)")
    println("[2] United States Dollar (USD)")
    println("[3] Japanese Yen (JPY)")
    println("[4] British Pound Sterling (GBP)")
    println("[5] Euro (EUR)")
    println("[6] Chinese Yuan Renminni (CNY)\n")
    val withdrawCurr = getChoice("Transaction Currency: ")
    var amount = getAmt("Withdraw Amount: ")
    amount = rates.convertCurrency(withdrawCurr, 1, amount)
    if (user.balance < amount){
        println("\nError: Insufficient balance. Withdrawal cancelled.\n") // ERROR!!!
        return
    }
    else {
        db.withdraw(user, amount)
        if (withdrawCurr != 1)  println("Equivalent in PHP: ${"%.2f".format(amount)} (Rate = ${"%.2f".format(rates.getRate(withdrawCurr))})")
        println("Updated Balance: ${"%.2f".format(user.balance)}")
        return
    }
}
fun exchange(rates: ExchangeRateSet){
    var exchanging = true
    while (exchanging) {
        println("Foreign Currency Exchange")
        println("Source Currency Option:")
        println("[1] Philippine Peso (PHP)")
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY)")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR)")
        println("[6] Chinese Yuan Renminni (CNY)\n")
        val sourceCurr = getChoice("Source Currency: ")
        val amount = getAmt("Source Amount: ")

        println()
        println("Exchanged Currency Options:")
        println("[1] Philippine Peso (PHP)")
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY)")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR)")
        println("[6] Chinese Yuan Renminni (CNY)\n")
        val targetCurr = getChoice("Exchange Currency: ")
        println("Exchange Amount: ${"%.2f".format(rates.convertCurrency(sourceCurr, targetCurr, amount))}")

        println()
        exchanging = yesNo("Convert another currency? (Y/N)? : ")
        if (exchanging) println()
    }

}
fun record(rates: ExchangeRateSet){
    println("Record Exchange Rate\n")
    println("[1] Philippine Peso (PHP)")
    println("[2] United States Dollar (USD)")
    println("[3] Japanese Yen (JPY)")
    println("[4] British Pound Sterling (GBP)")
    println("[5] Euro (EUR)")
    println("[6] Chinese Yuan Renminni (CNY)\n")
    val currency = getChoice("Select Foreign Currency: ")
    if (currency == 1){
        println("\nError: PHP is the base currency; its rate is fixed at 1.00.\n") // ERROR!!!
    }
    else{
        val rate = getAmt("Exchange Rate: ")
        rates.setRate(currency, rate)
    }
}
fun interest(db: UserDB){
    val user = getUser("Show Interest Amount", db)
    printAccountInfo(user)
    println("Interest Rate: ${(ANNUAL_INTEREST_RATE * 100).toInt()}%\n")

    val days = getDays("Total Number of Days: ")

    println()
    println("Day | Interest | Balance |")
    for (row in db.computeInterest(user, days)) {
        println("%-3d | %-8.2f | %.2f |".format(row.day, row.interest, row.balance))
    }
}

// Helpers
fun getUser(prompt: String, db: UserDB): User {
    while (true) {
        println(prompt)
        print("Account Name: ")
        val user = db.findUserByName(readln())
        if (user != null) {
            return user
        }
        else{
            println("\nError: Account not found.\n") // ERROR!!!
        }
    }
}
fun printAccountInfo(user: User){
    println("Current Balance: ${"%.2f".format(user.balance)}")
    println("Currency: ${user.currency}")
}
fun getAmt(prompt: String): Double {
    while (true) {
        print(prompt)
        val value = readln().trim().toDoubleOrNull()
        if (value != null && value.isFinite() && (value > 0) ) {
            return value
        }
        else println("\nError: Invalid amount. Must be a number greater than 0.\n") // ERROR!!!
    }
}
fun getChoice(prompt: String): Int{
    while (true) {
        print(prompt)
        val value = readln().trim().toIntOrNull()
        if (value != null && (value in 1..6) ) {
            return value
        }
        else println("\nError: Invalid choice. Enter a number from 1 to 6.\n") // ERROR!!!
    }
}
fun getDays(prompt: String): Int{
    while (true) {
        print(prompt)
        val value = readln().trim().toIntOrNull()
        if (value != null && (value > 0) ) {
            return value
        }
        else println("\nError: Invalid amount of days.\n") // ERROR!!!
    }
}
fun yesNo(prompt: String): Boolean{
    while (true) {
        print(prompt)
        val value = readln().trim().lowercase()
        if (value == "y"){
            return true
        }
        else if (value == "n"){
            return false
        }
        else println("\nError: Please enter Y or N.\n") // ERROR!!!
    }
}

fun main(){
    var isRunning = true

    val db = UserDB()
    val rates = ExchangeRateSet()

    while (isRunning) {
        printMainMenu()
        val choice = getChoice("Choice: ")
        println()

        if (db.isEmpty() && (choice == 2 || choice == 3 || choice == 6)) { // trying to withdraw/deposit/get interest while userbase is empty
            println("\nError: No account registered yet. Please register an account name first.\n") // ERROR!!!
        }
        else{
            when (choice) {
                1 -> registerAccount(db)
                2 -> deposit(db, rates)
                3 -> withdraw(db, rates)
                4 -> exchange(rates)
                5 -> record(rates)
                6 -> interest(db)
            }
        }
        println()
        isRunning = yesNo("Back to the Main Menu (Y/N): ")
        println()
    }
    println("Thank you for banking with us!\n")
}
