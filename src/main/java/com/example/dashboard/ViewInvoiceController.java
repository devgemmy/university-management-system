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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

// The Controller for the invoice details view shows detailed information about a specific invoice including ourse details, sports activities, and food items
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
    @FXML
    private Text courseCostsTotalLabel;
    @FXML
    private Text sportCostsTotalLabel;
    @FXML
    private Text foodCostsTotalLabel;

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
    private TableColumn<CourseEntry, Void> courseDeleteColumn;

    @FXML
    private TableView<FoodEntry> foodDtsTable;
    @FXML
    private TableColumn<FoodEntry, Integer> foodIndexColumn;
    @FXML
    private TableColumn<FoodEntry, String> foodItemColumn;
    @FXML
    private TableColumn<FoodEntry, Double> foodPriceColumn;
    @FXML
    private TableColumn<FoodEntry, Void> foodDeleteColumn;

    @FXML
    private TableView<SportEntry> sportDtsTable;
    @FXML
    private TableColumn<SportEntry, Integer> sportIndexColumn;
    @FXML
    private TableColumn<SportEntry, String> sportActivityColumn;
    @FXML
    private TableColumn<SportEntry, Double> sportPriceColumn;
    @FXML
    private TableColumn<SportEntry, Void> sportDeleteColumn;

    @FXML
    private ComboBox<String> courseSelectionComboBox;
    @FXML
    private TextField courseFeeField;
    @FXML
    private Button addCourseButton;

    @FXML
    private ComboBox<String> foodSelectionComboBox;
    @FXML
    private TextField foodPriceField;
    @FXML
    private Button addFoodButton;

    @FXML
    private ComboBox<String> sportsSelectionComboBox;
    @FXML
    private TextField sportsPriceField;
    @FXML
    private Button addSportsButton;

    // Controllers and utilities
    private Invoice currentInvoice;
    private DatabaseModel dbModel;
    private NumberFormat currencyFormatter;
    private InvoiceService invoiceService;
    private Map<String, String> availableCourses;
    // private ObservableList<String> availableFoodItems;
    // private ObservableList<String> availableSportsActivities;

    @FXML
    public void initialize() {
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        invoiceService = InvoiceService.getInstance();
        setupDeleteButton();

        // Course table bindings
        courseIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        courseDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        courseFeesColumn.setCellValueFactory(cellData -> cellData.getValue().feeProperty().asObject());
        courseFeesColumn.setCellFactory(_ -> new TableCell<>() {
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
        setupCourseDeleteColumn();
        // Food table
        foodIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        foodItemColumn.setCellValueFactory(cellData -> cellData.getValue().foodNameProperty());
        foodPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        foodPriceColumn.setCellFactory(_ -> new TableCell<>() {
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
        setupFoodDeleteColumn();

        // Sports table
        sportIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        sportActivityColumn.setCellValueFactory(cellData -> cellData.getValue().activityNameProperty());
        sportPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        sportPriceColumn.setCellFactory(_ -> new TableCell<>() {
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
        setupSportDeleteColumn();

        // Initialize controllers
        dbModel = DatabaseModel.getInstance();
        availableCourses = new HashMap<>();

        // Add price validation for all fields
        // observable
        courseFeeField.textProperty().addListener((_, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                courseFeeField.setText(oldValue);
            }
        });

        // observable
        foodPriceField.textProperty().addListener((_, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                foodPriceField.setText(oldValue);
            }
        });

        sportsPriceField.textProperty().addListener((_, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                sportsPriceField.setText(oldValue);
            }
        });
    }

    // This method transforms invoice data into a format suitable for display and
    // creates a map with all necessary information for the UI
    private Map<String, Object> transformInvoiceForDisplay(Invoice invoice) {
        Map<String, Object> displayData = new HashMap<>();

        // Transform course details
        Map<String, Object> courseInfo = new HashMap<>();
        String courseName = invoice.getCourseList().get("courseName");
        // Debug log
        System.out.println("Course name from invoice: " + courseName);
        System.out.println("Course fee from invoice: " + invoice.getCourseInvFees());
        // courseInfo.put("name", invoice.getCourseList().get("courseName"));
        courseInfo.put("name", courseName);
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

        displayData.put("totalInvSportsCost", invoice.getTotalSportsCost());
        displayData.put("totalInvFoodCost", invoice.getTotalFoodCost());
        displayData.put("totalInvCourseFees", invoice.getCourseInvFees());

        return displayData;
    }

    // This method loads all invoice details into the UI components and pdates
    // labels, tables, and charts with invoice data
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

        // Debug log
        System.out.println(
                "Dynamic Invoice sport costs: " + currencyFormatter.format(displayData.get("totalInvSportsCost")));
        System.out.println(
                "Dynamic Invoice food costs: " + currencyFormatter.format(displayData.get("totalInvFoodCost")));
        System.out.println(
                "Dynamic Invoice course fees: " + currencyFormatter.format(displayData.get("totalInvCourseFees")));

        // Error-Handling
        if (sportCostsTotalLabel != null) {
            sportCostsTotalLabel.setText(currencyFormatter.format(displayData.get("totalInvSportsCost")));
        } else {
            System.err.println("sportsCostsTotalLabel is null!");
        }

        if (foodCostsTotalLabel != null) {
            foodCostsTotalLabel.setText(currencyFormatter.format(displayData.get("totalInvFoodCost")));
        } else {
            System.err.println("foodCostsTotalLabel is null!");
        }

        if (courseCostsTotalLabel != null) {
            courseCostsTotalLabel.setText(currencyFormatter.format(displayData.get("totalInvCourseFees")));
        } else {
            System.err.println("courseCostsTotalLabel is null!");
        }
    }

    // private double calculateTotalCourseFees() {
    // double total = 0.0;
    // for (CourseEntry entry : courseDtsTable.getItems()) {
    // total += entry.feeProperty().get();
    // }
    // return total;
    // }

    // This loads course details into the course table and shows course name and
    // associated fees
    @SuppressWarnings("unchecked")
    private void loadCourseDetails(Map<String, Object> displayData) {
        ObservableList<CourseEntry> courseEntries = FXCollections.observableArrayList();

        // Map<String, Object> courseInfo = (Map<String, Object>)
        // displayData.get("course");
        // if (courseInfo != null && courseInfo.get("name") != null) {
        // courseEntries.add(new CourseEntry(1, (String) courseInfo.get("name"),
        // (Double) courseInfo.get("fee")));
        // }

        try {
            Map<String, Object> courseInfo = (Map<String, Object>) displayData.get("course");
            if (courseInfo != null) {
                String coursesString = (String) courseInfo.get("name");
                System.out.println("Processing courses string: " + coursesString);

                if (coursesString != null && !coursesString.isEmpty()) {
                    // Split into individual courses if multiple exist
                    String[] courses = coursesString.contains(";") ? coursesString.split(";")
                            : new String[] { coursesString };

                    int index = 1;
                    double totalFees = 0.0;

                    for (String course : courses) {
                        course = course.trim();
                        String[] parts = course.split(",");

                        if (parts.length == 2) {
                            String courseName = parts[0].trim();
                            // Remove any amounts in brackets from the course name
                            courseName = courseName.replaceAll("\\s*\\([^)]*\\)", "").trim();
                            try {
                                double courseFee = Double.parseDouble(parts[1].trim());
                                courseEntries.add(new CourseEntry(index++, courseName, courseFee));
                                totalFees += courseFee;
                                System.out.println("Added course: " + courseName + " with fee: " + courseFee);
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing fee for course: " + course);
                            }
                        } else {
                            System.err.println("Invalid course format: " + course);
                        }
                    }

                    // Update total fees if needed
                    if (Math.abs(totalFees - currentInvoice.getCourseInvFees()) > 0.01) {
                        currentInvoice.setCourseInvFees(totalFees);
                        updateCourseFeeInDatabase(totalFees);
                    }
                } else {
                    System.out.println("No courses found for invoice: " + currentInvoice.getInvoiceID());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading course details: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load course details: " + e.getMessage());
        }

        courseDtsTable.setItems(courseEntries);
    }

    private void updateCourseFeeInDatabase(double totalFees) {
        try (Connection conn = dbModel.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE FINANCES SET courseInvFees = ? WHERE invoiceID = ?")) {
            stmt.setDouble(1, totalFees);
            stmt.setString(2, currentInvoice.getInvoiceID());
            stmt.executeUpdate();
            System.out.println("Updated course fees in database to: " + totalFees);
        } catch (SQLException e) {
            System.err.println("Failed to update course fees in database: " + e.getMessage());
            showError("Failed to update course fees: " + e.getMessage());
        }
    }

    // This method loads food items into the food table and shows each food item and
    // its cost
    @SuppressWarnings("unchecked")
    private void loadFoodItems(Map<String, Object> displayData) {
        ObservableList<FoodEntry> foodEntries = FXCollections.observableArrayList();
        int index = 1;
        Map<String, Double> foodItems = (Map<String, Double>) displayData.get("foodItems");
        for (Map.Entry<String, Double> entry : foodItems.entrySet()) {
            foodEntries.add(new FoodEntry(index++, entry.getKey(), entry.getValue()));
        }
        foodDtsTable.setItems(foodEntries);
    }

    // This functon loads sports activities into the sports table shows each
    // activity and its cost
    @SuppressWarnings("unchecked")
    private void loadSportsActivities(Map<String, Object> displayData) {
        ObservableList<SportEntry> sportEntries = FXCollections.observableArrayList();
        int index = 1;
        Map<String, Double> sportsActivities = (Map<String, Double>) displayData.get("sportsActivities");
        for (Map.Entry<String, Double> entry : sportsActivities.entrySet()) {
            sportEntries.add(new SportEntry(index++, entry.getKey(), entry.getValue()));
        }
        sportDtsTable.setItems(sportEntries);
    }

    // This function updates the pie chart showing cost distribution and shows
    // relative proportions of course fees, sports costs, and food costs
    private void updatePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Course Fees", currentInvoice.getCourseInvFees()),
                new PieChart.Data("Sports Activities", currentInvoice.getTotalSportsCost()),
                new PieChart.Data("Food Items", currentInvoice.getTotalFoodCost()));
        costDistributionChart.setData(pieChartData);

        // Apply consistent colors to each slice
        pieChartData.forEach(data -> {
            String color = switch (data.getName()) {
                case "Course Fees" -> "#007A7A"; // Green for Courses
                case "Sports Activities" -> "#FFA84A"; // Yellow for Sports
                case "Food Items" -> "#DE6600"; // Orange for Food
                default -> "#000000";
            };
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
    }

    // This calculates the total cost of the invoice and sums course fees, sports
    // costs, and food costs
    private double calculateTotalCosts() {
        return currentInvoice.getCourseInvFees() +
                currentInvoice.getTotalSportsCost() +
                currentInvoice.getTotalFoodCost();
    }

    // Returns to the dashboard view
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

    // This handles invoice deletion with confirmation dialog and returns to
    // dashboard if deletion is successful
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

    // Sets the invoice to be displayed and loads its details
    public void setInvoice(Invoice invoice) {
        this.currentInvoice = invoice;
        // Debug log
        System.out.println("Setting invoice: " + invoice.getInvoiceID());
        System.out.println("Institution Details: " + invoice.getInstitutionDetails());
        System.out.println("Institution ID: " + invoice.getInstitutionDetails().get("institutionID"));

        // Initialize combo boxes
        loadAvailableCourses();
        loadAvailableFoodItems();
        loadAvailableSportsActivities();

        // Load invoice details
        loadInvoiceDetails();

        if (courseSelectionComboBox != null) {
            courseSelectionComboBox.setPromptText("Select Course");
            courseSelectionComboBox.setVisibleRowCount(10);
        }
    }

    private void setupDeleteButton() {
        deleteInvoiceButton.setOnAction(_ -> deleteInvoice());
    }

    @FXML
    private void handleAddCourse() {
        String selectedCourse = courseSelectionComboBox.getValue();
        String feeText = courseFeeField.getText();

        if (selectedCourse == null || currentInvoice == null) {
            showError("Please select a course");
            return;
        }

        if (feeText == null || feeText.isEmpty()) {
            showError("Please enter the course fee");
            return;
        }

        try {
            double courseFee = Double.parseDouble(feeText);

            // Get the course name without the ID
            String courseName = selectedCourse;
            if (selectedCourse.contains(" (")) {
                courseName = selectedCourse.substring(0, selectedCourse.lastIndexOf(" ("));
            }

            // Get current course string and fees
            String currentCourses = currentInvoice.getCourseList().get("courseName");
            double currentFees = currentInvoice.getCourseInvFees();

            // Format new course entry
            String newCourseEntry = courseName + ", " + courseFee;
            String updatedCourses = (currentCourses == null || currentCourses.isEmpty())
                    ? newCourseEntry
                    : currentCourses + "; " + newCourseEntry;

            double newTotalFees = currentFees + courseFee;

            // Update database
            try (Connection conn = dbModel.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE FINANCES SET courseName = ?, courseInvFees = ? WHERE invoiceID = ?")) {
                stmt.setString(1, updatedCourses);
                stmt.setDouble(2, newTotalFees);
                stmt.setString(3, currentInvoice.getInvoiceID());
                stmt.executeUpdate();

                // Update invoice object
                Map<String, String> courseList = currentInvoice.getCourseList();
                courseList.put("courseName", updatedCourses);
                currentInvoice.setCourseList(courseList);
                currentInvoice.setCourseInvFees(newTotalFees);

                // Refresh the display
                loadInvoiceDetails();
                updatePieChart();

                // Clear input fields
                courseSelectionComboBox.setValue(null);
                courseFeeField.clear();

            } catch (SQLException e) {
                System.err.println("Error adding course: " + e.getMessage());
                showError("Failed to add course: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Invalid course fee format");
        }
    }

    @FXML
    private void handleAddFood() {
        String selectedFood = foodSelectionComboBox.getValue();
        String priceText = foodPriceField.getText();

        if (selectedFood == null || priceText == null || priceText.isEmpty()) {
            showError("Please select a food item and enter its price");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            // Update the database
            String updateQuery = "UPDATE FINANCES SET foodItems = ?, totalFoodCost = ? WHERE invoiceID = ?";
            try (Connection conn = dbModel.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                // Get current food items and update
                Map<String, Double> foodItems = new HashMap<>(currentInvoice.getFoodItems());
                foodItems.put(selectedFood, price);
                String foodItemsStr = convertFoodItemsToString(foodItems);
                double totalFoodCost = foodItems.values().stream().mapToDouble(Double::doubleValue).sum();
                // double totalFoodCost = 0;
                // for (double cost : foodItems.values()) {
                // totalFoodCost += cost;
                // }

                stmt.setString(1, foodItemsStr);
                stmt.setDouble(2, totalFoodCost);
                stmt.setString(3, currentInvoice.getInvoiceID());
                stmt.executeUpdate();

                // Update the current invoice object
                currentInvoice.setFoodItems(foodItems);
                // currentInvoice.setTotalFoodCost(totalFoodCost);

                // Refresh the display
                loadInvoiceDetails();
                updatePieChart();

                // Clear input fields
                foodSelectionComboBox.setValue(null);
                foodPriceField.clear();

            } catch (SQLException e) {
                showError("Failed to update food items: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Invalid price format");
        }
    }

    @FXML
    private void handleAddSport() {
        String selectedSport = sportsSelectionComboBox.getValue();
        String priceText = sportsPriceField.getText();

        if (selectedSport == null || priceText == null || priceText.isEmpty()) {
            showError("Please select a sport activity and enter its price");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            // Update the database
            String updateQuery = "UPDATE FINANCES SET sportsActivity = ?, totalSportsCost = ? WHERE invoiceID = ?";
            try (Connection conn = dbModel.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                // Get current sports activities and update
                Map<String, Double> sportsActivities = new HashMap<>(currentInvoice.getSportsActivities());
                sportsActivities.put(selectedSport, price);
                String sportsActivitiesStr = convertSportsActivitiesToString(sportsActivities);
                double totalSportsCost = sportsActivities.values().stream().mapToDouble(Double::doubleValue).sum();
                // double totalSportsCost = 0;
                // for (double cost : sportsActivities.values()) {
                // totalSportsCost += cost;
                // }

                stmt.setString(1, sportsActivitiesStr);
                stmt.setDouble(2, totalSportsCost);
                stmt.setString(3, currentInvoice.getInvoiceID());
                stmt.executeUpdate();

                // Update the current invoice object
                currentInvoice.setSportsActivities(sportsActivities);
                // currentInvoice.setTotalSportsCost(totalSportsCost);

                // Refresh the display
                loadInvoiceDetails();
                updatePieChart();

                // Clear input fields
                sportsSelectionComboBox.setValue(null);
                sportsPriceField.clear();

            } catch (SQLException e) {
                showError("Failed to update sports activities: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Invalid price format");
        }
    }

    private void loadAvailableCourses() {
        if (currentInvoice == null)
            return;

        // Get institution ID directly from the invoice
        String institutionId = currentInvoice.getInstitutionDetails().get("institutionID");
        System.out.println("Loading courses for institution ID: " + institutionId);

        if (institutionId == null || institutionId.isEmpty()) {
            System.err.println("No institution ID available - skipping course loading");
            return;
        }

        try {
            // Try to find courses using both UKPRN and PUBUKPRN
            String courseQuery = """
                        SELECT DISTINCT k.KISCOURSEID, k.TITLE
                        FROM KISCOURSE k
                        WHERE k.UKPRN = ? OR k.PUBUKPRN = ?
                        ORDER BY k.TITLE
                    """;

            try (Connection conn = dbModel.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(courseQuery)) {

                pstmt.setString(1, institutionId);
                pstmt.setString(2, institutionId);
                ResultSet rs = pstmt.executeQuery();

                availableCourses.clear();
                ObservableList<String> courseNames = FXCollections.observableArrayList();

                while (rs.next()) {
                    String courseId = rs.getString("KISCOURSEID");
                    String courseName = rs.getString("TITLE");
                    if (courseId != null && !courseId.isEmpty() && courseName != null && !courseName.isEmpty()) {
                        String displayName = courseName + " (" + courseId + ")";
                        availableCourses.put(displayName, courseId);
                        courseNames.add(displayName);
                    }
                }

                System.out.println("Found " + courseNames.size() + " courses for institution ID: " + institutionId);

                if (!courseNames.isEmpty()) {
                    courseSelectionComboBox.setItems(courseNames);
                    courseSelectionComboBox.setVisibleRowCount(Math.min(10, courseNames.size()));
                } else {
                    System.err.println("No valid courses found for institution ID: " + institutionId);
                    // Try to find the institution in the INSTITUTION table to verify it exists
                    String instQuery = "SELECT LEGAL_NAME FROM INSTITUTION WHERE UKPRN = ? OR PUBUKPRN = ?";
                    try (PreparedStatement instStmt = conn.prepareStatement(instQuery)) {
                        instStmt.setString(1, institutionId);
                        instStmt.setString(2, institutionId);
                        ResultSet instRs = instStmt.executeQuery();
                        if (instRs.next()) {
                            System.out.println(
                                    "Institution exists but has no courses: " + instRs.getString("LEGAL_NAME"));
                        } else {
                            System.err.println("Institution not found in database");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load available courses: " + e.getMessage());
            showError("Failed to load courses: Database error");
        }
    }

    private void loadAvailableFoodItems() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT [Food Item] FROM FOODS";
            conn = dbModel.getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            ObservableList<String> foodItems = FXCollections.observableArrayList();

            while (rs.next()) {
                String foodName = rs.getString("Food Item");
                foodItems.add(foodName);
            }

            System.out.println("Found " + foodItems.size() + " food items");
            foodSelectionComboBox.setItems(foodItems);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load available food items: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAvailableSportsActivities() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT [Sports Activities] FROM SPORTS";
            conn = dbModel.getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            ObservableList<String> sportsActivities = FXCollections.observableArrayList();

            while (rs.next()) {
                String sportName = rs.getString("Sports Activities");
                sportsActivities.add(sportName);
            }

            System.out.println("Found " + sportsActivities.size() + " sports activities");
            sportsSelectionComboBox.setItems(sportsActivities);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load available sports activities: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertFoodItemsToString(Map<String, Double> foodItems) {
        return foodItems.entrySet().stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                .collect(Collectors.joining("; "));
    }

    private String convertSportsActivitiesToString(Map<String, Double> sportsActivities) {
        return sportsActivities.entrySet().stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                .collect(Collectors.joining("; "));
    }

    private void setupCourseDeleteColumn() {
        courseDeleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                CourseEntry entry = getTableView().getItems().get(getIndex());
                // Disable delete button for the first course
                deleteButton.setDisable(entry.indexProperty().get() == 1);
                deleteButton.setStyle("-fx-font-size: 11px; -fx-text-fill: #FFF; -fx-background-color: #dc4067");

                deleteButton.setOnAction(_ -> {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Delete Course");
                    confirmation.setHeaderText("Delete Course");
                    confirmation.setContentText("Are you sure you want to delete this course?");

                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        CourseEntry courseEntry = getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(courseEntry);

                        // Update indices for remaining items
                        int newIndex = 1;
                        ObservableList<CourseEntry> remainingCourses = getTableView().getItems();
                        for (CourseEntry remainingEntry : remainingCourses) {
                            remainingEntry.indexProperty().set(newIndex++);
                        }

                        // Rebuild course string and update total fees
                        StringBuilder courseNameBuilder = new StringBuilder();
                        double totalFees = 0.0;
                        boolean first = true;

                        for (CourseEntry course : remainingCourses) {
                            if (!first) {
                                courseNameBuilder.append("; ");
                            }
                            courseNameBuilder.append(course.courseNameProperty().get())
                                    .append(", ")
                                    .append(course.feeProperty().get());
                            totalFees += course.feeProperty().get();
                            first = false;
                        }

                        // Update the invoice object
                        Map<String, String> courseList = currentInvoice.getCourseList();
                        courseList.put("courseName", courseNameBuilder.toString());
                        currentInvoice.setCourseList(courseList);
                        currentInvoice.setCourseInvFees(totalFees);

                        // Update database
                        try (Connection conn = dbModel.getConnection();
                                PreparedStatement stmt = conn.prepareStatement(
                                        "UPDATE FINANCES SET courseName = ?, courseInvFees = ? WHERE invoiceID = ?")) {
                            stmt.setString(1, courseNameBuilder.toString());
                            stmt.setDouble(2, totalFees);
                            stmt.setString(3, currentInvoice.getInvoiceID());
                            stmt.executeUpdate();

                            // Update UI
                            updatePieChart();
                            totalCostsLabel.setText("Total Costs: " + currencyFormatter.format(calculateTotalCosts()));

                        } catch (SQLException e) {
                            System.err.println("Error updating course deletion: " + e.getMessage());
                            showError("Failed to update course deletion: " + e.getMessage());
                        }
                    }
                });

                setGraphic(deleteButton);
            }
        });
    }

    private void setupFoodDeleteColumn() {
        foodDeleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                deleteButton.setStyle("-fx-font-size: 11px; -fx-text-fill: #FFF; -fx-background-color: #dc4067");

                deleteButton.setOnAction(_ -> {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Delete Food Item");
                    confirmation.setHeaderText("Delete Food Item");
                    confirmation.setContentText("Are you sure you want to delete this food item?");

                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        FoodEntry foodEntry = getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(foodEntry);

                        // Update indices for remaining items
                        int newIndex = 1;
                        for (FoodEntry remainingEntry : getTableView().getItems()) {
                            remainingEntry.indexProperty().set(newIndex++);
                        }

                        // Update the database and totals
                        Map<String, Double> foodItems = new HashMap<>();
                        for (FoodEntry entry : getTableView().getItems()) {
                            foodItems.put(entry.foodNameProperty().get(), entry.priceProperty().get());
                        }
                        currentInvoice.setFoodItems(foodItems);
                        invoiceService.updateInvoice(currentInvoice);
                        updatePieChart();
                        totalCostsLabel.setText("Total Costs: " + currencyFormatter.format(calculateTotalCosts()));
                    }
                });

                setGraphic(deleteButton);
            }
        });
    }

    private void setupSportDeleteColumn() {
        sportDeleteColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                deleteButton.setStyle("-fx-font-size: 11px; -fx-text-fill: #FFF; -fx-background-color: #dc4067");

                deleteButton.setOnAction(_ -> {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Delete Sport Activity");
                    confirmation.setHeaderText("Delete Sport Activity");
                    confirmation.setContentText("Are you sure you want to delete this sport activity?");

                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        SportEntry sportEntry = getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(sportEntry);

                        // Update indices for remaining items
                        int newIndex = 1;
                        for (SportEntry remainingEntry : getTableView().getItems()) {
                            remainingEntry.indexProperty().set(newIndex++);
                        }

                        // Update the database and totals
                        Map<String, Double> sportsActivities = new HashMap<>();
                        for (SportEntry entry : getTableView().getItems()) {
                            sportsActivities.put(entry.activityNameProperty().get(), entry.priceProperty().get());
                        }
                        currentInvoice.setSportsActivities(sportsActivities);
                        invoiceService.updateInvoice(currentInvoice);
                        updatePieChart();
                        totalCostsLabel.setText("Total Costs: " + currencyFormatter.format(calculateTotalCosts()));
                    }
                });

                setGraphic(deleteButton);
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Helper classes for displaying course, food and sport entries in the table
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
