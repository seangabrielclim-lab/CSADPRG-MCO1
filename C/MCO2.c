/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: C
********************

MCO2: Banking Deposit and Withdrawal Reports

How to compile and run (bank_transactions.csv must be in the same folder):
  gcc -std=c99 -o mco2 mco2_banking.c

Files created by Generate Reports:
  data_validation_log.txt                   - rows that were removed, imputed or corrected
  report1_banking_privilege_tiers.csv       - Report 1
  report2_interest_expense_projection.csv   - Report 2
  report3_suspicious_client_behavior.csv    - Report 3
  report4_unusual_large_deposits.csv        - Report 4 (custom)
  summary.json                              - totals across the reports

*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

// Files
#define DATA_FILE     "bank_transactions.csv"
#define LOG_FILE      "data_validation_log.txt"
#define REPORT1_FILE  "report1_banking_privilege_tiers.csv"
#define REPORT2_FILE  "report2_interest_expense_projection.csv"
#define REPORT3_FILE  "report3_suspicious_client_behavior.csv"
#define REPORT4_FILE  "report4_unusual_large_deposits.csv"
#define SUMMARY_FILE  "summary.json"

// Limits
#define MAX_NAME          100
#define MAX_LINE          1024
#define MAX_FIELD_LEN     128
#define NUM_COLUMNS       8
#define MAX_TRANSACTIONS  20000
#define MAX_ACCOUNTS      1000

// Bank Rules
#define NUM_CURRENCY          6
#define PHP                   0
#define INTEREST_RATE         0.05
#define MIN_INITIAL_DEPOSIT   5000.00
#define FIRST_ACCOUNT_NUMBER  1092000001
#define PROJECTION_DAYS       30
#define ADB_MINIMUM           100000.00
#define SILVER_LIMIT          500000.00
#define GOLD_LIMIT            1000000.00
#define INTEREST_MINIMUM      10000.00
#define SUSPICIOUS_WITHDRAWAL 500000.00
#define SUSPICIOUS_VELOCITY   90.0
#define MIN_FLAG_DEPOSIT      30000.00   // Report 4: deposits below this are only flagged at 300%+
#define LOW_RISK_PCT          50.0
#define MEDIUM_RISK_PCT       75.0
#define HIGH_RISK_PCT         100.0
#define SMALL_DEPOSIT_PCT     300.0

// One bank account built from the transaction file
typedef struct {
    char name[MAX_NAME];
    int number;
    double balance;      // latest balance, in PHP
    int openDayNum;      // date of the first transaction
} Account;

// One cleaned PHP transaction row
typedef struct {
    int accIndex;       
    int day, month, year;
    int dayNum;          
    double amount;
    int isDeposit;       // 1 = deposit, 0 = withdrawal
    double balance;     
} Transaction;

// One row of a parsed CSV line before it is stored
typedef struct {
    char name[MAX_NAME];
    int number;
    int day, month, year;
    double amount;
    int isDeposit;
    double balance;
    int hasBalance;
    int isPHP;
} CsvRow;

// Report 1 row
typedef struct {
    int accIndex;
    double adb;
} TierRow;

// Report 2 row
typedef struct {
    int accIndex;
    double interest;
} InterestRow;

// Report 3 row: one account on one day
typedef struct {
    int accIndex;
    int dayNum, day, month, year;
    double withdrawals;
    int numDeposits;
    double deposits;
    int suspicious;
} DayStat;

// Display names of the supported currencies, in menu order
const char *CURRENCY_NAMES[NUM_CURRENCY] = {
    "Philippine Peso (PHP)",
    "United States Dollar (USD)",
    "Japanese Yen (JPY)",
    "British Pound Sterling (GBP)",
    "Euro (EUR)",
    "Chinese Yuan Renminni (CNY)"
};

// PHP value of 1 unit of each currency.
double rates[NUM_CURRENCY] = { 1.00, 62.00, 0.40, 84.00, 72.00, 9.00 };

// Large tables are global so they are not placed on the stack
Account accounts[MAX_ACCOUNTS];
int numAccounts = 0;
Transaction trans[MAX_TRANSACTIONS];
int numTrans = 0;
int maxAccountNumber = 0;

TierRow tierRows[MAX_ACCOUNTS];
InterestRow interestRows[MAX_ACCOUNTS];
DayStat dayStats[MAX_TRANSACTIONS];
int transOrder[MAX_TRANSACTIONS];
int unusualDeposits[MAX_TRANSACTIONS];
double eodBalance[MAX_ACCOUNTS][32];
int hasEod[MAX_ACCOUNTS][32];
double balanceBeforeMonth[MAX_ACCOUNTS];

// Input Helpers

// Discards whatever is left on the current input line, including the newline.
void clearInput(void)
{
    int charac;
    while ((charac = getchar()) != '\n' && charac != EOF)
        ;
}

// Accepts "2" or "[2]" so input can be typed like in the figures.
int readChoice(const char *msg, int min, int max)
{
    int value, numReturn;

    while (1) {
        printf("%s", msg);

        numReturn = scanf("%d", &value);
        if (numReturn == 0)
            numReturn = scanf("[%d]", &value);
        clearInput();

        if (numReturn == 1 && value >= min && value <= max)
            return value;

        printf("Invalid choice. Enter a number from %d to %d.\n", min, max);
    }
}

// Reads an amount greater than 0; asks again on invalid input
double readPositiveAmount(const char *msg)
{
    double value;
    int numReturn;

    while (1) {
        printf("%s", msg);
        numReturn = scanf("%lf", &value);
        clearInput();

        if (numReturn == 1 && value > 0)
            return value;

        printf("Invalid amount. Enter a number greater than 0.\n");
    }
}

