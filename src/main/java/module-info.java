module City.Wars.Tokyo.Reign {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires java.desktop;

    opens com to javafx.fxml;
    exports com;
}