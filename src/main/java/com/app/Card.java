package com.app;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Card implements Cloneable {
    public static final String CARD_REGEX = "^(?<id>\\S+)_(?<level>\\S+)";
    public static final int healByLevel = 15;


    public enum Characters {Character1, Character2, Character3, Character4, Unity}

    public enum CardType {shield, spell, common, timeStrike}

    private CardType type;
    private String name, character, rarity;
    private Integer level, price, damage, duration, upgradeCost, attackOrDefense, acc, id, isBreakable, upgradeLevel;
    public static LinkedHashMap<Integer, Card> allCards = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Image> cardImages = new LinkedHashMap<>();

    public Card() {
    }

    public Card(String name, CardType type, Integer level, Integer price, Integer damage, Integer duration,
                Integer upgradeCost, Integer attackOrDefense, String rarity, Integer acc,
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
        this.attackOrDefense = attackOrDefense;
        this.upgradeLevel = upgradeLevel;
    }

    public void showProperties(int pad) {
        System.out.printf("%-" + pad + "s", ("name: " + name));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("type: " + type));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("duration: " + duration));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("acc: " + acc));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("Att/Def: " + attackOrDefense));
        System.out.println();
    }

    public void showInGameProperties(int pad) {
        showProperties(pad);
        System.out.printf("%-" + pad + "s", ("level: " + level));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("price: " + price));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("upgradeCost:" + upgradeCost));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("breakable:" + (isBreakable != 0)));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("id: " + id));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("rarity: " + rarity));
        System.out.print("|");
        System.out.printf("%-" + pad + "s", ("Upgrade lvl: " + upgradeLevel));
        System.out.println();
    }

    public void showUpgradeProperties(int index, int NAME_PAD, int COST_PAD, int DETAILS_PAD){
        String cardName = String.format("%-" + NAME_PAD + "s", index + "- " + name + " (" + level + "->" + (level+1) + ")");
        String cardCost = String.format("%-" + COST_PAD + "s", upgradeCost);
        String firstDetail = getUpgradeFirstDetail();
        String secondDetail = getUpgradeSecondDetail();
        String details = String.format("%-" + DETAILS_PAD + "s", firstDetail + secondDetail);
        System.out.println(cardName + "|" + cardCost + "|" + details);
    }

    public void showPurchaseProperties(int index, int NAME_PAD, int COST_PAD, int DETAILS_PAD){
        String cardName = String.format("%-" + NAME_PAD + "s", index + "- " + name + " (" + level + ")");
        String cardCost = String.format("%-" + COST_PAD + "s", upgradeCost);
        String firstDetail = getPurchaseFirstDetail();
        String secondDetail = getPurchaseSecondDetail();
        String details = String.format("%-" + DETAILS_PAD + "s", firstDetail + secondDetail);
        System.out.println(cardName + "|" + cardCost + "|" + details);
    }
    private String getUpgradeFirstDetail() {
        String detail = "";
        if(isBreakable())
            detail = "ACC: " + acc + "->" + levelUpFormula(acc, level+1);
        else if(isHeal())
            detail = "added HP: " + level * healByLevel + "->" + (level+1) * healByLevel;
        else if(isPowerBooster()) {
            detail = "Multiplier: "
                    + String.format("%.2f", getPowerBoostMultiplier())
                    + " -> "
                    + String.format("%.2f", getPowerBoostMultiplierByLevel(level + 1));

        }
        else if(isCardMitigator()){
            detail = "Multiplier: "
                    + String.format("%.2f", getMitigatorMultiplier())
                    + " -> "
                    + String.format("%.2f", getMitigatorMultiplierByLevel(level + 1));
        }
        return detail;
    }

    private String getUpgradeSecondDetail(){
        String detail = "";
        if(isBreakable())
            detail = ", Att_Def: " + attackOrDefense + "->" + levelUpFormula(attackOrDefense, level+1);
        return detail;
    }

    private String getPurchaseFirstDetail(){
        String detail = "";
        if(isBreakable())
            detail = "ACC: " + acc;
        else if(isHeal())
            detail = "added HP: " + level * healByLevel;
        else if(isPowerBooster()) {
            detail = "Multiplier: " + String.format("%.2f", getPowerBoostMultiplier());
        }
        else if(isCardMitigator()){
            detail = "Multiplier: " + String.format("%.2f", getMitigatorMultiplier());
        }
        return detail;
    }

    private String getPurchaseSecondDetail(){
        String detail = "";
        if(isBreakable())
            detail = ", Att_Def: " + attackOrDefense;
        return detail;
    }

    @Override
    public Card clone() {
        try {
            return (Card) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
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
                Objects.equals(rarity, card.rarity) &&
                Objects.equals(acc, card.acc) &&
                Objects.equals(id, card.id) &&
                Objects.equals(isBreakable, card.isBreakable) &&
                Objects.equals(upgradeLevel, card.upgradeLevel);
    }

    public String getName() {
        return name;
    }

    public CardType getType() {
        return type;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getDamage() {
        return damage;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getUpgradeCost() {
        return upgradeCost;
    }

    public Integer getAcc() {
        return acc;
    }

    // 0: unbreakable, 1: breakable
    public boolean isBreakable() {
        return isBreakable == 1;
    }

    public boolean isShield() {
        if (id == 19)
            return true;
        if (id == 1)
            return true;
        if (id == 18)
            return true;
        return false;
    }

    public boolean isHeal() {
        if (id == 2)
            return true;
        if (id == 19)
            return true;
        return false;
    }

    public boolean isHoleChanger() {
        return id == 4;
    }

    public boolean isHoleRepairer() {
        return id == 5;
    }

    public boolean isRoundReducer() {
        return id == 6;
    }

    public boolean isPowerBooster() {
        return id == 3;
    }

    public boolean isCopyCard() {
        return id == 9;
    }

    public boolean isCardRemover() {
        return id == 7;
    }

    public boolean isCardMitigator() {
        return id == 8;
    }

    public Integer getId() {
        return id;
    }

    public Integer getAttackOrDefense() {
        return attackOrDefense;
    }

    public String getCharacter() {
        return character;
    }

    public Integer getUpgradeLevel() {
        return upgradeLevel;
    }

    public Integer getGamingDamage() {
        return damage / duration;
    }

    public Integer getGamingAttackOrDefense() {
        return attackOrDefense / duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setUpgradeCost(Integer upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    public void setAcc(Integer acc) {
        this.acc = acc;
    }

    public void setBreakable(Integer isBreakable) {
        this.isBreakable = isBreakable;
    }

    public void setAttackOrDefense(Integer attackOrDefense) {
        this.attackOrDefense = attackOrDefense;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setUpgradeLevel(Integer upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public void boostAttackDefense(Double multiplier) {
        attackOrDefense = (int) (attackOrDefense * multiplier);
    }

    public void boostACC(Double multiplier) {
        acc = (int) (acc * multiplier);
    }

    public static Integer levelUpFormula(Integer value, Integer level) {
        //double coefficient = Math.exp(-1*Double.valueOf(level)/10) + 1 + Math.exp(-0.1);
        return (int) (value * (Math.log(level) + 1));
    }

    public double getPowerBoostMultiplier(){
        return getPowerBoostMultiplierByLevel(level);
    }
    private double getPowerBoostMultiplierByLevel(int level){
        return 1.1 * (Math.log10(level) + 1);
    }

    public double getMitigatorMultiplier(){
        return  getMitigatorMultiplierByLevel(level);
    }

    private double getMitigatorMultiplierByLevel(int level){
        return 1/getPowerBoostMultiplierByLevel(level);
    }
    public void updateFieldsByLevel() {
        acc = acc == 0 ? 0 : levelUpFormula(acc, level);
        attackOrDefense = attackOrDefense == 0 ? 0 :levelUpFormula(attackOrDefense, level);
        upgradeCost = price * level;
    }

    public void upgrade(){
        level++;
        updateFieldsByLevel();
    }

    public boolean isUpgradable(){
        if(id == 1)
            return false;
        if(id == 4)
            return false;
        if(id == 5)
            return false;
        if(id == 6)
            return false;
        if(id == 7)
            return false;
        if(id == 9)
            return false;
        if(id == 18)
            return false;
        return true;
    }

    public static <T> Card findCardInlist(String property, T value, LinkedHashMap<Integer, Card> linkedHashMap) {
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