// Returns 1 for Y/y and 0 for N/n; asks again on anything else
int readYesNo(const char *msg)
{
    char answer;

    while (1) {
        printf("%s", msg);
        scanf(" %c", &answer);
        clearInput();

        if (answer == 'Y' || answer == 'y')
            return 1;
        if (answer == 'N' || answer == 'n')
            return 0;

        printf("Please enter Y or N.\n");
    }
}

// Text Helpers

// Turns several spaces in a row into one, e.g. "Kenn     Sy" -> "Kenn Sy"
void singleSpaces(char *text)
{
    int i, j = 0;

    for (i = 0; text[i] != '\0'; i++)
        if (text[i] != ' ' || j == 0 || text[j - 1] != ' ')
            text[j++] = text[i];
    text[j] = '\0';
}

// Compares two names ignoring uppercase/lowercase. Returns 1 if they match.
int sameName(const char *a, const char *b)
{
    while (*a && *b) {
        if (tolower(*a) != tolower(*b))
            return 0;
        a++;
        b++;
    }
    return *a == *b;
}

// Removes spaces, tabs and line endings (\n or \r\n) from both ends of a string
void trimText(char *text)
{
    int start = 0, end = (int) strlen(text), i;

    while (end > 0 && isspace(text[end - 1]))
        end--;
    while (start < end && isspace(text[start]))
        start++;

    for (i = 0; i < end - start; i++)
        text[i] = text[start + i];
    text[end - start] = '\0';
}

// Writes a number with thousands separators and 2 decimals into out, e.g. 1,234,567.89
void formatMoney(double value, char *out)
{
    char digits[64];
    int i, j = 0, start = 0, intLen;

    sprintf(digits, "%.2f", value);
    if (digits[0] == '-')
        out[j++] = digits[start++];

    intLen = (int) strlen(digits) - start - 3;
    for (i = start; digits[i] != '\0'; i++) {
        out[j++] = digits[i];
        if (i - start + 1 < intLen && (intLen - (i - start + 1)) % 3 == 0)
            out[j++] = ',';
    }
    out[j] = '\0';
}

// Prints a whole number with thousands separators, e.g. 9,189
void printCount(int n)
{
    if (n >= 1000) {
        printCount(n / 1000);
        printf(",%03d", n % 1000);
    } else {
        printf("%d", n);
    }
}

// Date Helper

// Returns 1 if the year has 29 days in February
int isLeapYear(int year)
{
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
}

// Returns the number of days in a month (month is 1 to 12)
int daysInMonth(int month, int year)
{
    const int days[12] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    if (month == 2 && isLeapYear(year))
        return 29;
    return days[month - 1];
}

// Counts days since 01/01/1970 so two dates can be subtracted
int toDayNumber(int day, int month, int year)
{
    int total = 0, i;

    for (i = 1970; i < year; i++)
        if (isLeapYear(i))
            total += 366;
        else
            total += 365;
    for (i = 1; i < month; i++)
        total += daysInMonth(i, year);
    return total + day - 1;
}

// CSV Parsing and Cleaning

// Splits one CSV line into fields; supports "quoted, fields". Returns the field count.
int splitCSV(const char *line, char fields[][MAX_FIELD_LEN], int maxFields)
{
    int count = 0, len = 0, inQuotes = 0;
    const char *p;

    for (p = line; ; p++) {
        if (*p == '"' && inQuotes && p[1] == '"') {
            if (count < maxFields && len < MAX_FIELD_LEN - 1)
                fields[count][len++] = '"';
            p++;
        } else if (*p == '"') {
            inQuotes = !inQuotes;
        } else if ((*p == ',' && !inQuotes) || *p == '\0') {
            if (count < maxFields) {
                fields[count][len] = '\0';
                trimText(fields[count]);
            }
            count++;
            len = 0;
            if (*p == '\0')
                break;
        } else if (count < maxFields && len < MAX_FIELD_LEN - 1) {
            fields[count][len++] = *p;
        }
    }
    return count;
}

// Converts text like "1,234.56" to a number. Returns 1 if it is a valid number.
int parseAmount(const char *text, double *value)
{
    char clean[MAX_FIELD_LEN];
    char extra;
    int i, j = 0;

    for (i = 0; text[i] != '\0'; i++)
        if (text[i] != ',')
            clean[j++] = text[i];
    clean[j] = '\0';

    return sscanf(clean, "%lf %c", value, &extra) == 1;
}

// Reads a dd/mm/yyyy date and checks that it really exists
int parseDate(const char *text, int *day, int *month, int *year)
{
    char extra;

    if (sscanf(text, "%d/%d/%d %c", day, month, year, &extra) != 3)
        return 0;
    if (*year < 1970 || *year > 2100 || *month < 1 || *month > 12)
        return 0;
    return *day >= 1 && *day <= daysInMonth(*month, *year);
}

// Checks an hours:minutes:seconds time (one-digit hours like 8:22:00 are accepted)
int isValidTime(const char *text)
{
    int hour, minute, second;
    char extra;

    if (sscanf(text, "%d:%d:%d %c", &hour, &minute, &second, &extra) != 3)
        return 0;
    return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59;
}

// Checks for exactly 10 digits and stores the number
int parseAccountNumber(const char *text, int *number)
{
    int i;

    if (strlen(text) != 10)
        return 0;
    for (i = 0; i < 10; i++)
        if (!isdigit(text[i]))
            return 0;
    return sscanf(text, "%d", number) == 1;
}

