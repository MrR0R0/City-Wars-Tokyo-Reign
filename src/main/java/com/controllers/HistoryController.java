package com.controllers;


import com.Main;
import com.app.User;
import com.database.Connect;
import com.menu.Menu;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HistoryController extends Menu implements Initializable {
    @FXML
    public TableView<HistoryModel> history_table;
    public TableColumn<HistoryModel, String> hostResult_col;
    public TableColumn<HistoryModel, String> hostLevel_col;
    public TableColumn<HistoryModel, String> hostName_col;
    public TableColumn<HistoryModel, String> date_col;
    public TableColumn<HistoryModel, String> guestName_col;
    public TableColumn<HistoryModel, String> guestLevel_col;
    public TableColumn<HistoryModel, String> guestResult_col;
    public Button back_but;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<HistoryModel> history = new ArrayList<>();
        try {
            history = Connect.getHistory(String.valueOf(loggedInUser.getId()));
        } catch (SQLException ignored){
        }
        hostResult_col.setCellValueFactory(cellData -> cellData.getValue().hostResultProperty());
        hostLevel_col.setCellValueFactory(cellData -> cellData.getValue().hostLevelProperty());
        hostName_col.setCellValueFactory(cellData -> cellData.getValue().hostNameProperty());
        date_col.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        guestResult_col.setCellValueFactory(cellData -> cellData.getValue().guestResultProperty());
        guestLevel_col.setCellValueFactory(cellData -> cellData.getValue().guestLevelProperty());
        guestName_col.setCellValueFactory(cellData -> cellData.getValue().guestNameProperty());

        for(HistoryModel historyModel : history){
            history_table.getItems().add(historyModel);
        }

        back_but.setOnMouseClicked(mouseEvent -> {
            try {
                Main.loadMainMenu();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

    }
}
