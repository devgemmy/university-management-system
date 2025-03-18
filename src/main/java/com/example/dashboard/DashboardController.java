package com.example.dashboard;

import javafx.beans.property.SimpleStringProperty;
// import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
// import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
// import javafx.scene.input.MouseEvent;
// import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Text;
import javafx.stage.Stage;
// import javafx.stage.Window;
// import javafx.stage.WindowEvent;
// import javafx.stage.StageStyle;
import javafx.application.Platform;
// import javafx.scene.Node;
// import javafx.scene.control.Tooltip;
// import javafx.scene.layout.AnchorPane;
// import javafx.scene.Node;
// import javafx.scene.control.Tooltip;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
// import java.util.Set;
// import java.math.BigDecimal;
// import java.util.Optional;

/*
This is the Main controller for the dashboard view
It handles all the dashboard functionality including charts, tables, and user interactions
 */
public class DashboardController implements Initializable {
    // UI Components marked with @FXML are linked to the FXML file
    // @FXML
    // private Button fontSizeButton;
    String invoiceYear;

    @FXML
    private Button adminButton;

    @FXML
    private ComboBox<String> timeFilter, monthPeriodDropdown, yearPeriodDropdown, uniAverageComboBox;

    @FXML
    private ComboBox<String> orderByComboBox, categoryComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private Label costLabel, titlePerSelect, totalFeesPerSelect, totalSportsPerSelect, totalFoodPerSelect,
            totalCosterSelect, viewInvoiceBtn, deleteInvoiceBtn;
    @FXML
    private PieChart totalCostsChart;
    @FXML
    private BarChart<String, Number> averageUniCostsBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button addInvoiceButton;
    @FXML
    private TableColumn<Invoice, String> invoiceID, studentName, institutionDetails, courseList, courseInvFees,
            totalSportsCost, totalFoodCost, invoiceDate;

    private DatabaseController dbController;

