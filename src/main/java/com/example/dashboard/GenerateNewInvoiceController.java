package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for the invoice generation view
 * Handles creation of new invoices with student, course, sports, and food
 * details
 */
public class GenerateNewInvoiceController {
    // UI Components for basic invoice information
    @FXML
    private TextField studentNameField;
    @FXML
    private ComboBox<String> institutionComboBox, courseComboBox;

    @FXML
    private TextField courseFeeField;
    @FXML
    private DatePicker invoiceDatePicker;

    @FXML
    private Button foodThirdRow, sportThirdRow;

    // Panes for sports and food activities
    @FXML
    private Pane sportsActivitiesInfo;
    private List<Pair<ComboBox<String>, TextField>> sportsFields = new ArrayList<>();

    @FXML
    private Pane foodSelectionInfo;
    private List<Pair<ComboBox<String>, TextField>> foodFields = new ArrayList<>();

    // Error labels for form validation
    @FXML
    private Label studentFieldErr;
    @FXML
    private Label feeFieldErr;
    @FXML
    private Label institutionFieldErr;
    @FXML
    private Label courseFieldErr;
    @FXML
    private Label dateFieldErr;
    @FXML
    private Label sportFieldErr;
    @FXML
    private Label sportPriceFieldErr;
    @FXML
    private Label foodFieldErr;
    @FXML
    private Label foodPriceFieldErr;

    private DatabaseModel dbModel;
    private Map<String, String> institutionsMap; // Maps UKPRN to institution name
    private Map<String, Map<String, String>> coursesMap; // Maps institution to available courses

    @FXML
    public void initialize() {
        dbModel = new DatabaseModel();

        // Hide error labels initially
        hideAllErrorLabels();

        // Set current date as default
        invoiceDatePicker.setValue(LocalDate.now());

        // Load institutions
        institutionsMap = dbModel.getInstitutions();
        institutionComboBox.setItems(FXCollections.observableArrayList(institutionsMap.values()));

        // Initialize sports and food fields
        initializeSportsFields();
        initializeFoodFields();

        // Add listeners for validation
        addValidationListeners();
    }

    private void hideAllErrorLabels() {
        studentFieldErr.setVisible(false);
        feeFieldErr.setVisible(false);
        institutionFieldErr.setVisible(false);
        courseFieldErr.setVisible(false);
        dateFieldErr.setVisible(false);
        sportFieldErr.setVisible(false);
        sportPriceFieldErr.setVisible(false);
        foodFieldErr.setVisible(false);
        foodPriceFieldErr.setVisible(false);
    }

    private void initializeSportsFields() {
        List<String> sportsActivities = dbModel.getSportsActivities();

        // Initialize the first two sports fields
        ComboBox<String> sport1 = (ComboBox<String>) sportsActivitiesInfo.lookup("#sportComboBox1");
        TextField price1 = (TextField) sportsActivitiesInfo.lookup("#sportCostField1");
        sport1.setItems(FXCollections.observableArrayList(sportsActivities));
        sportsFields.add(new Pair<>(sport1, price1));

        ComboBox<String> sport2 = (ComboBox<String>) sportsActivitiesInfo.lookup("#sportComboBox2");
        TextField price2 = (TextField) sportsActivitiesInfo.lookup("#sportCostField2");
        sport2.setItems(FXCollections.observableArrayList(sportsActivities));
        sportsFields.add(new Pair<>(sport2, price2));

        // Add button handler
        // Button addSportBtn = (Button) sportsActivitiesInfo.lookup("Button");
        // addSportBtn.setOnAction(e -> addNewSportField());
    }

    private void initializeFoodFields() {
        List<String> foodItems = dbModel.getFoodItems();

        // Initialize the first two food fields
        ComboBox<String> food1 = (ComboBox<String>) foodSelectionInfo.lookup("#foodComboBox1");
        TextField price1 = (TextField) foodSelectionInfo.lookup("#foodCostField1");
        food1.setItems(FXCollections.observableArrayList(foodItems));
        foodFields.add(new Pair<>(food1, price1));

        ComboBox<String> food2 = (ComboBox<String>) foodSelectionInfo.lookup("#foodComboBox2");
        TextField price2 = (TextField) foodSelectionInfo.lookup("#foodCostField2");
        food2.setItems(FXCollections.observableArrayList(foodItems));
        foodFields.add(new Pair<>(food2, price2));

        // Add button handler
        // Button addFoodBtn = (Button) foodSelectionInfo.lookup("Button");
        // addFoodBtn.setOnAction(e -> {
        // addNewFoodField();
        // foodThirdRow.setVisible(true);
        // });
    }

