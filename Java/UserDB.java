import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class UserDB {
    Scanner sc = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Transaction> dup = new ArrayList<>();
    private long largestId;

    public UserDB(){
        users = new ArrayList<>();
        largestId = 0;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public long getLargestId(){
        return largestId;
    }
    public ArrayList<Transaction> getSaveOrder(){
        return dup;
    }

    public ArrayList<Transaction> getTransactions(){
        return transactions;
    }

    public void setLargestId(long largestId){
        this.largestId  = largestId;
    }

    public void loadUsers(){
        Reader r = new Reader();
        transactions = r.readData();
        dup = new ArrayList<>(transactions);
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime));

        for(int i = 0; i < getSaveOrder().size(); i++){
            Transaction t = getSaveOrder().get(i);
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
        this.dup.add(t);
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime));
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

    public int generateReport1(){
        int n = 0;
        LocalDate today = getTransactions().getLast().getDate();
        LocalDate firstDay = today.withDayOfMonth(1);
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
               getUsers().get(i).setTier("Silver");
               n++;
           }
           else if(average >= 500000.00 && average < 1000000.00){
               getUsers().get(i).setTier("Gold");
               n++;
           }
           else if(average >= 1000000.00){
               getUsers().get(i).setTier("Platinum");
               n++;
           }
           else{
               getUsers().get(i).setTier(null);
           }
       }

        Writer w = new Writer();
        w.writeReport1(getUsers());

        return n;
    }

    public void generateReport2(){
        int n = getUsers().size();
        int m = 0;
        String[] names = new String[n];
        double[] amounts = new double[n];
        double[] interests = new double[n];

        for(int i = 0; i < n; i++){
            double[] amts = new double[30];
            User u = getUsers().get(i);
            if(u.getAmount() >= 10000){
                interestFunds(u, 30, u.getRate(), amts);
                names[m] = u.getName();
                amounts[m] = u.getAmount();
                interests[m] = amts[29] - u.getAmount();
                m++;
            }
        }

        for (int i = 0; i < m - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < m; j++) {
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

        for(int i = 0; i < Math.min(2, m); i++){
            System.out.printf("%s | %.2f | %.2f\n", names[i], amounts[i], interests[i]);
        }
        Writer w = new Writer();
        w.writeReport2(names, amounts, interests, m);
    }

    public void generateReport3(){
       HashMap<String, double[]> balances = new HashMap<>();

       for(int i = 0; i < getTransactions().size(); i++){
           Transaction t = getTransactions().get(i);
           String entry = t.getAccountName() + ":" + t.getDate().toString();
           if(!(balances.containsKey(entry))){
                if(t.getType().equals("deposit")){
                    balances.put(entry, new double[]{t.getAmount(), 0, 1});
                }
                else if(t.getType().equals("withdrawal")){
                    balances.put(entry, new double[]{0, t.getAmount(), 0});
                }

           }
           else{
               if(t.getType().equals("deposit")){
                   double count = balances.get(entry)[2];
                   double depositAmount = balances.get(entry)[0];
                   double withdrawalAmount = balances.get(entry)[1];
                   balances.put(entry, new double[]{t.getAmount() + depositAmount, withdrawalAmount, count + 1});
               }
               else if(t.getType().equals("withdrawal")){
                   double count = balances.get(entry)[2];
                   double depositAmount = balances.get(entry)[0];
                   double withdrawalAmount = balances.get(entry)[1];
                   balances.put(entry, new double[]{depositAmount, t.getAmount() + withdrawalAmount, count});
               }
           }

           double count = balances.get(entry)[2];
           double depositAmount = balances.get(entry)[0];
           double withdrawalAmount = balances.get(entry)[1];
           double turnoverVelocity = 0;

           if(depositAmount > 0){
               turnoverVelocity = (withdrawalAmount / depositAmount) * 100;
           }

           if(withdrawalAmount >= 500000 || turnoverVelocity >= 90){
               double[] a = {depositAmount, withdrawalAmount, count, 1, turnoverVelocity};
               balances.put(entry, a);
           }
           else{
               double[] a = {depositAmount, withdrawalAmount, count, 0, turnoverVelocity};
               balances.put(entry, a);
           }
       }

       ArrayList<String[]> sortedBalances = new ArrayList<>();
       balances.forEach((entry, amount) -> {
           String[] nameDate = entry.split(":");
           String name = nameDate[0];
           String date = nameDate[1];
           String count = String.format("%.0f", amount[2]);
           String turnover = String.format("%.2f", amount[4]);
           String risk = "";
           if(amount[3] > 0){
               risk = "Suspicious";
           }
           else{
               risk = "Normal";
           }
           sortedBalances.add(new String[]{name, date, String.format("%.2f", amount[1]), count, String.format("%.2f", amount[0]), turnover, risk});
       });
        sortedBalances.sort(Comparator
                .comparing((String[] r) -> r[1])
                .thenComparing(r -> r[6], Comparator.reverseOrder())
                .thenComparing(r -> r[0]));


        for(int i = 0; i < Math.min(2, sortedBalances.size()); i++){
            String[] sample = sortedBalances.get(i);
            System.out.printf("%s | %s | %s | %s | %s | %s | %s\n", sample[0], sample[1], sample[2], sample[3], sample[4], sample[5], sample[6]);
        }
       Writer w = new Writer();
       w.writeReport3(sortedBalances);
    }

    public void generateReport4(){
        TreeMap<YearMonth, Double> monthlyBalances = new TreeMap<>();
        YearMonth earliestMonth = YearMonth.from(getTransactions().get(0).getDate());
        double totalDeposit = 0;
        int depCount = 0;

        for(int i = 0; i < getTransactions().size(); i++){
            Transaction t = getTransactions().get(i);
            YearMonth currentMonth = YearMonth.from(t.getDate());

            if(currentMonth.isAfter(earliestMonth)){
                monthlyBalances.put(earliestMonth, totalDeposit/depCount);
                earliestMonth = currentMonth;
                totalDeposit = 0;
                depCount = 0;
            }
            if(t.getType().equals("deposit")){
                totalDeposit += t.getAmount();
                depCount++;
                //System.out.println(totalDeposit);
            }

        }
        monthlyBalances.put(earliestMonth, totalDeposit/depCount);

        
        int[] displayed = {0};
        monthlyBalances.forEach((key, v) -> {
            if (displayed[0] < 2) {
            System.out.printf("%s | %.2f\n", key, v);
            displayed[0]++;
            }
        });

        Writer w = new Writer();
        w.writeReport4(monthlyBalances);
    }

    public void generateSummary(){
        int count = getTransactions().size();
        double dep = 0;
        double withdrawal = 0;

        for (int i = 0; i < count; i++) {
            Transaction t = getTransactions().get(i);
            if(t.getType().equals("deposit")){
                dep+=t.getAmount();
            }
            else if(t.getType().equals("withdrawal")){
                withdrawal+=t.getAmount();
            }
        }

        double totalInterest = 0;
        for (User u : getUsers()) {
            if (u.getAmount() >= 10000) {
                double[] amts = new double[30];
                interestFunds(u, 30, u.getRate(), amts);
                totalInterest += amts[29] - u.getAmount();
            }
        }
        
        String json = """
        {
            "total_accounts": %d,
            "total_deposits": %.2f,
            "total_withdrawals": %.2f,
            "total_balance": %.2f,
            "total_interest_expense": %.2f
        }
        """.formatted(getUsers().size(), dep, withdrawal, (dep - withdrawal), totalInterest);

        System.out.println(json);
        Writer w = new Writer();
        w.writeSummary(json);
    }
}
