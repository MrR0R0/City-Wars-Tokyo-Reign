package com.menu;

import com.app.*;
import com.app.Error;
import com.menu.Menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

public class Shop extends Menu {
    final static private int CARDS_ON_PAGE = 10, NAME_PAD = 27, DETAILS_PAD = 35, COST_PAD = 5, PROP_PAD = 23;
    final static protected int purchasableAmount = 6, purchasableShieldOrSpell = 1, purchasableCommon = 3, purchasableTimeStrike = 2;
    final static String upgradeCardCommand = "(?i)upgrade card number (?<cardNum>\\d+)";
    final static String showWalletCommand = "(?i)show wallet";
    final static String buyCommand = "^(?i)buy card number (?<cardNum>\\d+)$";
    final static String showUpgradeableCards = "^(?i)show upgradable cards$";
    final static String showPurchasableCards = "^(?i)show purchasable cards$";
    final static String showCardProperties = "^(?i)show properties of card number (?<cardNum>\\d+)$";
    static protected ArrayList<Card> upgradableCards;  // filled with user's original cards
    static protected ArrayList<Card> purchasableCards; // filled with clones

    public static void handleInput(String input, Scanner scanner) {
        if (input.matches(backCommand)) {
            if (!Error.loginFirst()) {
                currentMenu = MenuType.Main;
                showCurrentMenu();
            }
        } else if (input.matches(showWalletCommand)) {
            System.out.println("Balance: " + loggedInUser.getWallet());
        } else if (input.matches(showUpgradeableCards)) {
            showUpgradeable(scanner);
        } else if (input.matches(upgradeCardCommand)) {
            Matcher matcher = getCommandMatcher(input, upgradeCardCommand);
            matcher.find();
            upgradeCard(matcher);
        }
        else if (input.matches(showPurchasableCards)){
            showPurchasable();
        }
        else if (input.matches(buyCommand)){
            Matcher matcher = getCommandMatcher(input, buyCommand);
            matcher.find();
            buyCard(matcher);
        }
    }

    private static void showUpgradeable(Scanner scanner) {
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
            }
            else if(command.matches(showCardProperties)){
                Matcher matcher = getCommandMatcher(command, showCardProperties);
                matcher.find();
                String input = matcher.group("cardNum");
                showCardProperties(input, upgradableCards);
            }
            else if (command.matches("^\\d+$")) {
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

    private static void showPurchasable(){
        int index = 1;
        updatePurchasableCards();
        showTopBar();
        for(Card card: purchasableCards){
            card.showPurchaseProperties(index, NAME_PAD, COST_PAD, DETAILS_PAD);
            index++;
        }
    }

    static private void showPage(int page, int numberOfPages) {
        int start = CARDS_ON_PAGE * (page - 1);
        int end = CARDS_ON_PAGE * (page);
        showTopBar();
        IntStream.range(start, Math.min(end, upgradableCards.size()))
                .forEach(i -> upgradableCards.get(i).showUpgradeProperties(i + 1, NAME_PAD, COST_PAD, DETAILS_PAD));
        MainMenu.showBottomBar(numberOfPages, page);
    }

    static private void showTopBar() {
        String name = String.format("%-" + Shop.NAME_PAD + "s", "Name");
        String cost = String.format("%-" + Shop.COST_PAD + "s", "Cost");
        String details = String.format("%-" + Shop.DETAILS_PAD + "s", "Details");
        System.out.println(name + "|" + cost + "|" + details);
    }

    static private void upgradeCard(Matcher matcher) {
        String input = matcher.group("cardNum");
        if (!input.matches("^\\d+$")) {
            System.out.println("Card index should be a number");
            return;
        }
        int cardNumber = Integer.parseInt(input) - 1;
        updateUpgradableCards();
        if (cardNumber >= upgradableCards.size()) {
            System.out.println("Out of bound index!");
            return;
        }
        Card selectedCard = upgradableCards.get(cardNumber);
        if (selectedCard.getUpgradeCost() > loggedInUser.getWallet()) {
            System.out.println("You don't have enough money!");
            return;
        }

        loggedInUser.reduceWallet(selectedCard.getUpgradeCost());
        selectedCard.upgrade();
        loggedInUser.updateCardSeriesByCards();
        System.out.println("Upgraded Card \"" + selectedCard.getName() + "\" to level " + selectedCard.getLevel());
    }

    static private void buyCard(Matcher matcher){
        String input = matcher.group("cardNum");
        if(!input.matches("^\\d+$")){
            System.out.println("Invalid card number");
            return;
        }
        int cardNumber = Integer.parseInt(input) - 1;
        updatePurchasableCards();
        if(cardNumber >= purchasableCards.size()){
            System.out.println("Out of bound index!");
            return;
        }
        Card selectedCard = purchasableCards.get(cardNumber);
        if (selectedCard.getPrice() > loggedInUser.getWallet()) {
            System.out.println("You don't have enough money!");
            return;
        }
        loggedInUser.reduceWallet(selectedCard.getPrice());
        loggedInUser.getCards().put(selectedCard.getId(), selectedCard);
        loggedInUser.updateCardSeriesByCards();
        System.out.println("Purchased Card \"" + selectedCard.getName() + "\"");
    }

    static private void updateUpgradableCards() {
        upgradableCards = new ArrayList<>();
        for (Map.Entry<Integer, Card> entry : loggedInUser.getCards().entrySet()) {
            Card card = entry.getValue();
            if (loggedInUser.getLevel() > card.getLevel() && card.isUpgradable()) {
                upgradableCards.add(entry.getValue());
            }
        }
    }

    static private void updatePurchasableCards() {
        purchasableCards = new ArrayList<>();
        HashSet<Integer> repeatedIds = new HashSet<>();
        int shieldOrSpellCounter = 0;
        int timeStrikeCounter = 0;
        int commonCounter = 0;
        for (Card card : Card.allCards.values()) {
            if (!loggedInUser.getCards().containsKey(card.getId())) {
                switch (card.getType()) {
                    case spell, shield -> {
                        if (shieldOrSpellCounter < purchasableShieldOrSpell) {
                            shieldOrSpellCounter++;
                            purchasableCards.add(card.clone());
                            repeatedIds.add(card.getId());
                        }
                    }
                    case common -> {
                        if (commonCounter < purchasableCommon) {
                            commonCounter++;
                            purchasableCards.add(card.clone());
                            repeatedIds.add(card.getId());
                        }
                    }
                    case timeStrike -> {
                        if (timeStrikeCounter < purchasableTimeStrike) {
                            timeStrikeCounter++;
                            purchasableCards.add(card.clone());
                            repeatedIds.add(card.getId());
                        }
                    }
                }
            }
        }

        // If available cards are less than purchasableCards with the given conditions
        // then it will be filled randomly
        for (Card card : Card.allCards.values()) {
            boolean hasRepeated = loggedInUser.getCards().containsKey(card.getId()) || repeatedIds.contains(card.getId());
            if (!hasRepeated && purchasableCards.size() < purchasableAmount) {
                purchasableCards.add(card.clone());
            }
            if(purchasableCards.size() >= purchasableAmount){
                break;
            }
        }
    }

    static private void showCardProperties(String input, ArrayList<Card> list){
        if(!input.matches("^\\d+$")){
            System.out.println("Card index should be a number!");
            return;
        }
        int index = Integer.parseInt(input) - 1;
        if(index >= list.size()){
            System.out.println("Out of bound index!");
            return;
        }
        list.get(index).showProperties(PROP_PAD);
    }
}