    private void addValidationListeners() {
        // Student name validation - allow letters, spaces, and common punctuation
        studentNameField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue != null && !newValue.matches("[a-zA-Z\\s\\-',.()]*")) {
                studentFieldErr.setVisible(true);
            } else {
                studentFieldErr.setVisible(false);
            }
        });

        // Course fee validation
        courseFeeField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*\\.?\\d*")) {
                feeFieldErr.setVisible(true);
            } else {
                feeFieldErr.setVisible(false);
            }
        });
    }

    @FXML
    private void handleInstitutionSelection(ActionEvent event) {
        String selectedInstitution = institutionComboBox.getValue();
        if (selectedInstitution != null) {
            String ukprn = getUKPRNForInstitution(selectedInstitution);
            Map<String, String> courses = dbModel.getCoursesByInstitution(ukprn);
            courseComboBox.setItems(FXCollections.observableArrayList(courses.keySet()));
            institutionFieldErr.setVisible(false);
        }
    }

    private String getUKPRNForInstitution(String institutionName) {
        return institutionsMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(institutionName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    @FXML
    private void handleCourseSelection(ActionEvent event) {
        courseFieldErr.setVisible(false);
    }

    // private void addNewSportField() {
    // ComboBox<String> sportCombo = new ComboBox<>();
    // TextField priceField = new TextField();

    // sportCombo.setPromptText("Sport Name");
    // priceField.setPromptText("Sport Price");

    // // Set Layout
    // sportCombo.setLayoutX(20.0);
    // sportCombo.setLayoutY(190.0);
    // priceField.setLayoutX(367.0);
    // priceField.setLayoutY(190.0);

    // // Set styles
    // sportCombo.setPrefHeight(45);
    // sportCombo.setPrefWidth(300);
    // sportCombo.setStyle("-fx-background-color: #FFFFFF; -fx-border-color:
    // #757575; -fx-border-radius: 8px;");

    // priceField.setPrefHeight(45);
    // priceField.setPrefWidth(300);
    // priceField.setStyle("-fx-background-color: #FFFFFF; -fx-border-color:
    // #757575; -fx-border-radius: 8px;");

    // // Add to pane
    // VBox container = new VBox(10);
    // container.getChildren().addAll(sportCombo, priceField);
    // container.setPadding(new Insets(10));
    // sportsActivitiesInfo.getChildren().add(container);

    // // Add to list
    // sportsFields.add(new Pair<>(sportCombo, priceField));

    // // Populate sports activities
    // sportCombo.setItems(FXCollections.observableArrayList(dbModel.getSportsActivities()));
    // }

    // private void addNewFoodField() {
    // ComboBox<String> foodCombo = new ComboBox<>();
    // TextField priceField = new TextField();

    // foodCombo.setPromptText("Food Name");
    // priceField.setPromptText("Food Price");

    // // Set Layout
    // foodCombo.setLayoutX(19.0);
    // foodCombo.setLayoutY(190.0);
    // priceField.setLayoutX(366.0);
    // priceField.setLayoutY(190.0);

    // // Set styles
    // foodCombo.setPrefHeight(45);
    // foodCombo.setPrefWidth(300);
    // foodCombo.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #757575;
    // -fx-border-radius: 8px;");

    // priceField.setPrefHeight(45);
    // priceField.setPrefWidth(300);
    // priceField.setStyle("-fx-background-color: #FFFFFF; -fx-border-color:
    // #757575; -fx-border-radius: 8px;");

    // // Add to pane
    // VBox container = new VBox(10);
    // container.getChildren().addAll(foodCombo, priceField);
    // container.setPadding(new Insets(10));
    // foodSelectionInfo.getChildren().add(container);

    // // Add to list
    // foodFields.add(new Pair<>(foodCombo, priceField));

    // // Populate food items
    // foodCombo.setItems(FXCollections.observableArrayList(dbModel.getFoodItems()));
    // }

    @FXML
    private void handleReset() {
        // Clear all fields
        studentNameField.clear();
        institutionComboBox.setValue(null);
        courseComboBox.setValue(null);
        courseFeeField.clear();
        invoiceDatePicker.setValue(LocalDate.now());

        // Clear sports fields
        sportsFields.forEach(pair -> {
            pair.getKey().setValue(null);
            pair.getValue().clear();
        });

        // Clear food fields
        foodFields.forEach(pair -> {
            pair.getKey().setValue(null);
            pair.getValue().clear();
        });

        // Hide all error labels
        hideAllErrorLabels();
    }

    /**
     * Handles the generation of a new invoice
     * Validates all fields and creates invoice in database
     */
    @FXML
    private void handleGenerateInvoice() {
        if (!validateFields()) {
            return;
        }

        try {
            // Prepare invoice data
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("studentName", studentNameField.getText());
            String institutionName = institutionComboBox.getValue();
            invoiceData.put("institutionId", getUKPRNForInstitution(institutionName));
            invoiceData.put("institutionName", institutionName);

            // Get the selected course name and its corresponding KISCOURSEID
            String selectedCourse = courseComboBox.getValue();
            String kiscourseid = getKISCOURSEIDForCourse(selectedCourse, institutionName);

            invoiceData.put("courseId", kiscourseid); // Use KISCOURSEID for the course ID
            invoiceData.put("courseName", selectedCourse); // Use full course name for display
            invoiceData.put("courseFees", Double.parseDouble(courseFeeField.getText()));
            invoiceData.put("invoiceDate", invoiceDatePicker.getValue());

            // Process sports activities
            Map<String, String> sportsActivities = new HashMap<>();
            for (Pair<ComboBox<String>, TextField> sportField : sportsFields) {
                String sport = sportField.getKey().getValue();
                String price = sportField.getValue().getText();
                if (sport != null && !sport.isEmpty() && price != null && !price.isEmpty()) {
                    sportsActivities.put(sport, price);
                }
            }
            invoiceData.put("sportsActivities", sportsActivities);

            // Process food items
            Map<String, String> foodItems = new HashMap<>();
            for (Pair<ComboBox<String>, TextField> foodField : foodFields) {
                String food = foodField.getKey().getValue();
                String price = foodField.getValue().getText();
                if (food != null && !food.isEmpty() && price != null && !price.isEmpty()) {
                    foodItems.put(food, price);
                }
            }
            invoiceData.put("foodItems", foodItems);

            // Generate invoice
            String invoiceId = dbModel.generateInvoice(invoiceData);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Invoice generated successfully with ID: " + invoiceId);
            alert.showAndWait();

            // Reset form
            handleReset();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to generate invoice: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gets the KISCOURSEID for a course at a specific institution
     * 
     * @param courseName      The name of the course
     * @param institutionName The name of the institution
     * @return The KISCOURSEID or course name if not found
     */
    private String getKISCOURSEIDForCourse(String courseName, String institutionName) {
        try {
            String query = """
                        SELECT k.KISCOURSEID
                        FROM KISCOURSE k
                        JOIN INSTITUTION i ON k.UKPRN = i.UKPRN
                        WHERE k.TITLE = ? AND i.LEGAL_NAME = ?
                    """;

            try (Connection conn = dbModel.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, courseName);
                stmt.setString(2, institutionName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("KISCOURSEID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseName; // Fallback to course name if KISCOURSEID not found
    }

    /**
     * Validates all form fields before invoice generation
     * Checks for required fields and proper formatting
     * 
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateFields() {
        boolean isValid = true;

        // Validate student name - allow letters, spaces, and common punctuation
        if (studentNameField.getText().isEmpty() || !studentNameField.getText().matches("[a-zA-Z\\s\\-',.()]*")) {
            studentFieldErr.setVisible(true);
            isValid = false;
        }

        // Validate institution
        if (institutionComboBox.getValue() == null) {
            institutionFieldErr.setVisible(true);
            isValid = false;
        }

        // Validate course
        if (courseComboBox.getValue() == null) {
            courseFieldErr.setVisible(true);
            isValid = false;
        }

        // Validate course fee
        if (courseFeeField.getText().isEmpty() || !courseFeeField.getText().matches("\\d*\\.?\\d*")) {
            feeFieldErr.setVisible(true);
            isValid = false;
        }

        // Validate date
        if (invoiceDatePicker.getValue() == null) {
            dateFieldErr.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Returns to the dashboard view showing all invoices
     */

    @FXML
    private void seeAllInvoices() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
        Stage dashboardStage = (Stage) studentNameField.getScene().getWindow();
        dashboardStage.setTitle("UMS Finance - Dashboard");
        dashboardStage.setScene(new Scene(loader.load(), 1200, 1000));
        dashboardStage.show();

        sportThirdRow.setVisible(false);
        foodThirdRow.setVisible(false);
    }

    // Helper class to store pairs of related UI components
    // Used for sports activities and food items where we need both
    // a selection field and a price field
    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
