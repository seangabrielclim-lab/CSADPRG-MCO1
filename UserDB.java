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

    public boolean depositFunds(User user, double amount){
        if(amount < 0){
            return false;
        }
        else{
            user.setAmount(user.getAmount()+amount);
            return true;
        }
    }

    public boolean withdrawFunds(User user, double amount){
        if(amount < 0){
            return false;
        }
        else{
            user.setAmount(user.getAmount()-amount);
            return true;
        }
    }

    public boolean interestFunds(User user, int days, double interest, double[] amounts){
        if(days < 0){
            return false;
        }
        else{
            double currentAmount = user.getAmount();
            double rate = user.getRate();
            interest = currentAmount * (rate/365);

            for(int i = 0; i < days; i++){
                currentAmount = currentAmount + interest;
                amounts[i] = currentAmount;
            }
            return true;
        }
    }
}
