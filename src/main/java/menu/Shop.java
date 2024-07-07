package menu;

import app.Card;
import app.Error;

import java.util.*;
import java.util.regex.Matcher;

public class Shop extends Menu{
    final static String upgradeCommand = "^(?i)upgrade\\s*$";
    final static String buyCommand = "^(?i)buy\\s*$";
    final static String showUpgradeableCards = "^(?i)show\\s+upgradable\\s+card$";
    final static String showAvailableCards = "^(?i)show\\s+available\\s+card$";
    final static String chooseCardCommand = "^(?i)\\s*(<cardName>\\w+)$";


    public static void handleInput(String input, Scanner scanner){
        {
            if (input.matches(backCommand)){
                if (Error.loginFirst())
                    return;
                currentMenu = MenuType.Main;
                showCurrentMenu();
            }
            if (input.matches(showUpgradeableCards)){
                Matcher matcher = getCommandMatcher(input, showUpgradeableCards);
                if (matcher.find())
                    showUpgradeable(matcher,scanner);
            }
        }

    }
    private static void showUpgradeable(Matcher matcher, Scanner scanner){
        LinkedHashMap<Integer, Card> upgradeableCards = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<Integer, Card> entry : loggedInUser.getCards().entrySet()) {
            i++;
            Card card = entry.getValue();
            if (loggedInUser.getLevel() >= card.getUpgradeLevel()) {
                upgradeableCards.put(card.getId(), card);
                System.out.print(card.getName() + " | ");
                if (i == 6)
                    System.out.println();
            }
        }

        System.out.println("Please choose a card to upgrade or back to shop");
        String input = scanner.nextLine();
        if (input.matches(backCommand)){
            return;
        }
        if (input.matches(chooseCardCommand)) {
            matcher = getCommandMatcher(input, chooseCardCommand);
            if (matcher.find() && Card.findCardInlist("name",matcher.group("cardName"),upgradeableCards) != null) {
                if (upgradeableCards.containsKey(Card.findCardInlist("name",matcher.group("cardName"),upgradeableCards).getId()))
                    chooseCardToUpgrade(matcher, scanner,upgradeableCards.get(Card.findCardInlist("name",matcher.group("cardName"),upgradeableCards).getId()));
                else
                    System.out.println("Please choose an upgradable card");
            }
        }
    }
    public static void showAvailable(Matcher matcher, Scanner scanner){
        LinkedHashMap<Integer, Card> availableCards = new LinkedHashMap<>();
        int i = 0;
        Random random = new Random();
        while (i < 6){
            Integer cardId = random.nextInt(Card.allCards.size());
            if (!loggedInUser.getCards().containsKey(cardId)){
                availableCards.put(cardId, Card.allCards.get(cardId));
                System.out.print(Card.allCards.get(cardId).getName() + " | ");
                if (i == 4)
                    System.out.println();
                else
                    System.out.println(" | ");
                i++;
            }
        }
        System.out.println("Please choose a card to buy or back to shop");
        String input = scanner.nextLine();
        if (input.matches(backCommand)){
            return;
        }
        if (input.matches(showAvailableCards)){
            matcher = getCommandMatcher(input, chooseCardCommand);
            if (matcher.find() && Card.findCardInlist("name",matcher.group("cardName"),availableCards) != null) {
                if (availableCards.containsKey(Card.findCardInlist("name",matcher.group("cardName"),availableCards).getId()))
                    chooseCardToBuy(matcher, scanner,availableCards.get(Card.findCardInlist("name",matcher.group("cardName"),availableCards).getId()));
                else
                    System.out.println("Please choose an available card");
            }
        }
    }
    private static void chooseCardToBuy(Matcher matcher, Scanner scanner, Card card){
        card.showProperties(25);
        System.out.println();
        System.out.println("buy " + card.getName() + " or back");

        String input = scanner.nextLine();
        if (input.matches(backCommand)){
            showAvailable(matcher,scanner);
        }
        if (input.matches(buyCommand)){
            matcher = getCommandMatcher(input, buyCommand);
            if (matcher.find()) {
                if (card.getPrice() <= loggedInUser.getWallet()) {
                    loggedInUser.addToCards(card.getId(),card);
                    loggedInUser.setCardsSeries(loggedInUser.getCardsSeries() + "," + card.getId() + "_" + card.getLevel());
                }
                else {
                    System.out.println("You don't have enough coins for buying " + card.getName());
                    showAvailable(matcher, scanner);
                }
            }
        }

    }

    private static void chooseCardToUpgrade(Matcher matcher, Scanner scanner, Card card){
        //padding
        String format = "%-15s : %s%n";
        System.out.printf(format, "Name", card.getName());
        System.out.printf(format, "Level", card.getLevel() + " --> " + (card.getLevel() + 1));
        System.out.printf(format, "ACC", card.getAcc() + " --> " + Card.levelUpFormula(card.getAcc(), card.getLevel()));
        System.out.printf(format, "Attack/Defense", card.getAttackOrDefense() + " --> " + Card.levelUpFormula(card.getAttackOrDefense(), card.getLevel()));
        System.out.printf(format, "Damage", card.getDamage() + " --> " + Card.levelUpFormula(card.getDamage(), card.getLevel()));
        System.out.printf(format, "Upgrade cost", card.getUpgradeCost());
        System.out.println();
        System.out.println("Upgrade " + card.getName() + " or back");
        String input = scanner.nextLine();
        if (input.matches(backCommand)){
            showUpgradeable(matcher,scanner);
        }
        if (input.matches(upgradeCommand)){
            matcher = getCommandMatcher(input, upgradeCommand);
            if (matcher.find()) {
                if (card.getUpgradeCost() <= loggedInUser.getWallet()) {
                    card.setAcc(Card.levelUpFormula(card.getAcc(), card.getLevel()));
                    card.setAttackOrDefense(Card.levelUpFormula(card.getAttackOrDefense(), card.getLevel()));
                    card.setDamage(Card.levelUpFormula(card.getDamage(), card.getLevel()));
                    card.setUpgradeCost(Card.levelUpFormula(card.getUpgradeCost(), card.getLevel()));
                    card.setLevel(card.getLevel() + 1);
                    System.out.println(card.getName() + " level has been upgraded to " + card.getLevel());
                }
                else {
                    System.out.println("You don't have enough coins for upgrading " + card.getName());
                    showUpgradeable(matcher, scanner);
                }
            }
        }
    }

}
