import java.util.ArrayList;
import java.util.Scanner;

public class UserDB {
    Scanner sc = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<User>();

    public UserDB(){
        users = new ArrayList<>();
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void displayUsers(){
        if(getUsers().isEmpty()){
            System.out.println("No users found.");
        }
        else{
            System.out.println("===LIST OF USERS===");
            for(int i = 0; i < getUsers().size(); i++){
                System.out.println(i+1 + ". " + getUsers().get(i).getName());
            }
        }
    }

    public User findUserByName(String name){
        for(int i = 0; i < getUsers().size(); i++){
            if(getUsers().get(i).getName().equals(name)){
                return getUsers().get(i);
            }
        }
        return null;
    }

    public void depositFunds(User user, double amount){
        user.setAmount(user.getAmount()+amount);
    }

    public void withdrawFunds(User user, double amount){
            user.setAmount(user.getAmount()-amount);
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
