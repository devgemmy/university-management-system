package com.example.demo1;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicantFormControllerTest extends ApplicationTest {

    private ApplicantFormController applicantFormController;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private DatePicker dobPicker;
    private Button saveButton;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/applicantForm.fxml"));
        Parent root = loader.load();
        applicantFormController = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();

        // Initialize the form fields
        firstNameField = lookup("#firstNameField").query();
        lastNameField = lookup("#lastNameField").query();
        emailField = lookup("#emailField").query();
        dobPicker = lookup("#dobPicker").query();
        saveButton = lookup("#saveButton").query();
    }

    @BeforeEach
    public void setUp() {
    //Ensure DashboardController is initialized before ApplicantFormController
        DashboardController dashboardController = new DashboardController();
        applicantFormController.setDashboardController(dashboardController);
        interact(() -> {
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            dobPicker.setValue(null);
        });
    }

    @Test
    public void testFormValidation() {
        TextField zipField = lookup("#zipField").query();

        interact(() -> {
            firstNameField.setText("John");
            lastNameField.setText("Doe");
            emailField.setText("john.doe@example.com");
            dobPicker.setValue(LocalDate.of(1990, 1, 1));
            zipField.setText("W1A1AA");
        });

        assertFalse(saveButton.isDisabled());
    }

    @Test
    public void testInvalidEmail() {
        // Enter invalid email
        interact(() -> {
            firstNameField.setText("John");
            lastNameField.setText("Doe");
            emailField.setText("invalid-email");
            dobPicker.setValue(LocalDate.of(1990, 1, 1));
        });

        // The save button should still be disabled
        assertTrue(saveButton.isDisabled());
    }

    @Test
    public void testMissingFirstName() {
        // Enter data without first name
        interact(() -> {
            lastNameField.setText("Doe");
            emailField.setText("john.doe@example.com");
            dobPicker.setValue(LocalDate.of(1990, 1, 1));
        });

        // The save button should still be disabled
        assertTrue(saveButton.isDisabled());
    }

    @Test
    public void testMissingDateOfBirth() {
        // Enter data without date of birth
        interact(() -> {
            firstNameField.setText("John");
            lastNameField.setText("Doe");
            emailField.setText("john.doe@example.com");
        });

        // The save button should still be disabled
        assertTrue(saveButton.isDisabled());
    }

    @Test
    public void testSaveApplicant() {
        TextField zipField = lookup("#zipField").query();

        interact(() -> {
            firstNameField.setText("John");
            lastNameField.setText("Doe");
            emailField.setText("john.doe@example.com");
            dobPicker.setValue(LocalDate.of(1990, 1, 1));
            zipField.setText("E1A2BC");
        });

        clickOn(saveButton);

        assertNotNull(applicantFormController.getExistingApplicant());
        assertEquals("John Doe", applicantFormController.getExistingApplicant().getFullName());
    }
}