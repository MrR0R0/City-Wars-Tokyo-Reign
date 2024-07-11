package com.app;

import com.database.Connect;
import javafx.scene.image.Image;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Cloneable {
    private String username, password, nickname, email, recoveryAns, recoveryQ, cardsSeries;
    private Image profile;
    private Integer wallet, level, id, XP, HP;

    // should initialize in signup
    private LinkedHashMap<Integer, Card> cards = new LinkedHashMap<>();

    static public LinkedHashMap<Integer, User> signedUpUsers;

    public User() {
    }

    public User(String username, String password, String nickname, String email, String recoveryAns,
                String recoveryQ, String cardsSeries, Integer wallet, Integer level, Integer id, Integer XP) {
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
    }

    public void showProperties() {
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

    public void showCards() {
        for (Card card : cards.values()) {
            card.showProperties(25);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getRecoveryAns() {
        return recoveryAns;
    }

    public int getRecoveryQ() {
        return Integer.parseInt(recoveryQ);
    }

    public String getCardsSeries() {
        return cardsSeries;
    }

    public Integer getWallet() {
        return wallet;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getId() {
        return id;
    }

    public Integer getXP() {
        return XP;
    }

    public Integer getHP() {
        return HP;
    }

    public void decreaseHP(int value) {
        HP -= value;
    }

    public void increaseXP(int value) {
        XP += value;
    }

    public void increaseHP(int value) {
        HP += value;
    }

    public void increaseMoney(int value) {
        wallet += value;
    }

    public LinkedHashMap<Integer, Card> getCards() {
        return cards;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRecoveryAns(String recoveryAns) {
        this.recoveryAns = recoveryAns;
    }

    public void setRecoveryQ(String recoveryQ) {
        this.recoveryQ = recoveryQ;
    }

    public void setCardsSeries(String cardsSeries) {
        this.cardsSeries = cardsSeries;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void addToCards(Integer id, Card card) {
        cards.put(id, card);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void reduceWallet(Integer value){
        wallet -= value;
    }

    public void setXP(Integer XP) {
        this.XP = XP;
    }

    public void setHP(Integer HP) {
        this.HP = HP;
    }


    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();
            cloned.cards = new LinkedHashMap<>();
            for (Integer key : this.cards.keySet()) {
                cloned.cards.put(key, this.cards.get(key).clone());
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
                Objects.equals(cards, user.cards);
    }

    public void addToTable() throws SQLException {
        Connect.insertUser(this);
    }

    public static <T> boolean isInUsersList(String property, T value) {
        for (User user : signedUpUsers.values()) {
            if (property.matches("^(?i)username") && user.getUsername().equals(value)) {
                return true;
            }
            if (property.matches("^(?i)password") && user.getPassword().equals(value)) {
                return true;
            }
            if (property.matches("^(?i)nickname") && user.getNickname().equals(value)) {
                return true;
            }
            if (property.matches("^(?i)email") && user.getEmail().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static String formatUsername(String name) {
        if (name.length() > 10) {
            return name.substring(0, 10) + "...";
        }
        return name;
    }

    public void giveRandomCard() {
        ArrayList<ArrayList<Card>> timeStrike = new ArrayList<>();
        ArrayList<ArrayList<Card>> common = new ArrayList<>();
        ArrayList<Card> shieldOrSpell = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            common.add(new ArrayList<>());
            timeStrike.add(new ArrayList<>());
        }
        for (Card card : Card.allCards.values()) {
            if (card.getType().equals(Card.CardType.spell) || card.getType().equals(Card.CardType.shield)) {
                shieldOrSpell.add(card);
            }
            if (card.getType().equals(Card.CardType.common)) {
                common.get(Integer.parseInt(String.valueOf(Connect.convertCharacterType(card.getCharacter()))) - 1).add(card);
            }
            if (card.getType().equals(Card.CardType.timeStrike)) {
                timeStrike.get(Integer.parseInt(String.valueOf(Connect.convertCharacterType(card.getCharacter()))) - 1).add(card);
            }
        }
        ArrayList<Card> finalCards = new ArrayList<>(getRandomSubset(shieldOrSpell, 8));
        for (int i = 0; i < 4; i++) {
            finalCards.addAll(getRandomSubset(common.get(i), 2));
            finalCards.addAll(getRandomSubset(timeStrike.get(i), 1));
        }
        for (Card card : finalCards) {
            cards.put(card.getId(), card);
        }
    }

    private static <T> List<T> getRandomSubset(List<T> list, int subsetSize) {
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(subsetSize, copy.size()));
    }

    public void updateCardsByCardSeries() {
        String CARD_REGEX = "^(?<id>\\S+)_(?<level>\\S+)";
        String[] series = cardsSeries.split(",");
        Pattern pattern = Pattern.compile(CARD_REGEX);
        for (String card : series) {
            Matcher matcher = pattern.matcher(card);
            matcher.find();
            int id = Integer.parseInt(matcher.group("id"));
            int level = Integer.parseInt(matcher.group("level"));
            cards.put(id, Card.allCards.get(id).clone());
            cards.get(id).setLevel(level);
            cards.get(id).updateFieldsByLevel();
        }
    }

    public void updateCardSeriesByCards() {
        StringBuilder tmpCardSeries = new StringBuilder();
        for (Card card : cards.values()) {
            tmpCardSeries.append(card.getId()).append("_").append(card.getLevel()).append(",");
        }
        tmpCardSeries.delete(tmpCardSeries.length() - 1, tmpCardSeries.length());
        cardsSeries = tmpCardSeries.toString();
    }

    public static Integer getIdByUsername(String username) {
        for (User user : User.signedUpUsers.values()) {
            if (user.getUsername().equals(username))
                return user.getId();
        }
        return -1;
    }

    public static Integer nextLevelXP(Integer level) {
        return (int) (Math.exp((level + 27.7) / 5) - Math.exp(5.5) + 20);
    }

    public Image getProfile() {
        return profile;
    }

    public void setProfile(Image profile) {
        this.profile = profile;
    }
}