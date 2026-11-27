import kotlin.math.roundToLong
import java.time.LocalDate
import java.time.LocalTime

const val ANNUAL_INTEREST_RATE = 0.05
const val MIN_INIT_DEPOSIT = 5000.0
// Class for holding row entries in the EOD interest calculation
data class RowEntry(val day: Int, val interest: Double, val balance: Double)

// Conpounding daily interest rounded to 2 decimals
// Moved here instead of inside UserDB class since Report 2 also needs it
fun interestRows(startBalance: Double, days: Int): List<RowEntry> {
    var balance = startBalance
    val rows = mutableListOf<RowEntry>()
    for (day in 1..days) {
        val interest = (balance * (ANNUAL_INTEREST_RATE / 365) * 100).roundToLong() / 100.0
        balance += interest
        rows.add(RowEntry(day, interest, balance))
    }
    return rows
}
// helper function for rounding to 2 decimals\
private fun round2dec(x: Double): Double {
    return (x * 100).roundToLong() / 100.0
}

class UserDB {

    private val users: MutableList<User> = mutableListOf()
    private val transactions: MutableList<Transaction> = mutableListOf()
    private var maxAccountNumber: Long = 0L

    // Loads each transaction and builds the data in the database from it
    fun load(result: LoadRes) {
        users.clear()
        transactions.clear()
        transactions.addAll(result.transactions)
        maxAccountNumber = result.maxAccNum ?: 0L
        for (transaction in transactions) {
            val curr_user = findUserByName(transaction.accountName)
            if (curr_user == null) users.add(User(transaction.accountName, transaction.accountNumber, transaction.currentBal))
            else curr_user.balance = transaction.currentBal
        }
    }
    // REQ 1: takes into account whether the current largest accountnum is found in just the csv file or the database (ie: more users have been generated since the start of the run)
    fun registerUser(name: String, initialDeposit: Double): User {
        val currHighest = maxOf(maxAccountNumber, users.maxOfOrNull { it.accountNumber } ?: 0L)
        val newNumber = if (currHighest == 0L) 1000000001L else currHighest + 1
        maxAccountNumber = newNumber
        val user = User(name, newNumber, initialDeposit)
        users.add(user)
        record(user, initialDeposit, "deposit")
        return user
    }

    fun record(user: User, amount: Double, type: String) {
        val transaction = Transaction(
            user.name, user.accountNumber, LocalDate.now(), LocalTime.now().withNano(0),
            "PHP", round2dec(amount), type, user.balance
        )
        transactions.add(transaction)
        addTransaction(transaction)
    }

    fun deposit(user: User, amount: Double): Boolean {
        if (amount <= 0) return false
        user.balance = round2dec(user.balance + amount)
        record(user, amount, "deposit")
        return true
    }

    fun withdraw(user: User, amount: Double): Boolean {
        if (amount <= 0 || amount > user.balance) return false
        user.balance = round2dec(user.balance - amount)
        record(user, amount, "withdrawal")
        return true
    }


    fun getUsers(): List<User> {
        return users
    }
    fun getTransactions(): List<Transaction> {
        return transactions
    }
    fun isEmpty(): Boolean {
        return users.isEmpty()
    }
    fun findUserByName(name: String): User? {
        return users.find { it.name.equals(name, ignoreCase = true) }
    }
    fun computeInterest(user: User, days: Int): List<RowEntry> {
        return interestRows(user.balance, days)
    }
}