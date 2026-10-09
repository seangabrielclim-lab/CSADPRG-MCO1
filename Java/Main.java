import java.util.Scanner;
import java.util.HashMap;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        boolean exited = false;
        int choice;

        UserDB userDB = new UserDB();
        HashMap<Integer, Double> Currencies = new HashMap();
        Currencies.put(1, 1.00); //php
        Currencies.put(2, 62.00); //usd
        Currencies.put(3, 0.40); //jpy
        Currencies.put(4, 84.00); //gbp
        Currencies.put(5, 72.00); //eur
        Currencies.put(6, 9.00); //cny

        do{
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

            switch (choice) {
                case 1:
                    System.out.println("Register Account Name");
                    System.out.print("Account Name: ");
                    String name = sc.nextLine();

                    User newUser = new User(name);
                    userDB.addUser(newUser);
                    break;
                case 2:
                    System.out.println("Deposit Amount");
                    System.out.print("Account Name: ");
                    String depositAcct = sc.nextLine();

                    User d = userDB.findUserByName(depositAcct);
                    if(d == null){
                        System.out.println("Account Not Found");
                    }
                    else{
                        System.out.printf("\nCurrent Balance: %.2f\n", d.getAmount());
                        System.out.println("Currency: " +  d.getCurrency());
                        System.out.print("Deposit Amount: ");
                        double deposit = sc.nextDouble();
                        sc.nextLine();
                        boolean depSuccess = userDB.depositFunds(d, deposit);
                        if(depSuccess){
                            System.out.println("\n***");
                            System.out.println("Account Name = " + d.getName());
                            System.out.printf("Deposit Amount = %.2f\n", deposit);
                        }
                        else{
                            System.out.println("Invalid Amount Deposited");
                        }
                    }
                    break;
                case 3:
                    System.out.println("Withdraw Amount");
                    System.out.print("Account Name: ");
                    String withdrawAcct = sc.nextLine();

                    User w = userDB.findUserByName(withdrawAcct);
                    if(w == null){
                        System.out.println("Account Not Found");
                    }
                    else{
                        System.out.printf("\nCurrent Balance: %.2f\n", w.getAmount());
                        System.out.println("Currency: " +  w.getCurrency());
                        System.out.print("Withdraw Amount: ");
                        double withdraw = sc.nextDouble();
                        sc.nextLine();
                        boolean withSuccess = userDB.withdrawFunds(w, withdraw);
                        if(withSuccess){
                            System.out.println("\n***");
                            System.out.println("Account Name = " + w.getName());
                            System.out.printf("Withdraw Amount = %.2f\n", withdraw);
                        }
                        else{
                            System.out.println("Invalid Amount Withdrawn");
                        }
                    }
                    break;
                case 4:
                    System.out.println("Foreign Currency Exchange");
                    System.out.println("Source Currency Option:");
                    System.out.println("[1] Philippine Peso (PHP)");
                    System.out.println("[2] United States Dollar (USD)");
                    System.out.println("[3] Japanese Yen (JPY)");
                    System.out.println("[4] British Pound Sterling (GBP)");
                    System.out.println("[5] Euro (EUR)");
                    System.out.println("[6] Chinese Yuan Renminni (CNY)");
                    System.out.println();

                    System.out.print("Source Currency: ");
                    int scOption = sc.nextInt();
                    System.out.print("Source Amount: ");
                    double sourceAmount = sc.nextDouble();
                    sc.nextLine();

                    sourceAmount = sourceAmount * Currencies.get(scOption);

                    System.out.println(" ");
                    System.out.println("Exchanged Currency Options:");
                    System.out.println("[1] Philippine Peso (PHP)");
                    System.out.println("[2] United States Dollar (USD)");
                    System.out.println("[3] Japanese Yen (JPY)");
                    System.out.println("[4] British Pound Sterling (GBP)");
                    System.out.println("[5] Euro (EUR)");
                    System.out.println("[6] Chinese Yuan Renmimi (CNY)");
                    System.out.println(" ");
                    System.out.print("Exchange Currency: ");
                    int ecOption = sc.nextInt();
                    double ecAmount = sourceAmount / Currencies.get(ecOption);

                    System.out.printf("Exchange Amount: %.2f\n", ecAmount);
                    sc.nextLine();
                    break;
                case 5:
                    System.out.println("Record Exchange Rate");
                    System.out.println();
                    System.out.println("[1] Philippine Peso (PHP)");
                    System.out.println("[2] United States Dollar (USD)");
                    System.out.println("[3] Japanese Yen (JPY)");
                    System.out.println("[4] British Pound Sterling (GBP)");
                    System.out.println("[5] Euro (EUR)");
                    System.out.println("[6] Chinese Yuan Renmimi (CNY)");
                    System.out.println();

                    System.out.print("Select Foreign Currency: ");
                    int fCurrency = sc.nextInt();
                    System.out.print("Exchange Rate: ");
                    double fRate = sc.nextDouble();
                    sc.nextLine();

                    Currencies.replace(fCurrency, fRate);
                    break;
                case 6:
                    System.out.println("Show Interest Amount");
                    System.out.print("Account Name: ");
                    String acctName = sc.nextLine();

                    User interestAcct = userDB.findUserByName(acctName);
                    if(interestAcct == null){
                        System.out.println("Account Not Found");
                    }
                    else {
                        System.out.println("Current Balance: " + interestAcct.getAmount());
                        System.out.println("Currency: " + interestAcct.getCurrency());
                        System.out.println("Interest Rate: 5%");
                        System.out.println();
                        System.out.print("Total Number of Days:");
                        int days = sc.nextInt();

                        if(days > 0){
                            double interestRate = interestAcct.getRate()/days;
                            double[] amounts = new double[days];
                            userDB.interestFunds(interestAcct, days, interestRate, amounts);
                            System.out.println("Day | Interest | Balance |");
                            for(int i = 0; i < days; i++){
                                System.out.printf("%-3d | %-8.2f | %.2f |\n", i+1, interestRate, amounts[i]);
                            }
                        }
                        System.out.println();
                    }
                    break;
            }
            System.out.println("Back to the Main Menu (Y/N): ");
            String back = sc.nextLine();
            if(back.equalsIgnoreCase("N")) {
                exited = true;
            }
        }while(!exited);
    }
}