import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class UserDB {
    Scanner sc = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private long largestId;

    public UserDB(){
        users = new ArrayList<>();
        Reader r = new Reader();
        transactions = r.readData();
        largestId = 0;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public long getLargestId(){
        return largestId;
    }

    public ArrayList<Transaction> getTransactions(){
        return transactions;
    }

    public void setLargestId(long largestId){
        this.largestId  = largestId;
    }

    public void loadUsers(){
        for(int i = 0; i < getTransactions().size(); i++){
            Transaction t = getTransactions().get(i);
            String aName = t.getAccountName();
            double bAmount = t.getCurrentBalance();
            long id = t.getAccountNumber();
            User u = this.findUserByName(aName);
            if(u == null){
                u = new User(aName, bAmount, id);
                users.add(u);
            }
            else{
                u.setAmount(bAmount);
            }
        }
    }

    public void addNewUser(String name, double amount){
        this.findLargestId();
        long newId = this.getLargestId() + 1;
        User user = new User(name, amount, newId);
        this.addNewTransaction(user, "PHP", amount, "deposit");
        this.setLargestId(newId);
        users.add(user);
    }

    public void addNewTransaction(User user, String currency, double amount, String type){
        Transaction t = new Transaction(user.getName(), user.getId(), LocalDate.now(), LocalTime.now(), currency, amount, type, user.getAmount());
        this.transactions.add(t);
        Writer w = new Writer();
        w.writeTransaction(t, user);
    }

    public void displayUsers(){
        if(getUsers().isEmpty()){
            System.out.println("No users found.");
        }
        else{
            System.out.println("===LIST OF USERS===");
            for(int i = 0; i < getUsers().size(); i++){
                System.out.println(i+1 + ". " + getUsers().get(i).getName() + " ID: " + getUsers().get(i).getId() + " Amount: " + getUsers().get(i).getAmount());
            }
        }
    }

    public void findLargestId(){
        long id = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber() > this.getLargestId()) {
                id = transaction.getAccountNumber();
                this.setLargestId(id);
            }
        }
    }

    public User findUserByName(String name){
        for(int i = 0; i < getUsers().size(); i++){
            if(getUsers().get(i).getName().equalsIgnoreCase(name)){
                return getUsers().get(i);
            }
        }
        return null;
    }

    public void depositFunds(User user, double amount, String currency){
        user.setAmount(user.getAmount()+amount);
        this.addNewTransaction(user, currency, amount, "deposit");
    }

    public void withdrawFunds(User user, double amount, String currency){
        user.setAmount(user.getAmount()-amount);
        this.addNewTransaction(user, currency, amount, "withdrawal");
    }

    public boolean interestFunds(User user, int days, double rate, double[] amounts){
        if(days < 0){
            return false;
        }
        else{
            double currentAmount = user.getAmount();
            double interestRate =  rate/365.0;
            for(int i = 0; i < days; i++){
                double interest = Math.round(currentAmount * interestRate * 100) / 100.0;
                currentAmount += interest;
                amounts[i] = currentAmount;
            }
            return true;
        }
    }

    public void generateReport1(){
        LocalDate today = getTransactions().getLast().getDate();
        LocalDate firstDay = today.withDayOfMonth(1);
        int sCtr = 0;
       for(int i = 0; i < getUsers().size(); i++){
           User u = getUsers().get(i);
           double mostRecentAmount = 0;
           double sum = 0;
           LocalDate mostRecentDate = firstDay;
           LocalDate oldestDate = today;

           for(int j = 0; j < getTransactions().size(); j++){
               Transaction t = getTransactions().get(j);
               YearMonth ym = YearMonth.from(t.getDate());

               if(t.getDate().isBefore(oldestDate) && t.getAccountName().equalsIgnoreCase(u.getName())){
                   oldestDate = t.getDate();
               }
               //searches for the last transaction before current month
               if(ym.isBefore(YearMonth.from(today)) && t.getAccountName().equalsIgnoreCase(u.getName())){
                   mostRecentAmount = t.getCurrentBalance();
                   mostRecentDate = t.getDate();
               }
               else if(t.getDate().isEqual(firstDay) && t.getAccountName().equalsIgnoreCase(u.getName())){
                   mostRecentAmount = t.getCurrentBalance();
                   mostRecentDate = t.getDate();
               }
               else if(ym.compareTo(YearMonth.from(today)) == 0 && t.getAccountName().equalsIgnoreCase(u.getName()) && mostRecentDate.isBefore(firstDay)){
                   mostRecentDate = firstDay;
               }
               else if(t.getDate().isEqual(mostRecentDate) && t.getAccountName().equalsIgnoreCase(u.getName())){
                   mostRecentAmount = t.getCurrentBalance();
               }

               //sums the values
               if(t.getDate().isAfter(mostRecentDate) && t.getAccountName().equalsIgnoreCase(u.getName())){
                   long daysBetween = ChronoUnit.DAYS.between(mostRecentDate, t.getDate());
                   sum += mostRecentAmount* daysBetween;
                   mostRecentDate = t.getDate();
                   mostRecentAmount = t.getCurrentBalance();
               }
           }

           u.setAccountAge(ChronoUnit.DAYS.between(oldestDate, today));
           if(!mostRecentDate.isAfter(YearMonth.from(today).atEndOfMonth())){
               long last = ChronoUnit.DAYS.between(mostRecentDate, YearMonth.from(today).atEndOfMonth());
               sum += mostRecentAmount* (last+1);
           }
           double average = sum / today.lengthOfMonth();
           u.setAdb(average);

           if(average >= 100000.00 && average < 500000.00){
               getUsers().get(i).setTier("silver");
               sCtr++;
           }
           else if(average >= 500000.00 && average < 1000000.00){
               getUsers().get(i).setTier("gold");
           }
           else if(average >= 1000000.00){
               getUsers().get(i).setTier("platinum");
           }
           else{
               getUsers().get(i).setTier(null);
           }
       }

        Writer w = new Writer();
        w.writeReport1(getUsers());
    }

    public void generateReport2(){
        int n = getUsers().size();
        String[] names = new String[n];
        double[] amounts = new double[n];
        double[] interests = new double[n];

        for(int i = 0; i < n; i++){
            double[] amts = new double[30];
            User u = getUsers().get(i);
            if(u.getAmount() >= 10000){
                interestFunds(u, 30, u.getRate(), amts);
                names[i] = u.getName();
                amounts[i] = u.getAmount();
                interests[i] = amts[29] - u.getAmount();
            }
        }

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if (interests[minIndex] < interests[j]) {
                    minIndex = j;
                }
            }

            double tempAmount = amounts[minIndex];
            double tempInterest = interests[minIndex];
            String tempName = names[minIndex];

            amounts[minIndex] = amounts[i];
            interests[minIndex] = interests[i];
            names[minIndex] = names[i];

            amounts[i] = tempAmount;
            interests[i] = tempInterest;
            names[i] = tempName;
        }

        for(int i = 0; i < 2; i++){
            System.out.printf("%s | %.2f | %.2f\n", names[i], amounts[i], interests[i]);
        }
        Writer w = new Writer();
        w.writeReport2(names, amounts, interests, n);
    }
}