    /**
     * Loads the pie chart showing total costs across all universities
     * Shows breakdown of course fees, sports costs, and food costs
     */
    @FXML
    private void loadTotalCostsChart() {
        // ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
        // new PieChart.Data("Courses", 535000),
        // new PieChart.Data("Food", 35000),
        // new PieChart.Data("Sports", 115000));

        // totalCostsChart.setData(pieData);
        // totalCostsChart.setTitle("Total Costs in 2022 for Business");
        // totalCostsChart.setClockwise(false);
        // totalCostsChart.setLegendSide(Side.BOTTOM);

        // costLabel.setTextFill(Color.BLACK);
        // costLabel.getStyleClass().add("cost-label");
        // costLabel.setText("£" +
        // String.valueOf(totalCostsChart.getData().getFirst().getPieValue()));

        // for (final PieChart.Data data : totalCostsChart.getData()) {
        // data.getNode().addEventHandler(
        // MouseEvent.MOUSE_CLICKED,
        // new EventHandler<MouseEvent>() {
        // @Override
        // public void handle(MouseEvent ev) {
        // // System.out.println("PieChart clicked");
        // costLabel.setTranslateX(ev.getSceneX() - costLabel.getLayoutX());
        // costLabel.setTranslateY(ev.getSceneY() - costLabel.getLayoutY());
        // costLabel.setText("£" + String.valueOf(data.getPieValue()));
        // }
        // });
        // }

        try {
            // Get all invoices and calculate totals the same way as loadTableData
            List<Invoice> invoices = dbController.getAllInvoices();
            double coursesCost = 0.0;
            double sportsCost = 0.0;
            double foodCost = 0.0;

            for (Invoice invoice : invoices) {
                coursesCost += invoice.getCourseInvFees();

                Map<String, Double> sportsActivities = invoice.getSportsActivities();
                if (sportsActivities != null) {
                    for (Double cost : sportsActivities.values()) {
                        sportsCost += cost;
                    }
                }

                Map<String, Double> foodItems = invoice.getFoodItems();
                if (foodItems != null) {
                    for (Double cost : foodItems.values()) {
                        foodCost += cost;
                    }
                }
            }

            // Initialize the data first
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Courses", coursesCost),
                    new PieChart.Data("Sports", sportsCost),
                    new PieChart.Data("Food", foodCost));

            // Set the data to the chart
            totalCostsChart.setData(pieChartData);
            totalCostsChart.setTitle("Total Costs by Category");
            totalCostsChart.setLegendVisible(false);

            // Set size for the pie chart
            totalCostsChart.setMinSize(400, 400);
            totalCostsChart.setPrefSize(400, 400);
            totalCostsChart.setMaxSize(400, 400);

            // Ensure the chart is laid out before adding labels
            totalCostsChart.layout();

            // Shift the chart left and down by adjusting its layout
            if (totalCostsChart.getParent() instanceof AnchorPane) {
                // AnchorPane parent = (AnchorPane) totalCostsChart.getParent();
                AnchorPane.setLeftAnchor(totalCostsChart, 20.0); // Left margin
                AnchorPane.setTopAnchor(totalCostsChart, 50.0); // Increased top margin
            }

            // Remove the costLabel from the center if it exists
            if (costLabel != null && costLabel.getParent() != null) {
                ((AnchorPane) costLabel.getParent()).getChildren().remove(costLabel);
            }

            // Add labels after ensuring the chart is properly initialized
            Platform.runLater(() -> {
                // Clear any existing labels
                if (totalCostsChart.getParent() instanceof AnchorPane) {
                    AnchorPane parent = (AnchorPane) totalCostsChart.getParent();
                    parent.getChildren()
                            .removeIf(node -> node instanceof Label && ((Label) node).getUserData() != null &&
                                    ((Label) node).getUserData().equals("pie-chart-label"));
                }

                // Create and position labels
                for (PieChart.Data data : pieChartData) {
                    // Create label with value
                    Label label = new Label(String.format("%s • £%,.2f", data.getName(), data.getPieValue()));
                    label.getStyleClass().add("pie-chart-label");
                    label.setUserData("pie-chart-label"); // Mark this label for future identification

                    // Add the label to the chart's parent
                    if (totalCostsChart.getParent() instanceof AnchorPane) {
                        AnchorPane parent = (AnchorPane) totalCostsChart.getParent();
                        parent.getChildren().add(label);

                        // Position label dynamically based on the pie slice
                        data.getNode().boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
                            if (newBounds != null && totalCostsChart.getData() != null) {
                                // Calculate the angle of the slice center
                                double angle = calcAngle(data);

                                // Get the center of the pie chart
                                double centerX = totalCostsChart.getLayoutX() + totalCostsChart.getWidth() / 2;
                                double centerY = totalCostsChart.getLayoutY() + totalCostsChart.getHeight() / 2;

                                // Calculate radius with different distances for each category
                                double radius = switch (data.getName()) {
                                    case "Courses" ->
                                        Math.min(totalCostsChart.getWidth(), totalCostsChart.getHeight()) * 0.25;
                                    case "Sports" ->
                                        Math.min(totalCostsChart.getWidth(), totalCostsChart.getHeight()) * 0.25;
                                    case "Food" ->
                                        Math.min(totalCostsChart.getWidth(), totalCostsChart.getHeight()) * 0.35;
                                    default -> Math.min(totalCostsChart.getWidth(), totalCostsChart.getHeight()) * 0.3;
                                };

                                // Calculate label position with adjusted angles for better placement
                                double adjustedAngle = angle;
                                if (data.getName().equals("Sports")) {
                                    adjustedAngle += 30; // Increase angle shift for Sports label
                                } else if (data.getName().equals("Food")) {
                                    adjustedAngle -= 15; // Adjust Food label shift
                                }

                                double labelX = centerX + radius * Math.cos(Math.toRadians(adjustedAngle));
                                double labelY = centerY - radius * Math.sin(Math.toRadians(adjustedAngle));

                                // Position the label
                                label.setLayoutX(labelX - label.getWidth() / 2);
                                label.setLayoutY(labelY - label.getHeight() / 2);
                            }
                        });
                    }
                }

                // Add hover effects for pie slices
                pieChartData.forEach(data -> {
                    Node node = data.getNode();
                    if (node != null) {
                        String defaultColor = switch (data.getName()) {
                            case "Courses" -> "#ff6b4a";
                            case "Sports" -> "#9370DB";
                            case "Food" -> "#4169E1";
                            default -> "#ff6b4a";
                        };

                        node.setStyle("-fx-pie-color: " + defaultColor + ";");

                        node.setOnMouseEntered(event -> {
                            node.setStyle("-fx-pie-color: derive(" + defaultColor + ", 20%);");
                        });

                        node.setOnMouseExited(event -> {
                            node.setStyle("-fx-pie-color: " + defaultColor + ";");
                        });
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading pie chart data: " + e.getMessage());
        }
    }

    private double calcAngle(PieChart.Data data) {
        if (totalCostsChart == null || totalCostsChart.getData() == null || totalCostsChart.getData().isEmpty()) {
            return 90; // Default angle if data is not available
        }

        double total = 0;
        for (PieChart.Data d : totalCostsChart.getData()) {
            total += d.getPieValue();
        }

        if (total == 0) {
            return 90; // Default angle if total is zero
        }

        double angle = 90; // Start at top (90 degrees)
        for (PieChart.Data d : totalCostsChart.getData()) {
            if (d == data) {
                angle -= (d.getPieValue() * 360 / total) / 2;
                break;
            }
            angle -= (d.getPieValue() * 360 / total);
        }
        return angle;
    }

    /**
     * Loads the bar chart showing average costs for each category in a selected
     * university
     * Updates when a different university is selected from the dropdown
     */
    @FXML
    private void loadAverageCostsChart() {
        averageUniCostsBarChart.getData().clear();
        String selectedUni = uniAverageComboBox.getValue();

        // Create Series instance with data types
        XYChart.Series<String, Integer> courseCosts = new XYChart.Series<>();
        courseCosts.setName("Courses"); // This is one of the Legends

        // Create Series instance with data types
        // XYChart.Series<String, Integer> courseCosts = new XYChart.Series<>();
        // courseCosts.setName("Courses"); // XYChart.Data(xAxis, yAxis);
        // courseCosts.getData().add(new XYChart.Data<>("2020", 25601));
        // courseCosts.getData().add(new XYChart.Data<>("2021", 20148));
        // courseCosts.getData().add(new XYChart.Data<>("2022", 12500));
        // courseCosts.getData().add(new XYChart.Data<>("2023", 122450));
        // courseCosts.getData().add(new XYChart.Data<>("2024", 54550));

        // XYChart.Series<String, Integer> foodCosts = new XYChart.Series<>();
        // foodCosts.setName("Food");
        // foodCosts.getData().add(new XYChart.Data<>("2020", 9560));
        // foodCosts.getData().add(new XYChart.Data<>("2021", 8148));
        // foodCosts.getData().add(new XYChart.Data<>("2022", 4200));
        // foodCosts.getData().add(new XYChart.Data<>("2023", 7480));
        // foodCosts.getData().add(new XYChart.Data<>("2024", 2950));

        // XYChart.Series<String, Integer> sportsCosts = new XYChart.Series<>();
        // sportsCosts.setName("Sports");
        // sportsCosts.getData().add(new XYChart.Data<>("2020", 4560));
        // sportsCosts.getData().add(new XYChart.Data<>("2021", 8148));
        // sportsCosts.getData().add(new XYChart.Data<>("2022", 1200));
        // sportsCosts.getData().add(new XYChart.Data<>("2023", 3450));
        // sportsCosts.getData().add(new XYChart.Data<>("2024", 9250));

        // Add the series in the Bar Chart;
        // averageUniCostsBarChart.getData().add(courseCosts);
        // averageUniCostsBarChart.getData().add(foodCosts);
        // averageUniCostsBarChart.getData().add(sportsCosts);
        // averageUniCostsBarChart.setTitle("Average Costs Per University in March");
        // averageUniCostsBarChart.setLegendSide(Side.TOP);

        // XYChart.Data(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Costs");

        try {
            List<Invoice> filteredInvoicesByInstitution = dbController.getAllInvoices();
            double courseFees = 0.0;
            double sportsCosts = 0.0;
            double foodCosts = 0.0;

            // Filter invoices if a specific university is selected
            if (selectedUni != null && !selectedUni.equals("All Universities")) {
                filteredInvoicesByInstitution = filteredInvoicesByInstitution.stream()
                        .filter(invoice -> selectedUni.equals(invoice.getInstitutionDetails().get("institutionName")))
                        .toList();

                if (filteredInvoicesByInstitution.isEmpty()) {
                    averageUniCostsBarChart.setTitle("No Data Available for " + selectedUni);
                    return;
                }
                averageUniCostsBarChart.setTitle("Average Cost - " + selectedUni);
            } else {
                averageUniCostsBarChart.setTitle("Average Cost Universities");
            }

            // Calculate averages of Invoices in the table with selected Institutions.
            for (Invoice invoice : filteredInvoicesByInstitution) {
                courseFees += invoice.getCourseInvFees() / filteredInvoicesByInstitution.size();

                Map<String, Double> sportsActivities = invoice.getSportsActivities();
                if (sportsActivities != null) {
                    for (Double cost : sportsActivities.values()) {
                        sportsCosts += cost / filteredInvoicesByInstitution.size();
                    }
                }

                Map<String, Double> foodItems = invoice.getFoodItems();
                if (foodItems != null) {
                    for (Double cost : foodItems.values()) {
                        foodCosts += cost / filteredInvoicesByInstitution.size();
                    }
                }
            }

            series.getData().add(new XYChart.Data<>("Course Fees", courseFees));
            series.getData().add(new XYChart.Data<>("Sports Costs", sportsCosts));
            series.getData().add(new XYChart.Data<>("Food Costs", foodCosts));

            averageUniCostsBarChart.getData().add(series);

            // Style the chart
            averageUniCostsBarChart.setStyle("-fx-background-color: white;");

            // Add value labels and style bars
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // Set different colors based on the category
                    String barColor = switch (data.getXValue()) {
                        case "Course Fees" -> "#ff6b4a"; // Keep existing orange color for courses
                        case "Food Costs" -> "#4169E1"; // Royal Blue for food
                        case "Sports Costs" -> "#9370DB"; // Medium Purple for sports
                        default -> "#ff6b4a";
                    };

                    node.setStyle("-fx-bar-fill: " + barColor + ";");

                    // Use the same formatting as pie chart and table
                    Label label = new Label(String.format("£%,.2f", data.getYValue().doubleValue()));
                    label.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");

                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            displayLabelForData(label, data);
                        }
                    });
                }
            }

            // Configure axes
            CategoryAxis xAxis = (CategoryAxis) averageUniCostsBarChart.getXAxis();
            NumberAxis yAxis = (NumberAxis) averageUniCostsBarChart.getYAxis();

            xAxis.setLabel("Cost Categories");
            yAxis.setLabel("Amount (£)");

            // Set y-axis range with some padding
            double maxValue = Math.max(Math.max(courseFees, sportsCosts), foodCosts);
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            // Add 10% padding
            yAxis.setUpperBound(maxValue * 1.1);
            // Create 10 tick marks
            yAxis.setTickUnit(maxValue / 10);

        } catch (Exception e) {
            e.printStackTrace();
            averageUniCostsBarChart.setTitle("Error Loading Data");
        }
    }

    private void displayLabelForData(Label label, XYChart.Data<String, Number> data) {
        Node node = data.getNode();
        label.setTranslateY(-10); // Position above the bar

        Pane chartPane = (Pane) averageUniCostsBarChart.lookup(".chart-plot-background");
        if (chartPane != null && !chartPane.getChildren().contains(label)) {
            chartPane.getChildren().add(label);

            // Position the label
            node.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
                if (newBounds != null) {
                    label.setLayoutX(newBounds.getMinX() + newBounds.getWidth() / 2 - label.getWidth() / 2);
                    label.setLayoutY(newBounds.getMinY() - label.getHeight());
                }
            });
        }
    }

    /**
     * Loads invoice data into the table and updates summary statistics
     * Calculates totals for courses, sports, and food costs
     */
    @FXML
    private void loadTableData() {
        // ERROR FIX: Reassigning invoiceTable to a new TableView is wrong.
        // The TableView is already defined in your FXML.
        // Therefore, I remove ERR_LINE 1 as it prevents invoiceTable from being null
        // since it is already injected via @FXML.
        // --- ERR_LINE 1: invoiceTable = new TableView<>();

        // SOME BIG DUMMY DATA
        // HashMap<String, Double> sportsDetails1 = new HashMap<String, Double>();
        // sportsDetails1.put("Basket Ball", 12.9);
        // sportsDetails1.put("Mountain Climbing", 9.8);

        // HashMap<String, Double> foodDetails1 = new HashMap<String, Double>();
        // foodDetails1.put("Cheese Pizza", 4.75);
        // foodDetails1.put("Indian Biryani", 1.40);

        // HashMap<String, String> courseDetails1 = new HashMap<String, String>();
        // courseDetails1.put("NC1600", "Business Computing");

        // HashMap<String, String> institutionDetails1 = new HashMap<String, String>();
        // institutionDetails1.put("10009785", "Imperial College");

        // final ObservableList<Invoice> invoiceTableData =
        // FXCollections.observableArrayList();
        // invoiceTableData.add(new Invoice("1", "Jatinder Alma", "12/08/2023", 21800.0,
        // 32.8, 176.43, courseDetails1, institutionDetails1, sportsDetails1,
        // foodDetails1));

        try {
            // Gets all invoices from FINANCES table
            List<Invoice> invoices = dbController.getAllInvoices();

            if (invoices.isEmpty()) {
                System.out.println("No invoices found in the database");
                return;
            }

            // Calculates cost totals
            double totalCourseFees = 0.0;
            double totalSportsCosts = 0.0;
            double totalFoodCosts = 0.0;

            for (Invoice invoice : invoices) {
                totalCourseFees += invoice.getCourseInvFees();
                totalSportsCosts += invoice.getTotalSportsCost();
                totalFoodCosts += invoice.getTotalFoodCost();
            }

            double grandTotal = totalCourseFees + totalSportsCosts + totalFoodCosts;

            // Update the table with all invoices
            ObservableList<Invoice> invoiceTableData = FXCollections.observableArrayList(invoices);
            invoiceTable.setItems(invoiceTableData);
            invoiceTable.setEditable(false);

            // Update summary labels with proper formatting
            totalFeesPerSelect.setText(String.format("£%,.2f", totalCourseFees)); // "£55,350,000.00"
            totalSportsPerSelect.setText(String.format("£%,.2f", totalSportsCosts)); // "£350,000.50"
            totalFoodPerSelect.setText(String.format("£%,.2f", totalFoodCosts)); // "£1,150,050.00"
            totalCosterSelect.setText(String.format("£%,.2f", grandTotal)); // "£61,045,850.85"

            // Update title with proper formatting
            titlePerSelect.setText("Total costs in 2022 for Business students: ");
            titlePerSelect.setText(String.format("Total costs for %,d records", invoices.size()));

            // Debug output
            System.out.println("Number of invoices: " + invoices.size());
            System.out.println("Total Course Fees: £" + String.format("%,.2f", totalCourseFees));
            System.out.println("Total Sports Costs: £" + String.format("%,.2f", totalSportsCosts));
            System.out.println("Total Food Costs: £" + String.format("%,.2f", totalFoodCosts));
            System.out.println("Grand Total: £" + String.format("%,.2f", grandTotal));

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to load invoice data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Opens the new invoice generation window
     * Creates a new stage for the invoice form
     */
    @FXML
    public void generateNewInvoice() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("generate-invoice-view.fxml"));
        Stage newInvoiceStage = (Stage) adminButton.getScene().getWindow();
        newInvoiceStage.setScene(new Scene(fxmlLoader.load(), 1200, 1000));
        newInvoiceStage.setTitle("UMS - Generate New Invoice");
        newInvoiceStage.setResizable(false);
        newInvoiceStage.show();
    }

    // Setup Connection to the SQLite Database, returns null if connection fails
    private Connection connectToDatabase() {

        // JDBC stands for Java Database Connector
        String driver = "jdbc:sqlite:";
        // String db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project
        // B/UMS-DB.db";
        String dbPath = System.getProperty("user.dir") + "/UMS-DB.db";
        final String DB_URL = driver + dbPath;

        try {
            // System.out.println("Connected to SQL DB successfully.");
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Database failed to connect.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Template method for executing SQL queries safely
     * Prevents SQL injection by using prepared statements
     */
    public ResultSet queryTheDB(String query, String param) throws SQLException {
        Connection conn = connectToDatabase();
        PreparedStatement sqlStatement = conn.prepareStatement(query);
        sqlStatement.setString(1, param); // Avoids SQL injection
        return sqlStatement.executeQuery();
    }

    /**
     * Sorts the invoice table data based on selected category and order
     * Categories include: Courses, Sports, Food, Date
     */
    public void sortDataByCategory(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        String selectedOrder = orderByComboBox.getValue();
        // System.out.println("Selected Order: " + selectedOrder + ", Selected
        // Category:" + selectedCategory);

        try {
            ObservableList<Invoice> items = invoiceTable.getItems();
            if (items == null || items.isEmpty() || selectedCategory == null) {
                return;
            }

            FXCollections.sort(items, (invoice1, invoice2) -> {
                int comparison = 0;

                switch (selectedCategory) {
                    case "Courses":
                        comparison = Double.compare(invoice1.getCourseInvFees(), invoice2.getCourseInvFees());
                        break;
                    case "Sports":
                        comparison = Double.compare(invoice1.getTotalSportsCost(), invoice2.getTotalSportsCost());
                        break;
                    case "Food":
                        comparison = Double.compare(invoice1.getTotalFoodCost(), invoice2.getTotalFoodCost());
                        break;
                    case "Date":
                    default:
                        comparison = invoice1.getInvoiceDate().compareTo(invoice2.getInvoiceDate());
                        break;
                }

                // Reverse the comparison if DESC is selected
                return "DESC".equals(selectedOrder) ? -comparison : comparison;
            });

            invoiceTable.setItems(items);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to sort data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Sets up time-based filtering for invoices
     * Handles monthly and yearly filtering options
     */
    @FXML
    public void setTimeFilter(ActionEvent event) {
        String selectedPeriod = timeFilter.getValue();
        if (selectedPeriod.equals("By Month")) {
            monthPeriodDropdown.setVisible(true);
            yearPeriodDropdown.setVisible(false);
            loadTableData(); // Reset to show all data
        } else if (selectedPeriod.equals("By Year")) {
            monthPeriodDropdown.setVisible(false);
            yearPeriodDropdown.setVisible(true);
            String selectedYear = yearPeriodDropdown.getValue();
            if (selectedYear != null) {
                filterInvoicesByYear(selectedYear);
            }
        } else {
            monthPeriodDropdown.setVisible(false);
            yearPeriodDropdown.setVisible(false);
            loadTableData(); // Reset to show all data
        }
    }

    public void setSortingPeriod() {
        // Filter the invoices by month
        // String monthQuery = "SELECT * FROM INVOICE WHERE INVOICE_DATE LIKE '%" +
        // searchField.getText() + "%'";
        // ResultSet monthData = queryTheDB(monthQuery);
    }

    /**
     * Opens detailed view for a selected invoice
     * Creates new window showing all invoice information
     */
    public void viewInvoice(Invoice invoice) throws IOException {
        FXMLLoader viewInvFXMLLoader = new FXMLLoader(DashboardApp.class.getResource("invoice-details-view.fxml"));
        Stage viewInvoiceStage = (Stage) adminButton.getScene().getWindow();
        viewInvoiceStage.setScene(new Scene(viewInvFXMLLoader.load(), 1200, 1000));
        // Get the controller and set the invoice
        ViewInvoiceController controller = viewInvFXMLLoader.getController();
        controller.setInvoice(invoice);

        viewInvoiceStage.setTitle("UMS - View Invoice Details");
        viewInvoiceStage.setResizable(false);
        viewInvoiceStage.show();
    }

    public void deleteInvoice() {
        // Delete the invoice data
        // String deleteQuery = "DELETE FROM INVOICE WHERE INVOICE_ID = ?";
        // queryTheDB(deleteQuery, "1");

        System.out.println("Delete Invoice Clicked");
    }

    /**
     * Initializes the dashboard with default values
     * Sets up dropdowns, charts, and table columns
     */
    private void initializeDashboardValues() {
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

        // Set fixed years from 2020 to 2025
        List<String> yearsInInvoices = new ArrayList<String>();
        for (Invoice invoice : invoiceTable.getItems()) {
            invoiceYear = invoice.getInvoiceDate().split("-")[0];
            if (!yearsInInvoices.contains(invoiceYear)) {
                yearsInInvoices.add(invoiceYear);
            }
        }
        ObservableList<String> years = FXCollections.observableArrayList(yearsInInvoices);
        // ObservableList<String> years = FXCollections.observableArrayList("2020",
        // "2021", "2022", "2023", "2024","2025");
        yearPeriodDropdown.setItems(years);

        // Add listener to yearPeriodDropdown
        yearPeriodDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterInvoicesByYear(newValue);
            }
        });

        // Get universities from database
        // Map<String, String> institutions = dbController.getInstitutions();
        // ArrayList<String> universities = new ArrayList<>();
        // universities.addAll(institutions.values());
        // Collections.sort(universities.subList(1, universities.size())); // Sort all
        // except "All Universities"
        ArrayList<String> univsInInvoices = new ArrayList<String>();
        // System.out.println(invoiceTable.getItems());
        for (Invoice invoice : invoiceTable.getItems()) {
            String invoiceUniversity = invoice.getInstitutionDetails().values().stream().findFirst().orElse("");
            univsInInvoices.add("All Universities"); // Add "All Universities" to the list
            // System.out.println(invoiceUniversity);
            if (!univsInInvoices.contains(invoiceUniversity)) {
                univsInInvoices.add(invoiceUniversity);
            }
        }
        // ObservableList<String> univs =
        // FXCollections.observableArrayList(universities);
        ObservableList<String> univs = FXCollections.observableArrayList(univsInInvoices);
        uniAverageComboBox.setItems(univs);
        uniAverageComboBox.setValue("All Universities"); // Set default selection

        // Load initial bar chart data
        loadAverageCostsChart();

        // Set initial title
        titlePerSelect.setText("Total costs for all institutions");
    }

    /**
     * Filters invoices based on selected year
     * Updates table and statistics to show only matching records
     */
    private void filterInvoicesByYear(String selectedYear) {
        try {
            if ("All Records".equals(timeFilter.getValue())) {
                loadTableData(); // Show all data
                return;
            }

            List<Invoice> allInvoices = dbController.getAllInvoices();
            List<Invoice> filteredInvoices = allInvoices.stream()
                    .filter(invoice -> {
                        invoiceYear = invoice.getInvoiceDate().split("-")[0];
                        return selectedYear.equals(invoiceYear);
                    })
                    .sorted((i1, i2) -> i1.getInvoiceDate().compareTo(i2.getInvoiceDate()))
                    .toList();

            invoiceTable.setItems(FXCollections.observableArrayList(filteredInvoices));
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to filter invoice data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Handles search functionality across all invoice fields
     * Searches in student names, institutions, courses, and activities
     */
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        try {
            List<Invoice> allInvoices = dbController.getAllInvoices();
            List<Invoice> filteredInvoices = allInvoices.stream()
                    .filter(invoice -> {
                        if (searchText.isEmpty()) {
                            return true; // Show all if search is empty
                        }

                        // Check student name
                        if (invoice.getStudentName().toLowerCase().contains(searchText)) {
                            return true;
                        }

                        // Check institution name
                        String institutionName = invoice.getInstitutionDetails().get("institutionName");
                        if (institutionName != null && institutionName.toLowerCase().contains(searchText)) {
                            return true;
                        }

                        // Check course details
                        Map<String, String> courseDetails = invoice.getCourseList();
                        if (courseDetails != null) {
                            // Check course ID
                            String courseId = courseDetails.get("courseID");
                            if (courseId != null && courseId.toLowerCase().contains(searchText)) {
                                return true;
                            }
                            // Check course name
                            String courseName = courseDetails.get("courseName");
                            if (courseName != null && courseName.toLowerCase().contains(searchText)) {
                                return true;
                            }
                        }

                        // Check sports activities
                        Map<String, Double> sportsActivities = invoice.getSportsActivities();
                        if (sportsActivities.keySet().stream()
                                .anyMatch(activity -> activity.toLowerCase().contains(searchText))) {
                            return true;
                        }

                        // Check food items
                        Map<String, Double> foodItems = invoice.getFoodItems();
                        if (foodItems.keySet().stream()
                                .anyMatch(item -> item.toLowerCase().contains(searchText))) {
                            return true;
                        }

                        // Check year
                        invoiceYear = invoice.getInvoiceDate().split("-")[0];
                        return searchText.equals(invoiceYear);
                    })
                    .sorted((i1, i2) -> i2.getInvoiceDate().compareTo(i1.getInvoiceDate()))
                    .toList();

            invoiceTable.setItems(FXCollections.observableArrayList(filteredInvoices));

            // Update status message
            String statusMessage = String.format("Found %d matching records", filteredInvoices.size());
            System.out.println(statusMessage);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to filter data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Update initialize method to include the new listener setup
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize database controller
        if (dbController == null) {
            dbController = new DatabaseController();
        }

        try {
            // Test database connection
            if (!dbController.testConnection()) {
                showError("Failed to connect to database. Please check your connection and try again.");
                return;
            }
        } catch (Exception e) {
            showError("Database initialization error: " + e.getMessage());
            return;
        }

        // Initialize table columns
        invoiceID.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        institutionDetails.setCellValueFactory(institutionData -> {
            Map<String, String> institutionCatalog = institutionData.getValue().getInstitutionDetails();
            String institutionName = institutionCatalog != null ? institutionCatalog.get("institutionName") : "";

            String formattedInstitution = institutionCatalog.entrySet().stream()
                    .map(entry -> institutionName + " (" + entry.getValue() + ")")
                    .findFirst()
                    .orElse("");
            return new SimpleStringProperty(formattedInstitution);
        });

        courseList.setCellValueFactory(courseData -> {
            Map<String, String> courseCatalog = courseData.getValue().getCourseList();
            String courseName = courseCatalog != null ? courseCatalog.get("courseName") : "";

            // String formattedCourse = courseCatalog.entrySet().stream()
            // .map(entry -> courseName + " (" + entry.getValue() + ")")
            // .findFirst()
            // .orElse("");
            return new SimpleStringProperty(courseName);
        });

        // courseInvFees.setCellValueFactory(new
        // PropertyValueFactory<>("courseInvFees"));
        courseInvFees.setCellValueFactory(feesData -> {
            double cFee = feesData.getValue().getCourseInvFees();
            return new SimpleStringProperty(String.format("£%,.2f", cFee));
        });

        // Format sports cost as "Amount, Count"
        totalSportsCost.setCellValueFactory(sportData -> {
            Invoice sportsInfo = sportData.getValue();
            double sCost = sportsInfo.getTotalSportsCost(); // Get the sports cost
            int sportCount = sportsInfo.getSportsActivities().size(); // Method to determine the count of sports
            // getInvItemsCount(sportsInfo.getSportsActivities());
            return new SimpleStringProperty(String.format("£%,.2f (%d Sports)", sCost, sportCount));
        });

        // Format food cost as "Amount, Count"
        totalFoodCost.setCellValueFactory(foodData -> {
            Invoice foodsInfo = foodData.getValue();
            double fCost = foodsInfo.getTotalFoodCost();
            int foodCount = foodsInfo.getFoodItems().size();
            // getInvItemsCount(sportsInfo.getSportsActivities());
            return new SimpleStringProperty(String.format("£%,.2f (%d Meals)", fCost, foodCount));
        });

        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

        // Add search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch();
        });

        // Initialize bar chart
        xAxis.setLabel("Cost Categories");
        yAxis.setLabel("Amount (£)");
        averageUniCostsBarChart.setTitle("Total Costs");
        averageUniCostsBarChart.setStyle(".chart-bar { -fx-bar-fill: #ff6b4a; }");

        // Initialize dashboard values first
        initializeDashboardValues();

        // Then load data and charts
        loadTableData();
        loadTotalCostsChart();
        loadAverageCostsChart();

        // Add double-click event handler for viewing invoice details
        // invoiceTable.setRowFactory(_ -> {
        // TableRow<Invoice> row = new TableRow<>();
        // row.setOnMouseClicked(event -> {
        // if (event.getClickCount() == 2 && (!row.isEmpty())) {
        // Invoice rowData = row.getItem();
        // System.out.println("Double click on: " + rowData.getStudentName());
        // }
        // });
        // return row;
        // });
        invoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
                if (selectedInvoice != null) {
                    try {
                        viewInvoice(selectedInvoice);
                    } catch (IOException e) {
                        showError("Failed to open invoice details: " + e.getMessage());
                    }
                }
            }
        });

        // Set up university dropdown
        Map<String, String> institutions = dbController.getInstitutions();
        ArrayList<String> universities = new ArrayList<>();
        universities.add("All Universities"); // Add option to view all universities
        universities.addAll(institutions.values()); // Add institution names
        Collections.sort(universities.subList(1, universities.size())); // Sort all except "All Universities"
        uniAverageComboBox.setItems(FXCollections.observableArrayList(universities));
        uniAverageComboBox.setValue("All Universities"); // Set default selection

        // Setup listener for university selection changes
        uniAverageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadAverageCostsChart();
            }
        });
    }

    // private double getTotalCostForCategory(String category) {
    // try {
    // String query = "SELECT SUM(COST) FROM ACTIVITY WHERE CATEGORY = ?";
    // PreparedStatement stmt = connectToDatabase().prepareStatement(query);
    // stmt.setString(1, category);
    // ResultSet rs = stmt.executeQuery();
    // if (rs.next()) {
    // return rs.getDouble(1);
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return 0.0;
    // }

    /**
     * Handles navigation back to Administrative Services page
     */
    @FXML
    private void handleAdminServices() {
        try {
            // Clean up resources
            if (dbController != null) {
                dbController.closeConnection();
            }

            // Clear existing data safely
            if (invoiceTable != null) {
                invoiceTable.setItems(null);
            }
            if (totalCostsChart != null && totalCostsChart.getData() != null) {
                totalCostsChart.getData().clear();
            }
            if (averageUniCostsBarChart != null) {
                averageUniCostsBarChart.getData().clear();
            }

            FXMLLoader viewAdminLoader = new FXMLLoader(getClass().getResource("admin-services-view.fxml"));
            Stage adminStage = (Stage) adminButton.getScene().getWindow();
            adminStage.setScene(new Scene(viewAdminLoader.load()));
            adminStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load administrative services view: " + e.getMessage());
        }
    }

    /**
     * Shows error alert when operations fail
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
