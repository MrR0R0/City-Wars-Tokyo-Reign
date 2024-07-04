package menu;

import app.Card;
import app.User;
import javafx.util.Pair;
import menu.authentication.Login;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

public class Play extends Menu {
    public static User tmepUser;
    public static class Cell{
        public boolean isHollow = false;
        public boolean isEmpty = true;
        public Pair<Integer, Card> card;
    }

    static final Integer durationLineSize = 21;
    static final Integer deckSize = 5;


    static private User guest;
    static private User host = loggedInUser.clone();
    static private User turnPlayer;
    static private Integer gameRound = 4;


    static private Card.Characters guestCharacter;
    static private Card.Characters hostCharacter;
    static private ArrayList<Cell> hostDurationLine = new ArrayList<>(durationLineSize);
    static private ArrayList<Cell> guestDurationLine = new ArrayList<>(durationLineSize);
    static private ArrayList<Cell> hostDeck = new ArrayList<>(deckSize);
    static private ArrayList<Cell> guestDeck = new ArrayList<>(deckSize);

    static private Random random = new Random();

    static private boolean isInBettingMode = false;
    static private boolean isInNormalMode = false;
    final static private String playModeCommand = "^select (?<Mode>\\w+) as thr play mode$";
    final static private String selectCharacter = "^(?<Character>\\w+)$";

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
                Menu.currentMenu = MenuType.Main;
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
            playing(scanner);
        }
        else {
            System.out.println("you should choose your Characters first");
        }

    }

    static public void choosePlayMode(Matcher matcher) {
        if (matcher.group("Mode").equals("Normal")) {
            isInNormalMode = true;
            isInBettingMode = false;
            System.out.println("Normal mode is selected");
        }
        else if (matcher.group("Mode").equals("Betting")) {
            isInNormalMode = false;
            isInBettingMode = true;
            System.out.println("Betting mode is selected");
        }
        else {
            System.out.println("Invalid play mode selected. Please choose either \"Normal\" or \"Betting\" mode.");
            return;
        }
        System.out.println("Please choose a character: " + String.join(", ", Card.Characters.Character1.name(), Card.Characters.Character2.name(), Card.Characters.Character3.name(), Card.Characters.Character4.name()));
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

    private static void playing(Scanner scanner){
        turnPlayer = random.nextInt(2) == 0 ? host : guest;
        //init first round
        initEachRound();


        while (!gameIsOver()) {
            String input = scanner.nextLine();
            selectCard(input);
            if (turnPlayer.equals(host)) {


            } else if (turnPlayer.equals(guest)) {


            }
        }
    }

    private static void initEachRound(){
        // remove everything
        hostDurationLine.forEach(cell -> {cell.isHollow = false;});
        hostDeck = new ArrayList<>(deckSize);
        guestDeck = new ArrayList<>(deckSize);
        hostDurationLine = new ArrayList<>(durationLineSize);
        guestDurationLine = new ArrayList<>(durationLineSize);


        // init everything
        host.setHP(loggedInUser.getHP());
        guest.setHP(tmepUser.getHP());
        hostDurationLine.get(random.nextInt(durationLineSize)).isHollow = true;
        guestDurationLine.get(random.nextInt(durationLineSize)).isHollow = true;

        for (int i = 0 ; i < deckSize ; i++){
            // maybe entrySet is wrong
            int randomId = random.nextInt(host.getDeckOfCards().entrySet().size());
            if (!hostDeck.contains(randomId)) {
                hostDeck.get(i).card = new Pair<>(host.getDeckOfCards().get(randomId).getId(),host.getDeckOfCards().get(randomId).clone());
                if (host.getDeckOfCards().get(randomId).getCharacter().equals(hostCharacter.name()))
                   hostDeck.get(i).card.getValue().setAttackOrDefense(Double.valueOf(hostDeck.get(i).card.getValue().getAttackOrDefense()*1.5).intValue());
            }
            randomId = random.nextInt(guest.getDeckOfCards().entrySet().size());
            if (!guestDeck.contains(randomId)) {
                guestDeck.get(i).card = new Pair<>(guest.getDeckOfCards().get(randomId).getId(), guest.getDeckOfCards().get(randomId).clone());
                if (host.getDeckOfCards().get(randomId).getCharacter().equals(hostCharacter.name()))
                    hostDeck.get(i).card.getValue().setAttackOrDefense(Double.valueOf(hostDeck.get(i).card.getValue().getAttackOrDefense()*1.5).intValue());
            }
        }
    }

    private static void selectCard(String input){
        String selectCardCommand = "^(?i)select card number (?<number>\\d+) player (?<player>\\S+)$";
        Matcher matcher = getCommandMatcher(input, selectCardCommand);
        if (matcher.find()) {
            Integer selectedNumber = Integer.parseInt(matcher.group("Number"));
            if (matcher.group("player").equals("host")) {
                hostDeck.get(selectedNumber).card.getValue().showProperties(15,true);
            }
        }
    }
    private static void placeCard(String input, User player){
        String placeCardCommand = "(?i)place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)";
        Matcher matcher = getCommandMatcher(input, placeCardCommand);
        if (matcher.find()) {
            int selectedCard = Integer.parseInt(matcher.group("CardNum"));
            int selectedCell = Integer.parseInt(matcher.group("CellNum"));
            if (player.equals(guest)){
                // special cards need to define here.

                //checking cells
                for (int i = selectedCell-1; i < guestDeck.get(selectedCard).card.getValue().getDuration(); i++){
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
                if (hostDeck.size()%2==1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size()-1)/2).card.getValue().getType())){
                    hostDeck.get(selectedCard).card.getValue().setGamingAttackOrDefense(Double.valueOf(hostDeck.get(selectedCard).card.getValue().getGamingAttackOrDefense() * 1.2).intValue());
                }

                // destruction of cards
                for (int i = selectedCell-1; i < guestDeck.get(selectedCard).card.getValue().getDuration(); i++){
                    guestDurationLine.get(i).card = new Pair<>(guestDeck.get(selectedCard).card.getValue().getId(),guestDeck.get(selectedCard).card.getValue().clone());
                    if (!hostDurationLine.get(i).isEmpty) {
                        if (hostDurationLine.get(i).card.getValue().getAcc() < guestDurationLine.get(i).card.getValue().getAcc()) {
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.remove(i);
                            if (random.nextInt(2) == 0) guest.setXP(guest.getXP() + 10);
                            else guest.setWallet(guest.getWallet() + 20);
                            System.out.println("host's card is destroyed");
                        }
                        else if (hostDurationLine.get(i).card.getValue().getAcc() > guestDurationLine.get(i).card.getValue().getAcc()) {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.remove(i);
                            System.out.println("guest's card is destroyed");
                        }
                        else {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.remove(i);
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.remove(i);
                            System.out.println("both cards are destroyed");
                        }
                    }
                }
                guestDeck.get(selectedCard).isEmpty = true;
                guestDeck.get(selectedCard).card = null;
                replaceRandomCard(player);
            }
            if (player.equals(host)){
                // special cards need to define here.

                //checking cells
                for (int i = selectedCell-1; i < hostDeck.get(selectedCard).card.getValue().getDuration(); i++){
                    if (hostDurationLine.get(i).isHollow) {
                        System.out.println("you can't place this card because the cell is hollow");
                        return;
                    }
                    if (!hostDurationLine.get(i).isEmpty) {
                        System.out.println("you can't place this card because the cell is not empty");
                        return;
                    }
                }
                System.out.println("card has been placed successfully");

                // same type with middle card
                if (hostDeck.size()%2==1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size()-1)/2).card.getValue().getType())){
                    hostDeck.get(selectedCard).card.getValue().setGamingAttackOrDefense(Double.valueOf(hostDeck.get(selectedCard).card.getValue().getGamingAttackOrDefense() * 1.2).intValue());
                }


                // destruction of cards
                for (int i = selectedCell-1; i < hostDeck.get(selectedCard).card.getValue().getDuration(); i++){
                    hostDurationLine.get(i).card = new Pair<>(hostDeck.get(selectedCard).card.getValue().getId(),hostDeck.get(selectedCard).card.getValue().clone());
                    if (!guestDurationLine.get(i).isEmpty) {
                        if (guestDurationLine.get(i).card.getValue().getAcc() < hostDurationLine.get(i).card.getValue().getAcc()) {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.remove(i);
                            if (random.nextInt(2) == 0) host.setXP(host.getXP() + 10);
                            else host.setWallet(host.getWallet() + 20);
                            System.out.println("guest's card is destroyed");
                        }
                        else if (guestDurationLine.get(i).card.getValue().getAcc() > hostDurationLine.get(i).card.getValue().getAcc()) {
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.remove(i);
                            System.out.println("host's card is destroyed");
                        }
                        else {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.remove(i);
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.remove(i);
                            System.out.println("both cards are destroyed");
                        }
                    }
                }
                hostDeck.get(selectedCard).isEmpty = true;
                hostDeck.get(selectedCard).card = null;
                replaceRandomCard(player);
            }
        }
    }

    private static boolean gameIsOver(){
        if ((host.getHP() <= 0 || guest.getHP() <= 0) && gameRound <= 0) {
            return true;
        }
        return false;
    }

    private static void replaceRandomCard(User player){
        ListIterator<Cell> iterator;
        if (player.equals(guest)){
            iterator = guestDeck.listIterator();
            // replace with empty card
            if (iterator.hasNext()) {
                if (iterator.next().isEmpty){
                    int randomId = random.nextInt(guest.getDeckOfCards().entrySet().size());
                    //same character boost
                    if (guest.getDeckOfCards().get(randomId).getCharacter().equals(guestCharacter.name()))
                        guest.getDeckOfCards().get(randomId).setGamingAttackOrDefense(Double.valueOf(guest.getDeckOfCards().get(randomId).getGamingAttackOrDefense()*1.5).intValue());
                    iterator.next().card = new Pair<>(guest.getDeckOfCards().get(randomId).getId(), guest.getDeckOfCards().get(randomId).clone());
                    System.out.println("card has been replaced successfully");
                }
            }
        }
        if (player.equals(host)){
            iterator = hostDeck.listIterator();
            // replace with empty card
            if (iterator.hasNext()) {
                if (iterator.next().isEmpty){
                    int randomId = random.nextInt(host.getDeckOfCards().entrySet().size());
                    //same character boost
                    if (host.getDeckOfCards().get(randomId).getCharacter().equals(hostCharacter.name()))
                        host.getDeckOfCards().get(randomId).setGamingAttackOrDefense(Double.valueOf(host.getDeckOfCards().get(randomId).getGamingAttackOrDefense()*1.5).intValue());
                    iterator.next().card = new Pair<>(host.getDeckOfCards().get(randomId).getId(), host.getDeckOfCards().get(randomId).clone());
                    System.out.println("card has been replaced successfully");
                }
            }
        }
    }

}
