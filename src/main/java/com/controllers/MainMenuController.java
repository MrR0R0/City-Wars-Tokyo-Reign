package com.controllers;

import com.Main;
import com.app.User;
import com.menu.MainMenu;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends MainMenu implements Initializable {

    public Button play_but;
    public Button history_but;
    public Button shop_but;
    public Button setting_but;
    public ImageView profile_image;
    public Label level_label;
    public ProgressBar XP_progress;
    public ImageView XP_image;
    public ImageView HP_image;
    public ImageView wallet_image;
    public Label HP_label;
    public Label wallet_label;
    public Button logout_but;
    public GridPane user_gridPane;
    public ImageView background_image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SettingController.secondBack.isEmpty()){
            background_image.setImage(new Image("file:src\\main\\resources\\com\\images\\Designer.png"));
        }
        else {
            background_image.setImage(new Image("file:src\\main\\resources\\com\\images\\main_background.png"));
        }
        play_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadGuestLogin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        history_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadHistory();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        shop_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadShop();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        setting_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadSetting();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        user_gridPane.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadUser();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        logout_but.setOnMouseClicked(mouseEvent -> {
            logout();
        });

        updateWalletLabel();
        showProfileImage();
        updateHPLabel();
        updateLevelLabel();
        updateXPProgress();

    }

    private void logout(){
        loggedInUser = new User();
        try {
            Main.loadLogin();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateWalletLabel() {
        wallet_label.setText(loggedInUser.getWallet().toString());
    }

    private void showProfileImage(){
        if (loggedInUser.getProfile() != null)
            profile_image.setImage(loggedInUser.getProfile());
    }
    private void updateLevelLabel(){
        level_label.setText(loggedInUser.getLevel().toString());
    }
    private void updateHPLabel(){
        HP_label.setText(loggedInUser.getHP().toString());
    }
    private void updateXPProgress(){
        XP_progress.setProgress((double) loggedInUser.getXP() /User.nextLevelXP(loggedInUser.getLevel()));
    }
}
