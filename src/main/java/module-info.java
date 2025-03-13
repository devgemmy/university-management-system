module com.example.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.dashboard to javafx.fxml;

    exports com.example.dashboard;
}