package menu;

import app.Card;
import app.Error;
import app.ProgramController;
import app.User;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

public class Shop extends Menu {
    final static private int CARDS_ON_PAGE = 10, NAME_PAD = 27, DETAILS_PAD = 35, COST_PAD = 5;
    final static String upgradeCardCommand = "(?i)upgrade card number (?<cardNum>\\d+)";
    final static String showWalletCommand = "(?i)show wallet";
    final static String buyCommand = "^(?i)buy\\s*$";
    final static String showUpgradeableCards = "^(?i)show\\s+upgradable\\s+card$";
    final static String showAvailableCards = "^(?i)show\\s+available\\s+card$";
    final static String chooseCardCommand = "^(?i)\\s*(<cardName>\\w+)$";
    static private ArrayList<Card> upgradableCards;


    public static void handleInput(String input, Scanner scanner){
        if (input.matches(backCommand)){
            if (!Error.loginFirst()) {
                currentMenu = MenuType.Main;
                showCurrentMenu();
            }
        }
        else if(input.matches(showWalletCommand)){
            System.out.println("Balance: " + loggedInUser.getWallet());
        }
        else if (input.matches(showUpgradeableCards)){
            showUpgradeable(scanner);
        }
        else if(input.matches(upgradeCardCommand)){
            Matcher matcher = getCommandMatcher(input, upgradeCardCommand);
            matcher.find();
            upgradeCard(matcher);
        }
    }
    private static void showUpgradeable(Scanner scanner){
        updateUpgradableCards();

        int numberOfPages = Math.ceilDiv(upgradableCards.size(), CARDS_ON_PAGE);
        int currentPage = 1;

        System.out.println("Please choose a card to upgrade or back to shop");
        System.out.println("You can also select other pages");
        showPage(currentPage, numberOfPages);

        while (true) {
            System.out.println("For viewing other pages enter the page's number;");
            System.out.println("to return to the shop menu, enter 'back'");
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (command.toLowerCase().matches("back")) {
                System.out.println("You will be directed to Shop menu");
                return;
            } else if (command.matches("^\\d+$")) {
                if (Integer.parseInt(command) > numberOfPages) {
                    System.out.println("Please enter a number between 1 & " + numberOfPages);
                } else {
                    currentPage = Integer.parseInt(command);
                    showPage(currentPage, numberOfPages);
                }
            } else {
                System.out.println("Invalid input!");
            }
        }
    }
    static private void showPage(int page, int numberOfPages) {
        int start = CARDS_ON_PAGE * (page - 1);
        int end = CARDS_ON_PAGE * (page);
        showTopBar();
        IntStream.range(start, Math.min(end, upgradableCards.size()))
                .forEach(i -> upgradableCards.get(i).showShopProperties(i+1, NAME_PAD, COST_PAD, DETAILS_PAD));
        menu.MainMenu.showBottomBar(numberOfPages, page);
    }
    static private void showTopBar() {
        String name = String.format("%-" + Shop.NAME_PAD + "s", "Name");
        String cost = String.format("%-" + Shop.COST_PAD + "s", "Cost");
        String details = String.format("%-" + Shop.DETAILS_PAD + "s", "Details");
        System.out.println(name + "|" + cost + "|" + details);
    }

    static private void upgradeCard(Matcher matcher){
        String input = matcher.group("cardNum");
        if(!input.matches("^\\d+$")){
            System.out.println("Card index should be a number");
            return;
        }
        int cardNumber = Integer.parseInt(input) - 1;
        updateUpgradableCards();
        if(cardNumber >= upgradableCards.size()){
            System.out.println("Out of bound index!");
            return;
        }
        Card selectedCard = upgradableCards.get(cardNumber);
        if(selectedCard.getUpgradeCost() > loggedInUser.getWallet()){
            System.out.println("You don't have enough money!");
            return;
        }

        loggedInUser.reduceWallet(selectedCard.getUpgradeCost());
        selectedCard.upgrade();
        loggedInUser.updateCardSeriesByCards();
        System.out.println("Upgraded Card \"" + selectedCard.getName() + "\" to level " + selectedCard.getLevel());
    }

    static private void updateUpgradableCards(){
        upgradableCards = new ArrayList<>();
        for (Map.Entry<Integer, Card> entry : loggedInUser.getCards().entrySet()) {
            Card card = entry.getValue();
            if (loggedInUser.getLevel() > card.getLevel() && card.isUpgradable()) {
                upgradableCards.add(entry.getValue());
            }
        }
    }


    /*
    public static void showAvailable(Scanner scanner){
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
    */
}
