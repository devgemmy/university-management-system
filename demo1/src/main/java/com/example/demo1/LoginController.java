package com.example.demo1;

import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class LoginController {
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;




    public void loginButtonOnAction(ActionEvent event) {
        if (usernameTextField.getText().isBlank() == false && enterPasswordField.getText().isBlank() == false) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password");
        }
    }

    @FXML
    private void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    //Handles login
    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        if (connectDB == null) {
            loginMessageLabel.setText("Database connection failed.");
            return;
        }

        String verifyLogin = "SELECT count(1) FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + enterPasswordField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    //loginMessageLabel.setText("You have successfully logged in!");
                    createAccountForm();
                } else {
                    loginMessageLabel.setText("Invalid login. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void createAccountForm() throws IOException {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 529);
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();

        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }






}