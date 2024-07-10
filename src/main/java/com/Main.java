package com;

import com.app.*;
import com.database.Connect;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class Main extends javafx.application.Application{
    private static Stage stage;
    private static Scene scene;
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        stage = primaryStage;
        Card.allCards = Connect.getCards();
        User.signedUpUsers = Connect.getUsers();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/Login-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setTitle("War city");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            Connect.updateDatabase();
            stage.close();
            Platform.exit();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void loadMainMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/MainMenu-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadHistory() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/History-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadSignup() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/SignUp-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/Login-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
}