package com.example.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private PieChart totalCostsChart;
    @FXML private Button addInvoiceButton;
    @FXML private Label costLabel;
    @FXML private TextField searchField;
    @FXML private BarChart<String, Integer> averageUniCostsBarChart;
    @FXML TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> invID;
    @FXML private TableColumn<Invoice, String> invName;
    @FXML private TableColumn<Invoice, String> invUniDts;
    @FXML private TableColumn<Invoice, String> invCourDts;
    @FXML private TableColumn<Invoice, Integer> invCourFees;
    @FXML private TableColumn<Invoice, String> invFoods;
    @FXML private TableColumn<Invoice, String> invSports;
    @FXML private TableColumn<Invoice, String> invDate;

    // Load total costs across all Universities
    @FXML private void loadTotalCostsChart() {

        ObservableList<PieChart.Data> pieData =
                FXCollections.observableArrayList(
                new PieChart.Data("Courses", 535000),
                new PieChart.Data("Food", 35000),
                new PieChart.Data("Sports", 115000)
        );

        totalCostsChart.setData(pieData);
        totalCostsChart.setTitle("Total Costs in 2022 for Business");
        totalCostsChart.setClockwise(false);
        totalCostsChart.setLegendSide(Side.BOTTOM);

        costLabel.setTextFill(Color.BLACK);
        costLabel.setStyle("-fx-font: 20 Arial;");
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
    @FXML private void loadAverageCostsChart() {
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

    // Alert the user before opening new Generate Invoice screen
    private void generateInvoice() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText(null);
        alert.setContentText("Add Invoice functionality will be implemented here.");
        alert.showAndWait();
    }

    // Setup Connection to the SQLite Database
    private Connection connectToDatabase() {
        // JDBC stands for Java Database Connector
        String driver = "jdbc:sqlite", db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project B/UMS-DB.db";
        final String DB_URL = driver + ":" + db;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQL DB successfully.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            System.out.println("Database failed to connect.");
        }

        return conn;
    }

    // Template function for each SQL query
    public ResultSet queryTheDB(String query) throws SQLException {
        Connection conn = connectToDatabase();
        try (PreparedStatement sqlStatement = conn.prepareStatement(query)) {
            return sqlStatement.executeQuery();
        }
    }

    // Initialise Data and required functions.
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTotalCostsChart();
        loadAverageCostsChart();

        addInvoiceButton.setOnAction(event -> generateInvoice());
    }
}