// Validates and converts one CSV row. Returns NULL if valid, otherwise the reason.
const char *parseRow(char f[][MAX_FIELD_LEN], int count, CsvRow *row)
{
    int i;

    if (count != NUM_COLUMNS)
        return "wrong number of columns";
    for (i = 0; i < NUM_COLUMNS - 1; i++)
        if (f[i][0] == '\0')
            return "missing value";

    if (strlen(f[0]) >= MAX_NAME)
        return "account name too long";
    strcpy(row->name, f[0]);

    if (!parseAccountNumber(f[1], &row->number))
        return "invalid account number";
    if (!parseDate(f[2], &row->day, &row->month, &row->year))
        return "invalid date";
    if (!isValidTime(f[3]))
        return "invalid time";
    if (!parseAmount(f[5], &row->amount) || row->amount <= 0)
        return "invalid amount";

    if (sameName(f[6], "deposit"))
        row->isDeposit = 1;
    else if (sameName(f[6], "withdrawal"))
        row->isDeposit = 0;
    else
        return "invalid transaction type";

    row->hasBalance = f[7][0] != '\0';
    if (row->hasBalance && !parseAmount(f[7], &row->balance))
        return "invalid current balance";

    row->isPHP = sameName(f[4], "PHP");
    return NULL;
}

// Accounts

// Returns the index of the account with this name (any case), or -1 if none.
int findAccount(const char *name)
{
    int i;
    for (i = 0; i < numAccounts; i++)
        if (sameName(accounts[i].name, name))
            return i;
    return -1;
}

// Returns the index of the account with this number, or -1 if none.
int findAccountByNumber(int number)
{
    int i;
    for (i = 0; i < numAccounts; i++)
        if (accounts[i].number == number)
            return i;
    return -1;
}

// Adds a new account and returns its index, or -1 if the table is full
int addAccount(const char *name, int number, int openDayNum)
{
    if (numAccounts == MAX_ACCOUNTS)
        return -1;

    strcpy(accounts[numAccounts].name, name);
    accounts[numAccounts].number = number;
    accounts[numAccounts].balance = 0.00;
    accounts[numAccounts].openDayNum = openDayNum;
    if (number > maxAccountNumber)
        maxAccountNumber = number;
    return numAccounts++;
}

// Stores a cleaned transaction and updates its account
void storeTransaction(int accIndex, int day, int month, int year, double amount, int isDeposit, double balance)
{
    Transaction *t;

    accounts[accIndex].balance = balance;
    if (numTrans == MAX_TRANSACTIONS)
        return;

    t = &trans[numTrans++];
    t->accIndex = accIndex;
    t->day = day;
    t->month = month;
    t->year = year;
    t->dayNum = toDayNumber(day, month, year);
    t->amount = amount;
    t->isDeposit = isDeposit;
    t->balance = balance;

    if (t->dayNum < accounts[accIndex].openDayNum)
        accounts[accIndex].openDayNum = t->dayNum;
}

// Data Gathering

// Reads, validates and cleans the CSV file. Returns 1 if the file was opened.
int loadData(int showLog)
{
    FILE *fp, *log;
    char line[MAX_LINE];
    char fields[NUM_COLUMNS + 2][MAX_FIELD_LEN];
    CsvRow row;
    const char *error;
    int lineNum = 1, totalRows = 0, phpRows = 0, otherCurrency = 0, errorRows = 0, imputed = 0, corrected = 0;
    int count, accIndex;
    double expected, difference;

    numTrans = 0;
    numAccounts = 0;
    maxAccountNumber = 0;

    fp = fopen(DATA_FILE, "r");
    if (fp == NULL) {
        if (showLog)
            printf("Could not open %s.\n", DATA_FILE);
        return 0;
    }

    log = NULL;
    if (showLog)
        log = fopen(LOG_FILE, "w");
    if (log != NULL)
        fprintf(log, "Data validation log for %s\n", DATA_FILE);

    // the first line is the header
    if (fgets(line, MAX_LINE, fp) == NULL) {
        fclose(fp);
        if (log != NULL)
            fclose(log);
        return 1;
    }

    while (fgets(line, MAX_LINE, fp) != NULL) {
        lineNum++;
        trimText(line);
        if (line[0] == '\0')
            continue;
        totalRows++;

        count = splitCSV(line, fields, NUM_COLUMNS + 2);

        // every account number in the file counts, even on rows removed below
        if (count >= 2 && parseAccountNumber(fields[1], &row.number) && row.number > maxAccountNumber)
            maxAccountNumber = row.number;

        error = parseRow(fields, count, &row);

        if (error != NULL) {
            errorRows++;
            if (log != NULL)
                fprintf(log, "Line %d removed (%s): %s\n", lineNum, error, line);
            continue;
        }

        if (!row.isPHP) {
            otherCurrency++;
            continue;
        }

        accIndex = findAccountByNumber(row.number);
        if (accIndex == -1)
            accIndex = addAccount(row.name, row.number, toDayNumber(row.day, row.month, row.year));
        if (accIndex == -1)
            continue;

        // the balance should be the account's previous balance plus or minus the amount
        if (row.isDeposit)
            expected = accounts[accIndex].balance + row.amount;
        else
            expected = accounts[accIndex].balance - row.amount;

        if (!row.hasBalance) {
            // impute a missing balance
            row.balance = expected;
            imputed++;
            if (log != NULL)
                fprintf(log, "Line %d: missing current_balance imputed as %.2f\n", lineNum, row.balance);
        } else {
            // correct a balance that does not add up (more than 1 centavo off)
            difference = row.balance - expected;
            if (difference < 0)
                difference = -difference;
            if (difference > 0.01) {
                if (log != NULL)
                    fprintf(log, "Line %d: current_balance %.2f does not match expected %.2f, corrected\n",
                            lineNum, row.balance, expected);
                row.balance = expected;
                corrected++;
            }
        }

        storeTransaction(accIndex, row.day, row.month, row.year, row.amount, row.isDeposit, row.balance);
        phpRows++;
    }
    fclose(fp);

    if (log != NULL) {
        fprintf(log, "Total rows: %d\nValid PHP rows: %d\nNon-PHP rows excluded: %d\n", totalRows, phpRows, otherCurrency);
        fprintf(log, "Rows removed for errors: %d\nRows with imputed balance: %d\n", errorRows, imputed);
        fprintf(log, "Rows with corrected balance: %d\n", corrected);
        fclose(log);
    }

    if (showLog) {
        printf("Processing dataset... (");
        printCount(totalRows);
        printf(" rows loaded, ");
        printCount(phpRows);
        printf(" filtered for PHP currency)\n");
        printf("Validation: %d rows with errors removed, %d rows imputed, %d balances corrected, %d non-PHP rows excluded (see %s)\n",
               errorRows, imputed, corrected, otherCurrency, LOG_FILE);
    }
    return 1;
}

