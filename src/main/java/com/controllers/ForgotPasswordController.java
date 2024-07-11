package com.controllers;

import com.Main;
import com.app.Error;
import com.app.User;
import com.menu.authentication.SignUp;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import com.menu.Menu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    public Button back_but;
    public TextField username_field;
    public TextField recoveryAns_field;
    public PasswordField newpass_field;
    public Label error_label;
    public Button login_but;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        error_label.setText("");
        back_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                try {
                    Main.loadLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        login_but.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                try {
                    handleLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    private void handleLogin() throws IOException {
        if(checkLogin()){
            Main.loadMainMenu();
        }
    }

    private boolean checkLogin(){
        String username = username_field.getText();
        String recoveryAns = recoveryAns_field.getText();
        String password = newpass_field.getText();
        ArrayList<String> questions = new ArrayList<>(Arrays.asList("What is your father’s name?",
                "What is your favourite color?", "What was the name of your first pet?"));
        if(!User.signedUpUsers.containsKey(User.getIdByUsername(username))){
            error_label.setText("Username \"" + username + "\" does not exist!");
            return false;
        }
        User tmpUser = User.signedUpUsers.get(User.getIdByUsername(username));
        if(!tmpUser.getRecoveryAns().equals(recoveryAns)){
            error_label.setText("Incorrect recovery answer");
            recoveryAns_field.clear();
            recoveryAns_field.setPromptText(questions.get(tmpUser.getRecoveryQ()-1));
            return false;
        }
        if(!SignUpController.validPasswordFormat(password, error_label)){
            return false;
        }
        User.signedUpUsers.get(User.getIdByUsername(username)).setPassword(password);
        Menu.loggedInUser = User.signedUpUsers.get(User.getIdByUsername(username));
        return true;
    }
}
