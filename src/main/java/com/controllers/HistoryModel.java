package com.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoryModel {
    private final StringProperty hostResult, hostLevel, hostName;
    private final StringProperty date;
    private final StringProperty guestResult, guestLevel, guestName;
    private final Integer winnerId, loserId;

    public HistoryModel(String hostResult, String hostLevel, String hostName, String date,
                     String guestResult, String guestLevel, String guestName, Integer winnerId, Integer loserId) {
        this.hostResult = new SimpleStringProperty(hostResult);
        this.hostLevel = new SimpleStringProperty(hostLevel);
        this.hostName = new SimpleStringProperty(hostName);
        this.date = new SimpleStringProperty(date);
        this.guestResult = new SimpleStringProperty(guestResult);
        this.guestLevel = new SimpleStringProperty(guestLevel);
        this.guestName = new SimpleStringProperty(guestName);
        this.winnerId = winnerId;
        this.loserId = loserId;
    }

    // Properties
    public StringProperty hostResultProperty() {
        return hostResult;
    }

    public StringProperty hostLevelProperty() {
        return hostLevel;
    }

    public StringProperty hostNameProperty() {
        return hostName;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty guestResultProperty() {
        return guestResult;
    }

    public StringProperty guestLevelProperty() {
        return guestLevel;
    }

    public StringProperty guestNameProperty() {
        return guestName;
    }

    public Integer getLoserId() {
        return loserId;
    }

    public Integer getWinnerId() {
        return winnerId;
    }
}
