package app;

import database.Connect;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class Card {
    private String name,type;
    private Integer level, price, damage, duration, upgradeCost, attackOrDefense, specialProperty, acc, id, isBreakable;
    public static LinkedHashMap<Integer, Card> allCards = new LinkedHashMap<>();

    public Card(){}
    public Card(String name, String type, Integer level, Integer price, Integer damage, Integer duration, Integer upgradeCost, Integer attackOrDefense, Integer specialProperty, Integer acc, Integer isBreakable, Integer id) {
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
    public void showProperties(){
        System.out.println("name: " + name);
        System.out.println("type: " + type);
        System.out.println("level: " + level);
        System.out.println("price: " + price);
        System.out.println("damage: " + damage);
        System.out.println("duration: " + duration);
        System.out.println("updateCost:" + upgradeCost);
        System.out.println("isBreakable:" + isBreakable);
        System.out.println("acc: " + acc);
        System.out.println("id: " + id);
        System.out.println("specialProperty: " + specialProperty);
        System.out.println("attackOrDefense: " + attackOrDefense);
    }
    public String getName() {return name;}
    public String getType() {return type;}
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


    public void setName(String name) {this.name = name;}
    public void setType(String type) {this.type = type;}
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

    public void addToTable() throws SQLException {
        Connect.insertCard(name,type,level,price,damage,duration, upgradeCost,attackOrDefense,upgradeCost,specialProperty,acc,isBreakable);
    }
}
