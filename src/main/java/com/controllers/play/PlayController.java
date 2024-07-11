package com.controllers.play;

import com.app.Card;
import com.app.User;
import com.menu.play.Cell;
import com.menu.play.Play;
import com.menu.play.Player;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static com.controllers.play.GuestLoginController.guestPlayer;
import static com.controllers.play.GuestLoginController.hostPlayer;

@SuppressWarnings("DuplicatedCode")
public class PlayController implements Initializable {
    public GridPane hostDurationLine_pane;
    public GridPane guestDurationLine_pane;
    public Label guestName_label;
    public Label hostName_label;
    public GridPane hostHand_pane;
    public GridPane guestHand_pane;

    static private Player turnPlayer, opponent, selectedCellOwner, selectedCardOwner;
    static private int roundCounter, selectedCardIndex, selectedCellIndex;
    static public int pot;
    static private final Random random = new Random();
    static final private int gameRounds = 4, handColumnWidth = 100;
    private Pane draggedCard;
    private boolean isCopyCardSelected = false;
    private int copyCardIndex;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostPlayer = new Player(User.signedUpUsers.get(2));
        hostPlayer.setCharacter(Card.Characters.Character1);
        guestPlayer = new Player(User.signedUpUsers.get(1));
        guestPlayer.setCharacter(Card.Characters.Character2);
        initEachRound();
        hostPlayer.getHand().set(0, Card.allCards.get(9));
        displayHand(hostPlayer);
        updateDurationLine(hostPlayer);
        updateDurationLine(guestPlayer);
    }


    private void placeCard(){
        if(selectedCellOwner == selectedCardOwner && selectedCardOwner == turnPlayer){
            if(checkPlacement()){
                changeTurn();
            }
            displayHand(guestPlayer);
            displayHand(hostPlayer);
            updateDurationLine(hostPlayer);
            updateDurationLine(guestPlayer);
            selectedCellOwner = null;
            selectedCardOwner = null;
            selectedCardIndex = -1;
            selectedCellIndex = -1;
        }
    }
    private boolean checkPlacement(){
        Card selectedCard = turnPlayer.getHand().get(selectedCardIndex);
        if(selectedCard.getDuration() + selectedCellIndex > Play.durationLineSize){
            System.out.println("Out of bound!");
            return false;
        }
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

        for (int i = selectedCellIndex; i < selectedCellIndex + selectedCard.getDuration(); i++) {
            if (i >= Play.durationLineSize) {
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

        int middleIndex = (Play.durationLineSize - 1) / 2;
        Card middleCard = turnPlayer.getDurationLine().get(middleIndex).getCard();
        if (middleCard != null) {
            if (random.nextInt(4) == 1 && selectedCard.getType().equals(middleCard.getType())) {
                selectedCard.boostAttackDefense(1.2);
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
    private void displayHand(Player player){
        GridPane handPane = hostHand_pane;
        int row = 0;
        if(player == guestPlayer){
            handPane = guestHand_pane;
            row = 1;
        }
        handPane.getChildren().clear();
        int index = 0;
        for (Card card : player.getHand()) {
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardView.setImage(Card.allCardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getPrice());
            cardPane.setCardLevel(0, 0, 150, 75, 16);
            cardPane.setPrefWidth(handColumnWidth);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            //update pane
            handPane.add(cardPane, index, row);
            //handler
            int finalIndex = index;
            cardPane.imageContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    cardPane.showPurchaseInfo(cardPane, 5);
                }
                else{
                    selectedCardOwner = player;
                    selectedCardIndex = finalIndex;
                    if(selectedCardOwner == turnPlayer){
                        if(card.isCopyCard()){
                            isCopyCardSelected = true;
                            copyCardIndex = selectedCardIndex;
                            System.out.println(copyCardIndex + " " + selectedCardIndex);
                        }
                        else if(isCopyCardSelected){
                            turnPlayer.getHand().set(copyCardIndex, turnPlayer.getHand().get(selectedCardIndex).clone());
                            isCopyCardSelected = false;
                            displayHand(turnPlayer);
                        }
                    }
                }
            });
            index++;
        }

    }
    private void updateDurationLine(Player player){
        GridPane tmp = hostDurationLine_pane;
        if(player == guestPlayer){
            tmp = guestDurationLine_pane;
        }
        tmp.getChildren().clear();
        for(int i=0; i< Play.durationLineSize; i++){
            CardPane cardPane = new CardPane();
            cardPane.setPrefWidth(handColumnWidth);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);
            int finalI = i;
            cardPane.setOnMouseClicked(event -> {
                selectedCellOwner = player;
                selectedCellIndex = finalI;
                placeCard();
            });
            Cell tmpCell = player.getDurationLine().get(i);
            if(tmpCell.isHollow()){
                cardPane.setHollow();
            }
            else if(tmpCell.isEmpty()){
                cardPane.setStyle("-fx-border-color: black;");
            }
            else if(!tmpCell.isEmpty()){
                cardPane.card = tmpCell.getCard();
                cardPane.cardView.setImage(Card.allCardImages.get(tmpCell.getCard().getId()));
                cardPane.setCardImage(80, 50, 0, 0, 0, 0);
            }
            if(player.getDurationLine().get(i).isShattered()){
                cardPane.setShatter();
            }
            tmp.add(cardPane, i, 0);
        }
    }

    private void initEachRound(){
        hostPlayer.initNewRound();
        guestPlayer.initNewRound();
        hostDurationLine_pane.getChildren().clear();
        guestDurationLine_pane.getChildren().clear();
        hostHand_pane.getChildren().clear();
        guestHand_pane.getChildren().clear();
        displayHand(hostPlayer);
        displayHand(guestPlayer);

        int randomPlayer = random.nextInt(2);
        turnPlayer = randomPlayer == 0 ? hostPlayer : guestPlayer;
        opponent = randomPlayer == 0 ? guestPlayer : hostPlayer;
        if (randomPlayer == 0) {
            System.out.println("Host starts");
        } else {
            System.out.println("guest starts");
        }
        roundCounter = gameRounds;

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
