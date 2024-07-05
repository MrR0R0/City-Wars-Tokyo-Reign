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
    static private ArrayList<Cell> hostDeck = new ArrayList<>();
    static private ArrayList<Cell> guestDeck = new ArrayList<>();
    static private Integer hostTotalAttack = 0;
    static private Integer guestTotalAttack = 0;

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
    public static void playing(Scanner scanner){
        turnPlayer = random.nextInt(2) == 0 ? host : guest;
        //init first round
        initEachRound();


        while (gameRound > 0) {
            gameRound -= 1;
            String input = scanner.nextLine();
            selectCard(input);
            if (turnPlayer.equals(host)) {
                placeCard(input);
            } else if (turnPlayer.equals(guest)) {
                placeCard(input);
            }
        }
        movingTimeLine();

        if (isGameOver() != null){
            result(isGameOver());
        }
        else {
            // next 4 round
            playing(scanner);
        }
    }

    private static void initEachRound(){
        // remove everything
        hostDurationLine.forEach(cell -> {cell.isHollow = false;});
        hostDeck = new ArrayList<>();
        guestDeck = new ArrayList<>(deckSize);
        hostDurationLine = new ArrayList<>();
        guestDurationLine = new ArrayList<>();
        initArrays(guestDeck,deckSize);
        initArrays(hostDeck,deckSize);
        initArrays(guestDurationLine,durationLineSize);
        initArrays(hostDurationLine,durationLineSize);

        hostTotalAttack = 0;
        guestTotalAttack =0;



        // init everything
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
        String selectCardCommand = "^select card number (?<number>\\d+) player (?<player>\\S+)$";
        Matcher matcher = getCommandMatcher(input, selectCardCommand);
        if (matcher.find()) {
            Integer selectedNumber = Integer.parseInt(matcher.group("Number"));
            if (matcher.group("player").equals("host")) {
                hostDeck.get(selectedNumber).card.getValue().showProperties(15,true);
            }
        }
    }
    private static void placeCard(String input){
        String placeCardCommand = "place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)";
        Matcher matcher = getCommandMatcher(input, placeCardCommand);
        if (matcher.find()) {
            int selectedCard = Integer.parseInt(matcher.group("CardNum"));
            int selectedCell = Integer.parseInt(matcher.group("CellNum"));
            if (turnPlayer.equals(guest)){
                // special cards need to define here ...

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
                if (random.nextInt(4) == 1 && hostDeck.size()%2==1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size()-1)/2).card.getValue().getType())){
                    hostDeck.get(selectedCard).card.getValue().setGamingAttackOrDefense(Double.valueOf(hostDeck.get(selectedCard).card.getValue().getGamingAttackOrDefense() * 1.2).intValue());
                }

                // destruction of cards
                for (int i = selectedCell-1; i < guestDeck.get(selectedCard).card.getValue().getDuration(); i++){
                    guestDurationLine.get(i).card = new Pair<>(guestDeck.get(selectedCard).card.getValue().getId(),guestDeck.get(selectedCard).card.getValue().clone());
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
            if (turnPlayer.equals(host)){
                // special cards need to define here ...

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
                if (random.nextInt(4) == 1 && hostDeck.size()%2==1 && hostDeck.get(selectedCard).card.getValue().getType().equals(hostDeck.get((hostDeck.size()-1)/2).card.getValue().getType())){
                    hostDeck.get(selectedCard).card.getValue().setGamingAttackOrDefense(Double.valueOf(hostDeck.get(selectedCard).card.getValue().getGamingAttackOrDefense() * 1.2).intValue());
                }


                // destruction of cards
                for (int i = selectedCell-1; i < hostDeck.get(selectedCard).card.getValue().getDuration(); i++){
                    hostDurationLine.get(i).card = new Pair<>(hostDeck.get(selectedCard).card.getValue().getId(),hostDeck.get(selectedCard).card.getValue().clone());
                    if (!guestDurationLine.get(i).isEmpty) {
                        if (guestDurationLine.get(i).card.getValue().getAcc() < hostDurationLine.get(i).card.getValue().getAcc()) {
                            guestDurationLine.get(i).isEmpty = true;
                            guestDurationLine.get(i).card = null;

                            // reward for destruction of card
                            if (random.nextInt(4) == 0) host.setXP(host.getXP() + 10);
                            else if (random.nextInt(4) == 1) host.setWallet(host.getWallet() + 20);


                            System.out.println("guest's card is destroyed");
                        }
                        else if (guestDurationLine.get(i).card.getValue().getAcc() > hostDurationLine.get(i).card.getValue().getAcc()) {
                            hostDurationLine.get(i).isEmpty = true;
                            hostDurationLine.get(i).card = null;
                            System.out.println("host's card is destroyed");
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
                hostDeck.get(selectedCard).isEmpty = true;
                hostDeck.get(selectedCard).card = null;
                replaceRandomCard();
            }
        }
    }

    private static void replaceRandomCard(){
        ListIterator<Cell> iterator;
        if (turnPlayer.equals(guest)){
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

                    // change player for next round
                    turnPlayer = host;
                }
            }
        }

        if (turnPlayer.equals(host)){
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

                    // change player for next round
                    turnPlayer = guest;
                }
            }
        }
    }

    private static void movingTimeLine(){
        for (int i = 0; i < durationLineSize; i++){
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println("Block "+(i+1) + " :");
            if (!guestDurationLine.get(i).isEmpty && guestDurationLine.get(i).card != null && !guestDurationLine.get(i).isHollow){
                guestTotalAttack += guestDurationLine.get(i).card.getValue().getGamingAttackOrDefense();
                host.setHP(host.getHP()-guestDurationLine.get(i).card.getValue().getGamingAttackOrDefense());
                guestDurationLine.get(i).card.getValue().showProperties(10,true);
                System.out.println("host HP: " + guest.getHP());
                System.out.println("host total damage: " + guestTotalAttack);
                // show properties of each cell
                // ...
            }
            else if (guestDurationLine.get(i).isEmpty) {
                System.out.println("Block is empty");
            }
            else if (guestDurationLine.get(i).isHollow) {
                System.out.println("Block is hollow");
            }
            if (!hostDurationLine.get(i).isEmpty && hostDurationLine.get(i).card != null && !hostDurationLine.get(i).isHollow){
                hostTotalAttack += hostDurationLine.get(i).card.getValue().getGamingAttackOrDefense();
                guest.setHP(guest.getHP()- hostDurationLine.get(i).card.getValue().getGamingAttackOrDefense());
                System.out.println("host HP: " + host.getHP());
                System.out.println("host total damage: " + hostTotalAttack);
                // show properties of each cell
                // ...
            }
            else if (guestDurationLine.get(i).isEmpty) {
                System.out.println("Block is empty");
            }
            else if (guestDurationLine.get(i).isHollow) {
                System.out.println("Block is hollow");
            }
            System.out.println("---------------------------------------------------------------------------------------");

            if(isGameOver() != null) {
                return;
            }
        }
    }

    private static User isGameOver(){
        if (guest.getHP() <= 0)
            return guest;
        else if (host.getHP() <= 0)
            return host;
        return null;
    }

    private static void result(User user){
        if (user.equals(host)){

        }
        if (user.equals(guest)){

        }
    }

    private static void printPlayGround(){
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(guest.getHP() + "                    " + host.getHP());
        System.out.println("..........................................................................");
        for (int i = 0; i < durationLineSize; i++){
            Cell hostCell = hostDurationLine.get(i);
            Cell guestCell = guestDurationLine.get(i);
            if (hostCell.isHollow)
                System.out.println("Hollow");

        }
    }

    private static void initArrays(ArrayList <Cell> list ,Integer capacity){
        for (int i = 0; i < capacity; i++){
            list.add(new Cell());
        }
    }
}
