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
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Play extends Menu {
    static final Integer durationLineSize = 21;
    static final Integer handSize = 5;

    static private final Player host = new Player(Menu.loggedInUser, durationLineSize, handSize);
    static private Player guest;
    static private Player turnPlayer;
    static private Player opponent;
    static private final Integer gameRounds = 4;
    static private int roundCounter;
    private static Player blindfoldedPlayer;

    static private Integer pot = 0;

    static private final Random random = new Random();

    public enum Mode {Normal, Betting}

    static private Mode playMode = null;
    final static private String playModeCommand = "^select (?<Mode>\\w+) as the play mode$";
    final static private String selectCharacter = "^(?<Character>\\w+)$";
    final static String selectCardCommand = "^select card number (?<number>\\d+) player (?<player>\\S+)$";
    final static String placeCardCommand = "place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)";
    final static String showPlaygroundCommand = "show playground";
    final static String showHandCommand = "show hand";


    static public void handleInput(String input, Scanner scanner) throws IOException {
        // should we have an exit command?
        if (input.matches(playModeCommand)) {
            Matcher matcher = getCommandMatcher(input, playModeCommand);
            matcher.find();
            choosePlayMode(matcher);
        }
        else if (playMode == null) {
            System.out.println("you should choose the play mode first");
        }
        else if (input.matches(Login.loginCommand)) {
            Matcher matcher = getCommandMatcher(input, Login.loginCommand);
            matcher.find();
            if (Login.checkLogIn(matcher, scanner)) {
                String username = matcher.group("Username");
                guest = new Player(User.signedUpUsers.get(User.getIdByUsername(username)), durationLineSize, handSize);
                if (guest.getId().equals(host.getId())) {
                    guest = null;
                    System.out.println("Invalid action: You cannot battle yourself.");
                    return;
                }
                System.out.println("user logged in successfully");
                System.out.println("Welcome " + username + "!");
            }
        }
        else if (input.matches(Login.forgotPassword)) {
            Matcher matcher = getCommandMatcher(input, Login.forgotPassword);
            if (matcher.find())
                Login.resetPassword(matcher, scanner);
        }
        else if (guest == null) {
            System.out.println("Second player should log in first. Please log in by typing 'user login -u <Username> -p <Password>'.");
        }
        else if (input.matches(selectCharacter)) {
            Matcher matcher = getCommandMatcher(input, selectCharacter);
            if (matcher.find()) {
                selectCharacter(matcher);
            }
        }
        else if (guest.getCharacter() != null && host.getCharacter() != null) {
            switch (playMode){
                case Normal -> playing(scanner);
                case Betting -> {
                    betting(scanner);
                    if(pot!=0)
                        playing(scanner);
                }
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
            guest.reduceWallet(guestBet);
            host.reduceWallet(hostBet);
            pot = guestBet + hostBet;
            System.out.println("The total pot is: " + pot);
        } else {
            System.out.println("Both players must have enough money to place the bet.");
            pot = 0;
        }
    }

    static public void choosePlayMode(Matcher matcher) {
        if (matcher.group("Mode").equals("Normal")) {
            playMode = Mode.Normal;
            System.out.println("Normal mode is selected");
            System.out.println("Second player should now log in!");
        } else if (matcher.group("Mode").equals("Betting")) {
            playMode = Mode.Betting;
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
        int randomPlayer = random.nextInt(2);
        turnPlayer = randomPlayer == 0 ? host : guest;
        opponent = randomPlayer == 0 ? guest : host;
        if (randomPlayer == 0) {
            System.out.println("Host starts");
        } else {
            System.out.println("guest starts");
        }

        //init first round
        initEachRound();
        roundCounter = gameRounds;
        while (roundCounter > 0) {
            String input = scanner.nextLine();
            if (input.matches(selectCardCommand))
                selectCard(input);
            else if (input.toLowerCase().matches(showPlaygroundCommand)) {
                printPlayGround();
            } else if (input.toLowerCase().matches(showHandCommand)) {
                if(blindfoldedPlayer != turnPlayer) {
                    turnPlayer.showHand();
                }
                else{
                    System.out.println("You can't see your hand due to blindfold");
                }
            } else if (input.matches(placeCardCommand)) {
                System.out.println(turnPlayer.getNickname() + "'s turn");
                if (placeCard(input)) {
                    if(blindfoldedPlayer == turnPlayer){
                        blindfoldedPlayer = null;
                    }
                    changeTurn();
                    roundCounter--;
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
            int selectedNumber = Integer.parseInt(matcher.group("number")) - 1;
            if (matcher.group("player").equals("host")) {
                host.getHand().get(selectedNumber).showInGameProperties(20);
            }
            if (matcher.group("player").equals("guest")) {
                guest.getHand().get(selectedNumber).showInGameProperties(20);
            }
        }
    }

    private static boolean placeCard(String input) {
        Matcher matcher = getCommandMatcher(input, placeCardCommand);
        matcher.find();
        int selectedCardIndex = Integer.parseInt(matcher.group("cardNum")) - 1;
        int selectedCellIndex = Integer.parseInt(matcher.group("cellNum")) - 1;

        if (selectedCardIndex >= turnPlayer.getHand().size() || selectedCardIndex < 0) {
            System.out.println("Index out of hand");
            return false;
        }

        if (selectedCellIndex >= durationLineSize || selectedCellIndex < 0) {
            System.out.println("Index out of duration line");
            return false;
        }

        Card selectedCard = turnPlayer.getHand().get(selectedCardIndex);

        // It will be wasted if used on a duration line without hollow cells
        if (selectedCard.isHoleChanger()) {
            turnPlayer.changeHole();
            turnPlayer.replaceCardInHand(selectedCardIndex);
            return false;
        }

        if (selectedCard.isHoleRepairer()) {
            if (turnPlayer.getDurationLine().get(selectedCellIndex).isHollow()) {
                turnPlayer.getDurationLine().get(selectedCellIndex).makeSolid();
                turnPlayer.replaceCardInHand(selectedCardIndex);
            } else {
                System.out.println("The cell is not hollow!");
            }
            return false;
        }

        if (selectedCard.isRoundReducer()) {
            roundCounter = Math.max(roundCounter - 2, 0);
            turnPlayer.replaceCardInHand(selectedCardIndex);
            return false;
        }

        // random card buff
        // It will be wasted if used on a duration line without cards
        if (selectedCard.isPowerBooster()) {
            int initialIndex = turnPlayer.getInitialIndexRandomCard();
            if (initialIndex == -1) {
                System.out.println("No cards on the track! You have wasted power booster card!");
            } else {
                for (int i = initialIndex; i < initialIndex + turnPlayer.getDurationLine().get(initialIndex).getCard().getDuration(); i++) {
                    turnPlayer.getDurationLine().get(i).getCard().boostAttackDefense(selectedCard.getPowerBoostMultiplier());
                }
            }
            turnPlayer.replaceCardInHand(selectedCardIndex);
            return false;
        }

        if (selectedCard.isCopyCard()) {
            if (selectedCellIndex >= turnPlayer.getHand().size()) {
                System.out.println("Invalid command");
                return false;
            }
            turnPlayer.getHand().set(selectedCardIndex, turnPlayer.getHand().get(selectedCellIndex).clone());
            return false;
        }

        if(selectedCard.isCardRemover()){
            int index = random.nextInt(opponent.getHand().size());
            turnPlayer.getHand().set(selectedCardIndex, opponent.getHand().get(index));
            opponent.getHand().remove(index);
            return false;
        }

        // random enemy card "attack or defence" nerf
        // random enemy card "ACC" nerf
        if(selectedCard.isCardMitigator()){
            int initialIndex = opponent.getInitialIndexRandomCard();
            if (initialIndex == -1) {
                System.out.println("No cards on the track! You have wasted mitigator card!");
            } else {
                for (int i = initialIndex; i < initialIndex + opponent.getDurationLine().get(initialIndex).getCard().getDuration(); i++) {
                    opponent.getDurationLine().get(i).getCard().boostAttackDefense(selectedCard.getMitigatorMultiplier());
                }
            }

            initialIndex = opponent.getInitialIndexRandomCard();
            if (initialIndex == -1) {
                System.out.println("No cards on the track! You have wasted mitigator card!");
            } else {
                for (int i = initialIndex; i < initialIndex + opponent.getDurationLine().get(initialIndex).getCard().getDuration(); i++) {
                    opponent.getDurationLine().get(i).getCard().boostACC(selectedCard.getMitigatorMultiplier());
                    applyCardsDynamic(turnPlayer.getDurationLine().get(i), opponent.getDurationLine().get(i));
                }
            }
            turnPlayer.replaceCardInHand(selectedCardIndex);
            return false;
        }

        if(selectedCard.isBlindfold()){
            blindfoldedPlayer = opponent;
            Collections.shuffle(opponent.getHand());
            turnPlayer.replaceCardInHand(selectedCardIndex);
            return false;
        }

        //Checking if the card can be placed
        //Cells should be empty and solid
        for (int i = selectedCellIndex; i < selectedCellIndex + selectedCard.getDuration(); i++) {
            if (i >= durationLineSize) {
                System.out.println("you can't place this card because your card is outside of track's boundaries");
                return false;
            }
            if (turnPlayer.getDurationLine().get(i).isHollow()) {
                System.out.println("you can't place this card because the cell is hollow");
                return false;
            }
            if (!turnPlayer.getDurationLine().get(i).isEmpty()) {
                System.out.println("you can't place this card because the cell is not empty");
                return false;
            }
        }

        //Check for matching middle card boost
        int middleIndex = (durationLineSize - 1) / 2;
        Card middleCard = turnPlayer.getDurationLine().get(middleIndex).getCard();
        if (middleCard != null) {
            if (random.nextInt(4) == 1 && selectedCard.getType().equals(middleCard.getType())) {
                selectedCard.boostAttackDefense(1.2);
                System.out.println("Middle card boost!");
            }
        }

        // Placing the card and checking for shatters
        // Shield effect is applied is here
        for (int i = selectedCellIndex; i < selectedCellIndex + selectedCard.getDuration(); i++) {
            Cell myCell = turnPlayer.getDurationLine().get(i);
            myCell.setCardInitialIndex(selectedCellIndex);
            myCell.setCardPair(new Pair<>(selectedCard.getId(), selectedCard.clone()));
            Cell oppCell = opponent.getDurationLine().get(i);
            applyCardsDynamic(myCell, oppCell);
        }

        // heal/heal shield
        if (selectedCard.isHeal()) {
            turnPlayer.increaseHP(Card.healByLevel * selectedCard.getLevel());
        }

        turnPlayer.replaceCardInHand(selectedCardIndex);
        return true;
    }

    private static void movingTimeLine() {
        for (int i = 0; i < durationLineSize; i++) {
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println("Block " + (i + 1) + " :");
            Cell hostCell = host.getDurationLine().get(i);
            Cell guestCell = guest.getDurationLine().get(i);
            if (guestCell.isHollow()) {
                System.out.println("Guest: Hollow cell");
            } else if (guestCell.isEmpty()) {
                System.out.println("Guest: Empty cell");
            } else if (guestCell.isShattered()) {
                System.out.println("Guest: Shattered cell");
            } else if (guestCell.getCard() != null) {
                guest.increaseRoundAttack(guestCell.getCard().getGamingAttackOrDefense());
                host.decreaseHP(guestCell.getCard().getGamingAttackOrDefense());
                guest.getDurationLine().get(i).getCard().showInGameProperties(10);
                System.out.println("host HP: " + host.getHP());
                System.out.println("host total damage: " + guest.getRoundAttack());
            }

            if (hostCell.isHollow()) {
                System.out.println("Host: Hollow cell");
            } else if (hostCell.isEmpty()) {
                System.out.println("Host: Empty cell");
            } else if (hostCell.isShattered()) {
                System.out.println("Host: Shattered cell");
            } else if (hostCell.getCard() != null) {
                host.increaseRoundAttack(hostCell.getCard().getGamingAttackOrDefense());
                guest.decreaseHP(hostCell.getCard().getGamingAttackOrDefense());
                host.getDurationLine().get(i).getCard().showInGameProperties(10);
                System.out.println("guest HP: " + guest.getHP());
                System.out.println("guest total damage: " + host.getRoundAttack());
            }
            if (isGameOver() != null) {
                host.updateTotalAttack();
                guest.updateTotalAttack();
                return;
            }
        }
        host.updateTotalAttack();
        guest.updateTotalAttack();
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
        Player loser = winner==host ? guest : host;
        String result = winner.getNickname() + " won!";

        System.out.println("Winner: " + winner.getNickname());
        winner.applyPostMatchUpdates(playMode, true, pot);
        System.out.println();
        System.out.println("Loser: " + loser.getNickname());
        loser.applyPostMatchUpdates(playMode, false, pot);
        winner.checkForLevelUpgrade();
        loser.checkForLevelUpgrade();
        pot = 0;
        String hostCons = winner == host ? winner.getConsequence() : loser.getConsequence();
        String guestCons = winner == guest ? winner.getConsequence() : loser.getConsequence();

        Connect.insertHistory(guest.getUsername(), guest.getLevel(), guestCons,
                host.getUsername(), host.getLevel(), hostCons, result,
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

    private static void changeTurn() {
        Player tmp = turnPlayer;
        turnPlayer = opponent;
        opponent = tmp;
    }

    public static void applyCardsDynamic(Cell myCell, Cell oppCell) {
        if(myCell.isEmpty() || oppCell.isEmpty()){
            return;
        }
        boolean myShieldAgainstBreakable = myCell.getCard().isShield() && oppCell.getCard().isBreakable();
        boolean myBreakableAgainstShield = oppCell.getCard().isShield() && myCell.getCard().isBreakable();
        boolean myBreakableLowerAcc = myCell.getCard().getAcc() < oppCell.getCard().getAcc() && myCell.getCard().isBreakable();
        boolean myHigherAccAgainstBreakable = myCell.getCard().getAcc() > oppCell.getCard().getAcc() && oppCell.getCard().isBreakable();
        boolean twoBreakableEqualAcc = myCell.getCard().getAcc().equals(oppCell.getCard().getAcc()) &&
                (myCell.getCard().isBreakable() && oppCell.getCard().isBreakable());

        //when my card shatters
        if (myBreakableAgainstShield || myBreakableLowerAcc) {
            myCell.shatter();
            oppCell.resetShatter();
            if (turnPlayer.checkCompleteShatter(myCell)) {
                opponent.rewardCompleteShatter(myCell);
            }
            System.out.println("your card is shattered");
        }
        //when opp card shatters
        else if (myShieldAgainstBreakable || myHigherAccAgainstBreakable) {
            oppCell.shatter();
            myCell.resetShatter();
            if (opponent.checkCompleteShatter(oppCell)) {
                turnPlayer.rewardCompleteShatter(myCell);
            }
            System.out.println("opponent's card is shattered");
        }
        //when both cards shatter
        else if (twoBreakableEqualAcc) {
            myCell.shatter();
            oppCell.shatter();
            System.out.println("both cards are shattered");
        }
    }
}