// Appends one transaction to the CSV file, creating the file with a header if needed.
// Returns 1 if it was saved, 0 if the file could not be written.
int appendToFile(const Account *acc, double amount, int isDeposit, double balance, struct tm *now)
{
    FILE *fp;
    int needHeader = 1, needNewline = 0;
    const char *type;

    if (isDeposit)
        type = "deposit";
    else
        type = "withdrawal";

    // check whether the file exists and ends with a newline
    fp = fopen(DATA_FILE, "rb");
    if (fp != NULL) {
        fseek(fp, 0, SEEK_END);
        if (ftell(fp) > 0) {
            needHeader = 0;
            fseek(fp, -1, SEEK_END);
            needNewline = fgetc(fp) != '\n';
        }
        fclose(fp);
    }

    fp = fopen(DATA_FILE, "a");
    if (fp == NULL) {
        // perror adds the reason from the system, e.g. "Permission denied"
        perror("Could not save the transaction to " DATA_FILE);
        return 0;
    }

    if (needHeader)
        fprintf(fp, "account_name,account_number,date,time,currency,amount,type,current_balance\n");
    if (needNewline)
        fprintf(fp, "\n");

    fprintf(fp, "%s,%010d,%02d/%02d/%04d,%02d:%02d:%02d,PHP,%.2f,%s,%.2f\n",
            acc->name, acc->number,
            now->tm_mday, now->tm_mon + 1, now->tm_year + 1900,
            now->tm_hour, now->tm_min, now->tm_sec,
            amount, type, balance);
    fclose(fp);
    return 1;
}

// Saves a transaction with the current date and time, then updates the account's balance.
// Returns 1 if saved. If the file cannot be written, nothing changes and 0 is returned.
int recordTransaction(int accIndex, double amount, int isDeposit, double newBalance)
{
    time_t seconds = time(NULL);
    struct tm *now = localtime(&seconds);

    if (!appendToFile(&accounts[accIndex], amount, isDeposit, newBalance, now))
        return 0;

    storeTransaction(accIndex, now->tm_mday, now->tm_mon + 1, now->tm_year + 1900,
                     amount, isDeposit, newBalance);
    return 1;
}

// Shared Transaction Helpers

// Prints the numbered list of currencies
void printCurrencyList(void)
{
    int i;
    for (i = 0; i < NUM_CURRENCY; i++)
        printf("[%d] %s\n", i + 1, CURRENCY_NAMES[i]);
}

// Converts an amount between any two currencies through PHP.
double convert(double amount, int from, int to)
{
    return amount * rates[from] / rates[to];
}

// Asks for an account name until an existing one is entered. Returns NULL only if no accounts exist.
Account *selectAccount(void)
{
    char name[MAX_NAME];
    int index;

    if (numAccounts == 0) {
        printf("No account registered yet. Please register an account first.\n");
        return NULL;
    }

    while (1) {
        printf("Account Name: ");
        scanf(" %99[^\n]", name);
        clearInput();
        trimText(name);
        singleSpaces(name);

        index = findAccount(name);
        if (index != -1)
            return &accounts[index];

        printf("Name does not exist. Please Enter an existing user.\n");
    }
}

// Shows the account's current balance and currency
void printAccountHeader(Account *acc)
{
    printf("Current Balance: %.2f\n", acc->balance);
    printf("Currency: PHP\n");
}

// Asks which currency the transaction is in. Returns its index (0 = PHP).
int readTransactionCurrency(void)
{
    printCurrencyList();
    return readChoice("Transaction Currency: ", 1, NUM_CURRENCY) - 1;
}

// Reads an amount in the given currency and returns its PHP equivalent.
double readAmountInPHP(const char *msg, int currency)
{
    double amount, inPHP;

    amount = readPositiveAmount(msg);
    if (currency == PHP)
        return amount;

    inPHP = convert(amount, currency, PHP);
    printf("Equivalent in PHP (rate %.2f): %.2f\n", rates[currency], inPHP);
    return inPHP;
}

// Checks for "First Last": at least two words, letters and . ' - only
int isValidName(const char *name)
{
    int i, hasSpace = 0;

    for (i = 0; name[i] != '\0'; i++) {
        if (name[i] == ' ')
            hasSpace = 1;
        else if (!isalpha(name[i]) && name[i] != '.' && name[i] != '\'' && name[i] != '-')
            return 0;
    }
    return hasSpace;
}

// Transactions

