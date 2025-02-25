module com.example.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.dashboard to javafx.fxml;
    exports com.example.dashboard;
}