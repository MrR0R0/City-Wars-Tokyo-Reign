package com.controllers.play;

import com.Main;
import com.app.User;
import com.menu.Menu;
import com.menu.authentication.Login;
import com.menu.play.Play;
import com.menu.play.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuestLoginController extends Login implements Initializable {

    @FXML
    public TextField username_field;
    public TextField password_field;
    public Label forgot_label;
    public Label error_label;
    public Label countdown_label;
    public Button login_but;

    private int wrongPasswordCounter;
    private String username;

    protected static Player hostPlayer;
    protected static Player guestPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostPlayer = new Player(loggedInUser);
        login_but.setOnAction(event -> {
            try {
                handleLogin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        forgot_label.setOnMouseClicked(this::forgotPassword);
        countdown_label.setText("");
        error_label.setText("");
        wrongPasswordCounter = 0;
    }

    private void forgotPassword(MouseEvent mouseEvent) {
    }

    private void handleLogin() throws IOException {
        if(checkLogIn()){
            guestPlayer = new Player(User.signedUpUsers.get(User.getIdByUsername(username)));
            Main.loadPreparePlay();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private boolean checkLogIn() {
        username = username_field.getText();
        String password = password_field.getText();
        Integer id = User.getIdByUsername(username);
        error_label.setText("");
        if(!User.signedUpUsers.containsKey(User.getIdByUsername(username))){
            error_label.setText("Username \"" + username + "\" does not exist!");
            return false;
        }
        if (guestPlayer.getId().equals(loggedInUser.getId())) {
            guestPlayer = null;
            System.out.println("Invalid action: You cannot battle yourself.");
            return false;
        }
        if(matchingPassword(id, password)){
            return true;
        }
        else{
            error_label.setText("Incorrect password :(");
            wrongPasswordCounter++;
        }
        if(wrongPasswordCounter >= maxTry-1){
            wrongPasswordCounter = 0;
            password_field.clear();
            username_field.clear();
            password_field.setEditable(false);
            username_field.setEditable(false);

            Instant start = Instant.now();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                long elapsedSeconds = Duration.between(start, Instant.now()).getSeconds();
                long secondsRemaining = 10 - elapsedSeconds;
                if (secondsRemaining > 0) {
                    Platform.runLater(() -> countdown_label.setText(secondsRemaining + " seconds remaining..."));
                } else {
                    Platform.runLater(() -> countdown_label.setText(""));
                    scheduler.shutdown();
                }
            };
            scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

            password_field.setEditable(true);
            username_field.setEditable(true);
        }
        return false;
    }
}
