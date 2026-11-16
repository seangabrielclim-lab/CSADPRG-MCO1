import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;

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
            for(int i = 0; i < getTransactions().size(); i++){
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
        this.addNewTransaction(user, currency, amount, "withdraw");
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
}
