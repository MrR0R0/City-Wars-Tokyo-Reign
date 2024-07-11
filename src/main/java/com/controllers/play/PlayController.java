package com.controllers.play;

import com.app.Card;
import com.app.User;
import com.menu.play.Player;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static com.controllers.play.GuestLoginController.guestPlayer;
import static com.controllers.play.GuestLoginController.hostPlayer;

public class PlayController implements Initializable {
    public GridPane hostDurationLine_pane;
    public GridPane guestDurationLine_pane;
    public Label guestName_label;
    public Label hostName_label;
    public GridPane hostHand_pane;
    public GridPane guestHand_pane;

    static private Player turnPlayer, opponent;
    static private int roundCounter;
    static public int pot;
    static private final Random random = new Random();
    static final private int gameRounds = 4, handColumnWidth = 100;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostPlayer = new Player(User.signedUpUsers.get(2));
        hostPlayer.setCharacter(Card.Characters.Character1);
        guestPlayer = new Player(User.signedUpUsers.get(1));
        guestPlayer.setCharacter(Card.Characters.Character2);
        initEachRound();
        /*
        hostDurationLine_pane.setOnDragOver(this::handleDragOver);
        hostDurationLine_pane.setOnDragDropped(this::handleDragDropped);
        guestDurationLine_pane.setOnDragOver(this::handleDragOver);
        guestDurationLine_pane.setOnDragDropped(this::handleDragDropped);
         */
    }


    @SuppressWarnings("DuplicatedCode")
    public void setupAndDisplayHand(Player player){
        GridPane handPane = hostHand_pane;
        int row = 0;
        if(player == guestPlayer){
            handPane = guestHand_pane;
            row = 1;
        }
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
            cardPane.imageContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    cardPane.showPurchaseInfo(cardPane, 5);
                }
            });
            index++;
        }

    }

    @SuppressWarnings("DuplicatedCode")
    public void initEachRound(){
        hostPlayer.initNewRound();
        guestPlayer.initNewRound();
        hostDurationLine_pane.getChildren().clear();
        guestDurationLine_pane.getChildren().clear();
        hostHand_pane.getChildren().clear();
        guestHand_pane.getChildren().clear();
        setupAndDisplayHand(hostPlayer);
        setupAndDisplayHand(guestPlayer);

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
}
