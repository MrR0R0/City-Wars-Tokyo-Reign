package com.controllers;

import com.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    public ProgressBar music_progress;
    public ProgressBar sound_progress;
    public Slider music_slider;
    public ImageView musicOn_image;
    public ImageView musicOff_image;
    public Slider sound_slider;
    public ImageView soundOn_image;
    public ImageView soundOff_image;
    public Button back_but;
    public ImageView secondBack_image;
    public Spinner music_spinner;

    public boolean isSliderChanging;
    public MediaPlayer mediaPlayer;
    public static String secondBack = "";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListenerForSlider(sound_slider,sound_progress);
        addListenerForSlider(music_slider,music_progress);
        soundOn_image.setVisible(true);
        musicOn_image.setVisible(true);
        soundOff_image.setVisible(false);
        musicOff_image.setVisible(false);
        soundOn_image.setOnMouseClicked(mouseEvent -> {
            if (soundOn_image.isVisible()) {
                soundOn_image.setVisible(false);
                soundOff_image.setVisible(true);
            }
        });
        soundOff_image.setOnMouseClicked(mouseEvent -> {
            if (soundOff_image.isVisible()) {
                soundOn_image.setVisible(true);
                soundOff_image.setVisible(false);
            }
        });
        musicOn_image.setOnMouseClicked(mouseEvent -> {
            if (musicOn_image.isVisible()) {
                musicOn_image.setVisible(false);
                musicOff_image.setVisible(true);
            }
        });
        musicOff_image.setOnMouseClicked(mouseEvent -> {
            if (musicOff_image.isVisible()) {
                musicOn_image.setVisible(true);
                musicOff_image.setVisible(false);
            }
        });
        back_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadMainMenu();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        secondBack_image.setOnMouseClicked(mouseEvent -> {
            if (secondBack.isEmpty()) {
                secondBack = secondBack_image.getImage().getUrl();
            }
            else {
                secondBack = "";
            }
        });

        music_spinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableArrayList("Option 1", "Option 2")
        ));
    }


    public void updateSlider(Slider timeSlider, ProgressBar progressBar) {
        Platform.runLater(() -> {
            double progress = timeSlider.getValue();
            progressBar.setProgress(progress / 100.0); // Set progress in range [0, 1]
        });
    }

    public void addListenerForSlider(Slider timeSlider, ProgressBar progressBar) {
        timeSlider.setMin(0);
        timeSlider.setMax(100);
        timeSlider.setValue(0);

        timeSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            isSliderChanging = isChanging;
            if (!isChanging) {
                updateSlider(timeSlider, progressBar);
            }
        });

        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isSliderChanging) {
                updateSlider(timeSlider, progressBar);
            }
        });
    }
}
