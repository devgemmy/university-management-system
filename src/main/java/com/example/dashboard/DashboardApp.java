package com.example.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardApp.class.getResource("dashboard-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene mainScene = new Scene(root, 1200, 1000);
        mainScene.getStylesheets().add(DashboardApp.class.getResource("styles.css").toExternalForm());

        primaryStage.setTitle("UMS Finance Dashboard");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}