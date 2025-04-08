package com.example.dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.time.format.DateTimeFormatter;

/**
 * Example class demonstrating how to handle invoiceID generation
 * when transferring data from INVOICES to FINANCES table
 */
public class FinanceTable {

    private static final String DB_URL = "jdbc:sqlite:UMS-DB.db";
    // private static final DateTimeFormatter DATE_FORMATTER =
    // DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Transfers data from INVOICES to FINANCES table, generating new invoiceIDs
     * for existing invoices that don't have one
     */
    public void populateFinancesFromExistingData() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // First, check if FINANCES table is empty
            String countQuery = "SELECT COUNT(*) FROM FINANCES";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(countQuery)) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("FINANCES table already contains data. Skipping population.");
                    return;
                }
            }

            // Query to join INVOICES, INSTITUTION, and KISCOURSE tables
            // Note: We're not selecting INVOICE_ID as it doesn't exist in INVOICES table
            String selectDataSQL = """
                        SELECT
                            i."Student Name" as STUDENT_NAME,
                            k.KISCOURSEID,
                            k.TITLE as COURSE_NAME,
                            i."Course Costs" as COURSE_FEES,
                            i."Sports Activities" as SPORTS_ACTIVITIES,
                            CAST(i."Sports Costs" AS REAL) as SPORTS_TOTAL_COST,
                            i."Food Items" as FOOD_ITEMS,
                            CAST(i."Food Costs" AS REAL) as FOOD_TOTAL_COST,
                            inst.UKPRN,
                            inst.INSTITUTION_NAME,
                            i."Date of Invoice" as INVOICE_DATE
                        FROM INVOICES i
                        LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
                        LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
                    """;

            String insertSQL = """
                        INSERT INTO FINANCES (
                            invoiceID, studentName, courseID, courseName, courseInvFees,
                            sportsActivity, totalSportsCost, foodItems, totalFoodCost,
                            institutionID, institutionName, invoiceDate
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (Statement selectStmt = conn.createStatement();
                    ResultSet rs = selectStmt.executeQuery(selectDataSQL);
                    PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                while (rs.next()) {
                    // Generate a new invoiceID for each existing invoice
                    String invoiceId = generateInvoiceID();

                    // Extract course fees from the formatted string (e.g., "COMP101 (9250.00)")
                    String courseCosts = rs.getString("COURSE_FEES");
                    double courseFees = extractCourseFees(courseCosts);

                    // Set values for FINANCES table
                    insertStmt.setString(1, invoiceId);
                    insertStmt.setString(2, rs.getString("STUDENT_NAME"));
                    insertStmt.setString(3, rs.getString("KISCOURSEID"));
                    insertStmt.setString(4, rs.getString("COURSE_NAME"));
                    insertStmt.setDouble(5, courseFees);
                    insertStmt.setString(6, rs.getString("SPORTS_ACTIVITIES"));
                    insertStmt.setDouble(7, rs.getDouble("SPORTS_TOTAL_COST"));
                    insertStmt.setString(8, rs.getString("FOOD_ITEMS"));
                    insertStmt.setDouble(9, rs.getDouble("FOOD_TOTAL_COST"));
                    insertStmt.setString(10, rs.getString("UKPRN"));
                    insertStmt.setString(11, rs.getString("INSTITUTION_NAME"));
                    insertStmt.setString(12, rs.getString("INVOICE_DATE"));

                    insertStmt.executeUpdate();
                }
                System.out.println("Data successfully populated into FINANCES table with new invoiceIDs.");
            }
        } catch (SQLException e) {
            System.err.println("Error populating FINANCES table: " + e.getMessage());
        }
    }

    /**
     * Generates a unique invoiceID using timestamp
     * Format: "INV" + 9-digit number + "TS"
     */
    private String generateInvoiceID() {
        return "INV" + String.format("%09d", System.currentTimeMillis() % 1000000000) + "TS";
    }

    /**
     * Extracts course fees from the formatted string
     * Example: "COMP101 (9250.00)" -> 9250.00
     */
    private double extractCourseFees(String courseCosts) {
        if (courseCosts == null || courseCosts.isEmpty()) {
            return 0.0;
        }

        try {
            // Find the last occurrence of '(' and ')'
            int startIndex = courseCosts.lastIndexOf('(');
            int endIndex = courseCosts.lastIndexOf(')');

            if (startIndex != -1 && endIndex != -1) {
                String costString = courseCosts.substring(startIndex + 1, endIndex).trim();
                return Double.parseDouble(costString);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Error parsing course fees: " + courseCosts);
        }

        return 0.0;
    }

    public static void main(String[] args) {
        FinanceTable example = new FinanceTable();
        example.populateFinancesFromExistingData();
    }
}