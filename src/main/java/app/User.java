package app;

import database.Connect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class User {
    private String username, password, nickname, email, recoveryAns, recoveryQ, cards;
    private Integer wallet, level;
    static public LinkedHashMap<String, User> signedUpUsers;

    public User() {}
    public User(String username, String password, String nickname, String email, String recoveryAns,
                String recoveryQ, String cards, Integer wallet, Integer level){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.recoveryAns = recoveryAns;
        this.recoveryQ = recoveryQ;
        this.cards = cards;
        this.wallet = wallet;
        this.level = level;
    }
    public void showProperties(){
        System.out.println("Username: " + username);
        System.out.println("password: " + password);
        System.out.println("nickname: " + nickname);
        System.out.println("email: " + email);
        System.out.println("recoveryAns: " + recoveryAns);
        System.out.println("recoveryQ: " + recoveryQ);
        System.out.println("Level:" + level);
        System.out.println("Wallet:" + wallet);
    }
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getNickname() {return nickname;}
    public String getEmail() {return email;}
    public String getRecoveryAns() {return recoveryAns;}
    public String getRecoveryQ() {return recoveryQ;}
    public String getCards() {return cards;}
    public Integer getWallet() {return wallet;}
    public Integer getLevel() {return level;}

    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setNickname(String nickname) {this.nickname = nickname;}
    public void setEmail(String email) {this.email = email;}
    public void setRecoveryAns(String recoveryAns) {this.recoveryAns = recoveryAns;}
    public void setRecoveryQ(String recoveryQ) {this.recoveryQ = recoveryQ;}
    public void setCards(String cards) {this.cards = cards;}
    public void setWallet(Integer wallet) {this.wallet = wallet;}
    public void setLevel(Integer level) {this.level = level;}

    public void addToTable() throws SQLException {
        Connect.insertUser(username, cards, password, nickname,
                        email, recoveryQ, recoveryAns, wallet);
    }

    public static <T> boolean isInUsersList(String property, T value){
        for(User user : signedUpUsers.values()){
            if(property.matches("^(?i)username") && user.getUsername().equals(value)){
                return true;
            }
            if (property.matches("^(?i)password") && user.getPassword().equals(value)){
                return true;
            }
            if (property.matches("^(?i)nickname") && user.getNickname().equals(value)){
                return true;
            }
            if (property.matches("^(?i)email") && user.getEmail().equals(value)){
                return true;
            }
        }
        return false;
    }
}