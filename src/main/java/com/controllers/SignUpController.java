package com.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController {
    public TextField username_label;
    public TextField password_label;
    public TextField email_label;
    public TextField confirmPass_label;
    public TextField nickname_label;
    public TextField answer_label;
    public Label firstError_label;
    public Label secondError_label;
    public Button firstNext_but;
    public Button secondNext_but;
    public Button back_but;
    public ComboBox question_comboBox;
    public ImageView pinkDie_image;
    public ImageView blueDie_image;


    public void initialize(URL location, ResourceBundle resources) {
    }
}
