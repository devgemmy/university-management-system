package com.example.dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Button logOutButton;
    @FXML
    private ComboBox<String> timeFilter, monthPeriodDropdown, yearPeriodDropdown, uniAverageComboBox;
    @FXML
    private TextField searchField;

    @FXML
    private Label costLabel, titlePerSelect, totalFeesPerSelect, totalSportsPerSelect, totalFoodPerSelect,
            totalCosterSelect, viewInvoiceBtn, deleteInvoiceBtn;
    @FXML
    private PieChart totalCostsChart;
    @FXML
    private BarChart<String, Integer> averageUniCostsBarChart;

    @FXML
    private ComboBox<String> categoryComboBox, orderByComboBox;
    @FXML
    private Button addInvoiceButton;
    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, String> invoiceID, studentName, institutionDetails, courseList, courseInvFees,
            totalSportsCost, totalFoodCost, invoiceDate;

    // Load total costs across all Universities
    @FXML
    private void loadTotalCostsChart() {

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Courses", 535000),
                new PieChart.Data("Food", 35000),
                new PieChart.Data("Sports", 115000));

        totalCostsChart.setData(pieData);
        totalCostsChart.setTitle("Total Costs in 2022 for Business");
        totalCostsChart.setClockwise(false);
        totalCostsChart.setLegendSide(Side.BOTTOM);

        costLabel.setTextFill(Color.BLACK);
        costLabel.getStyleClass().add("cost-label");
        costLabel.setText("£" + String.valueOf(totalCostsChart.getData().getFirst().getPieValue()));

        for (final PieChart.Data data : totalCostsChart.getData()) {
            data.getNode().addEventHandler(
                    MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent ev) {
                            // System.out.println("PieChart clicked");
                            costLabel.setTranslateX(ev.getSceneX() - costLabel.getLayoutX());
                            costLabel.setTranslateY(ev.getSceneY() - costLabel.getLayoutY());
                            costLabel.setText("£" + String.valueOf(data.getPieValue()));
                        }
                    });
        }
    }

    // Load Average costs for each category in a selected University in a Bar Chart
    @FXML
    private void loadAverageCostsChart() {
        // Create Series instance with data types
        XYChart.Series<String, Integer> courseCosts = new XYChart.Series<>();
        courseCosts.setName("Courses"); // This is one of the Legends
        // XYChart.Data(xAxis, yAxis);
        courseCosts.getData().add(new XYChart.Data<>("2020", 25601));
        courseCosts.getData().add(new XYChart.Data<>("2021", 20148));
        courseCosts.getData().add(new XYChart.Data<>("2022", 12500));
        courseCosts.getData().add(new XYChart.Data<>("2023", 122450));
        courseCosts.getData().add(new XYChart.Data<>("2024", 54550));

        XYChart.Series<String, Integer> foodCosts = new XYChart.Series<>();
        foodCosts.setName("Food");
        foodCosts.getData().add(new XYChart.Data<>("2020", 9560));
        foodCosts.getData().add(new XYChart.Data<>("2021", 8148));
        foodCosts.getData().add(new XYChart.Data<>("2022", 4200));
        foodCosts.getData().add(new XYChart.Data<>("2023", 7480));
        foodCosts.getData().add(new XYChart.Data<>("2024", 2950));

        XYChart.Series<String, Integer> sportsCosts = new XYChart.Series<>();
        sportsCosts.setName("Sports");
        sportsCosts.getData().add(new XYChart.Data<>("2020", 4560));
        sportsCosts.getData().add(new XYChart.Data<>("2021", 8148));
        sportsCosts.getData().add(new XYChart.Data<>("2022", 1200));
        sportsCosts.getData().add(new XYChart.Data<>("2023", 3450));
        sportsCosts.getData().add(new XYChart.Data<>("2024", 9250));

        // Add the series in the Bar Chart;
        averageUniCostsBarChart.getData().add(courseCosts);
        averageUniCostsBarChart.getData().add(foodCosts);
        averageUniCostsBarChart.getData().add(sportsCosts);
        averageUniCostsBarChart.setTitle("Average Costs Per University in March");
        averageUniCostsBarChart.setLegendSide(Side.TOP);
    }

    // Load Invoices data based on sort or search
    @FXML
    private void loadTableData() {
        // String tableQuery = "SELECT * FROM KISCOURSE where KISCOURSE.TITLE LIKE '%" +
        // searchField.getText() + "%'";
        // ResultSet tableData = queryTheDB(tableQuery);

        // ERROR FIX: Reassigning invoiceTable to a new TableView is wrong.
        // The TableView is already defined in your FXML.
        // Therefore, I remove ERR_LINE 1 as it prevents invoiceTable from being null
        // since it is already injected via @FXML.
        // --- ERR_LINE 1: invoiceTable = new TableView<>();

        // SOME BIG DUMMY DATA
        HashMap<String, Double> sportsDetails1 = new HashMap<String, Double>();
        sportsDetails1.put("Basket Ball", 12.9);
        sportsDetails1.put("Mountain Climbing", 9.8);
        HashMap<String, Double> sportsDetails2 = new HashMap<String, Double>();
        sportsDetails2.put("Basket Ball", 12.9);
        sportsDetails2.put("Table Tennis", 3.45);
        HashMap<String, Double> sportsDetails3 = new HashMap<String, Double>();
        sportsDetails3.put("Basket Ball", 12.9);
        HashMap<String, Double> sportsDetails4 = new HashMap<String, Double>();
        sportsDetails4.put("Hand Ball", 14.9);

        HashMap<String, Double> foodDetails1 = new HashMap<String, Double>();
        foodDetails1.put("Cheese Pizza", 4.75);
        foodDetails1.put("Indian Biryani", 1.40);
        HashMap<String, Double> foodDetails2 = new HashMap<String, Double>();
        foodDetails2.put("Sausage Pizza", 4.75);
        HashMap<String, Double> foodDetails3 = new HashMap<String, Double>();
        foodDetails3.put("Cheese Pizza", 4.75);
        foodDetails3.put("Fried Rice", 5.40);
        HashMap<String, Double> foodDetails4 = new HashMap<String, Double>();

        HashMap<String, String> courseDetails1 = new HashMap<String, String>();
        courseDetails1.put("NC1600", "Business Computing");
        HashMap<String, String> courseDetails2 = new HashMap<String, String>();
        courseDetails2.put("MEC1970", "International Relations");
        HashMap<String, String> courseDetails3 = new HashMap<String, String>();
        courseDetails3.put("ARD3450", "Data Analytics");
        HashMap<String, String> courseDetails4 = new HashMap<String, String>();
        courseDetails4.put("TY5767", "Data Administration");

        HashMap<String, String> institutionDetails1 = new HashMap<String, String>();
        institutionDetails1.put("10009785", "Imperial College");
        HashMap<String, String> institutionDetails2 = new HashMap<String, String>();
        institutionDetails2.put("10003534", "Kingston University");
        HashMap<String, String> institutionDetails3 = new HashMap<String, String>();
        institutionDetails3.put("10005675", "Coventry University");
        HashMap<String, String> institutionDetails4 = new HashMap<String, String>();
        institutionDetails4.put("10001270", "Sussex University");

        final ObservableList<Invoice> invoiceTableData = FXCollections.observableArrayList();
        invoiceTableData.add(new Invoice("1", "Jatinder Alma", "12/08/2023", 21800.0, 32.8, 176.43, courseDetails1,
                institutionDetails1, sportsDetails1, foodDetails1));
        invoiceTableData.add(new Invoice("2", "Ahmad Rosser", "28/01/2022", 19800.0, 82.7, 253.12, courseDetails2,
                institutionDetails2, sportsDetails2, foodDetails2));
        invoiceTableData.add(new Invoice("3", "Corey Stanton", "24/03/2020", 21750.0, 45.1, 120.50, courseDetails3,
                institutionDetails3, sportsDetails3, foodDetails3));
        invoiceTableData.add(new Invoice("4", "Justin Tanaka", "17/09/2022", 5189.0, 124.5, 317.87, courseDetails4,
                institutionDetails4, sportsDetails4, foodDetails4));

        invoiceTable.setItems(invoiceTableData);
        invoiceTable.setEditable(false);

        invoiceID.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        institutionDetails.setCellValueFactory(institutionData -> {
            HashMap<String, String> institutionCatalog = institutionData.getValue().getInstitutionDetails();
            String formattedInstitution = institutionCatalog.entrySet().stream()
                    .map(entry -> entry.getValue() + " (" + entry.getKey() + ")")
                    .findFirst()
                    .orElse(""); // Handling cases without an ID
            return new SimpleStringProperty(formattedInstitution);
        });
        courseList.setCellValueFactory(courseData -> {
            HashMap<String, String> courseCatalog = courseData.getValue().getCourseList();
            String formattedCourse = courseCatalog.entrySet().stream()
                    .map(entry -> entry.getValue() + " (" + entry.getKey() + ")")
                    .findFirst()
                    .orElse("");
            return new SimpleStringProperty(formattedCourse);
        });
        courseInvFees.setCellValueFactory(feesData -> {
            double cFee = feesData.getValue().getCourseInvFees();
            return new SimpleStringProperty(String.format("£%.2f", cFee));
        });

        totalSportsCost.setCellValueFactory(sportData -> {
            double sCost = sportData.getValue().getTotalSportsCost(); // Get the sports cost
            int sportCount = getInvItemsCount(sportData.getValue().getSportsActivityList()); // Method to determine the
                                                                                             // count of sports
            return new SimpleStringProperty(String.format("£%.2f (%d Sports)", sCost, sportCount));
        });
        totalFoodCost.setCellValueFactory(foodData -> {
            double fCost = foodData.getValue().getTotalFoodCost();
            int foodCount = getInvItemsCount(foodData.getValue().getFoodItemsList());
            return new SimpleStringProperty(String.format("£%.2f (%d Food)", fCost, foodCount));
        });

        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

        invoiceTable.setRowFactory(_ -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Invoice rowData = row.getItem();
                    System.out.println("Double click on: " + rowData.getStudentName());
                }
            });
            return row;
        });
    }

    // Alert the user before closing the application
    private void closeApplication() {
        Alert closeAlert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Clicking OK would close this application",
                ButtonType.OK,
                ButtonType.CANCEL);
        closeAlert.setTitle("Close UMS Finance Dashboard");
        closeAlert.showAndWait();

        // If Ok is clicked -> Close the app
        if (closeAlert.getResult() == ButtonType.OK) {
            Stage closingAppStage = (Stage) logOutButton.getScene().getWindow();
            closingAppStage.close();
        }
    }

    @FXML
    public void generateNewInvoice() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                DashboardApp.class.getResource("generate-invoice-view.fxml"));
        Stage newInvoiceStage = new Stage(); // Create a new stage for the second scene
        newInvoiceStage.setScene(new Scene(fxmlLoader.load(), 1200, 1000)); // Set the scene for the new stage
        newInvoiceStage.setTitle("UMS - Generate New Invoice");
        newInvoiceStage.setResizable(false);
        newInvoiceStage.show();
    }

    // Setup Connection to the SQLite Database
    private Connection connectToDatabase() {
        // JDBC stands for Java Database Connector
        String driver = "jdbc:sqlite", db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project B/UMS-DB.db";
        final String DB_URL = driver + ":" + db;

        try (Connection conn = DriverManager.getConnection(DB_URL);) {
            System.out.println("Connected to SQL DB successfully.");
            return conn;
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            System.out.println("Database failed to connect.");
            return null;
        }

    }

    private int getInvItemsCount(HashMap<String, Double> getTotalCosts) {
        return getTotalCosts.size();
    }

    // Template function for each SQL query
    public ResultSet queryTheDB(String query, String param) throws SQLException {
        Connection conn = connectToDatabase();
        PreparedStatement sqlStatement = conn.prepareStatement(query);
        sqlStatement.setString(1, param); // Avoids SQL injection
        return sqlStatement.executeQuery();
    }

    public void sortDataByCategory(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        String selectedOrder = orderByComboBox.getValue();
        System.out.println("Selected Order: " + selectedOrder + ", Selected Category:" + selectedCategory);

        // String sortQuery = "SELECT * FROM INVOICE ORDER BY " + selectedCategory + " "
        // + selectedOrder;
        // ResultSet sortedData = queryTheDB(sortQuery);
    }

    public void setTimeFilter(ActionEvent event) {
        String selectedPeriod = timeFilter.getValue();
        if (selectedPeriod.equals("By Month")) {
            monthPeriodDropdown.setVisible(true);
            yearPeriodDropdown.setVisible(false);
        } else if (selectedPeriod.equals("By Year")) {
            monthPeriodDropdown.setVisible(false);
            yearPeriodDropdown.setVisible(true);
        } else {
            monthPeriodDropdown.setVisible(false);
            yearPeriodDropdown.setVisible(false);
        }
    }

    public void setSortingPeriod() {
        // Filter the invoices by month
        // String monthQuery = "SELECT * FROM INVOICE WHERE INVOICE_DATE LIKE '%" +
        // searchField.getText() + "%'";
        // ResultSet monthData = queryTheDB(monthQuery);
    }

    public void viewInvoice() throws IOException {
        // View the invoice data
        // String viewQuery = "SELECT * FROM INVOICE WHERE INVOICE_ID = ?";
        // ResultSet viewData = queryTheDB(viewQuery, "1");

        // Open a new window to view the invoice
        FXMLLoader viewInvFXMLLoader = new FXMLLoader(
                DashboardApp.class.getResource("invoice-details-view.fxml"));
        Stage newInvoiceStage = new Stage();
        newInvoiceStage.setScene(new Scene(viewInvFXMLLoader.load(), 1200, 1000)); // Set the scene for the new stage
        newInvoiceStage.setTitle("UMS - Generate New Invoice");
        newInvoiceStage.setResizable(false);
        newInvoiceStage.show();
    }

    public void deleteInvoice() {
        // Delete the invoice data
        // String deleteQuery = "DELETE FROM INVOICE WHERE INVOICE_ID = ?";
        // queryTheDB(deleteQuery, "1");

        System.out.println("Delete Invoice Clicked");
    }

    // Initialise Data and required functions.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTotalCostsChart();
        loadAverageCostsChart();
        loadTableData();

        ObservableList<String> categories = FXCollections.observableArrayList("Courses", "Sports", "Food", "Date");
        categoryComboBox.setItems(categories);

        ObservableList<String> orderBy = FXCollections.observableArrayList("ASC", "DESC");
        orderByComboBox.setItems(orderBy);

        ObservableList<String> timeFilters = FXCollections.observableArrayList("All Records", "By Month", "By Year");
        timeFilter.setItems(timeFilters);

        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
                "November", "December");
        monthPeriodDropdown.setItems(months);

        ArrayList<String> yearsInInvoices = new ArrayList<String>();
        for (Invoice invoice : invoiceTable.getItems()) {
            String invoiceYear = invoice.getInvoiceDate().split("/")[2];
            if (!yearsInInvoices.contains(invoiceYear)) {
                yearsInInvoices.add(invoiceYear);
            }
        }
        ObservableList<String> years = FXCollections.observableArrayList(yearsInInvoices);
        yearPeriodDropdown.setItems(years);

        ArrayList<String> univsInInvoices = new ArrayList<String>();
        for (Invoice invoice : invoiceTable.getItems()) {
            String invoiceUniversity = invoice.getInstitutionDetails().values().stream().findFirst().orElse("");
            // System.out.println(invoiceUniversity);
            if (!univsInInvoices.contains(invoiceUniversity)) {
                univsInInvoices.add(invoiceUniversity);
            }
        }
        ObservableList<String> univs = FXCollections.observableArrayList(univsInInvoices);
        uniAverageComboBox.setItems(univs);

        logOutButton.setOnAction(_ -> closeApplication());
    }
}
