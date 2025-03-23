package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
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
    private void viewApplicantDetails() {
        showComingSoon("Applicant Details");
    }

    @FXML
    private void viewAttendance() {
        showComingSoon("Attendance Monitoring");
    }

    @FXML
    private void viewCourseSelection() {
        showComingSoon("Course Selection");
    }

    @FXML
    private void viewRestaurants() {
        showComingSoon("Restaurants");
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