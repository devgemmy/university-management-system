package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the Administrative Services view
 * Handles navigation to different services and logout functionality
 */
public class AdminServicesController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button fontSizeButton;

    /**
     * Handles the Finance Management button click
     * Navigates to the main dashboard
     */
    @FXML
    private void handleFinanceManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 1000);

            // Get the controller and ensure it's initialized
            DashboardController dashboardController = loader.getController();

            // Set the scene and show
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Initialize the dashboard data after showing the scene
            dashboardController.initialize(null, null);
        } catch (IOException e) {
            showError("Error loading Finance Management dashboard: " + e.getMessage());
        }
    }

    /**
     * Shows "Coming Soon" alert for services not yet implemented
     */
    private void showComingSoon(String service) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(service + " service is coming soon!");
        alert.showAndWait();
    }

    /**
     * Shows error alert when operations fail
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleApplicantDetails() {
        showComingSoon("Applicant Details");
    }

    @FXML
    private void handleAttendance() {
        showComingSoon("Attendance Monitoring");
    }

    @FXML
    private void handleCourseSelection() {
        showComingSoon("Course Selection");
    }

    @FXML
    private void handleRestaurants() {
        showComingSoon("Restaurants");
    }

    @FXML
    private void handleSportsSchool() {
        showComingSoon("Sports School");
    }

    /**
     * Handles logout button click
     * Returns to login screen
     */
    @FXML
    private void handleLogout() {
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

    /**
     * Handles font size button click
     * Toggles between normal and large font sizes
     */
    @FXML
    private void handleFontSize() {
        // Toggle between normal and large font sizes
        Scene scene = fontSizeButton.getScene();
        if (scene.getRoot().getStyle().contains("-fx-font-size: 16px;")) {
            scene.getRoot().setStyle("-fx-font-size: 12px;");
        } else {
            scene.getRoot().setStyle("-fx-font-size: 16px;");
        }
    }
}