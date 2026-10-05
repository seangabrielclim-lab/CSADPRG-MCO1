/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: Java
Paradigm(s): Object-oriented
********************
 */
import java.util.Scanner;

public class MCO1_BasicIO_Group10_Java {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        int choice;

        System.out.println("Select Transaction:");
        System.out.println("[1] Register Account Name");
        System.out.println("[2] Deposit Amount");
        System.out.println("[3] Withdraw Amount");
        System.out.println("[4] Currency Exchange");
        System.out.println("[5] Record Exchange Rates");
        System.out.println("[6] Show Interest Amount");
        System.out.println();

        System.out.print("Choice: ");
        choice = sc.nextInt();
        sc.nextLine();
        System.out.println();

        System.out.println("***");
        System.out.println("Choice = " + choice);

        // Register Account Name
        System.out.println("\nRegister Account Name");
        System.out.print("Account Name: ");
        String name = sc.nextLine();

        System.out.println("\n***");
        System.out.println("Account Name = " + name);

        //Deposit Amount
        System.out.println("\nDeposit Amount");
        System.out.print("Account Name: ");
        String depositAcct = sc.nextLine();

        System.out.println("Current Balance: 1000.00");
        System.out.println("Currency: PHP");
        System.out.println();
        System.out.print("Deposit Amount: ");
        double deposit = sc.nextDouble();
        sc.nextLine();

        System.out.println("\n***");
        System.out.println("Account Name = " + depositAcct);
        System.out.printf("Deposit Amount = %.2f\n", deposit);

        //Withdraw Amount
        System.out.println("\nWithdraw Amount");
        System.out.print("Account Name: ");
        String withdrawAcct = sc.nextLine();

        System.out.println("Current Balance: 1000.00");
        System.out.println("Currency: PHP");
        System.out.println();
        System.out.print("Withdraw Amount: ");
        double withdraw = sc.nextDouble();
        sc.nextLine();

        System.out.println("\n***");
        System.out.println("Account Name = " + withdrawAcct);
        System.out.printf("Withdraw Amount = %.2f\n", withdraw);

        //Foreign Currency Exchange
        System.out.println("\nForeign Currency Exchange");
        System.out.print("Source Amount (PHP): ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.println();
        System.out.println("Exchanged Currency");
        System.out.printf("[1] Philippine Peso (PHP) = %.2f\n", amount);
        System.out.printf("[2] United States Dollar (USD) = %.2f\n", amount*62.00);
        System.out.printf("[3] Japanese Yen (JPY) = %.2f\n", amount*0.40);
        System.out.printf("[4] British Pound Sterling (GBP) = %.2f\n", amount*84.00);
        System.out.printf("[5] Euro (EUR) = %.2f\n", amount*72.00);
        System.out.printf("[6] Chinese Yuan Renminni (CNY) = %.2f\n", amount*9.00);

        System.out.println("\n***");
        System.out.println("Source Currency = Philippine Peso (PHP)");
        System.out.printf("\nSource Amount (PHP) = %.2f", amount);

        //Record Currency Exchange
        System.out.println("\nRecord Exchange Rate");
        System.out.println();
        System.out.println("[1] Philippine Peso (PHP)");
        System.out.println("[2] United States Dollar (USD)");
        System.out.println("[3] Japanese Yen (JPY)");
        System.out.println("[4] British Pound Sterling (GBP)");
        System.out.println("[5] Euro (EUR)");
        System.out.println("[6] Chinese Yuan Renmimi (CNY)");
        System.out.println();

        System.out.print("Select Foreign Currency: ");
        String fCurrency = sc.nextLine();
        System.out.print("Exchange Rate: ");
        double fRate = sc.nextDouble();
        sc.nextLine();

        System.out.println("\n***");
        System.out.printf("Select Foreign Currency = %s\n", fCurrency);
        System.out.printf("Exchange Rate = %.2f", fRate);
        System.out.println();
    }
}

