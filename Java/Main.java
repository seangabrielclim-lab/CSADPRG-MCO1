import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.HashMap;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exited = false;
        int choice;

        UserDB userDB = new UserDB();
        HashMap<Integer, Double> Currencies = new HashMap();
        Currencies.put(1, 1.00); // php
        Currencies.put(2, 62.00); // usd
        Currencies.put(3, 0.40); // jpy
        Currencies.put(4, 84.00); // gbp
        Currencies.put(5, 72.00); // eur
        Currencies.put(6, 9.00); // cny

        User test = new User("Sean");
        test.setAmount(1000.00);
        userDB.addUser(test);

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
                                System.out.println("*** Error Invalid Input ***");
                            }
                        }
                        break;
                    case 2:
                        boolean done2 = false;
                        while (!done2) {
                            System.out.println("Deposit Amount");
                            System.out.print("Account Name: ");
                            String depositAcct = sc.nextLine();

                            try {
                                User d = userDB.findUserByName(depositAcct);
                                if (d == null) {
                                    System.out.println("*** Error Invalid Input ***");
                                } else {
                                    System.out.printf("\nCurrent Balance: %.2f\n", d.getAmount());
                                    System.out.println("Currency: " + d.getCurrency());
                                    System.out.print("Deposit Amount: ");
                                    double deposit = sc.nextDouble();
                                    if (!(deposit > 0) || Double.isInfinite(deposit)) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    }
                                    sc.nextLine();
                                    boolean depSuccess = userDB.depositFunds(d, deposit);
                                    if (depSuccess) {
                                        System.out.println("\n***");
                                        System.out.println("Account Name = " + d.getName());
                                        System.out.printf("Deposit Amount = %.2f\n", deposit);
                                        done2 = true;
                                    } else {
                                        System.out.println("*** Error Invalid Input ***");
                                    }
                                }
                            } catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("*** Error Invalid Input ***");
                                sc.nextLine();
                            }
                        }
                        break;
                    case 3:
                        boolean done3 = false;
                        while (!done3) {
                            System.out.println("Withdraw Amount");
                            System.out.print("Account Name: ");
                            String withdrawAcct = sc.nextLine();

                            try {
                                User w = userDB.findUserByName(withdrawAcct);
                                if (w == null) {
                                    System.out.println("*** Error Invalid Input ***");
                                } else {
                                    System.out.printf("\nCurrent Balance: %.2f\n", w.getAmount());
                                    System.out.println("Currency: " + w.getCurrency());
                                    System.out.print("Withdraw Amount: ");
                                    double withdraw = sc.nextDouble();
                                    if (!(withdraw > 0) || Double.isInfinite(withdraw)) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    }
                                    if (w.getAmount() < withdraw) {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    }
                                    sc.nextLine();
                                    boolean withSuccess = userDB.withdrawFunds(w, withdraw);
                                    if (withSuccess) {
                                        System.out.println("\n***");
                                        System.out.println("Account Name = " + w.getName());
                                        System.out.printf("Withdraw Amount = %.2f\n", withdraw);
                                        done3 = true;
                                    } else {
                                        System.out.println("*** Error Invalid Input ***");
                                    }
                                }
                            } catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("*** Error Invalid Input ***");
                                sc.nextLine();
                            }
                        }
                        break;
                    case 4:
                        boolean done4 = false;
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

                            System.out.print("Source Currency: ");

                            try {
                                int scOption = sc.nextInt();
                                if (!Currencies.containsKey(scOption)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                }
                                System.out.print("Source Amount: ");
                                double sourceAmount = sc.nextDouble();
                                if (!(sourceAmount > 0) || Double.isInfinite(sourceAmount)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                }
                                sourceAmount = sourceAmount * Currencies.get(scOption);
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
                                if (!Currencies.containsKey(ecOption)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                } else {
                                    double ecAmount = sourceAmount / Currencies.get(ecOption);
                                    System.out.printf("Exchange Amount: %.2f\n", ecAmount);
                                    sc.nextLine();
                                    done4 = true;
                                }
                            } catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("*** Error Invalid Input ***");
                                sc.nextLine();
                            }
                        }
                        break;
                    case 5:
                        boolean done5 = false;
                        while (!done5) {
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
                            try {
                                int fCurrency = sc.nextInt();
                                if (!Currencies.containsKey(fCurrency)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                }
                                System.out.print("Exchange Rate: ");
                                double fRate = sc.nextDouble();
                                if (!(fRate > 0) || Double.isInfinite(fRate)) {
                                    throw new IllegalArgumentException("*** Error Invalid Input ***");
                                }
                                Currencies.replace(fCurrency, fRate);
                                System.out.println("\n***");
                                if (fCurrency == 1) {
                                    System.out.printf("Select Foreign Currency = %s\n", fCurrency);
                                } else if (fCurrency == 2) {
                                    System.out.printf("Select Foreign Currency = USD\n");
                                } else if (fCurrency == 3) {
                                    System.out.printf("Select Foreign Currency = JPY\n");
                                } else if (fCurrency == 4) {
                                    System.out.printf("Select Foreign Currency = GBP\n");
                                } else if (fCurrency == 5) {
                                    System.out.printf("Select Foreign Currency = EUR\n");
                                } else {
                                    System.out.printf("Select Foreign Currency = CNY\n");
                                }
                                System.out.printf("Exchange Rate = %.2f", fRate);
                                System.out.println();
                                sc.nextLine();
                                done5 = true;
                            } catch (InputMismatchException | IllegalArgumentException e) {
                                System.out.println("*** Error Invalid Input ***");
                                sc.nextLine();
                            }
                        }
                        break;
                    case 6:
                        boolean done6 = false;
                        while (!done6) {
                            System.out.println("Show Interest Amount");
                            System.out.print("Account Name: ");
                            String acctName = sc.nextLine();

                            User interestAcct = userDB.findUserByName(acctName);
                            if (interestAcct == null) {
                                System.out.println("*** Error Invalid Input ***");
                            } else {
                                System.out.println("Current Balance: " + interestAcct.getAmount());
                                System.out.println("Currency: " + interestAcct.getCurrency());
                                System.out.println("Interest Rate: 5%");
                                System.out.println();
                                System.out.print("Total Number of Days:");
                                try {
                                    int days = sc.nextInt();
                                    if (days > 0 && days <= 3650) {
                                        sc.nextLine();
                                        double interestRate = 1 + interestAcct.getRate();
                                        double[] amounts = new double[days];
                                        userDB.interestFunds(interestAcct, days, interestRate, amounts);
                                        System.out.println("Day | Interest | Balance |");
                                        for (int i = 0; i < days; i++) {
                                            System.out.printf("%-3d | %-8.2f | %.2f |\n", i + 1, interestRate,
                                                    amounts[i]);
                                        }
                                        done6 = true;
                                    } else {
                                        throw new IllegalArgumentException("*** Error Invalid Input ***");
                                    }
                                    System.out.println();
                                } catch (InputMismatchException | IllegalArgumentException e) {
                                    System.out.println("*** Error Invalid Input ***");
                                    sc.nextLine();
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("*** Error Invalid Input ***");
                        break;
                }
                System.out.println("Back to the Main Menu (Y/N): ");
                String back = sc.nextLine();
                if (back.equalsIgnoreCase("N")) {
                    exited = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("*** Error Invalid Input ***");
                sc.nextLine();
            }
        } while (!exited);
    }
}