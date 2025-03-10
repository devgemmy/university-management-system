module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}