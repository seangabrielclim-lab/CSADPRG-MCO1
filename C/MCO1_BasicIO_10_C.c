/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: C
Paradigm(s): Functional 
********************
 */
 
#include <stdio.h>

int main()
{
    int choice = 0, currency = 0;
    char name[100];
    double amount = 0.0, rate = 0.0, input = 0.0;
    char *currencies[6] = {
        "Philippine Peso (PHP)",
        "United States Dollar (USD)",
        "Japanese Yen (JPY)",
        "British Pound Sterling (GBP)",
        "Euro (EUR)",
        "Chinese Yuan Renminni (CNY)"
    };

    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n\n");
    printf("Choice: ");
    scanf("%d", &choice);
    printf("\n***\n");
    printf("Choice = %d\n\n", choice);

    printf("Register Account Name\n");
    printf("Account Name: ");
    scanf(" %99[^\n]", name);
    printf("\n***\n");
    printf("Account Name = %s\n\n", name);

    printf("Deposit Amount\n");
    printf("Account Name: ");
    scanf(" %99[^\n]", name);
    printf("Current Balance: 1000.00\n");
    printf("Currency: PHP\n\n");
    printf("Deposit Amount: ");
    scanf("%lf", &amount);
    printf("\n***\n");
    printf("Account Name = %s\n", name);
    printf("Deposit Amount = %.2f\n\n", amount);

    printf("Withdraw Amount\n");
    printf("Account Name: ");
    scanf(" %99[^\n]", name);
    printf("Current Balance: 1000.00\n");
    printf("Currency: PHP\n\n");
    printf("Withdraw Amount: ");
    scanf("%lf", &amount);
    printf("\n***\n");
    printf("Account Name = %s\n", name);
    printf("Withdraw Amount = %.2f\n\n", amount);

    printf("Foreign Currency Exchange\n");
    printf("input Amount (PHP): ");
    scanf("%lf", &input);
    printf("\nExchanged Currency\n");
    printf("[1] Philippine Peso (PHP) = %.2f\n", input);
    printf("[2] United States Dollar (USD) = %.2f\n", input * 62.00);
    printf("[3] Japanese Yen (JPY) = %.2f\n", input * 0.40);
    printf("[4] British Pound Sterling (GBP) = %.2f\n", input * 84.00);
    printf("[5] Euro (EUR) = %.2f\n", input * 72.00);
    printf("[6] Chinese Yuan Renminni (CNY) = %.2f\n", input * 9.00);
    printf("\n***\n");
    printf("input Currency = Philippine Peso (PHP)\n");
    printf("input Amount (PHP) = %.2f\n\n", input);

    printf("Record Exchange Rate\n\n");
    printf("[1] Philippine Peso (PHP)\n");
    printf("[2] United States Dollar (USD)\n");
    printf("[3] Japanese Yen (JPY)\n");
    printf("[4] British Pound Sterling (GBP)\n");
    printf("[5] Euro (EUR)\n");
    printf("[6] Chinese Yuan Renminni (CNY)\n\n");
    printf("Select Foreign Currency: ");
    scanf(" [%d]", &currency);
    printf("Exchange Rate: ");
    scanf("%lf", &rate);
    printf("\n***\n");
    if (currency >= 1 && currency <= 6)
        printf("Select Foreign Currency = [%d] %s\n", currency, currencies[currency - 1]);
    else
        printf("Select Foreign Currency = [%d] (invalid selection)\n", currency);
    printf("Exchange Rate = %.2f\n", rate);

    return 0;
}
