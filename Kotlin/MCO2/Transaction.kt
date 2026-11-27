import java.time.LocalDate
import java.time.LocalTime
// val properties are used since transaction information cannot change
// class Transaction represents an entry in the csv file
data class Transaction(

    val accountName: String,
    val accountNumber: Long,
    val date: LocalDate,
    val time: LocalTime,
    val currency: String,
    val amount: Double,
    val transactionType: String,
    val currentBal: Double
)