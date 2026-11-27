import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

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

    public void writeReport3(ArrayList<String[]> sortedBalances) {
        try{
            FileWriter report3 = new FileWriter("report3_suspicious_client_behavior.csv");
            String header = "AccountName" + "," + "Date" + "," + "TotalWithdrawalAmounts" + "," + "NumberOfDeposits" + "," + "TotalDepositAmounts" + "," + "TurnoverVelocity" + "," + "RiskFlagLevel";
            report3.write(header + System.lineSeparator());
            for(int i = 0; i < sortedBalances.size(); i++){
                String name = sortedBalances.get(i)[0];
                String date = sortedBalances.get(i)[1];
                String withdraw = sortedBalances.get(i)[2];
                String counter = sortedBalances.get(i)[3];
                String deposit = sortedBalances.get(i)[4];
                String turnover = sortedBalances.get(i)[5];
                String risk = sortedBalances.get(i)[6];

                String line = name + "," + date + "," + withdraw + "," + counter + "," + deposit + "," + turnover + "," + risk;
                report3.write(line + System.lineSeparator());
            }
            report3.close();
        }catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }

    public void writeReport4(TreeMap<YearMonth, Double> monthlyBalances) {
        try{
            FileWriter report4 = new FileWriter("report4_average_deposits_per_month.csv");
            String header = "YearMonth" + "," + "AverageDeposit";
            report4.write(header + System.lineSeparator());
            monthlyBalances.forEach((key, v) -> {
                String yearmonth = key.toString();
                String formattedDeposit = String.format("%.2f", v);
                String line = yearmonth + "," + formattedDeposit;
                try{
                    report4.write(line + System.lineSeparator());
                }catch(IOException e){
                    System.out.println("Error writing to file.");
                    e.printStackTrace();
                }
            });
            report4.close();
        }catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }

    public void writeSummary(String json){
        try{
            FileWriter summaryWriter = new FileWriter("summary.json");
            summaryWriter.write(json);
            summaryWriter.close();
        }catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }
}

