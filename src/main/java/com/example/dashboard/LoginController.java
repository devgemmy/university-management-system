package com.example.dashboard;

// Import necessary JavaFX and Java utilities
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
// import java.sql.*;

/**
 * Controller for the login view
 * Handles user authentication and transition to the dashboard
 */
public class LoginController {
    // UI Components linked to the FXML file
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton, closeAppButton;
    @FXML
    private Label errorLabel;

    /**
     * Handles the close application button
     * Exits the application immediately
     */
    @FXML
    private void closeApplication() {
        // Alert the user before closing the application
        Alert closeAlert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Clicking OK would close this application",
                ButtonType.OK,
                ButtonType.CANCEL);
        closeAlert.setTitle("Close UMS Finance Dashboard");
        closeAlert.showAndWait();

        // If Ok is clicked -> Close the app
        if (closeAlert.getResult() == ButtonType.OK) {
            // Stage closingAppStage = (Stage) logOutButton.getScene().getWindow();
            // closingAppStage.close();
            Platform.exit();
        }

    }

    /**
     * Establishes connection to the SQLite database
     * Uses the database file in the current working directory
     * 
     * @return Connection object or null if connection fails
     */
    // private Connection connectToDatabase() {
    // try {
    // String dbPath = System.getProperty("user.dir") + "/UMS-DB.db";
    // String url = "jdbc:sqlite:" + dbPath;
    // return DriverManager.getConnection(url);
    // } catch (SQLException e) {
    // System.err.println("Database connection failed: " + e.getMessage());
    // return null;
    // }
    // }

    /**
     * Handles the login button click
     * Validates user credentials and loads dashboard if successful
     * Currently using simplified authentication for demo purposes
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // For demo purposes, using simple validation
        if (email.equals("admin@ums.com") && password.equals("uni123")) {
            try {
                // Load the administrative services view instead of dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-services-view.fxml"));
                Stage adminStage = (Stage) emailField.getScene().getWindow();
                Scene scene = new Scene(loader.load(), 1200, 1000);

                adminStage.setScene(scene);
                adminStage.setTitle("UMS Finance - Administrative Services");
                adminStage.show();
            } catch (IOException e) {
                errorLabel.setText("Error loading administrative services: " + e.getMessage());
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setText("Invalid email or password");
            errorLabel.setVisible(true);
        }
    }

    // logOutButton.setOnAction(_ -> closeApplication());
}

/*
 * -closeApplication(): void
 * -handleLogin(): void
 * 
 */