// Registers accounts with a unique name, a generated account number and an initial deposit
void registerAccount(void)
{
    char name[MAX_NAME];
    double initial;
    int index;
    int number;
    int oldMaxNumber;

    do {
        printf("Register Account\n");
        if (numAccounts == MAX_ACCOUNTS) {
            printf("Maximum number of accounts reached.\n");
            return;
        }

        while (1) {
            printf("Account Name: ");
            scanf(" %99[^\n]", name);
            clearInput();
            trimText(name);
            singleSpaces(name);

            if (!isValidName(name))
                printf("Please enter a first and last name (letters only, no commas).\n");
            else if (findAccount(name) != -1)
                printf("An account with this name is already registered.\n");
            else
                break;
        }

        while (1) {
            initial = readPositiveAmount("Initial Deposit: ");
            if (initial >= MIN_INITIAL_DEPOSIT)
                break;
            printf("Initial deposit must be at least 5,000.00.\n");
        }

        if (maxAccountNumber == 0)
            number = FIRST_ACCOUNT_NUMBER;
        else
            number = maxAccountNumber + 1;
        // open date is set to today by the first transaction
        oldMaxNumber = maxAccountNumber;
        index = addAccount(name, number, 999999);

        if (recordTransaction(index, initial, 1, initial)) {
            printf("Account created successfully! (Account Number: %010d)\n\n", number);
        } else {
            // remove the account again so it is not kept only in memory
            numAccounts--;
            maxAccountNumber = oldMaxNumber;
            printf("Account was not created.\n\n");
        }
    } while (readYesNo("Register another account? (Y/N): "));
}

// Adds money to an existing account and saves each deposit to the file
void deposit(void)
{
    Account *acc;
    double amount;

    printf("Deposit Amount\n");
    acc = selectAccount();
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("\n");

    do {
        amount = readAmountInPHP("Deposit Amount: ", readTransactionCurrency());
        if (recordTransaction((int) (acc - accounts), amount, 1, acc->balance + amount))
            printf("Updated Balance: %.2f\n\n", acc->balance);
        else
            printf("Deposit cancelled. Balance stays at %.2f\n\n", acc->balance);
    } while (readYesNo("Deposit another amount? (Y/N): "));
}

// Takes money out of an existing account if the balance allows it, saving each withdrawal
void withdraw(void)
{
    Account *acc;
    int currency;
    double amount;

    printf("Withdraw Amount\n");
    acc = selectAccount();
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("\n");

    do {
        if (acc->balance <= 0) {
            printf("No balance available to withdraw.\n");
            return;
        }

        while (1) {
            currency = readTransactionCurrency();
            amount = readAmountInPHP("Withdraw Amount: ", currency);
            if (amount <= acc->balance)
                break;
            printf("Insufficient balance. Please enter a smaller amount.\n");
        }

        if (recordTransaction((int) (acc - accounts), amount, 0, acc->balance - amount))
            printf("Updated Balance: %.2f\n\n", acc->balance);
        else
            printf("Withdrawal cancelled. Balance stays at %.2f\n\n", acc->balance);
    } while (readYesNo("Withdraw another amount? (Y/N): "));
}

// Converts an amount from one currency to another
void currencyExchange(void)
{
    int source, target;
    double amount;

    do {
        printf("Foreign Currency Exchange\n");
        printf("Source Currency Option:\n");
        printCurrencyList();
        printf("\n");
        source = readChoice("Source Currency: ", 1, NUM_CURRENCY) - 1;
        amount = readPositiveAmount("Source Amount: ");

        printf("\nExchanged Currency Options:\n");
        printCurrencyList();
        printf("\n");
        target = readChoice("Exchange Currency: ", 1, NUM_CURRENCY) - 1;

        printf("Exchange Amount: %.2f\n\n", convert(amount, source, target));
    } while (readYesNo("Convert another currency (Y/N)? "));
}

// Updates the exchange rate of a foreign currency
void recordExchangeRate(void)
{
    int currency;

    printf("Record Exchange Rate\n\n");
    printCurrencyList();
    printf("\n");

    while (1) {
        currency = readChoice("Select Foreign Currency: ", 1, NUM_CURRENCY) - 1;
        if (currency != PHP)
            break;
        printf("PHP is the base currency; its rate is fixed at 1.00. Please select another currency.\n");
    }

    rates[currency] = readPositiveAmount("Exchange Rate: ");
}

// Lists the projected daily interest and balance for a number of days
void showInterest(void)
{
    Account *acc;
    int days, day;
    double balance, interest;

    printf("Show Interest Amount\n");
    acc = selectAccount();
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("Interest Rate: %.0f%%\n\n", INTEREST_RATE * 100);

    days = readChoice("Total Number of Days: ", 1, 36500);

    // Projection only: the account balance itself is not changed.
    balance = acc->balance;
    printf("\nDay | Interest | Balance |\n");
    for (day = 1; day <= days; day++) {
        // round interest to centavos before adding, like the figure (0.14/day)
        interest = (int) (balance * (INTEREST_RATE / 365) * 100 + 0.5) / 100.0;
        balance = balance + interest;
        printf("%-3d | %-8.2f | %-7.2f |\n", day, interest, balance);
    }
}

// Report Helpers

// Returns the latest transaction date (the reference "today" for the reports)
int latestDayNum(void)
{
    int i, latest = 0;
    for (i = 0; i < numTrans; i++)
        if (trans[i].dayNum > latest)
            latest = trans[i].dayNum;
    return latest;
}

// Silver below 500,000, Gold below 1,000,000, Platinum otherwise
const char *privilegeTier(double adb)
{
    if (adb < SILVER_LIMIT)
        return "Silver";
    if (adb < GOLD_LIMIT)
        return "Gold";
    return "Platinum";
}

// Interest earned after a number of days, compounded daily at 5% per annum
double projectedInterest(double balance, int days)
{
    double projected = balance;
    int i;

    for (i = 0; i < days; i++)
        projected = projected + projected * (INTEREST_RATE / 365);
    return projected - balance;
}

// Writes a money value as a quoted CSV field, e.g. ,"1,234.56"
void writeMoneyField(FILE *fp, double value)
{
    char text[64];
    formatMoney(value, text);
    fprintf(fp, ",\"%s\"", text);
}

