import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Reader {
    public ArrayList<Transaction> readData(){
        String fileName = "bank_transactions.csv";
        String delimiter = ",";
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                // Split the line by the comma delimiter
                String[] values = line.split(delimiter);
                try{
                    if(values[0].isBlank() || values[4].isBlank() || values[6].isBlank()){
                        br.readLine();
                        //System.out.println("Skipped Line");
                    }
                    else{
                        String name = values[0];
                        long accountNum = Long.parseLong(values[1]);
                        LocalDate date = LocalDate.parse(values[2], DateTimeFormatter.ofPattern("d/M/yyyy"));
                        LocalTime time = LocalTime.parse(values[3], DateTimeFormatter.ofPattern("H:mm:ss"));
                        String currency = values[4];
                        double amount = Double.parseDouble(values[5]);
                        String type = values[6];
                        double balance = Double.parseDouble(values[7]);
                        Transaction t = new Transaction(name, accountNum, date, time, currency, amount, type, balance);
                        transactions.add(t);
                    }
                } catch(DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e){
                    br.readLine();
                    //System.out.println("Skipped Line");
                }
            }
            //System.out.println("Poop: " + transactions.size());
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        return transactions;
    }
}
