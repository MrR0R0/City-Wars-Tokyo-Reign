package menu;

import app.Card;
import app.User;
import database.Connect;

import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
    public static void handleInput(String input, Scanner scanner) throws SQLException {
        String addCardCommand = "^add card$";
        String showUsers = "^show users$";
        if (input.matches(addCardCommand)) {
            addCard(scanner);
        }
        if(input.matches(showUsers)){
            showUsers(25, 3, 6);
        }
    }

    private static void showUsers(int namePad, int levelPad, int walletPad){
        System.out.println(String.format("%-" + namePad + "s", "username") + "|" +
                            String.format("%-" + levelPad + "s", "lvl") + "|" +
                            String.format("%-" + walletPad + "s", "wallet"));
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
        if (name.isEmpty()) {
            return;
        }
        attack_defence = checkAttackDefence(scanner);
        if (attack_defence == -1) {
            return;
        }
        duration = checkDuration(scanner);
        if (duration == -1) {
            return;
        }
        damage = checkDamage(scanner);
        if (damage == -1) {
            return;
        }
        upgradeCost = checkUpgradeCost(scanner);
        if (upgradeCost == -1) {
            return;
        }
        type = checkType(scanner);
        if (type.isEmpty()) {
            return;
        }
        price = checkPrice(scanner);
        if (price == -1) {
            return;
        }
        ACC = checkACC(scanner);
        if (ACC == -1) {
            return;
        }
        isBreakable = checkBreakable(scanner);
        if (isBreakable == -1) {
            return;
        }
        characterIndex = checkCharacter(scanner);
        if (characterIndex == -1) {
            return;
        }

        Connect.insertCard(name, type, 1, price, damage, duration, upgradeCost, attack_defence, -1,
                0, ACC, isBreakable, String.valueOf(getEnumValueByIndex(Card.Characters.class, characterIndex)));

        System.out.println("Card created successfully!");
    }

    private static String checkCardName(Scanner scanner) {
        System.out.print("Enter card's name: ");
        String name = scanner.nextLine().trim().replaceAll(" +", " ");
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
        if (!attack_def.matches("\\d+")) {
            System.out.println("Attack/Def should be a number!");
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
        if (!duration.matches("\\d+")) {
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
        if (!damage.matches("\\d+")) {
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
        if (!upgradeCost.matches("\\d+")) {
            System.out.println("Upgrade cost should be a number!");
            return -1;
        }
        return Integer.parseInt(upgradeCost);
    }

    private static String checkType(Scanner scanner) {
        System.out.print("Enter card's type: ");
        String type = scanner.nextLine().trim().replaceAll(" +", " ");
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
        if (!price.matches("\\d+")) {
            System.out.println("Price should be a number!");
            return -1;
        }
        return Integer.parseInt(price);
    }

    private static int checkACC(Scanner scanner) {
        System.out.print("Enter card's ACC: ");
        String ACC = scanner.nextLine().trim().replaceAll(" +", " ");
        if (!ACC.matches("\\d+")) {
            System.out.println("ACC should be a number!");
            return -1;
        }
        return Integer.parseInt(ACC);
    }

    private static int checkBreakable(Scanner scanner) {
        System.out.print("Enter card's breakable (yes/no): ");
        String breakable = scanner.nextLine().trim().replaceAll(" +", " ");
        if (!breakable.equalsIgnoreCase("no") && !breakable.equalsIgnoreCase("yes")) {
            System.out.println("It should be a yes or no");
            return -1;
        }
        return breakable.equals("yes") ? 1 : 0;
    }

    private static int checkCharacter(Scanner scanner) {
        System.out.print("Enter card's character [1-4]: ");
        String character = scanner.nextLine().trim().replaceAll(" +", " ");
        if (!character.matches("\\d+")) {
            System.out.println("Character should be a number");
            return -1;
        }
        if (Integer.parseInt(character) < 1 || Integer.parseInt(character) > 4) {
            System.out.println("Character should be a number between 1 & 4");
            return -1;
        }
        return Integer.parseInt(character);
    }

    public static <T extends Enum<T>> T getEnumValueByIndex(Class<T> enumClass, int index) {
        T[] enumConstants = enumClass.getEnumConstants();
        if (index >= 0 && index < enumConstants.length) {
            return enumConstants[index];
        } else {
            return null;
        }
    }
}
