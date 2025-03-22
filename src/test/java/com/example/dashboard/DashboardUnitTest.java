package com.example.dashboard;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import javafx.event.ActionEvent;
import java.sql.Connection;
// import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardUnitTest {
    private DashboardController dashboard;

    @BeforeEach
    void setUp() {
        dashboard = new DashboardController();
    }

    @Test
    void testConnectToDatabase() throws SQLException {
        Connection actual = dashboard.connectToDatabase();
        System.out.println("Expected: A valid Connection object");
        System.out.println("Actual: " + actual);
        assertNotNull(actual, "Test failed ❌: Connection should not be null");
        System.out.println("Test passed ✅");
    }

    // @Test
    // void testQueryTheDB() throws SQLException {
    // ResultSet actual = dashboard.queryTheDB("SELECT * FROM invoices", "");
    // System.out.println("Expected: A valid ResultSet object");
    // System.out.println("Actual: " + actual);
    // assertNotNull(actual, "Test failed ❌: ResultSet should not be null");
    // System.out.println("Test passed ✅");
    // }

    // @Test
    // void testFilterInvoicesByYear() {
    // System.out.println("Testing filterInvoicesByYear");
    // dashboard.filterInvoicesByYear("2024");
    // System.out.println("Test passed ✅");
    // }

    // @Test
    // void testLoadCharts() {
    // dashboard.loadTotalCostsChart();
    // dashboard.loadAverageCostsChart();
    // dashboard.loadYearlyBarChart();
    // System.out.println("Charts loaded successfully ✅");
    // }

    // @Test
    // void testHandleAdminServices() {
    // dashboard.handleAdminServices();
    // System.out.println("Admin services handled successfully ✅");
    // }

    // @Test
    // void testInitializeDashboardValues() {
    // dashboard.initializeDashboardValues();
    // System.out.println("Dashboard values initialized ✅");
    // }

    // @Test
    // void testInitializeTableColumns() {
    // dashboard.initializeTableColumns();
    // System.out.println("Table columns initialized ✅");
    // }

    // @Test
    // void testHandleSearch() {
    // dashboard.handleSearch();
    // System.out.println("Search handled successfully ✅");
    // }

    // @Test
    // void testSetTimeFilter() {
    // ActionEvent event = new ActionEvent();
    // dashboard.setTimeFilter(event);
    // System.out.println("Time filter set successfully ✅");
    // }

    // @Test
    // void testSortDataByCategory() {
    // ActionEvent event = new ActionEvent();
    // dashboard.sortDataByCategory(event);
    // System.out.println("Data sorted by category ✅");
    // }
}