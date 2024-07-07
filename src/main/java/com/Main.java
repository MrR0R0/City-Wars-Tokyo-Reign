package com;

import com.app.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends javafx.application.Application{
    static public Parent root;
    static public Scene scene;
    static public Stage stage;

    public static void main(String[] args) throws SQLException, IOException {
        launch(args);
        ProgramController controller = new ProgramController();
        controller.run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Main-view.fxml")));
        root = loader.load();
        scene = new Scene(root, 400, 400);
        stage.setTitle("Just a test!");
        stage.setScene(scene);
        stage.show();
    }
}