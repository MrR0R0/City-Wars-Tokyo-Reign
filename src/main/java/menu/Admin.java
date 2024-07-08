package menu;

import app.Card;
import app.ProgramController;
import app.Error;
import app.User;
import database.Connect;

import java.sql.SQLException;
import java.util.Scanner;

public class Admin extends Menu {
    final static private String addCardCommand = "^add card$";
    final static private String editCardCommand = "^edit card$";
    final static private String showUsers = "^show users$";

    public static void handleInput(String input, Scanner scanner) throws SQLException {
        if (input.matches(backCommand)){
            if (Error.loginFirst())
                return;
            currentMenu = MenuType.Main;
            showCurrentMenu();
        }
        if (input.matches(addCardCommand)) {
            addCard(scanner);
        }
        if(input.matches(showUsers)){
            showUsers(25, 3, 6);
        }
        if(input.matches(editCardCommand)){
            editCards(scanner, 25);
        }
    }

    private static void showUsers(int namePad, int levelPad, int walletPad){
        System.out.println(String.format("%-" + namePad + "s", "username") + "|" +
                            String.format("%-" + levelPad + "s", "lvl") + "|" +
                            String.format("%-" + walletPad + "s", "wallet"));
        System.out.println("-".repeat(namePad + levelPad + walletPad + 2));
        for(User user: User.signedUpUsers.values()){
            String username = String.format("%-" + namePad + "s", user.getUsername());
            String level = String.format("%-" + levelPad + "s", user.getLevel());
            String wallet = String.format("%-" + walletPad + "s", user.getWallet());
            System.out.println(username + "|" + level + "|" + wallet);
        }
    }

    private static void addCard(Scanner scanner) throws SQLException {
        String name, type, id;
        int characterIndex, isBreakable, attack_defence, duration, damage, upgradeCost, price, ACC;

        System.out.println("Here you can create a new card :)");
        System.out.println("If any of your entries are incorrect, you will need to start over.");

        name = checkCardName(scanner);
        if (name.isEmpty() || name.equals("quit")) {
            return;
        }
        attack_defence = checkAttackDefence(scanner);
        if (attack_defence == -1 || attack_defence == -2) {
            return;
        }
        duration = checkDuration(scanner);
        if (duration == -1 || duration == -2) {
            return;
        }
        damage = checkDamage(scanner);
        if (damage == -1 || damage == -2) {
            return;
        }
        upgradeCost = checkUpgradeCost(scanner);
        if (upgradeCost == -1 || upgradeCost == -2) {
            return;
        }
        type = checkType(scanner);
        if (type.isEmpty()|| type.equals("quit")) {
            return;
        }
        price = checkPrice(scanner);
        if (price == -1 || price == -2) {
            return;
        }
        ACC = checkACC(scanner);
        if (ACC == -1 || ACC == -2) {
            return;
        }
        isBreakable = checkBreakable(scanner);
        if (isBreakable == -1 || isBreakable == -2) {
            return;
        }
        characterIndex = checkCharacter(scanner);
        if (characterIndex == -1 || characterIndex == -2) {
            return;
        }

        Connect.insertCard(name, type, 1, price, damage, duration, upgradeCost, attack_defence, -1,
                "", ACC, isBreakable, String.valueOf(getEnumValueByIndex(Card.Characters.class, characterIndex)));

        System.out.println("Card created successfully!");
    }

