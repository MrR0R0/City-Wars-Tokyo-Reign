package app;

import database.Connect;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card implements Cloneable{
    public static final String CARD_REGEX = "^(?<id>\\S+)_(?<level>\\S+)";
    public enum Characters {Character1 , Character2 , Character3 , Character4 , Unity}
    public enum CardType {shield, spell, common, timeStrike}

    private CardType type;
    private String name, character;
    private Integer level, price, damage, duration, upgradeCost, attackOrDefense, specialProperty, acc, id, isBreakable, upgradeLevel;
    public static LinkedHashMap<Integer, Card> allCards = new LinkedHashMap<>();

    public Card(){}
    public Card(String name, CardType type, Integer level, Integer price, Integer damage, Integer duration,
                Integer upgradeCost, Integer attackOrDefense, Integer specialProperty, Integer acc,
                Integer isBreakable, Integer id, Integer upgradeLevel) {
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
        this.upgradeLevel = upgradeLevel;
    }
    public void showProperties(int pad, boolean isInGame){
        System.out.printf("%-"+pad+"s", ("name: " + name));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("type: " + type));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("duration: " + duration));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("damage: " + damage));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("acc: " + acc));
        System.out.print("|");
        System.out.printf("%-"+pad+"s", ("Att/Def: " + attackOrDefense));
        if (!isInGame){
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("level: " + level));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("price: " + price));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("duration: " + duration));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("upgradeCost:" + upgradeCost));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("breakable:" + (isBreakable != 0)));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("id: " + id));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("special: " + specialProperty));
            System.out.print("|");
            System.out.printf("%-" + pad + "s", ("Upgrade lvl: " + upgradeLevel));
        }
        System.out.println();
    }

    @Override
    public Card clone() {
        try {
            return (Card) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // نباید اتفاق بیفتد
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return Objects.equals(type, card.type) &&
                Objects.equals(name, card.name) &&
                Objects.equals(character, card.character) &&
                Objects.equals(level, card.level) &&
                Objects.equals(price, card.price) &&
                Objects.equals(damage, card.damage) &&
                Objects.equals(duration, card.duration) &&
                Objects.equals(upgradeCost, card.upgradeCost) &&
                Objects.equals(attackOrDefense, card.attackOrDefense) &&
                Objects.equals(specialProperty, card.specialProperty) &&
                Objects.equals(acc, card.acc) &&
                Objects.equals(id, card.id) &&
                Objects.equals(isBreakable, card.isBreakable) &&
                Objects.equals(upgradeLevel, card.upgradeLevel);
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
    public Integer getUpgradeLevel() {return upgradeLevel;}
    public Integer getGamingDamage() {return damage/duration;}
    public Integer getGamingAttackOrDefense() {return attackOrDefense/duration;}
    public boolean getIsBreakable() {return isBreakable == 0;}

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
    public void setUpgradeLevel(Integer upgradeLevel) {this.upgradeLevel = upgradeLevel;}
    public void boostAttackDefense(Double multiplier){
        attackOrDefense = (int) (attackOrDefense * multiplier);
    }

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

    public static Integer levelUpFormula(Integer value, Integer level){
        return (int) (value *Math.exp(-1*Double.valueOf(level)/10)+1);
    }

    public static <T> Card findCardInlist(String property, T value, LinkedHashMap<Integer,Card> linkedHashMap) {
        for (Card card : linkedHashMap.values()) {
            if (property.matches("^(?i)name") && card.getName().equals(value)) {
                return card;
            }
            if (property.matches("^(?i)type") && card.getType().equals(value)) {
                return card;
            }
            if (property.matches("^(?i)character") && card.getCharacter().equals(value)) {
                return card;
            }
        }
        return null;
    }
}