// Sort helpers: highest value first, account number breaks ties
// Used by qsort for Report 1
int compareTier(const void *a, const void *b)
{
    const TierRow *x = a, *y = b;
    if (x->adb != y->adb) {
        if (x->adb > y->adb)
            return -1;
        return 1;
    }
    return accounts[x->accIndex].number - accounts[y->accIndex].number;
}

// Used by qsort for Report 2
int compareInterest(const void *a, const void *b)
{
    const InterestRow *x = a, *y = b;
    if (x->interest != y->interest) {
        if (x->interest > y->interest)
            return -1;
        return 1;
    }
    return accounts[x->accIndex].number - accounts[y->accIndex].number;
}

// Groups transactions by date, then account, keeping file order inside a group
int compareTransByDayAccount(const void *a, const void *b)
{
    const Transaction *x = &trans[*(const int *) a], *y = &trans[*(const int *) b];
    if (x->dayNum != y->dayNum)
        return x->dayNum - y->dayNum;
    if (x->accIndex != y->accIndex)
        return accounts[x->accIndex].number - accounts[y->accIndex].number;
    return *(const int *) a - *(const int *) b;
}

// Ascending by date, Suspicious before Normal, then account number
int compareDayStat(const void *a, const void *b)
{
    const DayStat *x = a, *y = b;
    if (x->dayNum != y->dayNum)
        return x->dayNum - y->dayNum;
    if (x->suspicious != y->suspicious)
        return y->suspicious - x->suspicious;
    return accounts[x->accIndex].number - accounts[y->accIndex].number;
}

// Balance before a deposit = balance after it minus the amount
double balanceBefore(const Transaction *t)
{
    return t->balance - t->amount;
}

// Deposit as a percentage of the balance before it
double depositPercent(const Transaction *t)
{
    return t->amount / balanceBefore(t) * 100;
}

// Risk level of a deposit for Report 4: 3 = High, 2 = Medium, 1 = Low, 0 = not flagged
int depositRiskLevel(const Transaction *t)
{
    double percent;

    // withdrawals and opening deposits (balance before is 0) are never flagged
    if (!t->isDeposit || balanceBefore(t) < 0.01)
        return 0;

    percent = depositPercent(t);
    if (t->amount >= MIN_FLAG_DEPOSIT) {
        if (percent >= HIGH_RISK_PCT)
            return 3;
        if (percent >= MEDIUM_RISK_PCT)
            return 2;
        if (percent >= LOW_RISK_PCT)
            return 1;
        return 0;
    }

    // smaller deposits are only flagged if they more than triple the balance
    if (percent >= SMALL_DEPOSIT_PCT)
        return 1;
    return 0;
}

// Turns a risk level number into its label
const char *riskLabel(int level)
{
    if (level == 3)
        return "High Risk";
    if (level == 2)
        return "Medium Risk";
    return "Low Risk";
}

// Used by qsort for Report 4: highest risk first, then highest percentage, then earlier date
int compareUnusualDeposit(const void *a, const void *b)
{
    const Transaction *x = &trans[*(const int *) a], *y = &trans[*(const int *) b];
    if (depositRiskLevel(x) != depositRiskLevel(y))
        return depositRiskLevel(y) - depositRiskLevel(x);
    if (depositPercent(x) != depositPercent(y)) {
        if (depositPercent(x) > depositPercent(y))
            return -1;
        return 1;
    }
    if (x->dayNum != y->dayNum)
        return x->dayNum - y->dayNum;
    return *(const int *) a - *(const int *) b;
}

// Reports

// Report 1: average daily balance of the latest month and privilege tier
void report1PrivilegeTiers(void)
{
    FILE *fp;
    char bal[64], adb[64];
    int i, a, d, count = 0, latest, month = 1, year = 1970, monthStart, numDays;
    double running, sum;

    latest = latestDayNum();
    for (i = 0; i < numTrans; i++)
        if (trans[i].dayNum == latest) {
            month = trans[i].month;
            year = trans[i].year;
        }
    monthStart = toDayNumber(1, month, year);
    numDays = daysInMonth(month, year);

    for (a = 0; a < numAccounts; a++) {
        balanceBeforeMonth[a] = 0.00;
        for (d = 1; d <= 31; d++)
            hasEod[a][d] = 0;
    }

    // the last transaction of a day gives that day's end-of-day balance
    for (i = 0; i < numTrans; i++) {
        if (trans[i].dayNum < monthStart) {
            balanceBeforeMonth[trans[i].accIndex] = trans[i].balance;
        } else if (trans[i].dayNum < monthStart + numDays) {
            eodBalance[trans[i].accIndex][trans[i].day] = trans[i].balance;
            hasEod[trans[i].accIndex][trans[i].day] = 1;
        }
    }

    // days without transactions keep the previous day's balance
    for (a = 0; a < numAccounts; a++) {
        running = balanceBeforeMonth[a];
        sum = 0.00;
        for (d = 1; d <= numDays; d++) {
            if (hasEod[a][d])
                running = eodBalance[a][d];
            sum += running;
        }
        if (sum / numDays >= ADB_MINIMUM) {
            tierRows[count].accIndex = a;
            tierRows[count].adb = sum / numDays;
            count++;
        }
    }
    qsort(tierRows, count, sizeof(TierRow), compareTier);

    fp = fopen(REPORT1_FILE, "w");
    if (fp == NULL) {
        printf("Could not create %s.\n", REPORT1_FILE);
        return;
    }
    fprintf(fp, "AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier\n");

    printf("Report 1: Banking Privilege Tiers (based on %02d/%04d)\n", month, year);
    printf("%-22s %10s %18s %20s %13s\n", "AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier");

    for (i = 0; i < count; i++) {
        Account *acc = &accounts[tierRows[i].accIndex];
        int age = latest - acc->openDayNum;

        fprintf(fp, "%s,%d", acc->name, age);
        writeMoneyField(fp, acc->balance);
        writeMoneyField(fp, tierRows[i].adb);
        fprintf(fp, ",%s\n", privilegeTier(tierRows[i].adb));

        if (i < 2) {
            formatMoney(acc->balance, bal);
            formatMoney(tierRows[i].adb, adb);
            printf("%-22s %10d %18s %20s %13s\n", acc->name, age, bal, adb, privilegeTier(tierRows[i].adb));
        }
    }
    fclose(fp);
    printf("(Full table exported to %s, %d rows)\n\n", REPORT1_FILE, count);
}

