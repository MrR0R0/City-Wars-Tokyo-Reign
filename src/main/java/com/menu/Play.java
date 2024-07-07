package com.menu;

import com.database.*;
import com.app.*;
import com.menu.authentication.*;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;

public class Play extends Menu {
    public static User tmepUser;

    public static class Cell {
        private boolean isHollow = false;
        private boolean isEmpty = true;
        //id-card pair
        private Pair<Integer, Card> card;
        public void makeSolid(){
            isHollow = false;
        }
    }

    static final Integer durationLineSize = 21;
    static final Integer deckSize = 5;


    static private User guest;
    static private final User host = loggedInUser.clone();
    static private User turnPlayer;
    static private Integer gameRound = 8;


    static private Card.Characters guestCharacter;
    static private Card.Characters hostCharacter;
    static private ArrayList<Cell> hostDurationLine = new ArrayList<>();
    static private ArrayList<Cell> guestDurationLine = new ArrayList<>();
    static private ArrayList<Cell> hostDeck = new ArrayList<>();
    static private ArrayList<Cell> guestDeck = new ArrayList<>();
    static private Integer hostTotalAttack = 0;
    static private Integer guestTotalAttack = 0;
    static private Integer pot = 0;

    static private final Random random = new Random();

    static private boolean isInBettingMode = false;
    static private boolean isInNormalMode = false;
    final static private String playModeCommand = "^select (?<Mode>\\w+) as the play mode$";
    final static private String selectCharacter = "^(?<Character>\\w+)$";
    final static String selectCardCommand = "^select card number (?<number>\\d+) player (?<player>\\S+)$";
    final static String placeCardCommand = "place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)";


    static public void handleInput(String input, Scanner scanner) throws IOException {
        // should we have an exit command?
        if (input.matches(playModeCommand)) {
            Matcher matcher = getCommandMatcher(input, playModeCommand);
            if (matcher.find()) {
                choosePlayMode(matcher);
            }
            return;
        }

        if (!isInBettingMode && !isInNormalMode) {
            System.out.println("you should choose the play mode first");
            return;
        }

        if (input.matches(Login.loginCommand)) {
            Matcher matcher = getCommandMatcher(input, Login.loginCommand);
            matcher.find();
            if (Login.checkLogIn(matcher, scanner)) {
                String username = matcher.group("Username");
                System.out.println("user logged in successfully");
                System.out.println("Welcome " + username + "!");
                tmepUser = User.signedUpUsers.get(User.getIdByUsername(username));
                guest = tmepUser.clone();
                System.out.println("Please choose a character: " + String.join(", ", Card.Characters.Character1.name(), Card.Characters.Character2.name(), Card.Characters.Character3.name(), Card.Characters.Character4.name()));
                return;
            }
            return;
        }
        if (input.matches(Login.forgotPassword)) {
            Matcher matcher = getCommandMatcher(input, Login.forgotPassword);
            if (matcher.find())
                Login.resetPassword(matcher, scanner);
            return;
        }

        if (guest == null) {
            System.out.println("Second player should log in first. Please log in by typing 'user login -u <Username> -p <Password>'.");
            return;
        }

        if (input.matches(selectCharacter)) {
            Matcher matcher = getCommandMatcher(input, selectCharacter);
            if (matcher.find()) {
                selectCharacter(matcher);
            }
        }

        if (guestCharacter != null && hostCharacter != null) {
            if (isInNormalMode)
                playing(scanner);
            if (isInBettingMode) {
                betting(scanner);
                if (pot != 0)
                    playing(scanner);
            }
        } else {
            System.out.println("you should choose your Characters first");
        }

    }

    private static void betting(Scanner scanner) {
        System.out.println("Enter the betting amount for the guest:");
        int guestBet = scanner.nextInt();
        System.out.println("Enter the betting amount for the host:");
        int hostBet = scanner.nextInt();

        if (guestBet <= guest.getWallet() && hostBet <= host.getWallet()) {
            guest.setWallet(guest.getWallet() - guestBet);
            host.setWallet(host.getWallet() - hostBet);
            pot = guestBet + hostBet;
            System.out.println("The total pot is: " + pot);
        } else {
            System.out.println("Both players must have enough money to place the bet.");
            pot = 0;
        }
    }

