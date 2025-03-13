package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GenerateNewInvoiceController implements Initializable {
    @FXML
    private Button allInvoiceButton, resetButton, generateInvoiceButton;

    @FXML
    private Pane studentCourseInfo, foodSelectionInfo, sportsActivitiesInfo, submitNewInvoice;

    @FXML
    private TextField studentNameField, courseFeeField;

    @FXML
    private ComboBox<String> institutionComboBox, courseComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize institution combo box with sample data
        ObservableList<String> institutions = FXCollections.observableArrayList(
                "Imperial College",
                "Kingston University",
                "Coventry University",
                "Sussex University");
        institutionComboBox.setItems(institutions);

        // Initialize course combo box with sample data
        ObservableList<String> courses = FXCollections.observableArrayList(
                "Business Computing",
                "International Relations",
                "Data Analytics",
                "Data Administration");
        courseComboBox.setItems(courses);
    }

    public void seeAllInvoices(ActionEvent event) throws IOException {
        Stage closingStage = (Stage) allInvoiceButton.getScene().getWindow();
        closingStage.close();
    }

    public void handleReset(ActionEvent event) {
        // Clear all input fields
        studentNameField.clear();
        institutionComboBox.setValue(null);
        courseComboBox.setValue(null);
        courseFeeField.clear();
    }

    public void handleGenerateInvoice(ActionEvent event) {
        // Validate inputs
        if (studentNameField.getText().isEmpty() ||
                institutionComboBox.getValue() == null ||
                courseComboBox.getValue() == null ||
                courseFeeField.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }

        try {
            // Parse course fee
            // double courseFee = Double.parseDouble(courseFeeField.getText());

            // Here you would typically:
            // 1. Create a new Invoice object
            // 2. Save it to your database
            // 3. Show success message
            // 4. Close the window or reset the form

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Invoice Generated");
            successAlert.setContentText("The invoice has been generated successfully!");
            successAlert.showAndWait();

            // Close the window after successful generation
            Stage stage = (Stage) generateInvoiceButton.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Course Fee");
            alert.setContentText("Please enter a valid number for the course fee.");
            alert.showAndWait();
        }
    }

    public void handleInstitutionSelection(ActionEvent event) {
        // You can add logic here to update course list based on selected institution
        // For example, load courses specific to the selected institution
    }

    public void handleCourseSelection(ActionEvent event) {
        // You can add logic here to update course fee or other fields based on selected
        // course
        // For example, set default course fee based on the selected course
    }
}
