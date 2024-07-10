package com.controllers;

import com.Main;
import com.app.Card;
import com.menu.Shop;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;


public class ShopController extends Shop implements Initializable {
    public static class CardPane extends GridPane {
        public Card card;
        public ImageView cardImage = new ImageView();
        public Label cardName = new Label();
        public Label cardPrice = new Label();
        public Label cardLevel = new Label();

        public void setCardImage(double fitHeight, double fitWidth, double v1, double v2, double v3, double v4) {
            cardImage.setVisible(true);
            cardImage.setFitHeight(fitHeight);
            cardImage.setFitWidth(fitWidth);
            this.setAlignment(Pos.CENTER);
            GridPane.setMargin(cardImage, new Insets(v1, v2, v3, v4));
            this.add(cardImage, 0, 0);
        }

        public void setCardName(double v1, double v2, double v3, double v4, double fontSize) {
            cardName.setVisible(true);
            cardName.setText(card.getName());
            cardName.setFont(Font.font(fontSize));
            cardName.getStyleClass().add("text");
            GridPane.setValignment(cardName, VPos.CENTER);
            GridPane.setHalignment(cardName, HPos.CENTER);
            GridPane.setMargin(cardName, new Insets(v1, v2, v3, v4));
            this.add(cardName, 0, 0);
        }

        public void setCardPrice(double v1, double v2, double v3, double v4, double fontSize, Integer price) {
            cardPrice.setVisible(true);
            cardPrice.setText(String.valueOf(price));
            cardPrice.setFont(Font.font(fontSize));
            cardPrice.getStyleClass().add("text");
            GridPane.setValignment(cardPrice, VPos.CENTER);
            GridPane.setHalignment(cardPrice, HPos.CENTER);
            GridPane.setMargin(cardPrice, new Insets(v1, v2, v3, v4));
            this.add(cardPrice, 0, 0);
        }

        public void setCardLevel(double v1, double v2, double v3, double v4, double fontSize) {
            cardLevel.setVisible(true);
            cardLevel.setText(String.valueOf(card.getLevel()));
            cardLevel.setFont(Font.font(fontSize));
            cardLevel.getStyleClass().add("text");
            GridPane.setValignment(cardPrice, VPos.CENTER);
            GridPane.setHalignment(cardPrice, HPos.CENTER);
            GridPane.setMargin(cardPrice, new Insets(v1, v2, v3, v4));
            this.add(cardLevel, 0, 0);
        }
    }

    private final Integer EachColumnWidth = 130;
    @FXML
    public GridPane upgrade_basePane;
    public GridPane upgrade_pane;
    public GridPane buy_basePane;
    public GridPane buy_pane;
    public GridPane shop_basePane;
    public GridPane cards_pane;
    public ImageView allCards_image;
    public ImageView deck_image;
    public Label result_label;
    public Label wallet_label;
    public Button back_but;
    // temp
    static public ArrayList<Image> cardImages = new ArrayList<>();


    // temp
    @Override
    public void initialize(URL location, ResourceBundle resources) {

//         temp
//        Image image = new Image(getClass().getResource("/com/images/card/1.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/2.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/3.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/4.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/5.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/6.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/7.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/8.png").toExternalForm());
//        cardImages.add(image);
//        image = new Image(getClass().getResource("/com/images/card/9.png").toExternalForm());
//        cardImages.add(image);
//         temp

        showUpgradableCards();
        showPurchasableCards();
        updateWalletLabel();
        result_label.setText("");

        allCards_image.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                shop_basePane.setVisible(false);
                cards_pane.setVisible(true);
                showAllCard();
            }
        });
        deck_image.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                shop_basePane.setVisible(false);
                cards_pane.setVisible(true);
                showUserDeck();
            }
        });
        back_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                try {
                    Main.loadMainMenu();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
    }


    private void showUpgradableCards() {
        int index = 0;
        updateUpgradableCards();
        for (Card card : upgradableCards) {
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardImage.setImage(Card.cardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getUpgradeCost());
            cardPane.setCardLevel(0, 40, 50, 0, 12);
            cardPane.setPrefWidth(EachColumnWidth);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            //add button
            Button upgradeButton = new Button();
            upgradeButton.setVisible(true);
            upgradeButton.setText("UPGRADE");
            upgradeButton.setFont(Font.font(11));
            upgradeButton.getStyleClass().add("text");
            upgradeButton.getStyleClass().add("box");
            upgradeButton.setPrefWidth(60);
            upgradeButton.setPrefHeight(20);
            GridPane.setValignment(upgradeButton, VPos.CENTER);
            GridPane.setHalignment(upgradeButton, HPos.CENTER);
            GridPane.setMargin(upgradeButton, new Insets(160, 0, 0, 0));

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(EachColumnWidth);
            upgrade_pane.getColumnConstraints().add(column);
            upgrade_pane.setPrefWidth(upgrade_pane.getPrefWidth() + EachColumnWidth);

            //update pane
            upgrade_pane.add(cardPane, index, 0);
            upgrade_pane.add(upgradeButton, index, 0);

            //add handlers
            upgradeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    upgradeCard(cardPane);
                }
            });

            index++;

        }
        //temp
