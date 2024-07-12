package com.controllers.play;

import com.app.Card;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CardPane extends GridPane {
    public Card card;
    public ImageView cardView = new ImageView();
    public ImageView shatterView = new ImageView();
    public Label name = new Label();
    public Label duration = new Label();
    public Label type = new Label();
    public Label price = new Label();
    public Label level = new Label();
    public Label attack = new Label();
    public Label ACC = new Label();

    public Image shatteredImage = new Image(getClass().getResource("/com/images/black-wall-crack-ai-generative-free-png.png").toExternalForm());
    public StackPane imageContainer = new StackPane();
    public StackPane cardPropertiesContainer = new StackPane();

    public CardPane(){}

    public void setCardImage(double fitHeight, double fitWidth, double v1, double v2, double v3, double v4) {
        cardView.setVisible(true);
        cardView.setFitHeight(fitHeight);
        cardView.setFitWidth(fitWidth);
        imageContainer.setMaxWidth(cardView.getFitHeight());
        imageContainer.setMaxHeight(cardView.getFitWidth());
        this.setAlignment(Pos.CENTER);
        cardView.setPreserveRatio(false);
        GridPane.setHalignment(cardView, HPos.CENTER);
        GridPane.setValignment(cardView, VPos.CENTER);
        GridPane.setHalignment(imageContainer, HPos.CENTER);
        GridPane.setValignment(imageContainer, VPos.CENTER);
        GridPane.setMargin(cardView, new Insets(v1, v2, v3, v4));
        GridPane.setMargin(imageContainer, new Insets(v1, v2, v3, v4));
        imageContainer.getChildren().add(cardView);

        this.add(imageContainer, 0, 0);
    }

    public void setCardName(double v1, double v2, double v3, double v4, double fontSize) {
        name.setVisible(true);
        name.setText(card.getName());
        name.setStyle("-fx-font-size: " + fontSize);
        name.getStyleClass().add("text");
        name.setAlignment(Pos.CENTER);
        GridPane.setValignment(name, VPos.CENTER);
        GridPane.setHalignment(name, HPos.CENTER);
        GridPane.setMargin(name, new Insets(v1, v2, v3, v4));
        this.add(name, 0, 0);
    }

    public void setCardPrice(double v1, double v2, double v3, double v4, double fontSize, Integer price) {
        this.price.setVisible(true);
        this.price.setText(String.valueOf(price));
        this.price.setStyle("-fx-font-size: " + fontSize);
        this.price.setStyle("-fx-text-fill: gold;" + "-fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
        this.price.setAlignment(Pos.CENTER);
        GridPane.setValignment(this.price, VPos.CENTER);
        GridPane.setHalignment(this.price, HPos.CENTER);
        GridPane.setMargin(this.price, new Insets(v1, v2, v3, v4));
        this.add(this.price, 0, 0);
    }

    public void setAttack(double v1, double v2, double v3, double v4, double fontSize, Integer price) {
        this.attack.setVisible(true);
        this.attack.setText(String.valueOf(price));
        this.attack.setStyle("-fx-font-size: " + fontSize);
        this.attack.setStyle("-fx-text-fill: #0066ff;" + "-fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
        this.attack.setAlignment(Pos.CENTER);
        GridPane.setValignment(this.attack, VPos.CENTER);
        GridPane.setHalignment(this.attack, HPos.CENTER);
        GridPane.setMargin(this.attack, new Insets(v1, v2, v3, v4));
        this.add(this.attack, 0, 0);
    }

    public void setACC(double v1, double v2, double v3, double v4, double fontSize, Integer price) {
        this.ACC.setVisible(true);
        this.ACC.setText(String.valueOf(price));
        this.ACC.setStyle("-fx-font-size: " + fontSize);
        this.ACC.setStyle("-fx-text-fill: #ff6a00;" + "-fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
        this.ACC.setAlignment(Pos.CENTER);
        GridPane.setValignment(this.ACC, VPos.CENTER);
        GridPane.setHalignment(this.ACC, HPos.CENTER);
        GridPane.setMargin(this.ACC, new Insets(v1, v2, v3, v4));
        this.add(this.ACC, 0, 0);
    }

    public void setCardLevel(double v1, double v2, double v3, double v4, double fontSize) {
        level.setVisible(true);
        level.setText(String.valueOf(card.getLevel()));
        level.setStyle("-fx-text-fill: #00ff00;" + "-fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
        GridPane.setMargin(level, new Insets(v1, v2, v3, v4));
        this.add(level, 0, 0);
    }

    public void setTimeStrikeType() {
        imageContainer.setStyle("-fx-background-color: #f500e0; -fx-border-color: #f500e0; -fx-border-width: 3; -fx-border-style: solid;");
    }

    public void setGlow(){
        imageContainer.setStyle("-fx-background-color: #00ffff; -fx-border-color: #00ffff; -fx-border-width: 3; -fx-border-style: solid;");
    }
    public void setNormal() {
        imageContainer.setStyle("-fx-background-color: #3c00ff; -fx-border-color: #3c00fd; -fx-border-width: 3; -fx-border-style: solid;");
    }
    public void setShield() {
        imageContainer.setStyle("-fx-background-color: #00bb3c; -fx-border-color: #00b903; -fx-border-width: 3; -fx-border-style: solid;");
    }
    public void setBooster() {
        imageContainer.setStyle(imageContainer.getStyle() + "-fx-border-color: gold; -fx-border-width: 2 0 2 0; -fx-background-insets: -1;");
    }
    public void setMitigated() {
        imageContainer.setStyle(imageContainer.getStyle() +"-fx-border-color: #ff0081; -fx-border-width: 2 0 2 0; -fx-background-insets: -1;");
    }
    public void setSpellType() {
        imageContainer.setStyle("-fx-border-color: linear-gradient(from 0.0% 50.0% to 100.0% 50.0%, reflect, #0051ff 0.0%, #ff0000 100.0%);-fx-border-width: 3; -fx-border-style: solid;-fx-background-color: linear-gradient(from 0.0% 50.0% to 100.0% 50.0%, reflect, #0051ff 0.0%, #ff0000 100.0%);");
    }
    public void setShatter() {
        shatterView.setImage(shatteredImage);
        shatterView.setVisible(true);
        shatterView.setFitHeight(cardView.getFitHeight());
        shatterView.setFitWidth(cardView.getFitWidth());
        imageContainer.setPrefHeight(shatterView.getFitHeight());
        imageContainer.setPrefWidth(shatterView.getFitWidth());
        this.setAlignment(Pos.CENTER);
        shatterView.setPreserveRatio(false);
        GridPane.setHalignment(shatterView, HPos.CENTER);
        GridPane.setValignment(shatterView, VPos.CENTER);
        GridPane.setHalignment(imageContainer, HPos.CENTER);
        GridPane.setValignment(imageContainer, VPos.CENTER);
        cardView.setOpacity(0.6);
        imageContainer.getChildren().add(shatterView);
    }

    public void setHollow(){
        setStyle("-fx-background-color: black;");
    }

    public void showProperties(Node node,double spacing) {

        VBox vBox = new VBox();
        vBox.setSpacing(spacing);

        name.setText(card.getName());
        type.setText("T: " + card.getType().name());
        duration.setText("D: " + card.getDuration().toString());
        ACC.setText("ACC: " + card.getAcc().toString());
        attack.setText("A/D: " + card.getAttackOrDefense().toString());
        this.name.setStyle("-fx-text-fill: #a8ffff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.type.setStyle("-fx-text-fill: #8800e7;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.duration.setStyle("-fx-text-fill: #ee0000;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.ACC.setStyle("-fx-text-fill: #ff6a00;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.attack.setStyle("-fx-text-fill: #0066ff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(name, type, duration,ACC,attack);

        // Align VBox to the top left of the StackPane
        StackPane.setAlignment(vBox, Pos.TOP_CENTER);

        // Add VBox to StackPane
        cardPropertiesContainer.getChildren().add(vBox);
        cardPropertiesContainer.setStyle("-fx-background-color: black");

        GridPane.setMargin(cardPropertiesContainer, new Insets(0, 0, 50, 0));
        // Add StackPane to the node
        ((Pane) node).getChildren().add(cardPropertiesContainer);

        //handler
        cardPropertiesContainer.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                ((GridPane) node).getChildren().remove(cardPropertiesContainer);
                setCardName(40,0,0,0,11);
            }
        });
    }

    public void showUpgradeInfo(Node node,double spacing){
        VBox vBox = new VBox();
        vBox.setSpacing(spacing);

        name.setText(card.getName());
        ACC.setText(card.getUpgradeFirstDetail());
        attack.setText(card.getUpgradeSecondDetail());
        this.name.setStyle("-fx-text-fill: #a8ffff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.ACC.setStyle("-fx-text-fill: #ff6a00;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.attack.setStyle("-fx-text-fill: #0066ff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(name,ACC,attack);

        // Align VBox to the top left of the StackPane
        StackPane.setAlignment(vBox, Pos.TOP_CENTER);

        // Add VBox to StackPane
        cardPropertiesContainer.getChildren().add(vBox);
        cardPropertiesContainer.setStyle("-fx-background-color: black");

        GridPane.setMargin(cardPropertiesContainer, new Insets(0, -10, 50, -10));
        // Add StackPane to the node
        ((Pane) node).getChildren().add(cardPropertiesContainer);

        //handler
        cardPropertiesContainer.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                ((GridPane) node).getChildren().remove(cardPropertiesContainer);
                setCardName(40,0,0,0,11);

            }
        });
    }
    public void showPurchaseInfo(Node node,double spacing){
        VBox vBox = new VBox();
        vBox.setSpacing(spacing);

        name.setText(card.getName());
        ACC.setText(card.getPurchaseFirstDetail());
        attack.setText(card.getPurchaseSecondDetail());
        this.name.setStyle("-fx-text-fill: #a8ffff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.ACC.setStyle("-fx-text-fill: #ff6a00;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");
        this.attack.setStyle("-fx-text-fill: #0066ff;" + "-fx-font-size: " + 10 + "; -fx-font-weight: bold;");

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(name,ACC,attack);

        // Align VBox to the top left of the StackPane
        StackPane.setAlignment(vBox, Pos.TOP_CENTER);

        // Add VBox to StackPane
        cardPropertiesContainer.getChildren().add(vBox);
        cardPropertiesContainer.setStyle("-fx-background-color: black");

        GridPane.setMargin(cardPropertiesContainer, new Insets(0, -10, 50, -10));
        // Add StackPane to the node
        ((Pane) node).getChildren().add(cardPropertiesContainer);

        //handler
        cardPropertiesContainer.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                ((GridPane) node).getChildren().remove(cardPropertiesContainer);
                setCardName(40,0,0,0,11);

            }
        });
    }

}