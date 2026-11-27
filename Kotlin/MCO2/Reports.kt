import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

// data classes for each record in each report for organizing
data class Report1Row(val name: String, val accountAge: Long, val balance: Double, val adb: Double, val tier: String)
data class Report2Row(val name: String, val balance: Double, val interest: Double)
data class Report3Row(val name: String, val date: LocalDate, val withdrawals: Double, val depositCount: Int, val deposits: Double, val velocity: Double, val flag: String)
data class Report4Row(val month: YearMonth, val averageWithdrawal: Double)

class DayTotals(val name: String, val date: LocalDate) {
    var withdrawals = 0.0
    var depositCount = 0
    var deposits = 0.0
}

class MonthTotals(val month: YearMonth) {
    var withdrawalTotal = 0.0
    var withdrawalCount = 0
}
// builds all entries for report1
fun report1(transactions: List<Transaction>): List<Report1Row> {
    val rows = ArrayList<Report1Row>()
    if (transactions.isEmpty()) return rows

    val sorted = transactions.sortedBy { it.date }

    val latest = sorted[sorted.size - 1].date
    val month = YearMonth.from(latest)
    val monthStart = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    // list of all names
    val names = ArrayList<String>()
    for (transaction in sorted) {
        if (!names.contains(transaction.accountName)) {
            names.add(transaction.accountName)
        }
    }

    for (name in names) {
        val endOfDay = DoubleArray(daysInMonth)
        val transacted = BooleanArray(daysInMonth)
        var opening = 0.0
        var firstDate = latest
        var currentBalance = 0.0

        for (transaction in sorted) {
            if (transaction.accountName != name) continue          // skip other accounts' transactions

            if (transaction.date.isBefore(firstDate)) firstDate = transaction.date
            currentBalance = transaction.currentBal

            if (transaction.date.isBefore(monthStart)) {
                opening = transaction.currentBal               // retains the last balance before the month starts
            } else {
                val index = transaction.date.dayOfMonth - 1        // day 1 corresponds to index 0
                endOfDay[index] = transaction.currentBal
                transacted[index] = true
            }
        }

        // Add up the balance of every day in the month
        var total = 0.0
        var balance = opening
        for (i in 0 until daysInMonth) {
            if (transacted[i]) balance = endOfDay[i]
            total += balance
        }
        val adb = total / daysInMonth

        if (adb >= 100000.0) {
            var tier = "Silver"
            if (adb >= 1000000.0) tier = "Platinum"
            else if (adb >= 500000.0) tier = "Gold"
            val age = ChronoUnit.DAYS.between(firstDate, latest)
            rows.add(Report1Row(name, age, currentBalance, adb, tier))
        }
    }

    rows.sortByDescending { it.adb }    // highest ADB goes first
    return rows
}

// builds all entries for report2
fun report2(users: List<User>): List<Report2Row> {
    val rows = ArrayList<Report2Row>()
    for (user in users) {
        if (user.balance >= 10000.0) {
            val interest = interestRows(user.balance, 30)
            val balAfter30Days = interest.last().balance
            rows.add(Report2Row(user.name, user.balance, balAfter30Days - user.balance))
        }
    }
    rows.sortByDescending { it.interest } // highest interest first
    return rows
}

// builds all entries for report3
fun report3(transactions: List<Transaction>): List<Report3Row> {

    val days = HashMap<String, DayTotals>()
    for (transaction in transactions) {
        val key = transaction.accountName + " " + transaction.date
        var d = days[key]
        if (d == null) {
            d = DayTotals(transaction.accountName, transaction.date)
            days[key] = d
        }
        if (transaction.transactionType == "deposit") {
            d.deposits += transaction.amount
            d.depositCount++
        } else {
            d.withdrawals += transaction.amount
        }
    }

    val rows = ArrayList<Report3Row>()
    for (d in days.values) {
        var flag = "Normal"
        var velocity = 0.0
        if (d.deposits > 0) {
            velocity = d.withdrawals / d.deposits * 100
        }
        if (d.withdrawals >= 500000.0 || velocity >= 90.0) flag = "Suspicious"
        rows.add(Report3Row(d.name, d.date, d.withdrawals, d.depositCount, d.deposits, velocity, flag))
    }
    // sort by date (ascending) then risk flag (descending)
    rows.sortByDescending { it.flag }
    rows.sortBy { it.date }
    return rows
}

// builds all entries for report4
// report 4 gives a report of the average withdrawals per month
fun report4(transactions: List<Transaction>): List<Report4Row> {
    val months = HashMap<YearMonth, MonthTotals>()
    for (transaction in transactions) {
        if (transaction.transactionType != "withdrawal") continue  // only withdrawals are taken into account
        val ym = YearMonth.from(transaction.date)
        var m = months[ym]
        if (m == null) {
            m = MonthTotals(ym)
            months[ym] = m
        }
        m.withdrawalTotal += transaction.amount
        m.withdrawalCount++
    }

    val rows = ArrayList<Report4Row>()
    for (m in months.values) {
        rows.add(Report4Row(m.month, m.withdrawalTotal / m.withdrawalCount))
    }
    rows.sortBy { it.month }  // oldest month goes first
    return rows
}

