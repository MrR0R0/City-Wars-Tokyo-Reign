package com.controllers;

import com.Main;
import com.app.User;
import com.menu.authentication.Captcha;
import com.menu.authentication.SignUp;
import com.sun.source.tree.ArrayAccessTree;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SignUpController extends SignUp implements Initializable {
    public TextField username_field;
    public PasswordField password_field;
    public TextField showPassword_field;
    public TextField email_field;
    public PasswordField confirmPass_field;
    public TextField nickname_field;
    public TextField answer_field;
    public TextField captcha_field;
    public Label firstError_label;
    public Label secondError_label;
    public Button firstNext_but;
    public Button secondNext_but;
    public Button back_but;
    public ComboBox<String> question_comboBox;
    public ImageView pinkDice_image;
    public ImageView blueDice_image;
    public GridPane firstGrid;
    public GridPane secondGrid;
    public GridPane captcha_pane;

    private boolean blueDicBoolean = true;
    public String captchaText;
    public ImageView captcha_image = new ImageView();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showPage(2);

        // First page setup
        pinkDice_image.setVisible(!blueDicBoolean);
        blueDice_image.setVisible(blueDicBoolean);
        showPassword_field.setVisible(false);
        showPassword_field.setEditable(false);
        firstNext_but.setOnAction(this::handleFirstNext);
        secondNext_but.setOnAction(this::handleSecondNext);
        back_but.setOnAction(this::handleBack);
        pinkDice_image.setOnMouseClicked(this::handleDiceClick);
        blueDice_image.setOnMouseClicked(this::handleDiceClick);
        firstError_label.setText("");
        secondError_label.setText("");

        // Second page setup
        question_comboBox.getItems().addAll("What is your father’s name?",
                "What is your favourite color?",
                "What was the name of your first pet?");
        captcha_field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                captcha_image.setVisible(true);
                setCaptchaImage();
            } else {
                captcha_image.setVisible(false);
            }
        });
    }

    private void handleDiceClick(MouseEvent mouseEvent) {
        blueDicBoolean = !blueDicBoolean;
        pinkDice_image.setVisible(!blueDicBoolean);
        blueDice_image.setVisible(blueDicBoolean);
        String randomPass = Captcha.generatePassword(10);
        showPassword_field.setText(randomPass);
        showPassword_field.setVisible(!blueDicBoolean);
        if (!blueDicBoolean)
            password_field.setText(randomPass);
        else
            password_field.clear();
        password_field.setVisible(blueDicBoolean);
    }

    private void handleFirstNext(ActionEvent actionEvent) {
        if (checkFirstPageInputs()) {
            showPage(2);
        }
    }

    private void handleSecondNext(ActionEvent actionEvent) {
        if (checkSecondPageInputs()) {
            recoveryQToIndex();
            User tmpUser = new User(username, pass, nickname, email, recoveryAns,
                    recoveryQ, "", SignUp.initialMoney, 1, User.signedUpUsers.size() + 1, 0);
            tmpUser.giveRandomCard();
            tmpUser.updateCardSeriesByCards();
            User.signedUpUsers.put(User.signedUpUsers.size()+1, tmpUser);
            loggedInUser = tmpUser;
            try {
                Main.loadMainMenu();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleBack(ActionEvent actionEvent) {
        showPage(1);
    }

    private boolean checkFirstPageInputs() {
        username = username_field.getText();
        pass = password_field.getText();
        email = email_field.getText();
        String passConf = confirmPass_field.getText();
        firstError_label.setText("");

        if (username.isBlank()) {
            firstError_label.setText("Blank username field");
            return false;
        }
        if (pass.isBlank()) {
            firstError_label.setText("Blank password field");
            return false;
        }
        if (passConf.isBlank()) {
            firstError_label.setText("Blank password confirmation field");
            return false;
        }
        if (email.isBlank()) {
            firstError_label.setText("Blank email field");
            return false;
        }
        if (username.length() < 5 || username.length() > 25) {
            firstError_label.setText("Username must be between 5 and 25 characters long");
            return false;
        }
        if (!username.matches(USERNAME_REGEX)) {
            firstError_label.setText("Username should consist of lowercase or uppercase letters, numbers, and underscores.");
            return false;
        }
        if (!email.matches(EMAIL_REGEX)) {
            firstError_label.setText("The email does not have the correct format");
            return false;
        }
        if(!validPasswordFormat(pass, firstError_label)){
            return false;
        }
        if (User.isInUsersList("username", username)) {
            firstError_label.setText("A user with the same username exists");
            return false;
        }
        if (!pass.equals(passConf)) {
            firstError_label.setText("Password confirmation doesn't match!");
            return false;
        }
        return true;
    }

    private boolean checkSecondPageInputs() {
        nickname = nickname_field.getText();
        recoveryAns = answer_field.getText();
        recoveryQ = question_comboBox.getValue();
        String captchaAns = captcha_field.getText();
        secondError_label.setText("");

        if (nickname.isBlank()) {
            secondError_label.setText("Blank nickname field");
            return false;
        }
        if (recoveryAns.isBlank()) {
            secondError_label.setText("Blank answer field");
            return false;
        }
        if (recoveryQ == null) {
            secondError_label.setText("Please select a recovery question");
            return false;
        }
        if (nickname.length() < 5 || nickname.length() > 25) {
            secondError_label.setText("Nickname must be between 5 and 25 characters long");
            return false;
        }
        if (!captchaText.equals(captchaAns)) {
            secondError_label.setText("Invalid captcha");
            return false;
        }
        return true;
    }

    private void showPage(int number) {
        boolean visibility = (number == 1);
        firstGrid.setVisible(visibility);
        firstGrid.setDisable(!visibility);
        secondGrid.setVisible(!visibility);
        secondGrid.setDisable(visibility);
    }

    private void setCaptchaImage() {
        captchaText = Captcha.generatePassword(5);
        captcha_image.setImage(Captcha.stringToImage(captchaText));
        captcha_image.setFitHeight(Captcha.stringToImage(captchaText).getHeight()-30);
        captcha_image.setFitWidth(Captcha.stringToImage(captchaText).getWidth());
        captcha_image.setVisible(true);
        StackPane stackPane = stackImage(captcha_image);
        stackPane.setStyle("-fx-border-color: #0800ff; -fx-border-width: 2; -fx-border-style: solid;");
        stackPane.setMaxHeight(Captcha.stringToImage(captchaText).getHeight()-30);
        stackPane.setMaxWidth(Captcha.stringToImage(captchaText).getWidth());
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);
        captcha_pane.add(stackPane,1,0);
    }

    private void recoveryQToIndex() {
        for (int i = 0; i < question_comboBox.getItems().size(); i++) {
            if (question_comboBox.getItems().get(i).equals(recoveryQ)) {
                recoveryQ = String.valueOf(i + 1);
                return;
            }
        }
    }

    static boolean validPasswordFormat(String password, Label label){
        if (emptyField(password, "Password")) {
            label.setText("Password is empty");
            return false;
        }

        //checking whether the password is weak
        if (password.length() < 8) {
            label.setText("Password should be at least 8 characters!");
            return false;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            label.setText("The password must contain at least one uppercase letter and one lowercase letter.");
            return false;
        }
        if (password.replaceAll("[a-zA-Z0-9]", "").isEmpty()) {
            label.setText("The password must contain at least one special character");
            return false;
        }
        return true;
    }
}
