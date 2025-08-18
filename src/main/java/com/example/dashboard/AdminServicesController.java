package com.example.dashboard;

import Restaurant.MainApp;
import com.example.attendance.Dashboard;
import com.example.course.MySwingGUI;
import com.example.demo1.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AdminServicesController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button fontSizeButton;

    @FXML
    private void viewFinanceDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdminServicesController.class.getResource("/com/example/dashboard/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 1000);
            // Set the scene and show
            Stage dashboardStage = (Stage) logoutButton.getScene().getWindow();
            dashboardStage.setTitle("UMS Finance - Dashboard");
            dashboardStage.setScene(scene);
            dashboardStage.show();
        } catch (IOException e) {
            showError("Error loading Finance Management dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showComingSoon(String service) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(service + " service is coming soon!");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void viewApplicantDetails() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/demo1/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 869);
            Stage applicantStage = new Stage();

            System.out.println("Stylesheet Loaded: " + getClass().getResource("/com/example/demo1/style.css"));
            scene.getStylesheets().add(getClass().getResource("/com/example/demo1/style.css").toExternalForm());

            applicantStage.setTitle("Dashboard");
            applicantStage.setResizable(false);
            applicantStage.setScene(scene);
            applicantStage.show();
            // showComingSoon("Applicant Details");
        } catch (IOException e) {
            showError("Error loading Applicant Details dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void viewAttendance() {
        SwingUtilities.invokeLater(() -> {
            Dashboard frame = new Dashboard();
            frame.setVisible(true);
        });

        // showComingSoon("Attendance Monitoring");
    }

    @FXML
    private void viewCourseSelection() {
        //showComingSoon("Course Selection");
        EventQueue.invokeLater(() -> {
            try {
                MySwingGUI frame = new MySwingGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void viewRestaurants() {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            // Change the close operation to dispose the window instead of exiting the application.
            app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Save stock when the app is closed
            app.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    app.saveStockBeforeExit();
                }
            });

            app.setVisible(true);
        });
        //showComingSoon("Restaurants");
    }

    @FXML
    private void viewSportsSchool() {
        showComingSoon("Sports School");
    }

    // Handles logout button click and returns to login screen
    @FXML
    private void appLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error returning to login: " + e.getMessage());
        }
    }
}

/*
 * List of Methods:
 * -viewFinanceDashboard(): void
 * -showComingSoon(service: String): void
 * -showError(message: String): void
 * -viewApplicantDetails(): void
 * -viewAttendance(): void
 * -viewCourseSelection(): void
 * -viewRestaurants(): void
 * -viewSportsSchool(): void
 * -appLogout(): void
 */