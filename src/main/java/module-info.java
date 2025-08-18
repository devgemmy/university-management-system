module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static javafx.graphics;
    requires transitive javafx.swing;
    requires transitive javafx.web;
    requires jdk.sctp;
    requires java.sql;
    requires jcalendar;
    requires jfreechart;

    // JavaFX and FXML access for Applicant details
    opens com.example.demo1 to javafx.graphics, javafx.fxml, org.testfx;
    exports com.example.demo1;

    // JavaFX and FXML access for Finance
    opens com.example.dashboard to javafx.fxml, javafx.graphics;
    exports com.example.dashboard;
}