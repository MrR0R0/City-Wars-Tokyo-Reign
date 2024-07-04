package app;

import database.Connect;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card {
    public static final String CARD_REGEX = "^(?<id>\\S+)_(?<level>\\S+)";
    public enum Characters {Character1 , Character2 , Character3 , Character4 , Unity}
    public enum CardType {shield, spell, common, timeStrike}

    private CardType type;
    private String name, character;
    private Integer level, price, damage, duration, upgradeCost, attackOrDefense, specialProperty, acc, id, isBreakable;
    public static LinkedHashMap<Integer, Card> allCards = new LinkedHashMap<>();

    public Card(){}
    public Card(String name, CardType type, Integer level, Integer price, Integer damage, Integer duration, Integer upgradeCost, Integer attackOrDefense, Integer specialProperty, Integer acc, Integer isBreakable, Integer id) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.price = price;
        this.damage = damage;
        this.duration = duration;
        this.upgradeCost = upgradeCost;
        this.isBreakable = isBreakable;
        this.acc = acc;
        this.id = id;
        this.specialProperty = specialProperty;
        this.attackOrDefense = attackOrDefense;
    }
    public void showProperties(int pad){
        System.out.printf("%-"+pad+"s", ("name: " + name));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("type: " + type));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("level: " + level));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("price: " + price));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("damage: " + damage));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("duration: " + duration));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("upgradeCost:" + upgradeCost));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("breakable:" + isBreakable));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("acc: " + acc));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("id: " + id));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("special: " + specialProperty));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("Att/Def: " + attackOrDefense));
        System.out.println();
    }

    public String getName() {return name;}
    public CardType getType() {return type;}
    public Integer getLevel() {return level;}
    public Integer getPrice() {return price;}
    public Integer getDamage() {return damage;}
    public Integer getDuration() {return duration;}
    public Integer getUpgradeCost() {return upgradeCost;}
    public Integer getAcc() {return acc;}
    public Integer isBreakable() {return isBreakable;}
    public Integer getId() {return id;}
    public Integer getSpecialProperty() {return specialProperty;}
    public Integer getAttackOrDefense() {return attackOrDefense;}
    public String getCharacter() {return character;}

    public void setName(String name) {this.name = name;}
    public void setType(CardType type) {this.type = type;}
    public void setLevel(Integer level) {this.level = level;}
    public void setPrice(Integer price) {this.price = price;}
    public void setDamage(Integer damage) {this.damage = damage;}
    public void setDuration(Integer duration) {this.duration = duration;}
    public void setUpgradeCost(Integer upgradeCost) {this.upgradeCost = upgradeCost;}
    public void setAcc(Integer acc) {this.acc = acc;}
    public void setBreakable(Integer isBreakable) {this.isBreakable = isBreakable;}
    public void setAttackOrDefense(Integer attackOrDefense) {this.attackOrDefense = attackOrDefense;}
    public void setSpecialProperty(Integer specialProperty) {this.specialProperty = specialProperty;}
    public void setId(Integer id) {this.id = id;}
    public void setCharacter(String character) {this.character = character;}


    public void addToTable() throws SQLException {
        Connect.insertCard(this.name, String.valueOf(this.type),this.level,this.price,this.damage,this.duration,this.upgradeCost,
                this.attackOrDefense,this.upgradeCost,this.specialProperty,this.acc,isBreakable,this.character);
    }

    public static void setCardLevelFromUser(User user) {
        String[] idAndLevelOfCards = user.getCardsSeries().split(",");
        Pattern pattern = Pattern.compile(CARD_REGEX);
        for (String idAndLevel : idAndLevelOfCards) {
            Matcher matcher = pattern.matcher(idAndLevel);
            if (matcher.find()){
                String id = matcher.group("id");
                String level = matcher.group("level");
                user.getDeckOfCards().get(Integer.parseInt(id)).setLevel(Integer.parseInt(level));
            }
        }
    }

    public static void updateUserCards(User user) {
        StringBuilder cardSeries = new StringBuilder();
        for (Card card : user.getDeckOfCards().values()) {
            cardSeries.append(card.getId()).append("_").append(card.getLevel()).append(",");
        }
        cardSeries.delete(cardSeries.length()-1, cardSeries.length());
        user.setCardsSeries(cardSeries.toString());
        System.out.println(cardSeries.toString());
    }
}
