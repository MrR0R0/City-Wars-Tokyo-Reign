package menu.play;

import app.Card;
import app.User;
import database.Connect;
import javafx.util.Pair;
import menu.Menu;
import menu.authentication.Login;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;

public class Play extends Menu {
    static final Integer durationLineSize = 21;
    static final Integer handSize = 5;

    static private Player host;
    static private Player guest;
    static private Player turnPlayer;
    static private Player opponent;
    static private Integer gameRound = 8;

    static private Integer pot = 0;

    static private final Random random = new Random();

    static private boolean isInBettingMode = false;
    static private boolean isInNormalMode = false;
    final static private String playModeCommand = "^select (?<Mode>\\w+) as the play mode$";
    final static private String selectCharacter = "^(?<Character>\\w+)$";
    final static String selectCardCommand = "^select card number (?<number>\\d+) player (?<player>\\S+)$";
    final static String placeCardCommand = "place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)";
    final static String showPlaygroundCommand = "show playground";


    static public void handleInput(String input, Scanner scanner) throws IOException {
        host = new Player(Menu.loggedInUser, durationLineSize, handSize);

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
                guest = new Player(User.signedUpUsers.get(User.getIdByUsername(username)), durationLineSize, handSize);
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

        if (guest.getCharacter() != null && host.getCharacter() != null) {
            if (isInNormalMode)
                playing(scanner);
            if (isInBettingMode){

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
            if (guest.getCharacter() == null) {
                guest.setCharacter(selectedCharacter);
                System.out.println("Guest selected character: " + selectedCharacter);
            } else {
                host.setCharacter(selectedCharacter);
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
            if(input.toLowerCase().matches(showPlaygroundCommand)){
                printPlayGround();
            }
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
        host.initNewRound();
        guest.initNewRound();
    }

    private static void selectCard(String input) {
        Matcher matcher = getCommandMatcher(input, selectCardCommand);
        if (matcher.find()) {
            int selectedNumber = Integer.parseInt(matcher.group("number"));
            if (matcher.group("player").equals("host")) {
                host.getHand().get(selectedNumber).showInGameProperties(20);
            }
            if (matcher.group("player").equals("guest")) {
                guest.getHand().get(selectedNumber).showInGameProperties(20);
            }
        }
    }

    private static void placeCard(String input) {
        Matcher matcher = getCommandMatcher(input, placeCardCommand);
        if (matcher.find()) {
            int selectedCardIndex = Integer.parseInt(matcher.group("cardNum")) - 1;
            int selectedCellIndex = Integer.parseInt(matcher.group("cellNum")) - 1;
            Card selectedCard = turnPlayer.getHand().get(selectedCardIndex);

            //hole repairer
            if(selectedCard.getId().equals(3)){
                if(turnPlayer.getDurationLine().get(selectedCellIndex).isHollow()){
                    turnPlayer.getDurationLine().get(selectedCellIndex).makeSolid();
                }
                else{
                    System.out.println("The cell is not hollow!");
                }
                return;
            }

            //Checking if the card can be placed
            //Cells should be empty and solid
            for (int i = selectedCellIndex; i < selectedCard.getDuration(); i++) {
                if (turnPlayer.getDurationLine().get(i).isHollow()) {
                    System.out.println("you can't place this card because the cell is hollow");
                    return;
                }
                if (!turnPlayer.getDurationLine().get(i).isEmpty()) {
                    System.out.println("you can't place this card because the cell is not empty");
                    return;
                }
            }

            //Check for matching middle card boost
            int middleIndex = (durationLineSize-1)/2;
            Card middleCard = turnPlayer.getDurationLine().get(middleIndex).getCard();
            if(random.nextInt(4) == 1 && selectedCard.getType().equals(middleCard.getType())){
                selectedCard.boostAttackDefense(1.2);
            }

            //Check for card destruction
            for(int i=selectedCellIndex; i < selectedCard.getDuration(); i++){
                turnPlayer.getDurationLine().get(i).setCardPair(new Pair<>(selectedCard.getId(), selectedCard.clone()));
                if(!opponent.getDurationLine().get(i).isEmpty()){
                    Cell myCell = turnPlayer.getDurationLine().get(i);
                    Cell oppCell = opponent.getDurationLine().get(i);
                    if(myCell.getCard().getAcc() < oppCell.getCard().getAcc()){
                        myCell.shatter();
                        System.out.println("your card is shattered");
                    }
                    else if(myCell.getCard().getAcc() > oppCell.getCard().getAcc()){
                        oppCell.shatter();
                        System.out.println("opponent's card is shattered");
                    }
                    else{
                        myCell.shatter();
                        oppCell.shatter();
                        System.out.println("both cards are shattered");
                    }
                }
            }

            /*
            check for complete shatters
            // reward for destruction of card
                            if (random.nextInt(4) == 0) guest.setXP(guest.getXP() + 10);
                            else if (random.nextInt(4) == 1) guest.setWallet(guest.getWallet() + 20);

                            System.out.println("host's card is destroyed");
             */

            turnPlayer.replaceCardInHand(selectedCardIndex);
            changeTurn();
        }
    }

    private static void movingTimeLine() {
        for (int i = 0; i < durationLineSize; i++) {
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println("Block " + (i + 1) + " :");
            Cell hostCell = host.getDurationLine().get(i);
            Cell guestCell = guest.getDurationLine().get(i);
            if(guestCell.isEmpty()){
                System.out.println("Guest: Empty cell");
            }
            else if(guestCell.isHollow()){
                System.out.println("Guest: Hollow cell");
            }
            else if(guestCell.isShattered()){
                System.out.println("Guest: Shattered cell");
            }
            else if(guestCell.getCard() != null){
                guest.increaseTotalAttack(guestCell.getCard().getGamingAttackOrDefense());
                host.decreaseHP(guestCell.getCard().getGamingAttackOrDefense());
                guest.getDurationLine().get(i).getCard().showInGameProperties(10);
                System.out.println("host HP: " + host.getHP());
                System.out.println("host total damage: " + guest.getTotalAttack());
            }

            if(hostCell.isEmpty()){
                System.out.println("Host: Empty cell");
            }
            else if(hostCell.isHollow()){
                System.out.println("Host: Hollow cell");
            }
            else if(hostCell.isShattered()){
                System.out.println("Host: Shattered cell");
            }
            else if(hostCell.getCard() != null){
                host.increaseTotalAttack(hostCell.getCard().getGamingAttackOrDefense());
                guest.decreaseHP(hostCell.getCard().getGamingAttackOrDefense());
                host.getDurationLine().get(i).getCard().showInGameProperties(10);
                System.out.println("guest HP: " + guest.getHP());
                System.out.println("guest total damage: " + host.getTotalAttack());
            }

            System.out.println("---------------------------------------------------------------------------------------");

            if (isGameOver() != null) {
                return;
            }
        }
    }

    //Returns the winner
    private static Player isGameOver() {
        if (guest.getHP() <= 0)
            return host;
        else if (host.getHP() <= 0)
            return guest;
        return null;
    }

    private static void result(Player winner) {
        Player loser = opponent;
        String result = "", winnerCons, loserCons, hostCons, guestCons;
        winnerCons = "XP: +" + 0.1 * winner.getHP() + " Coin: +" + 0.2 * winner.getHP();
        loserCons = "XP: +" + 0.01 * loser.getHP();
        winner.increaseXP((int) (0.1 * winner.getHP()));
        winner.increaseMoney((int) (0.2 * winner.getHP()));

        System.out.println("Winner: " + winner.getNickname());
        winner.showResultPrompt();
        System.out.println();
        System.out.println("Loser: " + loser.getNickname());
        loser.showResultPrompt();
        winner.checkForLevelUpgrade();
        loser.checkForLevelUpgrade();

        if(winner == host) {
            hostCons = winnerCons;
            guestCons = loserCons;
        }
        else{
            guestCons = winnerCons;
            hostCons = loserCons;
        }

        Connect.insertHistory(guest.getUsername(), guest.getLevel(), guestCons,
                            host.getUsername(), guest.getLevel(), hostCons,
                            result,
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            host.getId(), guest.getId());

        host.applyResults(Menu.loggedInUser);
        guest.applyResults(User.signedUpUsers.get(guest.getId()));

    }

    private static void printPlayGround() {
        System.out.println("_".repeat(4 * 21));
        System.out.println("Guest HP : " + guest.getHP() + " | Host HP : " + host.getHP());
        System.out.println(".".repeat(4 * 21));
        host.showDurationLine(3);
        System.out.println(".".repeat(4 * 21));
        guest.showDurationLine(3);
        System.out.println(".".repeat(4 * 21));
    }

    private static void changeTurn(){
        Player tmp = turnPlayer;
        turnPlayer = opponent;
        opponent = tmp;
    }
}