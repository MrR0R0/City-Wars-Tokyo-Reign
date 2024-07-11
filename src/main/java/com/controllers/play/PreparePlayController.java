package com.controllers.play;

import com.Main;
import com.app.Card;
import com.menu.play.Play;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.controllers.play.GuestLoginController.guestPlayer;
import static com.controllers.play.GuestLoginController.hostPlayer;
import static com.menu.Menu.loggedInUser;

public class PreparePlayController implements Initializable {
    public GridPane mode_pane;
    public GridPane character_pane;
    public Button next_but;
    public ComboBox<Play.Mode> mode_comboBox;
    public Button back_but;
    public Button start_but;
    public TextField hostBet_field;
    public TextField guestBet_field;
    public TextField pot_field;
    public Label error_label;
    public Pagination host_pagination;
    public Pagination guest_pagination;
    private int page = 1;
    protected static Play.Mode playMode;
    private PauseTransition pause;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        error_label.setText("");
        pot_field.setEditable(false);
        mode_comboBox.getItems().addAll(Play.Mode.Betting, Play.Mode.Normal);
        next_but.setOnAction(this::handleNextButton);
        start_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                handleStartButton();
            }
        });
        back_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if(page == 1) {
                    try {
                        Main.loadGuestLogin();
                    } catch (IOException ignored) {
                    }
                }
                if(page == 2){
                    page = 1;
                    showFirstPage();
                }
            }
        });


        host_pagination.setPageFactory(pageIndex -> {
            ImageView imageView = new ImageView(Card.charactersImage.get(Card.Characters.valueOf("Character" +(pageIndex+1))));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return imageView;
        });
        guest_pagination.setPageFactory(pageIndex -> {
            ImageView imageView = new ImageView(Card.charactersImage.get(Card.Characters.valueOf("Character" +(pageIndex+1))));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return imageView;
        });

        host_pagination.setCurrentPageIndex(0);
        guest_pagination.setCurrentPageIndex(0);

        pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            error_label.setTextFill(Color.GREEN);
            error_label.setText("Starting...");
            try {
                Main.loadPlayMenu();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        showFirstPage();
    }

    private void handleStartButton() {
        if(mode_comboBox.getValue().equals(Play.Mode.Betting)){
            if(checkBet()){
                playMode = Play.Mode.Betting;
                setCharacters();
                pause.play();
            }
        }
        if(mode_comboBox.getValue().equals(Play.Mode.Normal)){
            playMode = Play.Mode.Normal;
            setCharacters();
            pause.play();
        }
    }

    private void handleNextButton(ActionEvent actionEvent) {
        if(mode_comboBox.getValue() != null){
            page = 2;
            showSecondPage();
        }
        else{
            error_label.setText("You should select a mode first");
        }
    }

    public void showFirstPage(){
        mode_pane.setVisible(true);
        mode_pane.setDisable(false);
        character_pane.setVisible(false);
        character_pane.setDisable(true);
        error_label.setText("");
    }

    public void showSecondPage(){
        mode_pane.setVisible(false);
        mode_pane.setDisable(true);
        character_pane.setVisible(true);
        character_pane.setDisable(false);
        error_label.setText("");

        if(mode_comboBox.getValue().equals(Play.Mode.Normal)){
            hostBet_field.setVisible(false);
            guestBet_field.setVisible(false);
            pot_field.setVisible(false);
            hostBet_field.setDisable(true);
            guestBet_field.setDisable(true);
            pot_field.setDisable(true);
        }
        else{
            hostBet_field.setVisible(true);
            guestBet_field.setVisible(true);
            pot_field.setVisible(true);
            hostBet_field.setDisable(false);
            guestBet_field.setDisable(false);
            pot_field.setDisable(false);
        }
    }

    public boolean checkBet(){
        String hostBet = hostBet_field.getText();
        String guestBet = guestBet_field.getText();
        if((!hostBet.matches("^\\d+$") || hostBet.isBlank()) || hostBet.length() > 10){
            error_label.setText("Host: invalid bet");
            return false;
        }
        if((!guestBet.matches("^\\d+$") || guestBet.isBlank()) || guestBet.length() > 10){
            error_label.setText("Guest: invalid bet");
            return false;
        }
        int hostBetInt = Integer.parseInt(hostBet);
        int guestBetInt = Integer.parseInt(guestBet);
        if(hostBetInt > hostPlayer.getWallet()){
            error_label.setText("Host: not enough money");
            return false;
        }
        if(guestBetInt > guestPlayer.getWallet()){
            error_label.setText("Guest: not enough money");
            return false;
        }
        loggedInUser.reduceWallet(hostBetInt);
        guestPlayer.reduceWallet(guestBetInt);
        pot_field.setText(String.valueOf(hostBetInt + guestBetInt));
        return true;
    }

    public void setCharacters(){
        Card.Characters hostCharacter = Card.Characters.valueOf("Character" + (host_pagination.getCurrentPageIndex()+1));
        Card.Characters guestCharacter = Card.Characters.valueOf("Character" + (guest_pagination.getCurrentPageIndex()+1));
        hostPlayer.setCharacter(hostCharacter);
        guestPlayer.setCharacter(guestCharacter);
    }
}
