// ********************
// Last names: Chan, Cheng, Jugno, Lim
// Language: R
// Paradigm(s): Imperative, Object-Oriented
// ********************

const val ERR_MSG = "\n*** Error Invalid Input ***\n"

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
            println(ERR_MSG)
        }
        else {
            db.addUser(User(name))
            doneRegistering = true
        }
    }
    return
}
fun deposit(db: UserDB){
    val user = getUser("Deposit Amount", db)
    printAccountInfo(user)
    println()
    val amount = getAmt("Deposit Amount: ")
    db.deposit(user, amount)
    println("Updated Balance: ${"%.2f".format(user.balance)}")
}
fun withdraw(db: UserDB){
    val user = getUser("Withdraw Amount", db)
    printAccountInfo(user)
    println()
    while (true) {
        val amount = getAmt("Withdraw Amount: ")
        if (user.balance < amount){
            println(ERR_MSG)
        }
        else {
            db.withdraw(user, amount)
            println("Updated Balance: ${"%.2f".format(user.balance)}")
            return
        }
    }
}
fun exchange(rates: ExchangeRateSet){
    var exchanging = true
    while (exchanging) {
        println("Foreign Currency Exchange")
        println("Source Currency Option:")
        println("[1] Philippine Peso (PHP)")
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR")
        println("[6] Chinese Yuan Renmimi (CNY)\n")
        val sourceCurr = getChoice("Source Currency: ")
        val amount = getAmt("Source Amount: ")

        println()
        println("Exchanged Currency Options:")
        println("[1] Philippine Peso (PHP)")
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR")
        println("[6] Chinese Yuan Renmimi (CNY)\n")
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
    println("[6] Chinese Yuan Renmimi (CNY)\n")
    val currency = getChoice("Select Foreign Currency: ")
    val rate = getAmt("Exchange Rate: ")
    rates.setRate(currency, rate)
}
fun interest(db: UserDB){
    val user = getUser("Show Interest Amount", db)
    printAccountInfo(user)
    println("Interest Rate: ${(ANNUAL_INTEREST_RATE * 100).toInt()}%\n")

    val days = getDays("Total Number of Days: ")

    println()
    println("Day | Interest | Balance |")
    for (row in db.computeInterest(user, days)) {
        println("%-3d | %-8.2f | %.2f".format(row.day, row.interest, row.balance))
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
            println(ERR_MSG)
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
        else println(ERR_MSG)
    }
}
fun getChoice(prompt: String): Int{
    while (true) {
        print(prompt)
        val value = readln().trim().toIntOrNull()
        if (value != null && (value in 1..6) ) {
            return value
        }
        else println(ERR_MSG)
    }
}
fun getDays(prompt: String): Int{
    while (true) {
        print(prompt)
        val value = readln().trim().toIntOrNull()
        if (value != null && (value > 0) ) {
            return value
        }
        else println(ERR_MSG)
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
        else println(ERR_MSG)
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
            println(ERR_MSG)
        }
        else{
            when (choice) {
                1 -> registerAccount(db)
                2 -> deposit(db)
                3 -> withdraw(db)
                4 -> exchange(rates)
                5 -> record(rates)
                6 -> interest(db)
                else -> println(ERR_MSG)
            }
        }
        println()
        isRunning = yesNo("Back to the Main Menu (Y/N): ")
        println()
    }
}