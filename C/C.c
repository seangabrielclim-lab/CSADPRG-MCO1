/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: C
********************
*/

#include <stdio.h>

#define MAX_NAME      100
#define MAX_LINE      256
#define NUM_CURRENCY  6
#define PHP           0
#define INTEREST_RATE 0.05

typedef struct {
    char name[MAX_NAME];
    double balance;      /* always stored in PHP (base currency) */
    int registered;
} Account;

const char *CURRENCY_NAMES[NUM_CURRENCY] = {
    "Philippine Peso (PHP)",
    "United States Dollar (USD)",
    "Japanese Yen (JPY)",
    "British Pound Sterling (GBP)",
    "Euro (EUR)",
    "Chinese Yuan Renminni (CNY)"
};

const char *CURRENCY_CODES[NUM_CURRENCY] = {
    "PHP", "USD", "JPY", "GBP", "EUR", "CNY"
};

double rates[NUM_CURRENCY] = { 1.00, 62.00, 0.40, 84.00, 72.00, 9.00 };

void clearInput(void)
{
    int charac;
    while ((charac = getchar()) != '\n' && charac != EOF)
        ;
}

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

void printCurrencyList(void)
{
    int i;
    for (i = 0; i < NUM_CURRENCY; i++)
        printf("[%d] %s\n", i + 1, CURRENCY_NAMES[i]);
}

double convert(double amount, int from, int to)
{
    return amount * rates[from] / rates[to];
}

double roundCents(double value)
{
    return (long long) (value * 100 + 0.5) / 100.0;
}

int requireAccount(Account *acc)
{
    if (!acc->registered) {
        printf("No account registered yet. Please register an account name first.\n");
        return 0;
    }
    return 1;
}

void printAccountHeader(Account *acc)
{
    printf("Account Name: %s\n", acc->name);
    printf("Current Balance: %.2f\n", acc->balance);
    printf("Currency: PHP\n\n");
}

double readAmountInCurrency(const char *label)
{
    char msg[MAX_LINE];
    int currency;
    double amount, inPHP;

    printCurrencyList();
    currency = readChoice("Transaction Currency: ", 1, NUM_CURRENCY) - 1;

    sprintf(msg, "%s Amount: ", label);
    amount = readPositiveAmount(msg);

    if (currency == PHP)
        return amount;

    inPHP = roundCents(convert(amount, currency, PHP));
    printf("Equivalent in PHP (rate %.2f): %.2f\n", rates[currency], inPHP);
    return inPHP;
}



void registerAccount(Account *acc)
{
    printf("Register Account Name\n");
    printf("Account Name: ");
    scanf(" %99[^\n]", acc->name);
    clearInput();

    acc->registered = 1;
}

void deposit(Account *acc)
{
    double amount;

    printf("Deposit Amount\n");
    if (!requireAccount(acc))
        return;

    printAccountHeader(acc);
    amount = readAmountInCurrency("Deposit");

    acc->balance = roundCents(acc->balance + amount);
    printf("Updated Balance: %.2f\n", acc->balance);
}

void withdraw(Account *acc)
{
    double amount;

    printf("Withdraw Amount\n");
    if (!requireAccount(acc))
        return;

    printAccountHeader(acc);
    amount = readAmountInCurrency("Withdraw");

    if (amount > acc->balance) {
        printf("Insufficient balance. Withdrawal cancelled.\n");
        printf("Updated Balance: %.2f\n", acc->balance);
        return;
    }

    acc->balance = roundCents(acc->balance - amount);
    printf("Updated Balance: %.2f\n", acc->balance);
}

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

void recordExchangeRate(void)
{
    int currency;

    printf("Record Exchange Rate\n\n");
    printCurrencyList();
    printf("\n");

    currency = readChoice("Select Foreign Currency: ", 1, NUM_CURRENCY) - 1;
    if (currency == PHP) {
        printf("PHP is the base currency; its rate is fixed at 1.00.\n");
        return;
    }

    rates[currency] = readPositiveAmount("Exchange Rate: ");
}

void showInterest(Account *acc)
{
    int days, day;
    double balance, interest;

    printf("Show Interest Amount\n");
    if (!requireAccount(acc))
        return;

    printAccountHeader(acc);
    printf("Interest Rate: %.0f%%\n\n", INTEREST_RATE * 100);

    days = readChoice("Total Number of Days: ", 1, 36500);

    /* Projection only: the account balance itself is not changed. */
    balance = acc->balance;
    printf("\nDay | Interest | Balance |\n");
    for (day = 1; day <= days; day++) {
        interest = roundCents(balance * (INTEREST_RATE / 365));
        balance = roundCents(balance + interest);
        printf("%-3d | %-8.2f | %-7.2f |\n", day, interest, balance);
    }
}


int main(void)
{
    Account acc = { "", 0.00, 0 };
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
            case 1: registerAccount(&acc); break;
            case 2: deposit(&acc);         break;
            case 3: withdraw(&acc);        break;
            case 4: currencyExchange();    break;
            case 5: recordExchangeRate();  break;
            case 6: showInterest(&acc);    break;
        }

        printf("\n");
        running = readYesNo("Back to the Main Menu (Y/N): ");
    }

    printf("Thank you for banking with us!\n");
    return 0;
}
