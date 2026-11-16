import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Writer {
    public void writeTransaction(Transaction t, User u) {
        try{
            FileWriter writer = new FileWriter("bank_transactions.csv", true);

            //formatting for date and time
            LocalTime transactionTime = t.getTime();
            LocalDate date = t.getDate();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
            String formattedDate = date.format(dateFormat);
            String formattedTime = timeFormat.format(transactionTime);

            //writing to file
            String line = t.getAccountName() + "," + t.getAccountNumber() + "," + formattedDate + "," + formattedTime + "," + t.getCurrency() + "," + t.getAmount() + "," + t.getType() + "," + u.getAmount();
            writer.write(line + System.lineSeparator());
            //System.out.println("Successfully wrote to the file.");
            writer.close();
        } catch(IOException e){
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }
}
