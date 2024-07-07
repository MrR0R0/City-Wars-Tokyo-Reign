package com.app;

import com.database.Connect;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Cloneable {
    private String username, password, nickname, email, recoveryAns, recoveryQ, cardsSeries;
    private Integer wallet, level, id, XP , HP;

    // should initialize in signup
    private LinkedHashMap <Integer, Card> deckOfCards = new LinkedHashMap<>();

    static public LinkedHashMap<Integer, User> signedUpUsers;

    public User() {}
    public User(String username, String password, String nickname, String email, String recoveryAns,
                String recoveryQ, String cardsSeries, Integer wallet, Integer level, Integer id, Integer XP, Integer HP){
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
        this.XP = XP;
        this.HP = HP;
    }
    public void showProperties(){
        System.out.print("Username: " + username + "|");
        System.out.print("password: " + password + "|");
        System.out.print("nickname: " + nickname + "|");
        System.out.print("email: " + email + "|");
        System.out.print("recoveryAns: " + recoveryAns + "|");
        System.out.print("recoveryQ: " + recoveryQ + "|");
        System.out.print("Level:" + level + "|");
        System.out.print("Wallet:" + wallet + "|");
        System.out.print("XP:" + XP + "|");
        System.out.println("HP:" + HP + "|");
    }
    public void showCards(){
        for(Card card : deckOfCards.values()){
            card.showProperties(25,false);
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
    public Integer getXP() {return XP;}
    public Integer getHP() {return HP;}
    public LinkedHashMap<Integer, Card> getDeckOfCards() {return deckOfCards;}

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
    public void addToDeck(Integer id, Card card) {deckOfCards.put(id, card);}
    public void setId(Integer id) {this.id = id;}
    public void setXP(Integer XP) {this.XP = XP;}
    public void setHP(Integer HP) {this.HP = HP;}


    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();
            cloned.deckOfCards = new LinkedHashMap<>();
            for (Integer key : this.deckOfCards.keySet()) {
                cloned.deckOfCards.put(key, this.deckOfCards.get(key).clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(email, user.email) &&
                Objects.equals(recoveryAns, user.recoveryAns) &&
                Objects.equals(recoveryQ, user.recoveryQ) &&
                Objects.equals(cardsSeries, user.cardsSeries) &&
                Objects.equals(wallet, user.wallet) &&
                Objects.equals(level, user.level) &&
                Objects.equals(id, user.id) &&
                Objects.equals(XP, user.XP) &&
                Objects.equals(HP, user.HP) &&
                Objects.equals(deckOfCards, user.deckOfCards);
    }


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
                common.get(Integer.parseInt(String.valueOf(Connect.convertCharacterType(card.getCharacter()))) - 1).add(card);
            }
            if(card.getType().equals(Card.CardType.timeStrike)){
                timeStrike.get(Integer.parseInt(String.valueOf(Connect.convertCharacterType(card.getCharacter()))) - 1).add(card);
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

    public void getCardsFromTable(){
        String CARD_REGEX = "^(?<id>\\S+)_(?<level>\\S+)";
        String[] series = cardsSeries.split(",");
        Pattern pattern = Pattern.compile(CARD_REGEX);
        for (String card : series) {
            Matcher matcher = pattern.matcher(card);
            if (matcher.find()){
                int id = Integer.parseInt(matcher.group("id"));
                int level = Integer.parseInt(matcher.group("level"));
                deckOfCards.put(id, Card.allCards.get(id));
                deckOfCards.get(id).setLevel(level);
                deckOfCards.get(id).setAcc(Card.levelUpFormula(deckOfCards.get(id).getAcc(),deckOfCards.get(id).getLevel()));
                deckOfCards.get(id).setAttackOrDefense(Card.levelUpFormula(deckOfCards.get(id).getAttackOrDefense(),deckOfCards.get(id).getLevel()));
                deckOfCards.get(id).setDamage(Card.levelUpFormula(deckOfCards.get(id).getDamage(),deckOfCards.get(id).getLevel()));
                deckOfCards.get(id).setUpgradeCost(Card.levelUpFormula(deckOfCards.get(id).getUpgradeCost(),deckOfCards.get(id).getLevel()));
            }
        }
    }
    public static Integer getIdByUsername(String username){
        for(User user : User.signedUpUsers.values()){
            if(user.getUsername().equals(username))
                return user.getId();
        }
        return -1;
    }

    public static Integer nextLevelXP(Integer level){
        return (int) (Math.exp((double) (level + 35) /5) - Math.exp(5) + 20);
    }

}