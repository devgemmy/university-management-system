package com.example.demo1;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class HelloApplicationTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        HelloApplication app = new HelloApplication();
        app.start(stage);
        this.stage = stage;
    }

    @Test
    public void testStageTitle() {
        assertEquals("Dashboard", stage.getTitle());
    }

    @Test
    public void testSceneLoaded() {
        Scene scene = stage.getScene();
        assertNotNull(scene);
        assertEquals(1200, scene.getWidth());
        assertEquals(869, scene.getHeight());
    }

    @Test
    public void testStylesheetLoaded() {
        Scene scene = stage.getScene();
        assertTrue(scene.getStylesheets().contains(getClass().getResource("/com/example/demo1/style.css").toExternalForm()));
    }
}