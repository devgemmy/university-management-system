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

        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000);
        // scene.getStylesheets().add(DashboardApp.class.getResource("styles.css").toExternalForm());

        primaryStage.setTitle("UMS Finance Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}