/*
********************
Last names: Chan, Cheng, Jugno, Lim
Language: Java
Paradigm(s): Object-oriented
********************
 */

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.HashMap;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        //intialization and state variables
        boolean exited = false;
        int choice;
        String back = "";
        UserDB userDB = new UserDB();

        //hash map for currencies
        HashMap<Integer, Double> Currencies = new HashMap();
        Currencies.put(1, 1.00); // php
        Currencies.put(2, 62.00); // usd
        Currencies.put(3, 0.40); // jpy
        Currencies.put(4, 84.00); // gbp
        Currencies.put(5, 72.00); // eur
        Currencies.put(6, 9.00); // cny

        //test user
        User test = new User("n");
        userDB.addUser(test);

        //main loop
        do {
            System.out.println("Select Transaction:");
            System.out.println("[1] Register Account Name");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");
            System.out.println();

            System.out.print("Choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println();

                //switch case for main menu options
                switch (choice) {
                    case 1:
                        boolean done1 = false;
                        while (!done1) {
                            System.out.println("Register Account Name");
                            System.out.print("Account Name: ");
                            String name = sc.nextLine();
                            User check = userDB.findUserByName(name);
                            if (check == null) {
                                User newUser = new User(name);
                                userDB.addUser(newUser);
                                done1 = true;
                            } else {
                                System.out.println("\n*** Error Invalid Input ***\n");
                            }
                        }
                        break;
                    case 2:
                        int depCurrency = 0;
                        User d = null;

                        boolean done2 = false;
                        while (!done2) {
                            //checking if there are no users
                            if(userDB.getUsers().isEmpty()) {
                                System.out.println("\n*** Error Invalid Input ***\n");
                                done2 = true;
                            } else{
                                System.out.println("Deposit Amount");

                                //while loop for checking if user exists
                                boolean valDepName = false;
                                while (!valDepName) {
                                    System.out.print("Account Name: ");
                                    String depositAcct = sc.nextLine();

                                    d = userDB.findUserByName(depositAcct);
                                    if(d == null) {
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                    } else {
                                        System.out.printf("Current Balance: %.2f\n", d.getAmount());
                                        System.out.println("Currency: " + d.getCurrency());
                                        valDepName = true;
                                    }
                                }

                                System.out.println("\n[1] Philippine Peso (PHP)");
                                System.out.println("[2] United States Dollar (USD)");
                                System.out.println("[3] Japanese Yen (JPY)");
                                System.out.println("[4] British Pound Sterling (GBP)");
                                System.out.println("[5] Euro (EUR)");
                                System.out.println("[6] Chinese Yuan Renmimi (CNY)");

                                //while loop for checking if currency is valid
                                boolean valDepCurrency = false;
                                while (!valDepCurrency) {
                                    System.out.print("Transaction Currency: ");
                                    try{
                                        depCurrency = sc.nextInt();
                                        if (!Currencies.containsKey(depCurrency)) {
                                            throw new IllegalArgumentException("*** Error Invalid Input ***");
                                        } else {
                                            valDepCurrency = true;
                                            sc.nextLine();
                                        }
                                    }catch (InputMismatchException | IllegalArgumentException e) {
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                        sc.nextLine();
                                    }
                                }

                                //while loop for checking if amount is valid
                                boolean valDepAmount = false;
                                while (!valDepAmount) {
                                    try{
                                        System.out.print("Deposit Amount: ");
                                        double deposit = sc.nextDouble();
                                        if (!(deposit > 0) || Double.isInfinite(deposit)) {
                                            throw new IllegalArgumentException("*** Error Invalid Input ***");
                                        }else{
                                            sc.nextLine();
                                            deposit = deposit*Currencies.get(depCurrency);
                                            userDB.depositFunds(d, deposit);
                                            System.out.printf("Updated Balance: %.2f\n", d.getAmount());
                                            valDepAmount = true;
                                            done2 = true;
                                        }
                                    }catch(InputMismatchException | IllegalArgumentException e){
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                        sc.nextLine();
                                    }
                                }
                            }
                        }
                        break;
                    case 3:
                        int withCurrency = 0;
                        User w = null;

                        boolean done3 = false;
                        while (!done3) {
                            if(userDB.getUsers().isEmpty()){
                                System.out.println("\n*** Error Invalid Input ***\n");
                                done3 = true;
                            } else {
                                System.out.println("Withdraw Amount");

                                //while loop for checking if user exists
                                boolean valWitName = false;
                                while (!valWitName) {
                                    System.out.print("Account Name: ");
                                    String depositAcct = sc.nextLine();

                                    w = userDB.findUserByName(depositAcct);
                                    if(w == null) {
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                    } else {
                                        System.out.printf("Current Balance: %.2f\n", w.getAmount());
                                        System.out.println("Currency: " + w.getCurrency());
                                        valWitName = true;
                                    }
                                }

                                System.out.println("\n[1] Philippine Peso (PHP)");
                                System.out.println("[2] United States Dollar (USD)");
                                System.out.println("[3] Japanese Yen (JPY)");
                                System.out.println("[4] British Pound Sterling (GBP)");
                                System.out.println("[5] Euro (EUR)");
                                System.out.println("[6] Chinese Yuan Renmimi (CNY)");

                                //while loop for checking if currency is valid
                                boolean valWitCurrency = false;
                                while (!valWitCurrency) {
                                    System.out.print("Transaction Currency: ");
                                    try{
                                        withCurrency = sc.nextInt();
                                        if (!Currencies.containsKey(withCurrency)) {
                                            throw new IllegalArgumentException("*** Error Invalid Input ***");
                                        } else {
                                            valWitCurrency = true;
                                            sc.nextLine();
                                        }
                                    }catch (InputMismatchException | IllegalArgumentException e) {
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                        sc.nextLine();
                                    }
                                }

                                //while loop for checking if amount is valid
                                boolean valWitAmount = false;
                                while (!valWitAmount) {
                                    try{
                                        System.out.print("Withdraw Amount: ");
                                        double withdraw = sc.nextDouble();
                                        withdraw = withdraw*Currencies.get(withCurrency);
                                        if (!(withdraw > 0) || Double.isInfinite(withdraw)) {
                                            throw new IllegalArgumentException("*** Error Invalid Input ***");
                                        }
                                        else if (w.getAmount() < withdraw) {
                                            throw new IllegalArgumentException("*** Error Invalid Input ***");
                                        }
                                        else{
                                            sc.nextLine();
                                            userDB.withdrawFunds(w, withdraw);
                                            System.out.printf("Updated Amount: %.2f\n", w.getAmount());
                                            valWitAmount = true;
                                            done3 = true;
                                        }
                                    }catch(InputMismatchException | IllegalArgumentException e){
                                        System.out.println("\n*** Error Invalid Input ***\n");
                                        sc.nextLine();
                                    }
                                }
                            }
                        }
                        break;
                    case 4:
                        int scOption = 0;
                        double sourceAmount = 0;
                        boolean done4 = false;
                        String resume = "";

                        while (!done4) {
                            System.out.println("Foreign Currency Exchange");
                            System.out.println("Source Currency Option:");
                            System.out.println("[1] Philippine Peso (PHP)");
                            System.out.println("[2] United States Dollar (USD)");
                            System.out.println("[3] Japanese Yen (JPY)");
                            System.out.println("[4] British Pound Sterling (GBP)");
                            System.out.println("[5] Euro (EUR)");
                            System.out.println("[6] Chinese Yuan Renmimi (CNY)");
                            System.out.println();

                            //while loop for checking if currency selected is valid
                            boolean valSourceCurrency = false;
                            while (!valSourceCurrency) {
                                System.out.print("Source Currency: ");
                                try{
                                    scOption = sc.nextInt();
                                    if (!Currencies.containsKey(scOption)) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    } else {
                                        valSourceCurrency = true;
                                        sc.nextLine();
                                    }
                                }catch (InputMismatchException | IllegalArgumentException e) {
                                    System.out.println("\n*** Error Invalid Input ***\n");
                                    sc.nextLine();
                                }
                            }

                            //while loop for checking if amount inputted is valid
                            boolean valSourceAmount = false;
                            while (!valSourceAmount) {
                                System.out.print("Source Amount: ");
                                try{
                                    sourceAmount = sc.nextDouble();
                                    if (!(sourceAmount > 0) || Double.isInfinite(sourceAmount)) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    } else {
                                        sourceAmount = sourceAmount * Currencies.get(scOption);
                                        valSourceAmount = true;
                                        sc.nextLine();
                                    }
                                }catch (InputMismatchException | IllegalArgumentException e) {
                                    System.out.println("\n*** Error Invalid Input ***\n");
                                    sc.nextLine();
                                }
                            }

                            System.out.println("\nExchanged Currency Option:");
                            System.out.println("[1] Philippine Peso (PHP)");
                            System.out.println("[2] United States Dollar (USD)");
                            System.out.println("[3] Japanese Yen (JPY)");
                            System.out.println("[4] British Pound Sterling (GBP)");
                            System.out.println("[5] Euro (EUR)");
                            System.out.println("[6] Chinese Yuan Renmimi (CNY)");
                            System.out.println();

                            //while loop for checking if exchange currency is valid
                            boolean valDestCurrency = false;
                            while (!valDestCurrency) {
                                System.out.print("Exchanged Currency: ");
                                try{
                                    int ecOption = sc.nextInt();
                                    if (!Currencies.containsKey(ecOption)) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    } else {
                                        double ecAmount = sourceAmount / Currencies.get(ecOption);
                                        System.out.printf("Exchange Amount: %.2f\n", ecAmount);
                                        valDestCurrency = true;
                                        sc.nextLine();
                                    }
                                }catch (InputMismatchException | IllegalArgumentException e) {
                                    System.out.println("\n*** Error Invalid Input ***\n");
                                    sc.nextLine();
                                }
                            }

                            //while loop for checking if user want to continue exchanging currency and validation
                            boolean valContinuation = false;
                            while (!valContinuation) {
                                System.out.print("\nConvert another currency (Y/N)?... ");
                                resume =  sc.nextLine();
                                if(resume.equalsIgnoreCase("N") || resume.equalsIgnoreCase("Y")){
                                    valContinuation = true;
                                }
                                else{
                                    System.out.println("\n*** Error Invalid Input ***\n");
                                }
                            }

                            if(resume.equalsIgnoreCase("N")){
                                done4 = true;
                            }
                            System.out.println();
                        }
                        break;
                    case 5:
                        int fCurrency = 0;
                        System.out.println("Record Exchange Rate\n");
                        System.out.println("[1] Philippine Peso (PHP)");
                        System.out.println("[2] United States Dollar (USD)");
                        System.out.println("[3] Japanese Yen (JPY)");
                        System.out.println("[4] British Pound Sterling (GBP)");
                        System.out.println("[5] Euro (EUR)");
                        System.out.println("[6] Chinese Yuan Renmimi (CNY)\n");

                        //while loop for checking if currency selected is valid
                        boolean valid = false;
                        while (!valid) {
                            System.out.print("Select Foreign Currency: ");
                            try {
                                fCurrency = sc.nextInt();
                                if (!Currencies.containsKey(fCurrency)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                } else {
                                    valid = true;
                                }
                            }catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("\n*** Error Invalid Input ***\n");
                                sc.nextLine();
                            }
                        }

                        //while loop for checking if new rate is valid
                        boolean valRate = false;
                        while (!valRate) {
                            try{
                                System.out.print("Exchange Rate: ");
                                double fRate = sc.nextDouble();
                                if (!(fRate > 0) || Double.isInfinite(fRate)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                }
                                Currencies.replace(fCurrency, fRate);
                                sc.nextLine();
                                valRate = true;
                            }catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("\n*** Error Invalid Input ***\n");
                                sc.nextLine();
                            }
                        }
                        break;
                    case 6:
                        boolean done6 = false;
                        while (!done6) {
                            if (userDB.getUsers().isEmpty()) {
                                System.out.println("\n*** Error Invalid Input ***\n");
                                done6 = true;
                            }
                            else{
                                System.out.println("Show Interest Amount");
                                System.out.print("Account Name: ");
                                String acctName = sc.nextLine();

                                User interestAcct = userDB.findUserByName(acctName);
                                if (interestAcct == null) {
                                    System.out.println("\n*** Error Invalid Input ***\n");
                                } else {
                                    System.out.printf("Current Balance: %.2f\n", interestAcct.getAmount());
                                    System.out.println("Currency: " + interestAcct.getCurrency());
                                    System.out.println("Interest Rate: 5%");
                                    System.out.println();
                                    boolean validDays = false;
                                    while (!validDays) {
                                        System.out.print("Total Number of Days:");
                                        try {
                                            int days = sc.nextInt();
                                            if (days > 0 && days <= 3650) {
                                                sc.nextLine();
                                                validDays = true;
                                                double interestRate = interestAcct.getRate();
                                                double[] amounts = new double[days];
                                                userDB.interestFunds(interestAcct, days, interestRate, amounts);
                                                double prev = interestAcct.getAmount();
                                                System.out.println("Day | Interest | Balance |");
                                                for (int i = 0; i < days; i++) {
                                                    double interest = amounts[i] - prev;
                                                    System.out.printf("%-3d | %-8.2f | %.2f |\n", i + 1, interest, amounts[i]);
                                                    prev = amounts[i];
                                                }
                                                done6 = true;
                                            } else {
                                                throw new IllegalArgumentException("*** Error Invalid Input ***");
                                            }
                                            System.out.println();
                                        } catch (InputMismatchException | IllegalArgumentException e) {
                                            System.out.println("\n*** Error Invalid Input ***\n");
                                            sc.nextLine();
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("\n*** Error Invalid Input ***\n");
                        break;
                }

                //checks if the user wants to continue back to main menu, ensures only Y or N inputs are accepted
                boolean valContinue = false;
                while (!valContinue) {
                    System.out.print("\nBack to the Main Menu (Y/N): ");
                    back = sc.nextLine();

                    if (back.equalsIgnoreCase("Y") || back.equalsIgnoreCase("N")) {
                        valContinue = true;
                    } else {
                        System.out.println("\n*** Error Invalid Input ***\n");
                    }
                }

                //exits if the users inputs N
                if (back.equalsIgnoreCase("N")) {
                    exited = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("\n*** Error Invalid Input ***\n");
                sc.nextLine();
            }
        } while (!exited);
    }
}