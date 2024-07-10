package com.controllers;

import com.Main;
import com.app.User;
import com.menu.ProfileMenu;
import com.menu.authentication.SignUp;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ProfileController extends ProfileMenu implements Initializable {
    public TextField username_field;
    public TextField password_field;
    public TextField nickname_field;
    public TextField email_field;
    public TextField confirmPass_field;
    public Label error_label;
    public Button save_but;
    public Button back_but;

    private String username, password, email, nickname;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        error_label.setText("");
        save_but.setOnMouseClicked(event -> {
            try {
                handleSave();
            } catch (IOException e) {
                System.out.println(e);
            }
        });
        back_but.setOnMouseClicked(event -> {
            try {
                Main.loadMainMenu();
            } catch (IOException e) {
                System.out.println(e);
            }
        });
    }

    private void handleSave() throws IOException {
        if(checkInputs()){
            if(!username.isBlank()){
                loggedInUser.setUsername(username);
            }
            if(!password.isBlank()){
                loggedInUser.setPassword(password);
            }
            if(!email.isBlank()){
                loggedInUser.setEmail(email);
            }
            if(!nickname.isBlank()){
                loggedInUser.setNickname(nickname);
            }
            Main.loadMainMenu();
        }
    }

    private boolean checkInputs(){
        username = username_field.getText();
        password = password_field.getText();
        nickname = nickname_field.getText();
        email = email_field.getText();
        String passConf = confirmPass_field.getText();
        error_label.setText("");

        boolean isEverythingBlank = username.isBlank() && password.isBlank();
        isEverythingBlank = isEverythingBlank && email.isBlank();
        isEverythingBlank = isEverythingBlank && nickname.isBlank();

        if (isEverythingBlank) {
            error_label.setText("You should at least change one field");
            return false;
        }

        if(!username.isBlank()) {
            if (username.length() < 5 || username.length() > 25) {
                error_label.setText("Username must be between 5 and 25 characters long");
                return false;
            }
            if (!username.matches(SignUp.USERNAME_REGEX)) {
                error_label.setText("Username should consist of lowercase or uppercase letters, numbers, and underscores.");
                return false;
            }
            if (User.isInUsersList("username", username)) {
                error_label.setText("A user with the same username exists");
                return false;
            }
        }

        if (!email.isBlank() && !email.matches(SignUp.EMAIL_REGEX)) {
            error_label.setText("The email does not have the correct format");
            return false;
        }

        if (!password.equals(passConf)) {
            error_label.setText("Password confirmation doesn't match!");
            return false;
        }

        return true;
    }
}
