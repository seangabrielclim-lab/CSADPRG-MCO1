import java.util.Scanner;

public class MCO1_BasicIO_Group10_Java {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        boolean exited = false;
        int choice;
        /*
        UserDB userDB = new UserDB();
        PHP php = new PHP();
        USD usd = new USD();
        JPY jpy = new JPY();
        GBP gbp = new GBP();
        EUR eur = new EUR();
        CNY cny = new CNY();*/

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

            System.out.println("***");
            System.out.println("Choice = " + choice);

            switch (choice) {
                case 1:
                    System.out.println("Register Account Name");
                    System.out.print("Account Name: ");
                    String name = sc.nextLine();

                    //basic print statement block
                    System.out.println("\n***");
                    System.out.println("Account Name = " + name);

                /*IGNORE THIS PART
                User newUser = new User(name);
                userDB.addUser(newUser);

                User n  = userDB.findUserByName(name);
                System.out.println("***");
                System.out.println("Account Name = " + n.getName());*/
                    break;
                case 2:
                    System.out.println("Deposit Amount");
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

                /* IGNORE THIS PART
                User d = userDB.findUserByName(depositAcct);
                if(d == null){
                    System.out.println("Account Not Found");
                }
                else{
                    System.out.printf("\nCurrent Balance: %.2f", d.getAmount());
                    System.out.println("Currency: " +  d.getCurrency());
                    System.out.println();
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
                }*/
                    break;
                case 3:
                    System.out.println("Withdraw Amount");
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

                /* IGNORE THIS PART
                User w = userDB.findUserByName(withdrawAcct);
                if(w == null){
                    System.out.println("Account Not Found");
                }
                else{
                    System.out.printf("\nCurrent Balance: %.2f", w.getAmount());
                    System.out.println("Currency: " +  w.getCurrency());
                    System.out.println();
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
                }*/
                    break;
                case 4:
                    System.out.println("Foreign Currency Exchange");
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

                /* IGNORE THIS PART
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
                double amount = sc.nextDouble();
                sc.nextLine();

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
                double ecAmount = 0;
                switch (scOption) {
                    case 1:
                        switch(ecOption) {
                            case 1:
                                ecAmount = php.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = php.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = php.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = php.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = php.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = php.convertToCNY(amount);
                                break;
                        }
                        break;
                    case 2:
                        switch(ecOption) {
                            case 1:
                                ecAmount = usd.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = usd.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = usd.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = usd.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = usd.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = usd.convertToCNY(amount);
                                break;
                        }
                        break;
                    case 3:
                        switch(ecOption) {
                            case 1:
                                ecAmount = jpy.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = jpy.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = jpy.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = jpy.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = jpy.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = jpy.convertToCNY(amount);
                                break;
                        }
                        break;
                    case 4:
                        switch(ecOption) {
                            case 1:
                                ecAmount = gbp.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = gbp.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = gbp.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = gbp.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = gbp.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = gbp.convertToCNY(amount);
                                break;
                        }
                        break;
                    case 5:
                        switch(ecOption) {
                            case 1:
                                ecAmount = eur.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = eur.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = eur.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = eur.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = eur.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = eur.convertToCNY(amount);
                                break;
                        }
                        break;
                    case 6:
                        switch(ecOption) {
                            case 1:
                                ecAmount = cny.convertToPHP(amount);
                                break;
                            case 2:
                                ecAmount = cny.convertToUSD(amount);
                                break;
                            case 3:
                                ecAmount = cny.convertToJPY(amount);
                                break;
                            case 4:
                                ecAmount = cny.convertToGBP(amount);
                                break;
                            case 5:
                                ecAmount = cny.convertToEUR(amount);
                                break;
                            case 6:
                                ecAmount = cny.convertToCNY(amount);
                                break;
                        }
                        break;
                }
                System.out.printf("Exchange Amount: %.2f\n", ecAmount);
                sc.nextLine();*/
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
                    String fCurrency = sc.nextLine();
                    System.out.print("Exchange Rate: ");
                    double fRate = sc.nextDouble();
                    sc.nextLine();

                    System.out.println("\n***");
                    System.out.printf("Select Foreign Currency = %s\n", fCurrency);
                    System.out.printf("Exchange Rate = %.2f", fRate);
                    System.out.println();
                    break;
                case 6:
                    System.out.println("Show Interest Amount");
                    System.out.print("Account Name:");
                    String interestAcct = sc.nextLine();

                /* IGNORE THIS PART
                User inter = userDB.findUserByName(interestAcct);
                if(inter == null){
                    System.out.println("Account Not Found");
                }
                else {
                    System.out.println("Current Balance: " + inter.getAmount());
                    System.out.println("Currency: " + inter.getCurrency());
                    System.out.println("Interest Rate: 5%");
                    System.out.println();
                    System.out.print("Total Number of Days:");
                    int days = sc.nextInt();
                    if(days > 0){
                        double interestRate = 0;
                        double[] amounts = new double[days];
                        userDB.interestFunds(inter, days, interestRate, amounts);
                        System.out.println("Day | Interest | Balance |");
                        for(int i = 0; i < days; i++){
                            System.out.printf("%-3d | %-8.2f | %.2f |\n", i+1, interest, amounts[i]);
                        }
                    }

                    System.out.println();
                }*/
                    break;
            }
            System.out.println("Back to the Main Menu (Y/N):");
            String back = sc.nextLine();
            if(back.equalsIgnoreCase("N")) {
                exited = true;
            }
        }while(!exited);
    }
}

