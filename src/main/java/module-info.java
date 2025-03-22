module com.example.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens com.example.dashboard to javafx.fxml;

    exports com.example.dashboard;
}