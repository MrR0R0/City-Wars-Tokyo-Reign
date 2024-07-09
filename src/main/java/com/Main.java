package com;

import com.app.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends javafx.application.Application{
    @Override
    public void start(Stage stage) throws IOException {
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/SignUp-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1050, 700);
            stage.setTitle("War city");
            stage.setScene(scene);
            stage.show();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}