//        for (int i = 0; i < loggedInUser.getCards().entrySet().size(); i++) {
//
//            Image image = cardImages.get(i);
//
//            ImageView imageView = new ImageView(image);
//            imageView.setFitHeight(120);
//            imageView.setFitWidth(100);
//            GridPane.setValignment(imageView, VPos.CENTER);
//            GridPane.setHalignment(imageView, HPos.CENTER);
//            GridPane.setMargin(imageView, new Insets(0, 0, 50, 0));
//
//            Label name = new Label();
//            name.setVisible(true);
//            name.setText("NAME");
//            name.setFont(Font.font(11));
//            name.getStyleClass().add("text");
//            GridPane.setValignment(name, VPos.CENTER);
//            GridPane.setHalignment(name, HPos.CENTER);
//            GridPane.setMargin(name, new Insets(40, 0, 0, 0));
//
//            Label cost = new Label();
//            cost.setVisible(true);
//            cost.setText("COST");
//            cost.setFont(Font.font(11));
//            cost.getStyleClass().add("text");
//            GridPane.setValignment(cost, VPos.CENTER);
//            GridPane.setHalignment(cost, HPos.CENTER);
//            GridPane.setMargin(cost, new Insets(95, 0, 0, 0));
//
//            Button button = new Button();
//            button.setVisible(true);
//            button.setText("UPGRADE");
//            button.setFont(Font.font(11));
//            button.getStyleClass().add("text");
//            button.getStyleClass().add("box");
//            button.setPrefWidth(60);
//            button.setPrefHeight(20);
//            GridPane.setValignment(button, VPos.CENTER);
//            GridPane.setHalignment(button, HPos.CENTER);
//            GridPane.setMargin(button, new Insets(160, 0, 0, 0));
//
//
//            int columnIndex = i;
//
//            ColumnConstraints column = new ColumnConstraints();
//            column.setPrefWidth(130);
//            upgrade_pane.getColumnConstraints().add(column);
//            upgrade_pane.setPrefWidth(upgrade_pane.getPrefWidth() + 130);
//
//
//            upgrade_pane.add(imageView, columnIndex, 0);
//            upgrade_pane.add(name, columnIndex, 0);
//            upgrade_pane.add(button, columnIndex, 0);
//            upgrade_pane.add(cost, columnIndex, 0);
//        }
        //temp
    }

    private void showPurchasableCards() {
        int index = 0;
        updatePurchasableCards();
        for (Card card : purchasableCards) {
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardImage.setImage(Card.cardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getPrice());
            cardPane.setCardLevel(0, 40, 50, 0, 12);
            cardPane.setPrefWidth(EachColumnWidth);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            //add button
            Button buyButton = new Button();
            buyButton.setVisible(true);
            buyButton.setText("BUY");
            buyButton.setFont(Font.font(11));
            buyButton.getStyleClass().add("text");
            buyButton.getStyleClass().add("box");
            buyButton.setPrefWidth(60);
            buyButton.setPrefHeight(20);
            GridPane.setValignment(buyButton, VPos.CENTER);
            GridPane.setHalignment(buyButton, HPos.CENTER);
            GridPane.setMargin(buyButton, new Insets(160, 0, 0, 0));

            //update pane
            upgrade_pane.add(cardPane, index, 0);
            upgrade_pane.add(buyButton, index, 0);

            //add handlers
            buyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    buyCard(cardPane);
                }
            });
            index++;
        }
    }


    private void upgradeCard(CardPane cardPane) {
        Card selectedCard = cardPane.card;
        if (selectedCard.getUpgradeCost() > loggedInUser.getWallet()) {
            // show error...
            result_label.setStyle("-fx-text-fill: red");
            result_label.setText("You don't have enough money!");
            result_label.setVisible(true);
            return;
        }

        loggedInUser.reduceWallet(selectedCard.getUpgradeCost());
        selectedCard.upgrade();
        loggedInUser.updateCardSeriesByCards();
        updateWalletLabel();

        result_label.setStyle("-fx-text-fill: #00ff00");
        result_label.setText("\"Upgraded Card \" " + selectedCard.getName() + " \" to level " + selectedCard.getLevel());

//  changes in scene...
        showUpgradableCards();
//        cardPane.setVisible(false);
//        upgrade_basePane.getChildren().remove(cardPane);
//        upgrade_basePane.getColumnConstraints().remove(column);
//        upgrade_basePane.setPrefWidth(upgrade_pane.getPrefWidth() - EachColumnWidth);
    }

    private void buyCard(CardPane cardPane) {
        updatePurchasableCards();

        Card selectedCard = cardPane.card;
        if (selectedCard.getPrice() > loggedInUser.getWallet()) {
            result_label.setStyle("-fx-text-fill: red");
            result_label.setText("You don't have enough money!");
            result_label.setVisible(true);
            return;
        }
        loggedInUser.reduceWallet(selectedCard.getPrice());
        loggedInUser.getCards().put(selectedCard.getId(), selectedCard);
        loggedInUser.updateCardSeriesByCards();
        updateWalletLabel();

        result_label.setStyle("-fx-text-fill: #00ff00");
        result_label.setText("Purchased Card \"" + selectedCard.getName() + "\"");

//        changes in scene...
        showPurchasableCards();
    }

    private void showAllCard() {
        cards_pane.getChildren().clear();
        int index = 0;
        for (Map.Entry<Integer, Card> entry : Card.allCards.entrySet()) {
            Card card = entry.getValue();
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardImage.setImage(cardImages.get(index));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getPrice());
            cardPane.setPrefWidth(EachColumnWidth);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            int rowIndex = index / 4;
            int columnIndex = index % 4;
            if (cards_pane.getColumnConstraints().size() < 4) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPrefWidth(EachColumnWidth);
                cards_pane.getColumnConstraints().add(column);
            }
            cards_pane.add(cardPane, rowIndex, columnIndex);

            index++;
        }
    }

    private void showUserDeck() {
        cards_pane.getChildren().clear();
        int index = 0;
        for (Map.Entry<Integer, Card> entry : loggedInUser.getCards().entrySet()) {
            Card card = entry.getValue();
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardImage.setImage(cardImages.get(index));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardLevel(0, 40, 50, 0, 12);

            cardPane.setPrefWidth(130);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            int rowIndex = index / 4;
            int columnIndex = index % 4;

            if (cards_pane.getColumnConstraints().size() < 4) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPrefWidth(EachColumnWidth);
                cards_pane.getColumnConstraints().add(column);
            }
            cards_pane.add(cardPane, rowIndex, columnIndex);

            index++;
        }
    }

    private void updatePurchasableCards() {
        purchasableCards = new ArrayList<>();
        //repeatedIds ??
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
    }

    private void updateUpgradableCards() {
        upgradableCards = new ArrayList<>();
        for (Map.Entry<Integer, Card> entry : loggedInUser.getCards().entrySet()) {
            Card card = entry.getValue();
            if (loggedInUser.getLevel() > card.getLevel() && card.isUpgradable()) {
                upgradableCards.add(entry.getValue());
            }
        }
    }

    private void updateWalletLabel(){
        wallet_label.setText(loggedInUser.getWallet().toString());
    }
}


