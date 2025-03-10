package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

//Add blank input validation!!!

public class RegisterController {

    @FXML
    private Button closeButton;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField userNameTextField;


    public void registerButtonOnAction (ActionEvent event) {
        //registrationMessageLabel.setText("User registered successfully!");
        if (setPasswordField.getText().equals(confirmPasswordField.getText())) {
            registerUser();
            confirmPasswordLabel.setText("");

        } else {
            confirmPasswordLabel.setText("Passwords do not match!");

        }

    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();

    }

    public void registerUser() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        if (connectDB == null) {
            registrationMessageLabel.setText("Database connection failed.");
            return;
        }

        String firstname = firstNameTextField.getText();
        String lastname = lastNameTextField.getText();
        String username = userNameTextField.getText();
        String password = setPasswordField.getText();

        String insertFields = "INSERT INTO user_account(lastname, firstname, username, password) VALUES ('";
        String insertValues = firstname + "','" + lastname + "','" + username + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);

            registrationMessageLabel.setText("User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }



    }

}
