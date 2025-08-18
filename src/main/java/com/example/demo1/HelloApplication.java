package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /* FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400); */
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 869);
        // Debugging: Check if the CSS file is being loaded
        System.out.println("Stylesheet Loaded: " + getClass().getResource("/com/example/demo1/style.css"));
        // Ensure CSS file is linked properly
        scene.getStylesheets().add(getClass().getResource("/com/example/demo1/style.css").toExternalForm());
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Dashboard");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Disable JavaFX accessibility (works on macOS)
        //com.sun.javafx.application.PlatformImpl.setEnableAccessibility(false);
        // disables accessibility, which would cause stack overflow error, especially on MacOS
        System.setProperty("javafx.accessibilty.screen_reader", "false");
        launch();
    }
}