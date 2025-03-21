package com.example.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * Controller for the invoice details view
 * Shows detailed information about a specific invoice including
 * course details, sports activities, and food items
 */
public class ViewInvoiceController {
    // UI Components for displaying invoice information
    @FXML
    private Text invoiceIdLabel;
    @FXML
    private Text studentNameLabel;
    @FXML
    private Text institutionLabel;
    @FXML
    private Text totalCostsLabel;
    @FXML
    private Label invoiceDateLabel;
    @FXML
    private Button deleteInvoiceButton;
    @FXML
    private Button backToDashboardButton;
    @FXML
    private PieChart costDistributionChart;

    // Tables for displaying different cost categories
    @FXML
    private TableView<CourseEntry> courseDtsTable;
    @FXML
    private TableColumn<CourseEntry, Integer> courseIndexColumn;
    @FXML
    private TableColumn<CourseEntry, String> courseDetailsColumn;
    @FXML
    private TableColumn<CourseEntry, Double> courseFeesColumn;

    @FXML
    private TableView<FoodEntry> courseDtsTable1;
    @FXML
    private TableColumn<FoodEntry, Integer> foodIndexColumn;
    @FXML
    private TableColumn<FoodEntry, String> foodItemColumn;
    @FXML
    private TableColumn<FoodEntry, Double> foodPriceColumn;

    @FXML
    private TableView<SportEntry> courseDtsTable2;
    @FXML
    private TableColumn<SportEntry, Integer> sportIndexColumn;
    @FXML
    private TableColumn<SportEntry, String> sportActivityColumn;
    @FXML
    private TableColumn<SportEntry, Double> sportPriceColumn;

    // Controllers and utilities
    private Invoice currentInvoice;
    private DatabaseModel databaseModel;
    private NumberFormat currencyFormatter;
    private InvoiceService invoiceService;

    @FXML
    public void initialize() {
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        databaseModel = DatabaseModel.getInstance();
        invoiceService = InvoiceService.getInstance();
        setupDeleteButton();

        // Course table bindings
        courseIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        courseDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        courseFeesColumn.setCellValueFactory(cellData -> cellData.getValue().feeProperty().asObject());
        courseFeesColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });

        // Food table
        foodIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        foodItemColumn.setCellValueFactory(cellData -> cellData.getValue().foodNameProperty());
        foodPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        foodPriceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });

        // Sports table
        sportIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        sportActivityColumn.setCellValueFactory(cellData -> cellData.getValue().activityNameProperty());
        sportPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        sportPriceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });
    }

    /**
     * Transforms invoice data into a format suitable for display
     * Creates a map with all necessary information for the UI
     */
    private Map<String, Object> transformInvoiceForDisplay(Invoice invoice) {
        Map<String, Object> displayData = new HashMap<>();

        // Transform course details
        Map<String, Object> courseInfo = new HashMap<>();
        courseInfo.put("name", invoice.getCourseList().get("courseName"));
        courseInfo.put("fee", invoice.getCourseInvFees());
        displayData.put("course", courseInfo);

        // Transform sports activities (already in correct format)
        displayData.put("sportsActivities", invoice.getSportsActivities());

        // Transform food items (already in correct format)
        displayData.put("foodItems", invoice.getFoodItems());

        // Basic invoice info
        displayData.put("invoiceId", invoice.getInvoiceID());
        displayData.put("studentName", invoice.getStudentName());
        displayData.put("institutionName", invoice.getInstitutionDetails().get("institutionName"));
        displayData.put("invoiceDate", invoice.getInvoiceDate());
        displayData.put("totalCosts", calculateTotalCosts());

        return displayData;
    }

    /**
     * Loads all invoice details into the UI components
     * Updates labels, tables, and charts with invoice data
     */
    public void loadInvoiceDetails() {
        if (currentInvoice == null)
            return;

        Map<String, Object> displayData = transformInvoiceForDisplay(currentInvoice);

        // Update UI with transformed data
        invoiceIdLabel.setText("Invoice ID: " + displayData.get("invoiceId"));
        studentNameLabel.setText((String) displayData.get("studentName"));
        institutionLabel.setText((String) displayData.get("institutionName"));
        totalCostsLabel.setText("Total Costs: " + currencyFormatter.format(displayData.get("totalCosts")));
        invoiceDateLabel.setText((String) displayData.get("invoiceDate"));

        loadCourseDetails(displayData);
        loadFoodItems(displayData);
        loadSportsActivities(displayData);
        updatePieChart();
    }

    /**
     * Loads course details into the course table
     * Shows course name and associated fees
     */
    private void loadCourseDetails(Map<String, Object> displayData) {
        ObservableList<CourseEntry> courseEntries = FXCollections.observableArrayList();
        Map<String, Object> courseInfo = (Map<String, Object>) displayData.get("course");
        if (courseInfo != null && courseInfo.get("name") != null) {
            courseEntries.add(new CourseEntry(1, (String) courseInfo.get("name"), (Double) courseInfo.get("fee")));
        }
        courseDtsTable.setItems(courseEntries);
    }

    /**
     * Loads food items into the food table
     * Shows each food item and its cost
     */
    private void loadFoodItems(Map<String, Object> displayData) {
        ObservableList<FoodEntry> foodEntries = FXCollections.observableArrayList();
        int index = 1;
        Map<String, Double> foodItems = (Map<String, Double>) displayData.get("foodItems");
        for (Map.Entry<String, Double> entry : foodItems.entrySet()) {
            foodEntries.add(new FoodEntry(index++, entry.getKey(), entry.getValue()));
        }
        courseDtsTable1.setItems(foodEntries);
    }

    /**
     * Loads sports activities into the sports table
     * Shows each activity and its cost
     */
    private void loadSportsActivities(Map<String, Object> displayData) {
        ObservableList<SportEntry> sportEntries = FXCollections.observableArrayList();
        int index = 1;
        Map<String, Double> sportsActivities = (Map<String, Double>) displayData.get("sportsActivities");
        for (Map.Entry<String, Double> entry : sportsActivities.entrySet()) {
            sportEntries.add(new SportEntry(index++, entry.getKey(), entry.getValue()));
        }
        courseDtsTable2.setItems(sportEntries);
    }

    /**
     * Updates the pie chart showing cost distribution
     * Shows relative proportions of course fees, sports costs, and food costs
     */
    private void updatePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Course Fees", currentInvoice.getCourseInvFees()),
                new PieChart.Data("Sports Activities", currentInvoice.getTotalSportsCost()),
                new PieChart.Data("Food Items", currentInvoice.getTotalFoodCost()));
        costDistributionChart.setData(pieChartData);
    }

    /**
     * Calculates the total cost of the invoice
     * Sums course fees, sports costs, and food costs
     */
    private double calculateTotalCosts() {
        return currentInvoice.getCourseInvFees() +
                currentInvoice.getTotalSportsCost() +
                currentInvoice.getTotalFoodCost();
    }

    /**
     * Returns to the dashboard view
     */
    @FXML
    private void backToDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage dashboardStage = (Stage) backToDashboardButton.getScene().getWindow();
        dashboardStage.setTitle("UMS Finance - Dashboard");
        dashboardStage.setScene(scene);
        dashboardStage.show();

        // Stage closingStage = (Stage) backToDashboardButton.getScene().getWindow();
        // closingStage.close();
    }

    /**
     * Handles invoice deletion with confirmation dialog
     * Returns to dashboard if deletion is successful
     */
    @FXML
    private void deleteInvoice() {
        if (currentInvoice == null)
            return;

        boolean deleted = invoiceService.deleteInvoiceWithConfirmation(currentInvoice);
        if (deleted) {
            try {
                backToDashboard();
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "Failed to return to dashboard: " + e.getMessage()).showAndWait();
            }
        }
    }

    /**
     * Sets the invoice to be displayed and loads its details
     */
    public void setInvoice(Invoice invoice) {
        this.currentInvoice = invoice;
        loadInvoiceDetails();
    }

    /**
     * Sets up the delete button functionality
     */
    private void setupDeleteButton() {
        deleteInvoiceButton.setOnAction(event -> deleteInvoice());
    }
}

