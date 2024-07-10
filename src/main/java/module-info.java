module City.Wars.Tokyo.Reign {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires java.desktop;
    requires javafx.swing;

    exports com.controllers to javafx.fxml;

    opens com to javafx.fxml;
    exports com;
}