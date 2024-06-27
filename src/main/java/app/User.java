package app;

import database.Connect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class User {
    private String username, password, nickname, email, recoveryAns, recoveryQ, cardsSeries;
    private Integer wallet, level, id;

    // should initialize in signup
    private LinkedHashMap <Integer, Card> deckOfCards = new LinkedHashMap<>();

    static public LinkedHashMap<Integer, User> signedUpUsers;

    public User() {}
    public User(String username, String password, String nickname, String email, String recoveryAns,
                String recoveryQ, String cardsSeries, Integer wallet, Integer level, Integer id){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.recoveryAns = recoveryAns;
        this.recoveryQ = recoveryQ;
        this.cardsSeries = cardsSeries;
        this.wallet = wallet;
        this.level = level;
        this.id = id;
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
    public void showCards(){
        for(Card card : deckOfCards.values()){
            card.showProperties();
            System.out.println();
        }
    }
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getNickname() {return nickname;}
    public String getEmail() {return email;}
    public String getRecoveryAns() {return recoveryAns;}
    public int getRecoveryQ() {return Integer.parseInt(recoveryQ);}
    public String getCardsSeries() {return cardsSeries;}
    public Integer getWallet() {return wallet;}
    public Integer getLevel() {return level;}
    public Integer getId() {return id;}

    public LinkedHashMap<Integer, Card> getDeckOfCards() {
        return deckOfCards;
    }
    public void setID(Integer id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setNickname(String nickname) {this.nickname = nickname;}
    public void setEmail(String email) {this.email = email;}
    public void setRecoveryAns(String recoveryAns) {this.recoveryAns = recoveryAns;}
    public void setRecoveryQ(String recoveryQ) {this.recoveryQ = recoveryQ;}
    public void setCardsSeries(String cardsSeries) {this.cardsSeries = cardsSeries;}
    public void setWallet(Integer wallet) {this.wallet = wallet;}
    public void setLevel(Integer level) {this.level = level;}

    public void addToTable() throws SQLException {
        Connect.insertUser(this);
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

    public static String formatUsername(String name){
        if(name.length() > 10) {
            return name.substring(0, 10) + "...";
        }
        return name;
    }

    public void giveRandomCard(){
        ArrayList<ArrayList<Card>> timeStrike = new ArrayList<>();
        ArrayList<ArrayList<Card>> common = new ArrayList<>();
        ArrayList<Card> shieldOrSpell = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            common.add(new ArrayList<>());
            timeStrike.add(new ArrayList<>());
        }
        for(Card card : Card.allCards.values()){
            if(card.getType().equals(Card.CardType.spell) || card.getType().equals(Card.CardType.shield)){
                shieldOrSpell.add(card);
            }
            if(card.getType().equals(Card.CardType.common)){
                common.get(Integer.parseInt(Connect.convertCharacterType(card.getCharacter())) - 1).add(card);
            }
            if(card.getType().equals(Card.CardType.timeStrike)){
                timeStrike.get(Integer.parseInt(Connect.convertCharacterType(card.getCharacter())) - 1).add(card);
            }
        }
        ArrayList<Card> finalCards = new ArrayList<>(getRandomSubset(shieldOrSpell, 8));
        for(int i=0; i<4; i++){
            finalCards.addAll(getRandomSubset(common.get(i), 2));
            finalCards.addAll(getRandomSubset(timeStrike.get(i), 1));
        }
        for(Card card : finalCards){
            deckOfCards.put(card.getId(), card);
        }
    }

    private static <T> List<T> getRandomSubset(List<T> list, int subsetSize) {
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(subsetSize, copy.size()));
    }

    public static Integer getIdByUsername(String username){
        for(User user : User.signedUpUsers.values()){
            if(user.getUsername().equals(username))
                return user.getId();
        }
        return -1;
    }
}