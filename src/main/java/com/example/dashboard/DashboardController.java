package com.example.dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimerTask;
import java.util.Timer;

public class DashboardController implements Initializable {
    String invoiceYear;

    @FXML
    private Button adminButton;

    @FXML
    protected ComboBox<String> timeFilter, monthPeriodDropdown, yearPeriodDropdown, uniAverageComboBox;

    @FXML
    protected ComboBox<String> orderByComboBox, categoryComboBox;

    @FXML
    private TextField searchField;

    @FXML
    protected TableView<Invoice> invoiceTable;

    @FXML
    protected Label costLabel, titlePerSelect, totalFeesPerSelect, totalSportsPerSelect, totalFoodPerSelect,
            totalCosterSelect;
    // viewInvoiceBtn, deleteInvoiceBtn;
    @FXML
    protected PieChart totalCostsChart;
    @FXML
    protected BarChart<String, Number> averageUniCostsBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button addInvoiceButton;
    @FXML
    private TableColumn<Invoice, String> invoiceID, studentName, institutionDetails, courseList, courseInvFees,
            totalSportsCost, totalFoodCost, invoiceDate;

    @FXML
    private Button refreshChartButton;

    @FXML
    private AnchorPane loadingOverlay;

    @FXML
    private Label loadingLabel;

    private DatabaseModel dbController;

    private volatile boolean isLoading = false;

    // Filter types
    private enum FilterType {
        ALL_RECORDS("All Records"),
        BY_YEAR("By Year"),
        BY_MONTH("By Month");

        private final String displayName;

        FilterType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private DataCache dataCache;

    private static class DataCache {
        // By Month (1-12) -> All invoices for that month across all years
        Map<Integer, List<Invoice>> monthlyData = new HashMap<>();
        // By Year -> All invoices for that year
        Map<Integer, List<Invoice>> yearlyData = new HashMap<>();
        // All invoices (to avoid repeated DB calls)
        List<Invoice> allInvoices = new ArrayList<>();
        // Track totals for each month across all years
        Map<Integer, MonthlyTotals> monthlyTotals = new HashMap<>();
    }

    @SuppressWarnings("unused")
    private static class MonthlyTotals {
        double totalCourseFees = 0.0;
        double totalSportsCosts = 0.0;
        double totalFoodCosts = 0.0;
        int count = 0;
    }

    private boolean isProcessingTimeFilter = false;

