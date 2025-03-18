package com.example.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.scene.Parent;

public class DashboardApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardApp.class.getResource("login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene mainScene = new Scene(root, 1200, 800);
        mainScene.getStylesheets().add(DashboardApp.class.getResource("styles.css").toExternalForm());

        stage.setTitle("UMS Finance - Login");
        stage.setResizable(false);
        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}