    static public void choosePlayMode(Matcher matcher) {
        if (matcher.group("Mode").equals("Normal")) {
            isInNormalMode = true;
            isInBettingMode = false;
            System.out.println("Normal mode is selected");
            System.out.println("Second player should now log in!");
        } else if (matcher.group("Mode").equals("Betting")) {
            isInNormalMode = false;
            isInBettingMode = true;
            System.out.println("Betting mode is selected");
            System.out.println("Second player should now log in!");
        } else {
            System.out.println("Invalid play mode selected. Please choose either \"Normal\" or \"Betting\" mode.");
        }
    }

    // each player choose their character
    static public void selectCharacter(Matcher matcher) {
        try {
            Card.Characters selectedCharacter = Card.Characters.valueOf(matcher.group("Character"));
            if (guestCharacter == null) {
                guestCharacter = selectedCharacter;
                System.out.println("Guest selected character: " + selectedCharacter);
            } else {
                hostCharacter = selectedCharacter;
                System.out.println("Host selected character: " + selectedCharacter);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid character selected. Please choose one of the following: " + String.join(", ", Card.Characters.Character1.name(), Card.Characters.Character2.name(), Card.Characters.Character3.name(), Card.Characters.Character4.name()));
        }
    }

    // handling playing events
    public static void playing(Scanner scanner) {
        turnPlayer = random.nextInt(2) == 0 ? host : guest;
        //init first round
        initEachRound();

        while (gameRound > 0) {
            String input = scanner.nextLine();
            if(input.matches(selectCardCommand))
                selectCard(input);
            else if(input.matches(placeCardCommand)) {
                gameRound--;
                if (turnPlayer.equals(host)) {
                    System.out.println("Host's turn");
                    placeCard(input);
                } else if (turnPlayer.equals(guest)) {
                    System.out.println("Guest's turn");
                    placeCard(input);
                }
                printPlayGround();
            }
        }
        movingTimeLine();

        if (isGameOver() != null) {
            result(Objects.requireNonNull(isGameOver()));
            System.out.println("Guest will now be logged out automatically");
            Menu.currentMenu = MenuType.Main;
        } else {
            // next 4 round
            playing(scanner);
        }
    }

    private static void initEachRound() {
        // remove everything
        hostDeck = new ArrayList<>();
        guestDeck = new ArrayList<>();
        hostDurationLine = new ArrayList<>();
        guestDurationLine = new ArrayList<>();
        initArrays(guestDurationLine, durationLineSize);
        initArrays(hostDurationLine, durationLineSize);
        hostDurationLine.forEach(cell -> cell.isHollow = false);
        guestDurationLine.forEach(cell -> cell.isHollow = false);

        hostTotalAttack = 0;
        guestTotalAttack = 0;



        // init everything
        gameRound = 8;
        hostDurationLine.get(random.nextInt(durationLineSize)).isHollow = true;
        guestDurationLine.get(random.nextInt(durationLineSize)).isHollow = true;

        HashSet<Integer> hostRepeatedIds = new HashSet<>();
        HashSet<Integer> guestRepeatedIds = new HashSet<>();

        Integer randomId;
        while (hostDeck.size() < deckSize || guestDeck.size() < deckSize) {
            if (hostDeck.size() < deckSize) {
                randomId = getRandomKey(host.getDeckOfCards());
                if (!hostRepeatedIds.contains(randomId)) {
                    Cell cell = new Cell();
                    cell.card = new Pair<>(host.getDeckOfCards().get(randomId).getId(), host.getDeckOfCards().get(randomId).clone());
                    cell.isEmpty = false;
                    if (cell.card.getValue().getCharacter().equals(hostCharacter.name()))
                        cell.card.getValue().setAttackOrDefense(Double.valueOf(cell.card.getValue().getAttackOrDefense() * 1.5).intValue());
                    hostDeck.add(cell);
                    hostRepeatedIds.add(randomId);
                }
            }
            if (guestDeck.size() < deckSize) {
                randomId = getRandomKey(guest.getDeckOfCards());
                if (!guestRepeatedIds.contains(randomId)) {
                    Cell cell = new Cell();
                    cell.card = new Pair<>(guest.getDeckOfCards().get(randomId).getId(), guest.getDeckOfCards().get(randomId).clone());
                    cell.isEmpty = false;
                    if (cell.card.getValue().getCharacter().equals(guestCharacter.name()))
                        cell.card.getValue().setAttackOrDefense(Double.valueOf(cell.card.getValue().getAttackOrDefense() * 1.5).intValue());
                    guestDeck.add(cell);
                    guestRepeatedIds.add(randomId);
                }
            }
        }
    }

    private static void selectCard(String input) {
        Matcher matcher = getCommandMatcher(input, selectCardCommand);
        if (matcher.find()) {
            int selectedNumber = Integer.parseInt(matcher.group("number"));
            if (matcher.group("player").equals("host")) {
                hostDeck.get(selectedNumber).card.getValue().showProperties(20, true);
            }
            if (matcher.group("player").equals("guest")) {
                guestDeck.get(selectedNumber).card.getValue().showProperties(20, true);
            }
        }
    }

    private static void placeCard(String input) {
        Matcher matcher = getCommandMatcher(input, placeCardCommand);
        if (matcher.find()) {
            int selectedCard = Integer.parseInt(matcher.group("cardNum"));
            int selectedCell = Integer.parseInt(matcher.group("cellNum"));
            if (turnPlayer.equals(guest)) {
                // special cards need to define here ...

                //hole repairer
                if(guestDeck.get(selectedCard).card.getKey().equals(3)){
                    if(guestDurationLine.get(selectedCell).isHollow){
                        guestDurationLine.get(selectedCard).makeSolid();
                    }
                    else{
                        System.out.println("The cell is not hollow!");
                    }
                    return;
                }

                //checking cells
                for (int i = selectedCell - 1; i < guestDeck.get(selectedCard).card.getValue().getDuration(); i++) {
                    if (guestDurationLine.get(i).isHollow) {
                        System.out.println("you can't place this card because the cell is hollow");
                        return;
                    }
                    if (!guestDurationLine.get(i).isEmpty) {
                        System.out.println("you can't place this card because the cell is not empty");
                        return;
                    }
                }
                System.out.println("card has been placed successfully");

                // same type with middle card
                if (random.nextInt(4) == 1 && hostDeck.size() % 2 == 1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size() - 1) / 2).card.getValue().getType())) {
                    hostDeck.get(selectedCard).card.getValue().boostAttackDefense(1.2);
                }

                // destruction of cards
                for (int i = selectedCell - 1; i < guestDeck.get(selectedCard).card.getValue().getDuration(); i++) {
                    guestDurationLine.get(i).card = new Pair<>(guestDeck.get(selectedCard).card.getValue().getId(), guestDeck.get(selectedCard).card.getValue().clone());
                    if (!hostDurationLine.get(i).isEmpty) {
                        if (hostDurationLine.get(i).card.getValue().getAcc() < guestDurationLine.get(i).card.getValue().getAcc()) {
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.get(i).card = null;

                            // reward for destruction of card
                            if (random.nextInt(4) == 0) guest.setXP(guest.getXP() + 10);
                            else if (random.nextInt(4) == 1) guest.setWallet(guest.getWallet() + 20);

                            System.out.println("host's card is destroyed");
                        }
                        else if (hostDurationLine.get(i).card.getValue().getAcc() > guestDurationLine.get(i).card.getValue().getAcc()) {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.get(i).card = null;
                            System.out.println("guest's card is destroyed");
                        }
                        else {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.get(i).card = null;
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.get(i).card = null;
                            System.out.println("both cards are destroyed");
                        }
                    }
                }
                guestDeck.get(selectedCard).isEmpty = true;
                guestDeck.get(selectedCard).card = null;
                replaceRandomCard();
            }
            else if (turnPlayer.equals(host)) {
                // special cards need to define here ...

                //hole repairer
                if(hostDeck.get(selectedCard).card.getKey().equals(3)){
                    if(hostDurationLine.get(selectedCell).isHollow){
                        hostDurationLine.get(selectedCard).makeSolid();
                    }
                    else{
                        System.out.println("The cell is not hollow!");
                    }
                    return;
                }

                //checking cells
                for (int i = selectedCell - 1; i < hostDeck.get(selectedCard).card.getValue().getDuration(); i++) {
                    if (hostDurationLine.get(i).isHollow) {
                        System.out.println("you can't place this card because the cell is hollow");
                        return;
                    }
                    if (!hostDurationLine.get(i).isEmpty) {
                        System.out.println("you can't place this card because the cell is not empty");
                        return;
                    }
                }

                // same type with middle card
                if (random.nextInt(4) == 1 && hostDeck.size() % 2 == 1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size() - 1) / 2).card.getValue().getType())) {
                    hostDeck.get(selectedCard).card.getValue().boostAttackDefense(1.2);
                }

                //destruction of cards
                for (int i = selectedCell - 1; i < hostDeck.get(selectedCard).card.getValue().getDuration(); i++) {
                     if (!guestDurationLine.get(i).isEmpty) {
                        if (guestDurationLine.get(i).card.getValue().getAcc() < hostDurationLine.get(i).card.getValue().getAcc()) {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.get(i).card = null;

                            // reward for destruction of card
                            if (random.nextInt(4) == 0) host.setXP(host.getXP() + 10);
                            else if (random.nextInt(4) == 1) host.setWallet(host.getWallet() + 20);


                            System.out.println("guest's card is destroyed");
                        } else if (guestDurationLine.get(i).card.getValue().getAcc() > hostDurationLine.get(i).card.getValue().getAcc()) {
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.get(i).card = null;
                            System.out.println("host's card is destroyed");
                        } else {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.get(i).card = null;
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.get(i).card = null;
                            System.out.println("both cards are destroyed");
                        }
                    }
                }
                hostDeck.get(selectedCard).isEmpty = true;
                hostDeck.get(selectedCard).card = null;
                replaceRandomCard();
            }
        }
    }

    private static void replaceRandomCard() {
        ListIterator<Cell> iterator;
        if (turnPlayer.equals(guest)) {
            iterator = guestDeck.listIterator();
            // replace with empty card
            while (iterator.hasNext()) {
                if (iterator.next().isEmpty) {
                    int randomId = getRandomKey(guest.getDeckOfCards());
                    //same character boost
                    if (guest.getDeckOfCards().get(randomId).getCharacter().equals(guestCharacter.name()))
                        guest.getDeckOfCards().get(randomId).boostAttackDefense(1.5);
                    iterator.next().card = new Pair<>(guest.getDeckOfCards().get(randomId).getId(), guest.getDeckOfCards().get(randomId).clone());
                    System.out.println("card has been replaced successfully");

                    // change player for next round
                    turnPlayer = host;
                }
            }
        }
        if (turnPlayer.equals(host)) {
            iterator = hostDeck.listIterator();
            // replace with empty card
            while (iterator.hasNext()) {
                if (iterator.next().isEmpty) {
                    int randomId = getRandomKey(host.getDeckOfCards());
                    //same character boost
                    if (host.getDeckOfCards().get(randomId).getCharacter().equals(hostCharacter.name()))
                        host.getDeckOfCards().get(randomId).boostAttackDefense(1.5);
                    iterator.next().card = new Pair<>(host.getDeckOfCards().get(randomId).getId(), host.getDeckOfCards().get(randomId).clone());
                    System.out.println("card has been replaced successfully");

                    // change player for next round
                    turnPlayer = guest;
                }
            }
        }
    }

    private static void movingTimeLine() {
        for (int i = 0; i < durationLineSize; i++) {
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println("Block " + (i + 1) + " :");
            if (!guestDurationLine.get(i).isEmpty && guestDurationLine.get(i).card != null && !guestDurationLine.get(i).isHollow) {
                guestTotalAttack += guestDurationLine.get(i).card.getValue().getGamingAttackOrDefense();
                host.setHP(host.getHP() - guestDurationLine.get(i).card.getValue().getGamingAttackOrDefense());
                guestDurationLine.get(i).card.getValue().showProperties(10, true);
                System.out.println("host HP: " + guest.getHP());
                System.out.println("host total damage: " + guestTotalAttack);
                // show properties of each cell
                // ...
            } else if (guestDurationLine.get(i).isEmpty) {
                System.out.println("Block is empty");
            } else if (guestDurationLine.get(i).isHollow) {
                System.out.println("Block is hollow");
            }
            if (!hostDurationLine.get(i).isEmpty && hostDurationLine.get(i).card != null && !hostDurationLine.get(i).isHollow) {
                hostTotalAttack += hostDurationLine.get(i).card.getValue().getGamingAttackOrDefense();
                guest.setHP(guest.getHP() - hostDurationLine.get(i).card.getValue().getGamingAttackOrDefense());
                System.out.println("host HP: " + host.getHP());
                System.out.println("host total damage: " + hostTotalAttack);
                // show properties of each cell
                // ...
            } else if (guestDurationLine.get(i).isEmpty) {
                System.out.println("Block is empty");
            } else if (guestDurationLine.get(i).isHollow) {
                System.out.println("Block is hollow");
            }
            System.out.println("---------------------------------------------------------------------------------------");

            if (isGameOver() != null) {
                return;
            }
        }
    }

    private static User isGameOver() {
        if (guest.getHP() <= 0)
            return host;
        else if (host.getHP() <= 0)
            return guest;
        return null;
    }

    private static void result(User user) {
        String result = "", hostCons = "", guestCons="";
        if (user.equals(host)) {
            result = "Host Won!";
            hostCons = "XP: +" + 0.1 * host.getHP() + " Coin: +" + 0.2 * host.getHP();
            guestCons = "XP: +" + 0.01 * guest.getHP();
            host.setXP(Double.valueOf(host.getXP() + 0.1 * host.getHP()).intValue());
            host.setWallet(Double.valueOf(host.getWallet() + 0.2 * host.getHP()).intValue());
            System.out.println("The winner is " + host.getNickname());
            if (isInBettingMode) {
                host.setWallet(host.getWallet() +pot);
                System.out.println("All the pot is for " + host.getNickname());
                System.out.println("obtained coin: " + pot);
            }
            if (isInNormalMode){
                host.setWallet(Double.valueOf(host.getWallet() + 0.2 * host.getHP()).intValue());
                System.out.println("obtained coin: " + 0.2 * host.getHP());
            }
            System.out.println("obtained XP: " + 0.1 * host.getHP());
            System.out.println("total XP: " + host.getXP());
            System.out.println("require XP to next level: " + User.nextLevelXP(host.getLevel()));
            if (User.nextLevelXP(host.getLevel()) < host.getXP()) {
                host.setLevel(host.getLevel() + 1);
                System.out.println(host.getNickname() + "'s level is upgraded to " + host.getLevel());
            }
            System.out.println();
            guest.setXP(Double.valueOf(guest.getXP() + 0.01 * guest.getHP()).intValue());
            System.out.println("The loser is " + guest.getNickname());
            if (isInBettingMode) {
                guest.setWallet(guest.getWallet()-pot);
                System.out.println("lost coin: " + pot);
            }
            System.out.println("obtained XP: " + 0.01 * guest.getHP());
            System.out.println("total XP: " + guest.getXP());
            System.out.println("require XP to next level: " + User.nextLevelXP(guest.getLevel()));
            if (User.nextLevelXP(guest.getLevel()) < guest.getXP()) {
                guest.setLevel(guest.getLevel() + 1);
                System.out.println(guest.getNickname() + "'s level is upgraded to " + guest.getLevel());
            }

        }
        else if (user.equals(guest)) {
            result = "Guest Won!";
            guestCons = "XP: +" + 0.1 * guest.getHP() + " Coin: +" + 0.2 * guest.getHP();
            hostCons = "XP: +" + 0.01 * host.getHP();
            guest.setXP(Double.valueOf(guest.getXP() + 0.1 * guest.getHP()).intValue());
            System.out.println("The winner is " + guest.getNickname());
            if (isInBettingMode) {
                guest.setWallet(guest.getWallet() +pot);
                System.out.println("All the pot is for " + guest.getNickname());
                System.out.println("obtained coin: " + pot);
            }
            if (isInNormalMode){
                guest.setWallet(Double.valueOf(guest.getWallet() + 0.2 * guest.getHP()).intValue());
                System.out.println("obtained coin: " + 0.2 * guest.getHP());
            }
            System.out.println("obtained XP: " + 0.1 * guest.getHP());
            System.out.println("total XP: " + guest.getXP());
            System.out.println("require XP to next level: " + User.nextLevelXP(guest.getLevel()));
            if (User.nextLevelXP(guest.getLevel()) < guest.getXP()) {
                guest.setLevel(guest.getLevel() + 1);
                System.out.println(guest.getNickname() + "'s level is upgraded to " + guest.getLevel());
            }
            System.out.println();
            host.setXP(Double.valueOf(host.getXP() + 0.01 * host.getHP()).intValue());
            System.out.println("The loser is " + host.getNickname());
            if (isInBettingMode) {
                host.setWallet(host.getWallet()-pot);
                System.out.println("lost coin: " + pot);
            }
            System.out.println("obtained XP: " + 0.01 * host.getHP());
            System.out.println("total XP: " + host.getXP());
            System.out.println("require XP to next level: " + User.nextLevelXP(host.getLevel()));
            if (User.nextLevelXP(host.getLevel()) < host.getXP()) {
                host.setLevel(host.getLevel() + 1);
                System.out.println(host.getNickname() + "'s level is upgraded to " + host.getLevel());
            }
        }

        Connect.insertHistory(guest.getUsername(), guest.getLevel(), guestCons,
                            host.getUsername(), guest.getLevel(), hostCons,
                            result,
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            host.getId(), guest.getId());

        Menu.loggedInUser.setWallet(host.getWallet());
        Menu.loggedInUser.setXP(host.getXP());
        Menu.loggedInUser.setLevel(host.getLevel());
        User.signedUpUsers.get(guest.getId()).setWallet(guest.getWallet());
        User.signedUpUsers.get(guest.getId()).setXP(guest.getXP());
        User.signedUpUsers.get(guest.getId()).setLevel(guest.getLevel());
    }

    private static void printPlayGround() {
        System.out.println("__________________________________________________________________________");
        System.out.println("Guest HP : " + guest.getHP() + " | Host HP : " + host.getHP());
        System.out.println("..........................................................................");
        for (int i = 0; i < durationLineSize; i++) {
            Cell hostCell = hostDurationLine.get(i);
            Cell guestCell = guestDurationLine.get(i);
            if (hostCell.card != null && guestCell.card != null) {
                System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getName(), hostCell.card.getValue().getName());
                System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getAcc(), hostCell.card.getValue().getAcc());
                System.out.printf("%-7d | %-7d%n", guestCell.card.getValue().getAttackOrDefense() / guestCell.card.getValue().getDuration(), hostCell.card.getValue().getAttackOrDefense());
                System.out.printf("%-7d | %-7d%n", guestCell.card.getValue().getDamage(), hostCell.card.getValue().getDamage());
            }
            else if (hostCell.card != null) {
                if (guestCell.isHollow) {
                    System.out.printf("%-7s | %-7s%n", "", hostCell.card.getValue().getName());
                    System.out.printf("%-7s | %-7s%n", "", hostCell.card.getValue().getAcc());
                    System.out.printf("%-7s | %-7d%n", "Hollow", hostCell.card.getValue().getAttackOrDefense());
                    System.out.printf("%-7s | %-7d%n", "", hostCell.card.getValue().getDamage());
                }
                else if (guestCell.isEmpty) {
                    System.out.printf("%-7s | %-7s%n", "", hostCell.card.getValue().getAcc());
                    System.out.printf("%-7s | %-7s%n", "Empty", hostCell.card.getValue().getAcc());
                    System.out.printf("%-7s | %-7d%n", "", hostCell.card.getValue().getDamage());
                }
            }
            else if (guestCell.card != null) {
                if (hostCell.isHollow) {
                    System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getName(), "");
                    System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getAcc(), "");
                    System.out.printf("%-7d | %-7s%n", guestCell.card.getValue().getAttackOrDefense(), "Hollow");
                    System.out.printf("%-7d | %-7s%n", guestCell.card.getValue().getDamage(), "");
                } else if (hostCell.isEmpty) {
                    System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getName(), "");
                    System.out.printf("%-7s | %-7s%n", guestCell.card.getValue().getAcc(), "");
                    System.out.printf("%-7d | %-7s%n", guestCell.card.getValue().getAttackOrDefense(), "Empty");
                    System.out.printf("%-7d | %-7s%n", guestCell.card.getValue().getDamage(), "");
                }
            }
            else {
                if (hostCell.isHollow) {
                    System.out.printf("%-7s | %-7s%n", "Hollow", "Hollow");
                    System.out.printf("%-7s | %-7s%n", "", "");
                    System.out.printf("%-7s | %-7s%n", "", "");
                }
                else if (hostCell.isEmpty) {
                    System.out.printf("%-7s | %-7s%n", "Empty", "Empty");
                    System.out.printf("%-7s | %-7s%n", "", "");
                    System.out.printf("%-7s | %-7s%n", "", "");
                }
            }
            System.out.println("..........................................................................");
        }
    }

    private static void initArrays(ArrayList<Cell> list, Integer capacity) {
        for (int i = 0; i < capacity; i++) {
            list.add(new Cell());
        }
    }

    public static <K, V> K getRandomKey(HashMap<K, V> map) {
        // Convert the keys to a List
        List<K> keys = new ArrayList<>(map.keySet());

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(keys.size());

        // Retrieve the key at the random index
        return keys.get(randomIndex);
    }
}