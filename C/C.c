/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: C
Paradigm: Imperative
********************
*/

#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX_NAME      100
#define MAX_ACCOUNTS  50
#define NUM_CURRENCY  6
#define PHP           0
#define INTEREST_RATE 0.05
#define START_BALANCE 1000.00

// One bank account: owner name and balance
typedef struct {
    char name[MAX_NAME];
    double balance;      // always stored in PHP (base currency)
} Account;

// Display names of the supported currencies, in menu order
const char *CURRENCY_NAMES[NUM_CURRENCY] = {
    "Philippine Peso (PHP)",
    "United States Dollar (USD)",
    "Japanese Yen (JPY)",
    "British Pound Sterling (GBP)",
    "Euro (EUR)",
    "Chinese Yuan Renminni (CNY)"
};

// PHP value of 1 unit of each currency. PHP is the base and stays 1.00.
double rates[NUM_CURRENCY] = { 1.00, 62.00, 0.40, 84.00, 72.00, 9.00 };

// ---------- Input helpers ----------

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

// Returns 1 if any registered account already has this name (any case).
int nameTaken(Account accounts[], int count, const char *name)
{
    int i;
    for (i = 0; i < count; i++)
        if (sameName(accounts[i].name, name))
            return 1;
    return 0;
}

// Returns the index of the account with this name, or -1 if none.
int findAccount(Account accounts[], int count, const char *name)
{
    int i;
    for (i = 0; i < count; i++)
        if (strcmp(accounts[i].name, name) == 0)
            return i;
    return -1;
}

// Asks for an account name until an existing one is entered. Returns NULL only if no accounts exist.
Account *selectAccount(Account accounts[], int count)
{
    char name[MAX_NAME];
    int index;

    if (count == 0) {
        printf("No account registered yet. Please register an account name first.\n");
        return NULL;
    }

    while (1) {
        printf("Account Name: ");
        scanf(" %99[^\n]", name);
        clearInput();

        index = findAccount(accounts, count, name);
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


// Adds a new account with a unique name and the starting balance
void registerAccount(Account accounts[], int *count)
{
    char newName[MAX_NAME];

    printf("Register Account Name\n");
    if (*count == MAX_ACCOUNTS) {
        printf("Maximum number of accounts reached.\n");
        return;
    }

    while (1) {
        printf("Account Name: ");
        scanf(" %99[^\n]", newName);
        clearInput();

        if (!nameTaken(accounts, *count, newName))
            break;

        printf("An account with this name is already registered.\n");
    }

    strcpy(accounts[*count].name, newName);
    accounts[*count].balance = START_BALANCE;
    (*count)++;
}

// Adds money to an existing account
void deposit(Account accounts[], int count)
{
    Account *acc;
    double amount;

    printf("Deposit Amount\n");
    acc = selectAccount(accounts, count);
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("\n");
    amount = readAmountInPHP("Deposit Amount: ", readTransactionCurrency());

    acc->balance = acc->balance + amount;
    printf("Updated Balance: %.2f\n", acc->balance);
}

// Takes money out of an existing account if the balance allows it
void withdraw(Account accounts[], int count)
{
    Account *acc;
    int currency;
    double amount;

    printf("Withdraw Amount\n");
    acc = selectAccount(accounts, count);
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("\n");

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

    acc->balance = acc->balance - amount;
    printf("Updated Balance: %.2f\n", acc->balance);
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
void showInterest(Account accounts[], int count)
{
    Account *acc;
    int days, day;
    double balance, interest;

    printf("Show Interest Amount\n");
    acc = selectAccount(accounts, count);
    if (acc == NULL)
        return;

    printAccountHeader(acc);
    printf("Interest Rate: %.0f%%\n\n", INTEREST_RATE * 100);

    days = readChoice("Total Number of Days: ", 1, 36500);

    balance = acc->balance;
    printf("\nDay | Interest | Balance |\n");
    for (day = 1; day <= days; day++) {
        // round interest to centavos before adding, like the figure (0.14/day)
        interest = (int) (balance * (INTEREST_RATE / 365) * 100 + 0.5) / 100.0;
        balance = balance + interest;
        printf("%-3d | %-8.2f | %-7.2f |\n", day, interest, balance);
    }
}


// Shows the main menu and runs the chosen transaction until the user exits
int main(void)
{
    Account accounts[MAX_ACCOUNTS];
    int numAccounts = 0;
    int choice;
    int running = 1;

    while (running) {
        printf("\nSelect Transaction:\n");
        printf("[1] Register Account Name\n");
        printf("[2] Deposit Amount\n");
        printf("[3] Withdraw Amount\n");
        printf("[4] Currency Exchange\n");
        printf("[5] Record Exchange Rates\n");
        printf("[6] Show Interest Amount\n\n");

        choice = readChoice("Choice: ", 1, 6);
        printf("\n");

        switch (choice) {
            case 1: registerAccount(accounts, &numAccounts); break;
            case 2: deposit(accounts, numAccounts);          break;
            case 3: withdraw(accounts, numAccounts);         break;
            case 4: currencyExchange();                      break;
            case 5: recordExchangeRate();                    break;
            case 6: showInterest(accounts, numAccounts);     break;
        }

        printf("\n");
        running = readYesNo("Back to the Main Menu (Y/N): ");
    }

    printf("Thank you for banking with us!\n");
    return 0;
}
