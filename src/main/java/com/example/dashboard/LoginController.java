package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
// import java.sql.*;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton, closeAppButton;
    @FXML
    private Label errorLabel;

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

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // For demo purposes, using simple validation
        if (email.equals("admin@ums.com") && password.equals("uni123")) {
            System.out.println("Your email and password are correct! Logging in now");
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
            errorLabel.setText("Your email or password is invalid or this account doesn't exist. Try again.");
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