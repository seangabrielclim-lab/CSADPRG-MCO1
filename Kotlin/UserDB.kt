import kotlin.math.roundToLong

const val ANNUAL_INTEREST_RATE = 0.05
// Class for holding row entries in the EOD interest calculation
data class RowEntry(val day: Int, val interest: Double, val balance: Double)

class UserDB {

    private val users: MutableList<User> = mutableListOf()


    fun addUser(user: User) {
        users.add(user)
    }
    fun getUsers(): List<User> {
        return users
    }
    fun isEmpty(): Boolean {
        return users.isEmpty()
    }
    fun findUserByName(name: String): User? {
        return users.find { it.name.equals(name, ignoreCase = true) }
    }
    fun deposit(user: User, amount: Double): Boolean {
        if (amount <= 0) return false
        user.balance += amount
        return true
    }
    fun withdraw(user: User, amount: Double): Boolean {
        if (amount <= 0 || amount > user.balance) return false
        user.balance -= amount
        return true
    }
    fun computeInterest(user: User, days: Int): List<RowEntry> {
        var balance = user.balance
        val interestRows = mutableListOf<RowEntry>()
        for (day in 1..days) {
            val interest = (balance * (ANNUAL_INTEREST_RATE / 365) * 100).roundToLong() / 100.0
            balance += interest
            interestRows.add(RowEntry(day, interest, balance))
        }
        return interestRows
    }
}