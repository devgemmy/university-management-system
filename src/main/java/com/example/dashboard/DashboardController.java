package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class DashboardController {

    public CategoryAxis xAxis;
    public NumberAxis yAxis;
    @FXML private ComboBox<String> filterDropdown;
    @FXML private ComboBox<String> yearDropdown;

    @FXML private TextField searchField;
    @FXML private TableView<String> dataTable;
    @FXML private PieChart totalCostsChart;
    @FXML private BarChart<String, Number> averageCostsChart;
    @FXML private Label totalCostsLabel;
    @FXML private Button addInvoiceButton;

    private Connection connect() {
        // JDBC stands for Java Database Connector
        String driver = "jdbc:sqlite",
                db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project B/UMS-DB.db";
        // final String USER = "your_username";
        // final String PASSWORD = "your_password";
        final String DB_URL = driver + ":" + db;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected to SQL DB successfully.");

        return conn;
    }

    @FXML
    protected void initialize() {
        // Dropdown filter values
        filterDropdown.setItems(FXCollections.observableArrayList("All records", "By Month", "By Year"));
        yearDropdown.setItems(FXCollections.observableArrayList("2024", "2023", "2022", "2021", "2020"));

        // Initialize table view with sample data
        ObservableList<String> sampleData = FXCollections.observableArrayList("Sample Data");
        dataTable.setItems(sampleData);

        loadTableData();
        loadPieChartData();
        loadBarChartData();

        // Set event listeners
        addInvoiceButton.setOnAction(event -> generateInvoice());
    }

    private void loadTableData() {
        // ObservableList<String> data = FXCollections.observableArrayList();
        String searchQuery = "SELECT * FROM FINANCE where KISCOURSEID LIKE '%" + searchField.getText() + "%'";

        try (
            Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(searchQuery);
        ) {
            String invIDgen = "INV" + System.currentTimeMillis();
            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                String invoiceID = results.getString("invoiceID");
                String studentName = results.getString("studentName");

                Date invoiceDate = results.getDate("invoiceDate");
//                   courseID TEXT,
//                   courseName TEXT,
//                   courseInvFees INT,
//                   institutionID TEXT,
//                   institutionName TEXT,



                double courseInvFees = results.getDouble("courseInvFees");
                String sportsActivity = results.getString("sportsActivity");
                String foodItems = results.getString("foodItems");


                // Parse sports and food costs
                double totalSportsCost = getFoodOrSportsCosts(sportsActivity);
                double totalFoodCost = getFoodOrSportsCosts(foodItems);

                // Insert into FINANCE table
                String insertQuery = "INSERT INTO FINANCE (studentName, courseCost, sportsCost, foodCost, invoiceDate) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, studentName);
                    insertStmt.setDouble(2, courseInvFees);
                    insertStmt.setDouble(3, totalSportsCost);
                    insertStmt.setDouble(4, totalFoodCost);
                    insertStmt.setDate(5, invoiceDate);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Data successfully inserted into FINANCE table.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // dataTable.setItems(data);
    }

    private void loadPieChartData() {
        // Total costs PieChart with sample data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Courses", 535000),
                new PieChart.Data("Food", 35000),
                new PieChart.Data("Sports", 115000)
        );
        totalCostsChart.setData(pieData);
    }

    private void loadBarChartData() {
        XYChart.Series<String, Number> series2022 = new XYChart.Series<>();
        series2022.setName("2022");
        series2022.getData().add(new XYChart.Data<>("Jan", 15000));
        series2022.getData().add(new XYChart.Data<>("Feb", 18000));
        series2022.getData().add(new XYChart.Data<>("Mar", 20000));
        series2022.getData().add(new XYChart.Data<>("Apr", 25000));

        averageCostsChart.getData().add(series2022);
    }

    private void generateInvoice() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText(null);
        alert.setContentText("Add Invoice functionality will be implemented here.");
        alert.showAndWait();
    }

    // Method to parse and sum costs from a given string
    private static double getFoodOrSportsCosts(String costStr) {
        ArrayList<Double> costs = new ArrayList<>();
        double total = 0.0;

        if (costStr != null && !costStr.isEmpty()) {
            String[] items = costStr.split(";");
            for (String item : items) {
                String[] parts = item.split("\\(");
                if (parts.length == 2) {
                    try {
                        double cost = Double.parseDouble(parts[1].replace(")", ""));
                        costs.add(cost);
                        total += cost;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing cost in: " + item);
                    }
                }
            }
        }

        // Print ArrayList and total cost for debugging
        System.out.println("Parsed Costs: " + costs);
        System.out.println("Total Cost: " + total);

        return total;
    }
}