// Report 2: expected interest in the next 30 days. Returns the total interest expense.
double report2InterestProjection(void)
{
    FILE *fp;
    char bal[64], interest[64];
    int i, count = 0;
    double total = 0.00;

    for (i = 0; i < numAccounts; i++) {
        if (accounts[i].balance >= INTEREST_MINIMUM) {
            interestRows[count].accIndex = i;
            interestRows[count].interest = projectedInterest(accounts[i].balance, PROJECTION_DAYS);
            total += interestRows[count].interest;
            count++;
        }
    }
    qsort(interestRows, count, sizeof(InterestRow), compareInterest);

    fp = fopen(REPORT2_FILE, "w");
    if (fp == NULL) {
        printf("Could not create %s.\n", REPORT2_FILE);
        return total;
    }
    fprintf(fp, "AccountName,CurrentBalance,ExpectedInterest\n");

    printf("Report 2: Interest Expense Projection (next %d days)\n", PROJECTION_DAYS);
    printf("%-22s %18s %18s\n", "AccountName", "CurrentBalance", "ExpectedInterest");

    for (i = 0; i < count; i++) {
        Account *acc = &accounts[interestRows[i].accIndex];

        fprintf(fp, "%s", acc->name);
        writeMoneyField(fp, acc->balance);
        writeMoneyField(fp, interestRows[i].interest);
        fprintf(fp, "\n");

        if (i < 2) {
            formatMoney(acc->balance, bal);
            formatMoney(interestRows[i].interest, interest);
            printf("%-22s %18s %18s\n", acc->name, bal, interest);
        }
    }
    fclose(fp);
    printf("(Full table exported to %s, %d rows)\n\n", REPORT2_FILE, count);
    return total;
}

// Report 3: daily withdrawals, deposits and turnover velocity per account
void report3SuspiciousBehavior(void)
{
    FILE *fp;
    char wd[64], dep[64], vel[64];
    int i, count = 0;
    double velocity;

    for (i = 0; i < numTrans; i++)
        transOrder[i] = i;
    qsort(transOrder, numTrans, sizeof(int), compareTransByDayAccount);

    // build one DayStat per account per day
    for (i = 0; i < numTrans; i++) {
        Transaction *t = &trans[transOrder[i]];

        if (count == 0 || dayStats[count - 1].dayNum != t->dayNum || dayStats[count - 1].accIndex != t->accIndex) {
            dayStats[count].accIndex = t->accIndex;
            dayStats[count].dayNum = t->dayNum;
            dayStats[count].day = t->day;
            dayStats[count].month = t->month;
            dayStats[count].year = t->year;
            dayStats[count].withdrawals = 0.00;
            dayStats[count].deposits = 0.00;
            dayStats[count].numDeposits = 0;
            count++;
        }

        if (t->isDeposit) {
            dayStats[count - 1].deposits += t->amount;
            dayStats[count - 1].numDeposits++;
        } else {
            dayStats[count - 1].withdrawals += t->amount;
        }
    }

    // velocity is only defined on days with deposits
    for (i = 0; i < count; i++) {
        DayStat *s = &dayStats[i];
        s->suspicious = s->withdrawals >= SUSPICIOUS_WITHDRAWAL;
        if (s->deposits > 0 && s->withdrawals / s->deposits * 100 >= SUSPICIOUS_VELOCITY)
            s->suspicious = 1;
    }
    qsort(dayStats, count, sizeof(DayStat), compareDayStat);

    fp = fopen(REPORT3_FILE, "w");
    if (fp == NULL) {
        printf("Could not create %s.\n", REPORT3_FILE);
        return;
    }
    fprintf(fp, "AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity(%%),RiskFlagLevel\n");

    printf("Report 3: Suspicious Client Behavior\n");
    printf("%-22s %-10s %16s %8s %16s %10s %-10s\n", "AccountName", "Date", "TotalWithdrawal", "Deposits", "TotalDeposit", "Velocity%", "RiskFlag");

    for (i = 0; i < count; i++) {
        DayStat *s = &dayStats[i];
        const char *flag;

        if (s->suspicious)
            flag = "Suspicious";
        else
            flag = "Normal";

        if (s->deposits > 0) {
            velocity = s->withdrawals / s->deposits * 100;
            formatMoney(velocity, vel);
        } else {
            strcpy(vel, "N/A");
        }

        fprintf(fp, "%s,%02d/%02d/%04d", accounts[s->accIndex].name, s->day, s->month, s->year);
        writeMoneyField(fp, s->withdrawals);
        fprintf(fp, ",%d", s->numDeposits);
        writeMoneyField(fp, s->deposits);
        fprintf(fp, ",\"%s\",%s\n", vel, flag);

        if (i < 2) {
            formatMoney(s->withdrawals, wd);
            formatMoney(s->deposits, dep);
            printf("%-22s %02d/%02d/%04d %16s %8d %16s %10s %-10s\n",
                   accounts[s->accIndex].name, s->day, s->month, s->year, wd, s->numDeposits, dep, vel, flag);
        }
    }
    fclose(fp);
    printf("(Full table exported to %s, %d rows)\n\n", REPORT3_FILE, count);
}