    @FXML
    protected void loadTotalCostsChart() {
        // Clear existing data
        totalCostsChart.getData().clear();
        double courseFees = 0.0;
        double sportsCosts = 0.0;
        double foodCosts = 0.0;

        try {
            if (totalFeesPerSelect != null && !totalFeesPerSelect.getText().isEmpty()) {
                courseFees = Double.parseDouble(totalFeesPerSelect.getText().replace("£", "").replace(",", ""));
            }
            if (totalSportsPerSelect != null && !totalSportsPerSelect.getText().isEmpty()) {
                sportsCosts = Double.parseDouble(totalSportsPerSelect.getText().replace("£", "").replace(",", ""));
            }
            if (totalFoodPerSelect != null && !totalFoodPerSelect.getText().isEmpty()) {
                foodCosts = Double.parseDouble(totalFoodPerSelect.getText().replace("£", "").replace(",", ""));
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Course Fees", courseFees),
                    new PieChart.Data("Sports Costs", sportsCosts),
                    new PieChart.Data("Food Costs", foodCosts));

            totalCostsChart.setData(pieChartData);
            totalCostsChart.setTitle("Total Costs by Category");
            totalCostsChart.setLegendVisible(false);
            totalCostsChart.setMinSize(400, 400);
            totalCostsChart.setPrefSize(400, 400);
            totalCostsChart.setMaxSize(400, 400);

            totalCostsChart.layout();
            if (totalCostsChart.getParent() instanceof AnchorPane) {
                // AnchorPane parent = (AnchorPane) totalCostsChart.getParent();
                AnchorPane.setLeftAnchor(totalCostsChart, 20.0); // Left margin
                AnchorPane.setTopAnchor(totalCostsChart, 50.0); // Increased top margin
            }

            if (costLabel != null && costLabel.getParent() != null) {
                ((AnchorPane) costLabel.getParent()).getChildren().remove(costLabel);
            }

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
                        // obs, oldNode, newNode
                        data.getNode().boundsInParentProperty().addListener((_, _, newBounds) -> {
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

                pieChartData.forEach(data -> {
                    Node node = data.getNode();
                    if (node != null) {
                        String defaultColor = switch (data.getName()) {
                            case "Course Fees" -> "#007A7A"; // Green
                            case "Sports Costs" -> "#FFA84A"; // Yellow
                            case "Food Costs" -> "#DE6600"; // Orange
                            default -> "#000000";
                        };

                        node.setStyle("-fx-pie-color: " + defaultColor + ";");
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading total costs chart: " + e.getMessage());
        }
    }

    private double calcAngle(PieChart.Data data) {
        totalCostsChart.setTitle("Total Costs by Category");
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

    @FXML
    protected void loadAverageCostsChart() {
        averageUniCostsBarChart.getData().clear();
        String selectedUni = uniAverageComboBox.getValue();

        // Create Series instance with data types
        XYChart.Series<String, Integer> courseCosts = new XYChart.Series<>();
        courseCosts.setName("Courses"); // This is one of the Legends

        // XYChart.Data(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average Costs");

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
                averageUniCostsBarChart.setTitle("Average Cost per University");
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
            averageUniCostsBarChart.setStyle("-fx-background-color: white;");

            // Add value labels and style bars
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // Set consistent colors based on the category
                    String barColor = switch (data.getXValue()) {
                        case "Course Fees" -> "#007A7A"; // Green for Courses
                        case "Sports Costs" -> "#FFA84A"; // Yellow for Sports
                        case "Food Costs" -> "#DE6600"; // Orange for Food
                        default -> "#007A7A";
                    };

                    node.setStyle("-fx-bar-fill: " + barColor + ";");

                    // Use the same formatting as pie chart and table
                    Label label = new Label(String.format("£%,.2f", data.getYValue().doubleValue()));
                    label.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");

                    // obs, oldNode, newNode
                    data.nodeProperty().addListener((_, _, newNode) -> {
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

            double maxValue = Math.max(Math.max(courseFees, sportsCosts), foodCosts);
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);

            yAxis.setUpperBound(maxValue * 1.1);
            yAxis.setTickUnit(maxValue / 10);

        } catch (Exception e) {
            e.printStackTrace();
            averageUniCostsBarChart.setTitle("Error Loading Data");
        }
    }

    private void displayLabelForData(Label label, XYChart.Data<String, Number> data) {
        Node node = data.getNode();
        label.setTranslateY(-10);

        Pane chartPane = (Pane) averageUniCostsBarChart.lookup(".chart-plot-background");
        if (chartPane != null && !chartPane.getChildren().contains(label)) {
            chartPane.getChildren().add(label);

            // obs, oldNode, newNode
            node.boundsInParentProperty().addListener((_, _, newBounds) -> {
                if (newBounds != null) {
                    label.setLayoutX(newBounds.getMinX() + newBounds.getWidth() / 2 - label.getWidth() / 2);
                    label.setLayoutY(newBounds.getMinY() - label.getHeight());
                }
            });
        }
    }

    @FXML
    protected void loadTableData() {
        if (isLoading)
            return;
        setLoading(true);

        try {
            List<Invoice> filteredInvoices;
            String selectedFilter = timeFilter.getValue();
            switch (selectedFilter) {
                case "By Year":
                    String yearStr = yearPeriodDropdown.getValue();
                    if (yearStr != null) {
                        int year = Integer.parseInt(yearStr);
                        filteredInvoices = new ArrayList<>(dataCache.yearlyData.getOrDefault(year, new ArrayList<>()));
                    } else {
                        filteredInvoices = new ArrayList<>(dataCache.allInvoices);
                    }
                    break;
                case "By Month":
                    String monthStr = monthPeriodDropdown.getValue();
                    if (monthStr != null) {
                        int month = getMonthNumber(monthStr);
                        filteredInvoices = new ArrayList<>(
                                dataCache.monthlyData.getOrDefault(month, new ArrayList<>()));
                    } else {
                        filteredInvoices = new ArrayList<>(dataCache.allInvoices);
                    }
                    break;
                default:
                    filteredInvoices = new ArrayList<>(dataCache.allInvoices);
            }
            // Sort by date descending (already in memory, so this is fast)
            filteredInvoices.sort((i1, i2) -> i2.getInvoiceDate().compareTo(i1.getInvoiceDate()));

            // Calculates cost totals
            double totalCourseFees = 0.0;
            double totalSportsCosts = 0.0;
            double totalFoodCosts = 0.0;
            for (Invoice invoice : filteredInvoices) {
                totalCourseFees += invoice.getCourseInvFees();
                totalSportsCosts += invoice.getTotalSportsCost();
                totalFoodCosts += invoice.getTotalFoodCost();
            }
            // Update UI on JavaFX Application Thread
            final List<Invoice> finalFilteredInvoices = filteredInvoices;
            final double finalTotalCourseFees = totalCourseFees;
            final double finalTotalSportsCosts = totalSportsCosts;
            final double finalTotalFoodCosts = totalFoodCosts;
            // Update the table with all invoices
            Platform.runLater(() -> {
                ObservableList<Invoice> invoiceTableData = FXCollections.observableArrayList(finalFilteredInvoices);
                invoiceTable.setItems(invoiceTableData);
                invoiceTable.setEditable(false);
                updateTotalLabels(finalTotalCourseFees, finalTotalSportsCosts, finalTotalFoodCosts);
                loadTotalCostsChart();
                // updatePieChart();
                loadYearlyBarChart();
                setLoading(false);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                showError("Error loading data: " + e.getMessage());
                setLoading(false);
            });
        }
    }

    private void updateTotalLabels(double totalCourseFees, double totalSportsCosts, double totalFoodCosts) {
        double grandTotal = totalCourseFees + totalSportsCosts + totalFoodCosts;
        // Update summary labels with proper formatting
        totalFeesPerSelect.setText(String.format("£%,.2f", totalCourseFees)); // "£55,350,000.00"
        totalSportsPerSelect.setText(String.format("£%,.2f", totalSportsCosts)); // "£350,000.50"
        totalFoodPerSelect.setText(String.format("£%,.2f", totalFoodCosts)); // "£1,150,050.00"
        totalCosterSelect.setText(String.format("£%,.2f", grandTotal)); // "£61,045,850.85"
        // Get the current number of records from the table
        int recordCount = invoiceTable.getItems().size();
        // titlePerSelect.setText("Total costs in 2022 for Business students: ");
        titlePerSelect.setText(String.format("Total costs for %,d records", recordCount));

        // Debug output
        System.out.println("Total Course Fees: £" + String.format("%,.2f", totalCourseFees));
        System.out.println("Total Sports Costs: £" + String.format("%,.2f", totalSportsCosts));
        System.out.println("Total Food Costs: £" + String.format("%,.2f", totalFoodCosts));
        System.out.println("Grand Total: £" + String.format("%,.2f", grandTotal));
    }

    private int getMonthNumber(String monthName) {
        return switch (monthName) {
            case "January" -> 1;
            case "February" -> 2;
            case "March" -> 3;
            case "April" -> 4;
            case "May" -> 5;
            case "June" -> 6;
            case "July" -> 7;
            case "August" -> 8;
            case "September" -> 9;
            case "October" -> 10;
            case "November" -> 11;
            case "December" -> 12;
            default -> 1;
        };
    }

    @FXML
    private void generateNewInvoice() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("generate-invoice-view.fxml"));
        Stage newInvoiceStage = (Stage) adminButton.getScene().getWindow();
        newInvoiceStage.setScene(new Scene(fxmlLoader.load(), 1200, 1000));
        newInvoiceStage.setTitle("UMS - Generate New Invoice");
        newInvoiceStage.setResizable(false);
        newInvoiceStage.show();
    }

    // Setup Connection to the SQLite Database, returns null if connection fails
    protected Connection connectToDatabase() {
        // JDBC stands for Java Database Connector
        // String db = "/Users/macbookair/Documents/BRUNEL/YEAR 1/Group Project
        // B/UMS-DB.db";
        try {
            String driver = "jdbc:sqlite:";
            String dbPath = System.getProperty("user.dir") + "/UMS-DB.db";
            final String DB_URL = driver + dbPath;
            return DriverManager.getConnection(DB_URL);
            // System.out.println("Connected to SQL DB successfully.");
        } catch (SQLException e) {
            System.err.println("Database failed to connect.");
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("exports")
    public ResultSet queryTheDB(String query, String param) throws SQLException {
        Connection conn = connectToDatabase();
        PreparedStatement sqlStatement = conn.prepareStatement(query);
        sqlStatement.setString(1, param); // Avoids SQL injection
        return sqlStatement.executeQuery();
    }

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
            showError("Failed to sort data: " + e.getMessage());
        }
    }

    @FXML
    public void setTimeFilter(ActionEvent event) {
        if (isProcessingTimeFilter) {
            return;
        }

        try {
            isProcessingTimeFilter = true;
            String selectedPeriod = timeFilter.getValue();
            System.out.println("Selected filter: " + selectedPeriod);

            if (selectedPeriod == null) {
                return;
            }

            // if (selectedPeriod.equals("By Month")) {
            // monthPeriodDropdown.setVisible(true);
            // yearPeriodDropdown.setVisible(false);
            // loadTableData(); // Reset to show all data
            // } else if (selectedPeriod.equals("By Year")) {
            // monthPeriodDropdown.setVisible(false);
            // yearPeriodDropdown.setVisible(true);
            // String selectedYear = yearPeriodDropdown.getValue();
            // if (selectedYear != null) {
            // filterInvoicesByYear(selectedYear);
            // }
            // } else {
            // monthPeriodDropdown.setVisible(false);
            // yearPeriodDropdown.setVisible(false);
            // loadTableData();
            // }

            switch (selectedPeriod) {
                case "By Year" -> {
                    System.out.println("Entering By Year case");
                    monthPeriodDropdown.setVisible(false);
                    yearPeriodDropdown.setVisible(true);
                    // if (!isInitializing) {
                    // loadTableData();
                    // }
                    loadTableData();
                }
                case "By Month" -> {
                    System.out.println("Entering By Month case");
                    monthPeriodDropdown.setVisible(true);
                    yearPeriodDropdown.setVisible(false);

                    loadTableData();
                }
                case "All Records" -> {
                    monthPeriodDropdown.setVisible(false);
                    yearPeriodDropdown.setVisible(false);

                    loadTableData();
                }
            }

        } finally {
            isProcessingTimeFilter = false;
        }

    }

    public void viewInvoice(Invoice invoice) throws IOException {
        try {
            FXMLLoader viewInvFXMLLoader = new FXMLLoader(DashboardApp.class.getResource("invoice-details-view.fxml"));
            Stage viewInvoiceStage = (Stage) adminButton.getScene().getWindow();
            viewInvoiceStage.setScene(new Scene(viewInvFXMLLoader.load(), 1200, 1000));

            ViewInvoiceController controller = viewInvFXMLLoader.getController();
            if (controller == null) {
                throw new IOException("Failed to get ViewInvoiceController");
            }
            controller.setInvoice(invoice);

            viewInvoiceStage.setTitle("UMS - View Invoice Details");
            viewInvoiceStage.setResizable(false);
            viewInvoiceStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open invoice details: " + e.getMessage());
        }

    }

    protected void initializeDashboardValues() {
        ObservableList<String> categories = FXCollections.observableArrayList("Courses", "Sports", "Food", "Date");
        categoryComboBox.setItems(categories);

        ObservableList<String> orderBy = FXCollections.observableArrayList("ASC", "DESC");
        orderByComboBox.setItems(orderBy);

        ObservableList<String> timeFilters = FXCollections.observableArrayList(
                FilterType.ALL_RECORDS.toString(),
                FilterType.BY_YEAR.toString(),
                FilterType.BY_MONTH.toString());
        // "All Records", "By Month", "By Year"
        timeFilter.setItems(timeFilters);

        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
                "November", "December");
        monthPeriodDropdown.setItems(months);

        ObservableList<String> years = FXCollections.observableArrayList("2020",
                "2021", "2022", "2023", "2024", "2025");
        yearPeriodDropdown.setItems(years);

        yearPeriodDropdown.valueProperty().addListener((_, _, newValue) -> {
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

        try {
            List<Invoice> invoices = dbController.getAllInvoices();
            Set<String> uniqueUniversities = new HashSet<>();
            // Extract unique university names from invoices
            for (Invoice invoice : invoices) {
                // String uniName =
                // invoice.getInstitutionDetails().values().stream().findFirst().orElse("");
                // System.out.println(invoiceTable.getItems());
                String uniName = invoice.getInstitutionDetails().get("institutionName");
                if (uniName != null && !uniName.trim().isEmpty()) {
                    uniqueUniversities.add(uniName);
                }
            }
            ArrayList<String> univsInInvoices = new ArrayList<>();

            univsInInvoices.add("All Universities"); // Add "All Universities" to the list
            univsInInvoices.addAll(uniqueUniversities); // Add unique institution names
            Collections.sort(univsInInvoices.subList(1, univsInInvoices.size()));
            uniAverageComboBox.setItems(FXCollections.observableArrayList(univsInInvoices));
            uniAverageComboBox.setValue("All Universities");
            // loadAverageCostsChart();
            titlePerSelect.setText("Total costs for all institutions");

            System.out.println("All default data has been initialised  successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load institutions: " + e.getMessage());
        }
    }

    protected void filterInvoicesByYear(String selectedYear) {
        try {
            if (FilterType.ALL_RECORDS.toString().equals(timeFilter.getValue())) {
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
            showError("Failed to filter invoice data: " + e.getMessage());
        }
    }

    protected void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            List<Invoice> allInvoices = dbController.getAllInvoices();
            List<Invoice> filteredInvoices;

            if (searchText.isEmpty()) {
                // Reset to default view
                filteredInvoices = allInvoices;
                uniAverageComboBox.setValue("All Universities");
                loadYearlyBarChart();
            } else {
                filteredInvoices = allInvoices.stream()
                        .filter(invoice -> {
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
            }

            invoiceTable.setItems(FXCollections.observableArrayList(filteredInvoices));

            // Calculate totals for filtered results
            double totalCourseFees = 0.0;
            double totalSportsCosts = 0.0;
            double totalFoodCosts = 0.0;

            for (Invoice invoice : filteredInvoices) {
                totalCourseFees += invoice.getCourseInvFees();
                totalSportsCosts += invoice.getTotalSportsCost();
                totalFoodCosts += invoice.getTotalFoodCost();
            }

            double grandTotal = totalCourseFees + totalSportsCosts + totalFoodCosts;
            // Update summary labels
            totalFeesPerSelect.setText(String.format("£%,.2f", totalCourseFees));
            totalSportsPerSelect.setText(String.format("£%,.2f", totalSportsCosts));
            totalFoodPerSelect.setText(String.format("£%,.2f", totalFoodCosts));
            totalCosterSelect.setText(String.format("£%,.2f", grandTotal));
            // Update status message
            String statusMessage = String.format("Found %d matching records", filteredInvoices.size());
            System.out.println(statusMessage);
            titlePerSelect.setText(statusMessage);
            if (filteredInvoices.size() == 0) {
                showError(statusMessage);
            }
            // Update pie chart with new totals
            loadTotalCostsChart();
            // Update bar chart with filtered data
            loadYearlyBarChart();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to filter data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    protected void loadYearlyBarChart() {
        averageUniCostsBarChart.getData().clear();

        // Create series for each category
        XYChart.Series<String, Number> coursesSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> sportsSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> foodSeries = new XYChart.Series<>();

        try {
            List<Invoice> invoices = dbController.getAllInvoices();
            String searchText = searchField.getText().toLowerCase().trim();
            // Filter invoices based on search text if present
            if (!searchText.isEmpty()) {
                invoices = invoices.stream()
                        .filter(invoice -> {
                            String studentName = invoice.getStudentName().toLowerCase();
                            String institutionName = invoice.getInstitutionDetails().get("institutionName")
                                    .toLowerCase();
                            Map<String, String> courseDetails = invoice.getCourseList();
                            String courseName = courseDetails != null ? courseDetails.get("courseName").toLowerCase()
                                    : "";

                            return studentName.contains(searchText) ||
                                    institutionName.contains(searchText) ||
                                    courseName.contains(searchText);
                        })
                        .toList();
            }
            // Filter by selected university if not "All Universities"
            String selectedUni = uniAverageComboBox.getValue();
            if (selectedUni != null && !selectedUni.equals("All Universities")) {
                invoices = invoices.stream()
                        .filter(invoice -> selectedUni.equals(invoice.getInstitutionDetails().get("institutionName")))
                        .toList();
            }
            if (invoices.isEmpty()) {
                averageUniCostsBarChart.setTitle("No Data Available" +
                        (selectedUni != null && !selectedUni.equals("All Universities") ? " for " + selectedUni : ""));
                return;
            }
            averageUniCostsBarChart.setTitle("Average Yearly Costs by Category" +
                    (selectedUni != null && !selectedUni.equals("All Universities") ? " - " + selectedUni
                            : " - All Universities"));

            String searchTextValue = searchText.isEmpty() ? "All Records" : searchText;
            if (searchText != null && !searchText.isEmpty()) {
                averageUniCostsBarChart.setTitle("Average Yearly Costs by Category for " +
                        searchTextValue);
            }

            // Initialize yearly totals maps
            Map<String, Double> yearlyCourseCosts = new HashMap<>();
            Map<String, Double> yearlySportsCosts = new HashMap<>();
            Map<String, Double> yearlyFoodCosts = new HashMap<>();
            Map<String, Integer> yearlyInvoiceCounts = new HashMap<>();

            // Initialize years (2020-2025)
            List<String> years = List.of("2020", "2021", "2022", "2023", "2024", "2025");
            years.forEach(year -> {
                yearlyCourseCosts.put(year, 0.0);
                yearlySportsCosts.put(year, 0.0);
                yearlyFoodCosts.put(year, 0.0);
                yearlyInvoiceCounts.put(year, 0);
            });

            // Calculate totals and counts for each year
            for (Invoice invoice : invoices) {
                String year = invoice.getInvoiceDate().split("-")[0];
                if (years.contains(year)) {
                    yearlyCourseCosts.merge(year, invoice.getCourseInvFees(), Double::sum);
                    yearlySportsCosts.merge(year, invoice.getTotalSportsCost(), Double::sum);
                    yearlyFoodCosts.merge(year, invoice.getTotalFoodCost(), Double::sum);
                    yearlyInvoiceCounts.merge(year, 1, Integer::sum);
                }
            }

            // Calculate averages and add to series
            years.forEach(year -> {
                int count = yearlyInvoiceCounts.get(year);
                if (count > 0) {
                    // Calculate averages
                    double avgCourse = yearlyCourseCosts.get(year) / count;
                    double avgSports = yearlySportsCosts.get(year) / count;
                    double avgFood = yearlyFoodCosts.get(year) / count;

                    // Add to series
                    coursesSeries.getData().add(new XYChart.Data<>(year, avgCourse));
                    sportsSeries.getData().add(new XYChart.Data<>(year, avgSports));
                    foodSeries.getData().add(new XYChart.Data<>(year, avgFood));
                }
            });

            averageUniCostsBarChart.getData().addAll(coursesSeries, sportsSeries, foodSeries);
            averageUniCostsBarChart.setStyle("-fx-background-color: white;");

            styleBarChartSeries(coursesSeries, "#007A7A"); // Green for Courses
            styleBarChartSeries(sportsSeries, "#FFA84A"); // Yellow for Sports
            styleBarChartSeries(foodSeries, "#DE6600"); // Orange for Food

            xAxis.setLabel("Year");
            yAxis.setLabel("Average Amount (£)");

            double maxValue = averageUniCostsBarChart.getData().stream()
                    .flatMap(series -> series.getData().stream())
                    .mapToDouble(data -> data.getYValue().doubleValue())
                    .max()
                    .orElse(1000.0);

            // Set y-axis range with padding
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(maxValue * 1.1); // Add 10% padding
            yAxis.setTickUnit(maxValue / 10); // Create 10 tick marks

        } catch (Exception e) {
            e.printStackTrace();
            averageUniCostsBarChart.setTitle("Error Loading Data");
        }
    }

    private void styleBarChartSeries(XYChart.Series<String, Number> series, String color) {
        series.getData().forEach(data -> {
            Node node = data.getNode();
            if (node != null) {
                node.setStyle("-fx-bar-fill: " + color + ";");

                Label label = new Label(String.format("£%,.2f", data.getYValue().doubleValue()));
                label.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");

                // obs, oldNode, newNode
                data.nodeProperty().addListener((_, _, newNode) -> {
                    if (newNode != null) {
                        displayLabelForData(label, data);
                    }
                });
            }
        });
    }

    @FXML
    private void resetChartToDefault() {
        uniAverageComboBox.setValue("All Universities");
        loadYearlyBarChart();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (dbController == null) {
            dbController = new DatabaseModel();
        }

        try {
            // Test database connection
            if (!dbController.testConnection()) {
                showError("Failed to connect to database. Please check your connection and try again.");
                return;
            }
            initializeDataCache();
        } catch (Exception e) {
            showError("Database initialization error: " + e.getMessage());
            return;
        }
        initializeTableColumns();

        ObservableList<String> timeFilters = FXCollections.observableArrayList(
                "All Records", "By Year", "By Month");
        timeFilter.setItems(timeFilters);
        timeFilter.setValue("All Records");

        // Set up month dropdown with all 12 months
        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");
        monthPeriodDropdown.setItems(months);
        monthPeriodDropdown.setValue("January"); // Set default to January

        yearPeriodDropdown.setVisible(false);
        yearPeriodDropdown.setDisable(true);
        monthPeriodDropdown.setVisible(false);
        monthPeriodDropdown.setDisable(true);

        // observable, oldValue, newValue
        searchField.textProperty().addListener((_, _, _) -> {
            handleSearch();
        });

        xAxis.setLabel("Cost Categories");
        yAxis.setLabel("Amount (£)");

        initializeDashboardValues();
        loadTableData();
        loadTotalCostsChart();
        loadAverageCostsChart();
        setupEventListeners();

        Map<String, String> institutions = dbController.getInstitutions();
        ArrayList<String> universities = new ArrayList<>();
        universities.add("All Universities"); // Add option to view all universities
        universities.addAll(institutions.values()); // Add institution names
        Collections.sort(universities.subList(1, universities.size())); // Sort all except "All Universities"
        uniAverageComboBox.setItems(FXCollections.observableArrayList(universities));
        uniAverageComboBox.setValue("All Universities"); // Set default selection

        // observable, oldValue, newValue
        uniAverageComboBox.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                loadAverageCostsChart();
            }
        });

    }

    protected void initializeTableColumns() {
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
    }

    @FXML
    protected void handleAdminServices() {
        try {
            // Clean up resources
            if (dbController != null) {
                dbController.closeConnection();
            }

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
            adminStage.setTitle("UMS Finance - Administrative Services");
            adminStage.setScene(new Scene(viewAdminLoader.load()));
            adminStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load administrative services view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        Platform.runLater(() -> {
            loadingOverlay.setVisible(loading);
            timeFilter.setDisable(loading);
            monthPeriodDropdown.setDisable(loading);
            yearPeriodDropdown.setDisable(loading);
            searchField.setDisable(loading);
            orderByComboBox.setDisable(loading);
            categoryComboBox.setDisable(loading);
            if (refreshChartButton != null) {
                refreshChartButton.setDisable(loading);
            }
        });
    }

    private void initializeDataCache() {
        dataCache = new DataCache();
        try {
            dataCache.allInvoices = dbController.getAllInvoices();
            System.out.println("Loaded " + dataCache.allInvoices.size() + " total invoices");

            // Initialize month buckets (1-12)
            for (int i = 1; i <= 12; i++) {
                dataCache.monthlyData.put(i, new ArrayList<>());
                dataCache.monthlyTotals.put(i, new MonthlyTotals());
            }

            for (Invoice invoice : dataCache.allInvoices) {
                String[] dateParts = invoice.getInvoiceDate().split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);

                dataCache.monthlyData.get(month).add(invoice);

                MonthlyTotals totals = dataCache.monthlyTotals.get(month);
                totals.totalCourseFees += invoice.getCourseInvFees();
                totals.totalSportsCosts += invoice.getTotalSportsCost();
                totals.totalFoodCosts += invoice.getTotalFoodCost();
                totals.count++;

                dataCache.yearlyData
                        .computeIfAbsent(year, _ -> new ArrayList<>())
                        .add(invoice);
            }

            // Log cache statistics
            System.out.println("Cache initialized with " + dataCache.allInvoices.size() + " invoices");
            dataCache.monthlyData.forEach((month, invoices) -> System.out
                    .println("Month " + month + " has " + invoices.size() + " invoices"));

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize data cache: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // observable, oldValue, newValue
        uniAverageComboBox.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !isLoading) {
                loadYearlyBarChart();
            }
        });

        // observable, oldValue, newValue
        yearPeriodDropdown.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !isLoading) {
                loadTableData();
            }
        });

        // observable, oldValue, newValue
        monthPeriodDropdown.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !isLoading && "By Month".equals(timeFilter.getValue())) {
                loadTableData();
            }
        });

        // observable, oldValue, newValue
        searchField.textProperty().addListener((_, _, _) -> {
            if (!isLoading) {
                // Cancel any existing timer
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
                // Create new timer for 500ms delay
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> handleSearch());
                    }
                }, 500);
            }
        });

        // observable, oldValue, newValue
        timeFilter.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !isLoading) {
                setTimeFilter(null);
            }
        });
    }

    private Timer searchTimer;
}

/*
 * List of Methods:
 * - loadTotalCostsChart(): void
 * - loadAverageCostsChart(): void
 * - loadYearlyBarChart(): void
 * - styleBarChartSeries(series: XYChart.Series<String, Number>, color: String):
 * void
 * - resetChartToDefault(): void
 * - handleAdminServices(): void
 * - showError(message: String): void
 * - initializeDataCache(): void
 * - setupEventListeners(): void
 * - setLoading(loading: boolean): void
 * - initializeDashboardValues(): void
 * - initializeTableColumns(): void
 * - handleSearch(): void
 * - filterInvoicesByYear(selectedYear: String): void
 * - setTimeFilter(event: ActionEvent): void
 * - getMonthNumber(monthName: String): int
 * - queryTheDB(query: String, param: String): ResultSet
 * - connectToDatabase(): Connection
 * - generateNewInvoice(): void
 * - viewInvoice(invoice: Invoice): void
 * - sortDataByCategory(event: ActionEvent): void
 * - initialize(url: URL, resourceBundle: ResourceBundle): void
 */
