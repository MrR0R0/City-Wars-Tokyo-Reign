package com.controllers.play;

import com.Main;
import com.app.User;
import com.menu.play.Player;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.controllers.play.GuestLoginController.guestPlayer;
import static com.controllers.play.GuestLoginController.hostPlayer;

public class EndGameController implements Initializable {
    public Button back_but;
    public Label winner_label;
    public Label winnerCoin_label;
    public Label winnerDamage_label;
    public Label winnerXP_obtained_label;
    public Label winnerXP_total_label;
    public Label winnerXP_required_label;
    public Label loser_label;
    public Label loserCoin_label;
    public Label loserDamage_label;
    public Label loserXP_obtained_label;
    public Label loserXP_total_label;
    public Label loserXP_required_label;

    protected static Player winner, loser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        winner_label.setText("Winner: " + winner.getNickname());
        winnerCoin_label.setText("Coins: " + winner.getObtainedCoins());
        winnerXP_obtained_label.setText("XP: +" + winner.getObtainedXP());
        winnerDamage_label.setText("Damage: +" + winner.getTotalAttack());
        winnerXP_total_label.setText("Total XP: +" + winner.getXP());
        winnerXP_required_label.setText("Required XP for next level: " + User.nextLevelXP(winner.getLevel()));


        loser_label.setText("Loser: " + loser.getNickname());
        loserCoin_label.setText("Coins: " + loser.getObtainedCoins());
        loserXP_obtained_label.setText("XP: +" + loser.getObtainedXP());
        loserDamage_label.setText("Damage: +" + loser.getTotalAttack());
        loserXP_total_label.setText("Total XP: +" + loser.getXP());
        loserXP_required_label.setText("Required XP for next level: " + User.nextLevelXP(loser.getLevel()));

        back_but.setOnAction(event -> {
            try {
                Main.loadMainMenu();
                guestPlayer = null;
                hostPlayer = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