// File writing function
private fun writeCsv(path: String, header: String, lines: List<String>) {
    try {
        val text = StringBuilder()
        text.append(header).append("\n")
        for (line in lines) {
            text.append(line).append("\n")
        }
        File(path).writeText(text.toString())
    } catch (e: IOException) {
        println("Error writing to file: $path")
    }
}



private fun summaryJson(db: UserDB, r2: List<Report2Row>): String {
    var totalDeposits = 0.0
    var totalWithdrawals = 0.0
    for (transaction in db.getTransactions()) {
        if (transaction.transactionType == "deposit") totalDeposits += transaction.amount else totalWithdrawals += transaction.amount
    }
    var totalBalance = 0.0
    for (user in db.getUsers()) totalBalance += user.balance
    var totalInterest = 0.0
    for (row in r2) totalInterest += row.interest

    return "{\n" +
            "  \"total_accounts\": ${db.getUsers().size},\n" +
            "  \"total_deposits\": ${money(totalDeposits)},\n" +
            "  \"total_withdrawals\": ${money(totalWithdrawals)},\n" +
            "  \"total_balance\": ${money(totalBalance)},\n" +
            "  \"total_interest_expense\": ${money(totalInterest)}\n" +
            "}"
}

fun generateReports(db: UserDB) {
    val transactions = db.getTransactions()
    println("\nGenerating reports...")
    println("Outputs saved to individual files...")

    // Generate report 1
    val r1 = report1(transactions)
    val lines1 = ArrayList<String>()
    for (row in r1) {
        lines1.add("${row.name},${row.accountAge},${money(row.balance)},${money(row.adb)},${row.tier}")
    }
    writeCsv("report1_banking_privilege_tiers.csv", "AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier", lines1)
    println("\nReport 1: Banking Privilege Tiers")
    for (i in 0 until minOf(2, r1.size)) {               // preview for the first 2 rows only
        val row = r1[i]
        println("${row.name} | ${row.accountAge} | ${money(row.balance)} | ${money(row.adb)} | ${row.tier}")
    }
    println("\n(Full table exported to report1_banking_privilege_tiers.csv)")

    // Generate report 2
    val r2 = report2(db.getUsers())
    val lines2 = ArrayList<String>()
    for (row in r2) {
        lines2.add("${row.name},${money(row.balance)},${money(row.interest)}")
    }
    writeCsv("report2_interest_expense_projection.csv", "AccountName,CurrentBalance,ExpectedInterest", lines2)
    println("\nReport 2: Interest Expense Projection")
    for (i in 0 until minOf(2, r2.size)) {
        val row = r2[i]
        println("${row.name} | ${money(row.balance)} | ${money(row.interest)}")
    }
    println("\n(Full table exported to report2_interest_expense_projection.csv)")

    // Generate reporrt 3
    val r3 = report3(transactions)
    val lines3 = ArrayList<String>()
    for (row in r3) {
        lines3.add("${row.name},${row.date.format(WRITE_DATE_FORMAT)},${money(row.withdrawals)},${row.depositCount},${money(row.deposits)},${money(row.velocity)},${row.flag}")
    }
    writeCsv("report3_suspicious_client_behavior.csv", "AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity,RiskFlagLevel", lines3)
    println("\nReport 3: Suspicious Client Behavior")
    for (i in 0 until minOf(2, r3.size)) {
        val row = r3[i]
        println("${row.name} | ${row.date.format(WRITE_DATE_FORMAT)} | ${money(row.withdrawals)} | ${row.depositCount} | ${money(row.deposits)} | ${money(row.velocity)} | ${row.flag}")
    }
    println("\n(Full table exported to report3_suspicious_client_behavior.csv)")

    // Generate report 4
    val r4 = report4(transactions)
    val lines4 = ArrayList<String>()
    for (row in r4) {
        lines4.add("${row.month},${money(row.averageWithdrawal)}")
    }
    writeCsv("report4_average_withdrawals_per_month.csv", "YearMonth,AverageWithdrawal", lines4)
    println("\nReport 4: Average Monthly Withdrawals")
    for (i in 0 until minOf(2, r4.size)) {
        val row = r4[i]
        println("${row.month} | ${money(row.averageWithdrawal)}")
    }
    println("\n(Full table exported to report4_average_withdrawals_per_month.csv)")

    // Genereate summary json
    val json = summaryJson(db, r2)
    try {
        File("summary.json").writeText(json + "\n")
    } catch (e: IOException) {
        println("Error writing summary.json")
    }
    println("\nSummary State (summary.json):")
    println(json)
}
