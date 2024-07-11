package com.controllers;

import com.Main;
import com.app.Card;
import com.menu.Shop;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;


import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.controllers.play.CardPane;


public class ShopController extends Shop implements Initializable {

    private final Integer EachColumnWidth = 130;
    @FXML
    public GridPane upgrade_basePane;
    public GridPane upgrade_pane;
    public GridPane buy_basePane;
    public GridPane buy_pane;
    public GridPane shop_basePane;
    public GridPane cards_pane;
    public GridPane base_cards_pane;
    public ImageView allCards_image;
    public ImageView deck_image;
    public Label result_label;
    public Label wallet_label;
    public Button back_but;

    private int page = 1;

    // temp
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateWalletLabel();
        result_label.setText("");
        showFirstPage();
        allCards_image.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                page = 2;
                shop_basePane.setVisible(false);
                base_cards_pane.setVisible(true);
                showAllCard();
            }
        });
        deck_image.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                page = 2;
                shop_basePane.setVisible(false);
                base_cards_pane.setVisible(true);
                showUserDeck();
            }
        });
        back_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (page == 1) {
                    try {
                        Main.loadMainMenu();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                if (page == 2) {
                    page = 1;
                    showFirstPage();
                }
            }
        });
    }


    private void showFirstPage() {
        shop_basePane.setVisible(true);
        base_cards_pane.setVisible(false);
        showPurchasableCards();
        showUpgradableCards();
    }

    private void showUpgradableCards() {
        int index = 0;
        updateUpgradableCards();
        upgrade_pane.getChildren().clear();
        for (Card card : upgradableCards) {
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardView.setImage(Card.allCardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getUpgradeCost());
            cardPane.setCardLevel(0, 0, 150, 75, 16);
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
            cardPane.imageContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    cardPane.showUpgradeInfo(cardPane, 5);
                }
            });

            index++;

        }
    }

    private void showPurchasableCards() {
        int index = 0;
        updatePurchasableCards();
        buy_pane.getChildren().clear();
        for (Card card : purchasableCards) {
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardView.setImage(Card.allCardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getPrice());
            cardPane.setCardLevel(0, 0, 150, 75, 16);
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
            buy_pane.add(cardPane, index, 0);
            buy_pane.add(buyButton, index, 0);


            //handler
            buyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    buyCard(cardPane);
                }
            });
            cardPane.imageContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    cardPane.showPurchaseInfo(cardPane, 5);
                }
            });

            index++;
        }
    }

    private void upgradeCard(CardPane cardPane) {
        Card selectedCard = cardPane.card;
        upgrade_pane.getChildren().clear();
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

//      changes in scene
        showUpgradableCards();
    }

    private void buyCard(CardPane cardPane) {
        updatePurchasableCards();
        upgrade_pane.getChildren().clear();
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

//      changes in scene
        showPurchasableCards();
    }

    private void showAllCard() {
        cards_pane.getChildren().clear();
        int index = 0;
        for (Map.Entry<Integer, Card> entry : Card.allCards.entrySet()) {
            Card card = entry.getValue();
            CardPane cardPane = new CardPane();
            cardPane.card = card;
            cardPane.cardView.setImage(Card.allCardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardPrice(95, 0, 0, 0, 11, card.getPrice());

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
            cards_pane.add(cardPane, columnIndex, rowIndex);


            //handlers
            cardPane.imageContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    cardPane.showProperties(cardPane, 5);
                }
            });

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
            cardPane.cardView.setImage(Card.allCardImages.get(card.getId()));
            cardPane.setCardImage(120, 90, 0, 0, 50, 0);
            cardPane.setCardName(40, 0, 0, 0, 11);
            cardPane.setCardLevel(0, 0, 150, 75, 16);

            cardPane.setPrefWidth(130);
            cardPane.setPrefHeight(100);
            cardPane.setVisible(true);
            GridPane.setHalignment(cardPane, HPos.CENTER);
            GridPane.setValignment(cardPane, VPos.CENTER);

            int rowIndex = index / 4;
            int columnIndex = index % 4;

            if (cards_pane.getColumnConstraints().size() < 4) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPrefWidth(130);
                cards_pane.getColumnConstraints().add(column);
            }
            cards_pane.add(cardPane, columnIndex, rowIndex);

            //handlers
            cardPane.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    cardPane.showProperties(cardPane, 5);
                }
            });

            index++;
        }
    }

    private void updateWalletLabel() {
        wallet_label.setText(loggedInUser.getWallet().toString());
    }
}