// Report 4: Unusual Large Deposits, labeled Low, Medium or High Risk, highest risk first
void report4UnusualDeposits(void)
{
    FILE *fp;
    char amount[64], before[64], after[64], percent[64];
    int i, count = 0;

    for (i = 0; i < numTrans; i++)
        if (depositRiskLevel(&trans[i]) > 0)
            unusualDeposits[count++] = i;
    qsort(unusualDeposits, count, sizeof(int), compareUnusualDeposit);

    fp = fopen(REPORT4_FILE, "w");
    if (fp == NULL) {
        printf("Could not create %s.\n", REPORT4_FILE);
        return;
    }
    fprintf(fp, "AccountName,AccountNumber,Date,DepositAmount,BalanceBefore,BalanceAfter,DepositPercent(%%),RiskLevel\n");

    printf("Report 4: Unusual Large Deposits\n");
    printf("%-22s %-10s %-10s %16s %16s %16s %10s %-11s\n", "AccountName", "AccountNo", "Date", "Deposit", "BalanceBefore", "BalanceAfter", "Percent", "RiskLevel");

    for (i = 0; i < count; i++) {
        Transaction *t = &trans[unusualDeposits[i]];
        Account *acc = &accounts[t->accIndex];

        fprintf(fp, "%s,%010d,%02d/%02d/%04d", acc->name, acc->number, t->day, t->month, t->year);
        writeMoneyField(fp, t->amount);
        writeMoneyField(fp, balanceBefore(t));
        writeMoneyField(fp, t->balance);
        writeMoneyField(fp, depositPercent(t));
        fprintf(fp, ",%s\n", riskLabel(depositRiskLevel(t)));

        if (i < 2) {
            formatMoney(t->amount, amount);
            formatMoney(balanceBefore(t), before);
            formatMoney(t->balance, after);
            formatMoney(depositPercent(t), percent);
            printf("%-22s %010d %02d/%02d/%04d %16s %16s %16s %10s %-11s\n",
                   acc->name, acc->number, t->day, t->month, t->year, amount, before, after, percent,
                   riskLabel(depositRiskLevel(t)));
        }
    }
    fclose(fp);
    printf("(Full table exported to %s, %d rows)\n\n", REPORT4_FILE, count);
}

// Writes summary.json with totals across all reports
void writeSummary(double totalInterest)
{
    FILE *fp;
    int i;
    double deposits = 0.00, withdrawals = 0.00, balance = 0.00;

    for (i = 0; i < numTrans; i++) {
        if (trans[i].isDeposit)
            deposits += trans[i].amount;
        else
            withdrawals += trans[i].amount;
    }
    for (i = 0; i < numAccounts; i++)
        balance += accounts[i].balance;

    printf("Summary State (%s):\n", SUMMARY_FILE);
    printf("{\"total_accounts\": %d, \"total_deposits\": %.2f, \"total_withdrawals\": %.2f, "
           "\"total_balance\": %.2f, \"total_interest_expense\": %.2f}\n",
           numAccounts, deposits, withdrawals, balance, totalInterest);

    fp = fopen(SUMMARY_FILE, "w");
    if (fp == NULL) {
        printf("Could not create %s.\n", SUMMARY_FILE);
        return;
    }
    fprintf(fp, "{\n  \"total_accounts\": %d,\n  \"total_deposits\": %.2f,\n  \"total_withdrawals\": %.2f,\n"
                "  \"total_balance\": %.2f,\n  \"total_interest_expense\": %.2f\n}\n",
            numAccounts, deposits, withdrawals, balance, totalInterest);
    fclose(fp);
}

// Generate Reports menu: load the file first, then generate all reports
void generateReports(void)
{
    int choice, loaded = 0;
    double totalInterest;

    while (1) {
        printf("Generate Reports\n");
        printf("[1] Load the file\n");
        printf("[2] Generate Reports\n\n");
        choice = readChoice("Enter choice: ", 1, 2);
        printf("\n");

        if (choice == 1) {
            loaded = loadData(1);
            if (!loaded)
                return;
            printf("\n");
        } else if (!loaded) {
            printf("Please load the file first.\n\n");
        } else if (numTrans == 0) {
            printf("The file has no valid PHP transactions to report on.\n");
            return;
        } else {
            printf("Generating reports...\n");
            printf("Outputs saved to individual files...\n\n");
            report1PrivilegeTiers();
            totalInterest = report2InterestProjection();
            report3SuspiciousBehavior();
            report4UnusualDeposits();
            writeSummary(totalInterest);
            return;
        }
    }
}

// Main Menu

// Loads existing accounts, then shows the main menu until the user exits
int main(void)
{
    int choice;
    int running = 1;

    if (loadData(0))
        printf("Loaded %d accounts from %s.\n", numAccounts, DATA_FILE);
    else
        printf("%s not found. It will be created when the first account is registered.\n", DATA_FILE);

    while (running) {
        printf("\nSelect Transaction:\n");
        printf("[1] Register Account\n");
        printf("[2] Deposit Amount\n");
        printf("[3] Withdraw Amount\n");
        printf("[4] Currency Exchange\n");
        printf("[5] Record Exchange Rates\n");
        printf("[6] Show Interest Amount\n");
        printf("[7] Generate Reports\n\n");

        choice = readChoice("Choice: ", 1, 7);
        printf("\n");

        switch (choice) {
            case 1: registerAccount();    break;
            case 2: deposit();            break;
            case 3: withdraw();           break;
            case 4: currencyExchange();   break;
            case 5: recordExchangeRate(); break;
            case 6: showInterest();       break;
            case 7: generateReports();    break;
        }

        printf("\n");
        running = readYesNo("Back to the Main Menu (Y/N): ");
    }

    printf("Thank you for banking with us!\n");
    return 0;
}
