package com;

import com.app.*;
import com.database.Connect;
import com.menu.Menu;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        fillAllCardImages();
        Menu.loggedInUser = User.signedUpUsers.get(2);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/Play-view.fxml"));
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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/GuestLogin-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadShop() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/Shop-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/User-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadGuestLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/Login-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }
    public static void loadPreparePlay() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/PreparePlay-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }

    public static void loadEndGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/EndGame-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1050, 700);
        stage.setScene(scene);
    }

    private static void fillAllCardImages(){
        for(int i=1; i<=Card.allCards.size(); i++){
            Card.allCardImages.put(i, new Image("file:src\\main\\resources\\com\\images\\card\\" + i + ".png"));
        }
    }

    public static Stage getStage() {
        return stage;
    }
}