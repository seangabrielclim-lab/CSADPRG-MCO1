import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.io.IOException
import java.io.File


const val CSV_FILENAME = "bank_transactions.csv"

private const val CSV_HEADER = "account_name,account_number,date,time,currency,amount,type,current_balance"

// For parsing entries
val READ_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
val READ_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m:ss")
// For writing entries
val WRITE_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
val WRITE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

// Information to gather after loading
data class LoadRes(
    val transactions: List<Transaction>,
    val maxAccNum: Long?,
    val numValidRows: Int,
    val numErrRows: Int
)

// Formats a number to have 2 decimals
fun money(money: Double): String = "%.2f".format(money)

// Parses an entry in the datasheet, null if the row is bad
fun parseEntry(line: String): Transaction? {
    val entryElements = line.split(",").map {it.trim()} // cuts the entries every time a "," is spotted, stray spaces are removed
    if (entryElements.size < 8 || entryElements.take(8).any {it.isEmpty() }) return null
    try {
        val transactionType = entryElements[6].lowercase()
        val amount = entryElements[5].toDouble()
        val curr_balance = entryElements[7].toDouble()
        if ((transactionType != "deposit" && transactionType != "withdrawal") || !amount.isFinite() || !curr_balance.isFinite()) return null
        else {

            return Transaction(entryElements[0],
                entryElements[1].toLong(),
                LocalDate.parse(entryElements[2], READ_DATE_FORMAT),
                LocalTime.parse(entryElements[3], READ_TIME_FORMAT),
                entryElements[4].uppercase(),
                amount, transactionType, curr_balance)
        }
    } catch (e: Exception) {

        return null

    }
}

// Reads all transactions and returns it as LoadRes
fun readAllTransactions(filename: String = CSV_FILENAME): LoadRes {
    val file = File(filename)
    if (!file.exists()) return LoadRes(emptyList(), null, 0, 0)
    val parsed_lines = file.readLines().drop(1).filter { it.isNotBlank() }.map { parseEntry(it) }
    val valid_rows = parsed_lines.filterNotNull()

    return LoadRes(
        transactions = valid_rows.filter { it.currency == "PHP" },
        maxAccNum = valid_rows.maxOfOrNull { it.accountNumber },
        numValidRows = valid_rows.size,
        numErrRows = parsed_lines.size - valid_rows.size
    )
}

// Adds a transaction to the CSV file
fun addTransaction(t: Transaction, filename: String = CSV_FILENAME) {
    val file = File(filename)

    try {
        if (!file.exists() || file.length() == 0L) {
            file.writeText(CSV_HEADER + "\n")
        }
        file.appendText(
            listOf(
                t.accountName, t.accountNumber, t.date.format(WRITE_DATE_FORMAT), t.time.format(WRITE_TIME_FORMAT),
                t.currency, money(t.amount), t.transactionType, money(t.currentBal)
            ).joinToString(",") + "\n"
        )
    } catch (e: IOException) {
        println("Error writing on file.")
    }

}

