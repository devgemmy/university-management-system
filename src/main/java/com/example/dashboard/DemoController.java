package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;

public class DemoController {
    @FXML private ComboBox<String> filterDropdown;
    @FXML private ComboBox<String> yearDropdown;

    @FXML private TextField searchField;
    @FXML private TableView<String> dataTable;
    @FXML private PieChart totalCostsChart;
    @FXML private BarChart<String, Number> averageCostsChart;
    @FXML private Label totalCostsLabel;
    @FXML private Button addInvoiceButton;

    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private Connection connect() {
        // JDBC stands for Java Database Connector
        String driver = "jdbc:sqlite", db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project B/UMS-DB.db";
        final String DB_URL = driver + ":" + db;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQL DB successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database failed to connect.");
        }

        return conn;
    }

    @FXML
    protected void initialize() throws SQLException {
        // Dropdown filter values
        filterDropdown.setItems(FXCollections.observableArrayList("All records", "By Month", "By Year"));
        yearDropdown.setItems(FXCollections.observableArrayList("2024", "2023", "2022", "2021", "2020"));

        // Initialize table view with sample data
        ObservableList<String> initialInvoiceData = FXCollections.observableArrayList("Loading Invoice Data ...");
        dataTable.setItems(initialInvoiceData);

        loadTableData();
        // loadPieChartData();
        // loadBarChartData();

        // Set event listeners
        // addInvoiceButton.setOnAction(event -> generateInvoice());
    }

    private void loadTableData() throws SQLException {
        ObservableList<String> tableData = FXCollections.observableArrayList();
        String searchQuery = "SELECT * FROM KISCOURSE where KISCOURSE.TITLE LIKE '%" + searchField.getText() + "%'";
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(searchQuery);
        ResultSet results = stmt.executeQuery();

        if (results.next()) {
            String invIDgen = "INV" + System.currentTimeMillis();
            String studentName = results.getString("studentName");
            Date invoiceDate = results.getDate("invoiceDate");
            // courseID TEXT,
            // courseName TEXT,
            // courseInvFees INT,
            // institutionID TEXT,
            // institutionName TEXT,

            double courseInvFees = results.getDouble("courseInvFees");
            String sportsActivity = results.getString("sportsActivity");
            String foodItems = results.getString("foodItems");


            // Parse sports and food costs
            double totalSportsCost = getFoodOrSportsCosts(sportsActivity);
            double totalFoodCost = getFoodOrSportsCosts(foodItems);

            // Insert into FINANCES table
            String insertQuery = "INSERT INTO FINANCES (studentName, courseInvFees, totalSportsCost, totalFoodCost, invoiceDate) VALUES (?, ?, ?, ?, ?)";
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

        dataTable.setItems(tableData);
    }

//    private void loadPieChartData() {
//        // Total costs PieChart with sample data
//        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
//                new PieChart.Data("Courses", 535000),
//                new PieChart.Data("Food", 35000),
//                new PieChart.Data("Sports", 115000)
//        );
//        totalCostsChart.setData(pieData);
//    }

//    private void loadBarChartData() {
//
//        series2022.setName("2022");
//        series2022.getData().add(new XYChart.Data<>("Jan", 15000));
//        series2022.getData().add(new XYChart.Data<>("Feb", 18000));
//        series2022.getData().add(new XYChart.Data<>("Mar", 20000));
//        series2022.getData().add(new XYChart.Data<>("Apr", 25000));
//
//        averageCostsChart.getData().add(series2022);
//    }

//    private void generateInvoice() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Invoice");
//        alert.setHeaderText(null);
//        alert.setContentText("Add Invoice functionality will be implemented here.");
//        alert.showAndWait();
//    }

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

//        HashMap<String, String> courseDetails = new HashMap<String, String>();
//        courseDetails.put("NC1600", "Business Computing");
//        HashMap<String, Double> sportsDetails = new HashMap<String, Double>();
//        sportsDetails.put("Basket Ball", 12.9);
//        sportsDetails.put("Table Tennis", 3.45);
//        HashMap<String, Double> foodDetails = new HashMap<String, Double>();
//        foodDetails.put("Cheese Pizza", 4.75);
//        foodDetails.put("Indian Biryani", 1.40);
//        HashMap<String, String> institutionDetails = new HashMap<String, String>();
//        institutionDetails.put("NC1600", "Business Computing");
        /*
        courseDetails,
        institutionDetails,
        sportsDetails,
        foodDetails
        */