    private static String checkCardName(Scanner scanner) {
        System.out.print("Enter card's name: ");
        String name = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(name)){
            return "quit";
        }
        for (Card card : Card.allCards.values()) {
            if (card.getName().equals(name)) {
                System.out.println("A card with the same name exists");
                return "";
            }
        }
        return name;
    }

    private static int checkAttackDefence(Scanner scanner) {
        System.out.print("Enter card's attack/def [10-100]: ");
        String attack_def = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(attack_def)){
            return -2;
        }
        if (!attack_def.matches("^\\d+$")) {
            System.out.println("Attack/Def should be a positive number!");
            return -1;
        }
        if (Integer.parseInt(attack_def) < 10 || Integer.parseInt(attack_def) > 100) {
            System.out.println("Attack/Defence should be a number between 10 and 100");
            return -1;
        }
        return Integer.parseInt(attack_def);
    }

    private static int checkDuration(Scanner scanner) {
        System.out.print("Enter card's duration [1-5]: ");
        String duration = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(duration)){
            return -2;
        }
        if (!duration.matches("^\\d+$")) {
            System.out.println("Duration should be a number!");
            return -1;
        }
        if (Integer.parseInt(duration) > 5 || Integer.parseInt(duration) < 1) {
            System.out.println("Duration should be a number between 1 and 5");
            return -1;
        }
        return Integer.parseInt(duration);
    }

    private static int checkDamage(Scanner scanner) {
        System.out.print("Enter card's damage [10-50]: ");
        String damage = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(damage)){
            return -2;
        }
        if (!damage.matches("^\\d+$")) {
            System.out.println("Damage should be a number!");
            return -1;
        }
        if (Integer.parseInt(damage) > 50 || Integer.parseInt(damage) < 10) {
            System.out.println("Damage should be a number between 10 and 50");
            return -1;
        }
        return Integer.parseInt(damage);
    }

    private static int checkUpgradeCost(Scanner scanner) {
        System.out.print("Enter card's upgrade cost: ");
        String upgradeCost = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(upgradeCost)){
            return -2;
        }
        if (!upgradeCost.matches("^\\d+$")) {
            System.out.println("Upgrade cost should be a number!");
            return -1;
        }
        return Integer.parseInt(upgradeCost);
    }

    private static String checkType(Scanner scanner) {
        System.out.print("Enter card's type: ");
        String type = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(type)){
            return "quit";
        }
        try {
            Card.CardType.valueOf(type);
            return type;
        } catch (IllegalArgumentException e) {
            System.out.println("The type does not exist");
            return "";
        }
    }

    private static int checkPrice(Scanner scanner) {
        System.out.print("Enter card's price: ");
        String price = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(price)){
            return -2;
        }
        if (!price.matches("^\\d+$")) {
            System.out.println("Price should be a number!");
            return -1;
        }
        return Integer.parseInt(price);
    }

    private static int checkACC(Scanner scanner) {
        System.out.print("Enter card's ACC: ");
        String ACC = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(ACC)){
            return -2;
        }
        if (!ACC.matches("^\\d+$")) {
            System.out.println("ACC should be a number!");
            return -1;
        }
        return Integer.parseInt(ACC);
    }

    private static int checkBreakable(Scanner scanner) {
        System.out.print("Enter card's breakable (yes/no): ");
        String breakable = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(breakable)){
            return -2;
        }
        if (!breakable.equalsIgnoreCase("no") && !breakable.equalsIgnoreCase("yes")) {
            System.out.println("It should be a yes or no");
            return -1;
        }
        return breakable.equals("yes") ? 1 : 0;
    }

    private static int checkCharacter(Scanner scanner) {
        System.out.print("Enter card's character [1-4]: ");
        String character = scanner.nextLine().trim().replaceAll(" +", " ");
        if(ProgramController.checkQuit(character)){
            return -2;
        }
        if (!character.matches("^\\d+$")) {
            System.out.println("Character should be a number");
            return -1;
        }
        if (Integer.parseInt(character) < 1 || Integer.parseInt(character) > 4) {
            System.out.println("Character should be a number between 1 & 4");
            return -1;
        }
        return Integer.parseInt(character);
    }

    private static int checkID(Scanner scanner){
        System.out.print("Enter the card's id: ");
        String id = scanner.nextLine().trim();
        if(ProgramController.checkQuit(id)){
            return -2;
        }
        if(!id.matches("\\d+")){
            System.out.println("ID should be a number!");
            return -1;
        }
        if(!Card.allCards.containsKey(Integer.parseInt(id))){
            System.out.println("Incorrect ID");
            return -1;
        }
        return Integer.parseInt(id);
    }

    public static <T extends Enum<T>> T getEnumValueByIndex(Class<T> enumClass, int index) {
        T[] enumConstants = enumClass.getEnumConstants();
        if (index >= 0 && index < enumConstants.length) {
            return enumConstants[index];
        } else {
            return null;
        }
    }

    private static void editCards(Scanner scanner, int pad){
        System.out.println("Here are available cards:");
        int id = -1;
        for(Card card : Card.allCards.values()){
            System.out.println(card.getId() + "- " + card.getName());
        }
        while(id == -1){
            id = checkID(scanner);
            if(id == -2){
                return;
            }
        }

        while(true){
            showTopBar(pad);
            Card.allCards.get(id).showProperties(pad);
            editField(id, scanner);
            System.out.print("Do you wish to continue editing? (yes/no)");
            String ans = scanner.nextLine().trim();
            if(ans.equalsIgnoreCase("no")){
                return;
            }
        }


    }

    private static void editField(int id, Scanner scanner){
        System.out.print("Select a field: ");
        String field = scanner.nextLine().trim();
        switch (field){
            case "1" ->{
                String name;
                while(true){
                    name = checkCardName(scanner);
                    if(name.equals("quit")){
                        return;
                    }
                    if(!name.isEmpty()){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setName(name);
                        return;
                    }
                }
            }
            case "2" ->{
                String type;
                while(true){
                    type = checkType(scanner);
                    if(type.equals("quit")){
                        return;
                    }
                    if(!type.isEmpty()){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setType(Card.CardType.valueOf(type));
                        return;
                    }
                }
            }
            case "3" ->{
                int price;
                while(true){
                    price = checkPrice(scanner);
                    if(price == -2){
                        return;
                    }
                    if(price != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setPrice(price);
                        return;
                    }
                }
            }
            case "4" ->{
                int damage;
                while(true){
                    damage = checkDamage(scanner);
                    if(damage == -2){
                        return;
                    }
                    if(damage != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setDamage(damage);
                        return;
                    }
                }
            }
            case "5" ->{
                int duration;
                while(true){
                    duration = checkDuration(scanner);
                    if(duration == -2){
                        return;
                    }
                    if(duration != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setDuration(duration);
                        return;
                    }
                }
            }
            case "6" ->{
                int updateCost;
                while(true){
                    updateCost = checkUpgradeCost(scanner);
                    if(updateCost == -2){
                        return;
                    }
                    if(updateCost != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setUpgradeCost(updateCost);
                        return;
                    }
                }
            }
            case "7" ->{
                int breakable;
                while(true){
                    breakable = checkBreakable(scanner);
                    if(breakable == -2){
                        return;
                    }
                    if(breakable != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setBreakable(breakable);
                        return;
                    }
                }
            }
            case "8" ->{
                int ACC;
                while(true){
                    ACC = checkACC(scanner);
                    if(ACC == -2){
                        return;
                    }
                    if(ACC != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setAcc(ACC);
                        return;
                    }
                }
            }
            case "9" ->{
                int att_def;
                while(true){
                    att_def = checkAttackDefence(scanner);
                    if(att_def == -2){
                        return;
                    }
                    if(att_def != -1){
                        if(!doubleCheck(scanner))
                            return;
                        Card.allCards.get(id).setAttackOrDefense(att_def);
                        return;
                    }
                }
            }
            case "quit" ->{
                return;
            }
        }
    }

    private static void showTopBar(int pad){
        System.out.printf("%-"+pad+"s", "1-name");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "2-type");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "level");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "3-price");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "4-damage");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "5-duration");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "6-upgradeCost");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "7-breakable");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "8-acc");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "id");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "special");
        System.out.print("|");
        System.out.printf("%-"+pad+"s", "9-Att/Def");
        System.out.println();
    }

    private static boolean doubleCheck(Scanner scanner){
        System.out.println("Are you sure?(y/n)");
        String ans = scanner.nextLine().trim();
        return ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y");
    }
}
