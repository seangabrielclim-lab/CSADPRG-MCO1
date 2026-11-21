import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Writer {
    public void writeTransaction(Transaction t, User u) {
        try{
            FileWriter writer = new FileWriter("bank_transactions.csv", true);

            //formatting for date and time
            LocalTime transactionTime = t.getTime();
            LocalDate date = t.getDate();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
            String formattedDate = date.format(dateFormat);
            String formattedTime = timeFormat.format(transactionTime);

            //formatting for amount
            String formattedAmount = String.format("%.2f", t.getAmount());
            String formattedCurrentBalance = String.format("%.2f", u.getAmount());

            //writing to file
            String line = t.getAccountName() + "," + t.getAccountNumber() + "," + formattedDate + "," + formattedTime + "," + t.getCurrency() + "," + formattedAmount + "," + t.getType() + "," + formattedCurrentBalance;
            writer.write(line + System.lineSeparator());
            //System.out.println("Successfully wrote to the file.");
            writer.close();
        } catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }

    public void writeReport1(ArrayList<User> udb) {
        int n = udb.size();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if (udb.get(j).getAdb() > udb.get(minIndex).getAdb()) {
                    minIndex = j;
                }
            }

            User temp = udb.get(i);
            udb.set(i, udb.get(minIndex));
            udb.set(minIndex, temp);
        }
        try{
            FileWriter report1Writer = new FileWriter("report1_banking_privilege_tiers.csv");
            String header = "AccountName" + "," + "AccountAge" + "," + "CurrentBalance" + "," + "AverageDailyBalance" + "," + "PrivilegeTier";
            report1Writer.write(header + System.lineSeparator());

            for(int i = 0; i < udb.size(); i++){
                User u = udb.get(i);
                if(u.getAdb() >= 100000.00){
                    String formattedAdb = String.format("%.2f", u.getAdb());
                    String formattedAmount = String.format("%.2f", u.getAmount());
                    String line = u.getName() + "," + u.getAccountAge() + "," + formattedAmount + "," + formattedAdb + "," + u.getTier();
                    report1Writer.write(line + System.lineSeparator());
                }
            }
            report1Writer.close();
        }catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }

    public void writeReport2(String[] names, double[] amounts, double[] interests, int n) {
        try{
            FileWriter report2 = new FileWriter("report2_interest_expense_projection.csv");
            String header = "AccountName" + "," + "CurrentBalance" + "," + "ExpectedInterest";
            report2.write(header + System.lineSeparator());

            for (int i = 0; i < n; i++){
                String formattedAmount = String.format("%.2f", amounts[i]);
                String formattedInterest = String.format("%.2f", interests[i]);
                String line = names[i] + "," + formattedAmount + "," + formattedInterest;
                report2.write(line + System.lineSeparator());
            }
            report2.close();
        }catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }
}

