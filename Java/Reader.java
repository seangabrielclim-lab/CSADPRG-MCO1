import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Reader {
    public ArrayList<Transaction> readData(){
        String fileName = "bank_transactions.csv";
        ArrayList<Transaction> transactions = new ArrayList<>();
        int total = 0, skippedBlank = 0, skippedCurrency = 0, parseErrors = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // header
            while ((line = br.readLine()) != null) {
                total++;
                String[] v = line.split(",", -1);

                if (v.length < 8) { parseErrors++; continue; }

                boolean missing = false;
                for (int i = 0; i < 8; i++) {
                    if (v[i].isBlank()) { missing = true; break; }
                }
                if (missing) { skippedBlank++; continue; }

                if (!v[4].trim().equalsIgnoreCase("PHP")) { skippedCurrency++; continue; }

                try {
                    String name = v[0].trim();
                    long accountNum = Long.parseLong(v[1].trim());
                    LocalDate date = LocalDate.parse(v[2].trim(), DateTimeFormatter.ofPattern("d/M/yyyy"));
                    LocalTime time = LocalTime.parse(v[3].trim(), DateTimeFormatter.ofPattern("H:mm:ss"));
                    String currency = v[4].trim();
                    double amount = Double.parseDouble(v[5].trim());
                    String type = v[6].trim().toLowerCase();
                    double balance = Double.parseDouble(v[7].trim());
                    transactions.add(new Transaction(name, accountNum, date, time, currency, amount, type, balance));
                } catch (DateTimeParseException | NumberFormatException e) {
                    parseErrors++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        System.out.println("Processing dataset... (" + total + " rows loaded, " + transactions.size() + " filtered for PHP currency)");
        System.out.println("Skipped (missing values): " + skippedBlank);
        System.out.println("Invalid rows: " + parseErrors);
        return transactions;
    }
}