/**
 * Helper class for displaying course entries in the table
 */
class CourseEntry {
    private final javafx.beans.property.IntegerProperty index;
    private final javafx.beans.property.StringProperty courseName;
    private final javafx.beans.property.DoubleProperty fee;

    public CourseEntry(int index, String courseName, double fee) {
        this.index = new javafx.beans.property.SimpleIntegerProperty(index);
        this.courseName = new javafx.beans.property.SimpleStringProperty(courseName);
        this.fee = new javafx.beans.property.SimpleDoubleProperty(fee);
    }

    public javafx.beans.property.IntegerProperty indexProperty() {
        return index;
    }

    public javafx.beans.property.StringProperty courseNameProperty() {
        return courseName;
    }

    public javafx.beans.property.DoubleProperty feeProperty() {
        return fee;
    }
}

class FoodEntry {
    private final javafx.beans.property.IntegerProperty index;
    private final javafx.beans.property.StringProperty foodName;
    private final javafx.beans.property.DoubleProperty price;

    public FoodEntry(int index, String foodName, double price) {
        this.index = new javafx.beans.property.SimpleIntegerProperty(index);
        this.foodName = new javafx.beans.property.SimpleStringProperty(foodName);
        this.price = new javafx.beans.property.SimpleDoubleProperty(price);
    }

    public javafx.beans.property.IntegerProperty indexProperty() {
        return index;
    }

    public javafx.beans.property.StringProperty foodNameProperty() {
        return foodName;
    }

    public javafx.beans.property.DoubleProperty priceProperty() {
        return price;
    }
}

class SportEntry {
    private final javafx.beans.property.IntegerProperty index;
    private final javafx.beans.property.StringProperty activityName;
    private final javafx.beans.property.DoubleProperty price;

    public SportEntry(int index, String activityName, double price) {
        this.index = new javafx.beans.property.SimpleIntegerProperty(index);
        this.activityName = new javafx.beans.property.SimpleStringProperty(activityName);
        this.price = new javafx.beans.property.SimpleDoubleProperty(price);
    }

    public javafx.beans.property.IntegerProperty indexProperty() {
        return index;
    }

    public javafx.beans.property.StringProperty activityNameProperty() {
        return activityName;
    }

    public javafx.beans.property.DoubleProperty priceProperty() {
        return price;
    }
}
