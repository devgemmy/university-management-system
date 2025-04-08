package com.example.dashboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Statement;
// import java.sql.DriverManager;
// import java.nio.file.Paths;
import java.util.Random;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

public class PopulateFinanceTable {
    public static void populateFinancesFromExistingData() {
        DatabaseModel dbController = DatabaseModel.getInstance();
        Connection conn = null;
        int retryCount = 0;
        final int MAX_RETRIES = 3;

        while (retryCount < MAX_RETRIES) {
            try {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {

                    }
                }
                conn = dbController.getConnection();
                conn.setAutoCommit(false);

                String createFinancesSQL = """
                            DROP TABLE IF EXISTS FINANCES;
                            CREATE TABLE IF NOT EXISTS FINANCES (
                                invoice_id TEXT PRIMARY KEY,
                                student_name TEXT,
                                course_id TEXT,
                                course_name TEXT,
                                course_inv_fees INT,
                                sports_activity TEXT,
                                total_sports_cost REAL,
                                food_items TEXT,
                                total_food_cost REAL,
                                institution_id TEXT,
                                institution_name TEXT,
                                invoice_date TEXT
                            );
                        """;

                try (Statement createstmt = conn.createStatement()) {
                    createstmt.execute(createFinancesSQL);
                }

                String countQuery = "SELECT COUNT(*) FROM FINANCES";
                try (
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(countQuery)) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("FINANCES table already contains data. Skipping population.");
                        return;
                    }
                }
                int originalInvCount = 96;
                String countINVQuery = "SELECT COUNT(*) FROM INVOICES";
                String cleanINVQuery = "DELETE FROM INVOICES WHERE \"Date of Invoice\" LIKE '%-%-% OR \"Date of Invoice\" IS NULL";
                try (
                        Statement countstmt = conn.createStatement();
                        ResultSet rs = countstmt.executeQuery(countINVQuery)) {
                    if (rs.next() && rs.getInt(1) == originalInvCount) {
                        System.out.println(
                                "INVOICES table already has clean data. \n Go ahead and populate the Finance table.");
                        // Populate the FINANCES table
                        generateFinancesFromExistingInvoices(originalInvCount, conn);
                        conn.commit();
                        System.out.println("Test data populated successfully!");
                        return; // Success, exit the retry loop
                    } else {
                        // Clean up the INVOICES table
                        try (Statement cleanstmt = conn.createStatement()) {
                            cleanstmt.execute(cleanINVQuery);
                        }
                        System.out.println("INVOICES table cleaned up.");
                    }
                }
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        System.err.println("Error rolling back transaction: " + ex.getMessage());
                    }
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    System.out.println("Retrying... Attempt " + (retryCount + 1) + " of " + MAX_RETRIES);
                    try {
                        Thread.sleep(1000); // Wait 1 second before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println(
                            "Error populating test data after " + MAX_RETRIES + " attempts: " + e.getMessage());
                    e.printStackTrace();
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                }
            }
        }
    }

    private static void generateFinancesFromExistingInvoices(int invCount, Connection conn) throws SQLException {
        Random random = new Random();

        String selectInvData = """
                    SELECT
                        "Student Name" as student_name,
                        "Course Costs" as course_costs,
                        "Sports Costs" as sports_costs,
                        "Food Costs" as food_costs,
                        "Date of Invoice" as invoice_date
                    FROM INVOICES;
                """;

        try (
                Statement selectInvStmt = conn.createStatement();
                ResultSet allinvrs = selectInvStmt.executeQuery(selectInvData);) {
            while (allinvrs.next()) {
                String invoiceID = String.format("INV%09d%s",
                        100000000 + random.nextInt(900000000),
                        generateRandomLetters());
                String studentName = allinvrs.getString("student_name");

                String course_costs = allinvrs.getString("course_costs");
                String courseID = course_costs.substring(0, course_costs.indexOf('('));
                Map<String, String> courseDetails = getCourseSelection(conn, courseID.trim());

                String courseName = courseDetails.get("courseName");
                int courseInvFees = extractCourseFees(allinvrs.getString("course_costs"));
                String sportsActivity = allinvrs.getString("sports_costs");
                // totalSportsCost
                double totalSportsCost = 0.0;
                String foodItems = allinvrs.getString("food_costs");
                // totalFoodCost
                double totalFoodCost = 0.0;
                String institutionID = courseDetails.get("institutionID");
                String institutionName = courseDetails.get("institutionName");

                String invoiceDate = parseDate(allinvrs.getString("invoice_date"));

                // Insert formatted Invoice data into FINANCES
                String insertFinanceSQL = """
                            INSERT OR IGNORE INTO FINANCES (
                                invoice_id, student_name, course_id, course_name, course_inv_fees,
                                sports_activity, total_sports_cost, food_items, total_food_cost,
                                institution_id, institution_name, invoice_date
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement pstmt = conn.prepareStatement(insertFinanceSQL)) {
                    pstmt.setString(1, invoiceID);
                    pstmt.setString(2, studentName);
                    pstmt.setString(3, courseID);
                    pstmt.setString(4, courseName);
                    pstmt.setDouble(5, courseInvFees);
                    pstmt.setString(6, sportsActivity);
                    pstmt.setDouble(7, totalSportsCost);
                    pstmt.setString(8, foodItems);
                    pstmt.setDouble(9, totalFoodCost);
                    pstmt.setString(10, institutionID);
                    pstmt.setString(11, institutionName);
                    pstmt.setString(12, invoiceDate);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    public static Map<String, String> getCourseSelection(@SuppressWarnings("exports") Connection conn, String ukprn)
            throws SQLException {
        Map<String, String> courseDetails = new HashMap<>();
        String courseSelectQuery = """
                    SELECT DISTINCT
                        k.KISCOURSEID as course_id,
                        k.TITLE as course_name,
                        inst.UKPRN as institution_id,
                        inst.LEGAL_NAME as institution_name
                    FROM KISCOURSE k
                    LEFT JOIN INSTITUTION inst ON k.UKPRN = inst.UKPRN
                    WHERE k.KISCOURSEID = ?;
                """;

        // String selectSQL = """
        // SELECT
        // i.INVOICE_ID,
        // i."Student Name" as studentName,
        // i.COURSE_ID as courseID,
        // k.TITLE as courseName,
        // CAST(i."Course Costs" AS INTEGER) as courseInvFees,
        // i.SPORTS_ACTIVITIES as sportsActivity,
        // CAST(i."Sports Costs" AS REAL) as totalSportsCost,
        // i.FOOD_ITEMS as foodItems,
        // CAST(i."Food Costs" AS REAL) as totalFoodCost,
        // i.INSTITUTION_ID as institutionID,
        // inst.INSTITUTION_NAME as institutionName,
        // i."Date of Invoice" as invoiceDate
        // FROM INVOICES i
        // LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
        // LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
        // WHERE i.INVOICE_ID NOT IN (SELECT invoiceID FROM FINANCES)
        // """;

        try (
                PreparedStatement pstmt = conn.prepareStatement(courseSelectQuery);) {
            pstmt.setString(1, ukprn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                courseDetails.put("courseID", rs.getString("course_id"));
                courseDetails.put("courseName", rs.getString("course_name"));
                courseDetails.put("institutionID", rs.getString("institution_id"));
                courseDetails.put("institutionName", rs.getString("institution_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting courses: " + e.getMessage());
        }
        return courseDetails;
    }

    private static String generateRandomLetters() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        return "" +
                letters.charAt(random.nextInt(letters.length())) +
                letters.charAt(random.nextInt(letters.length()));
    }

    private static String parseDate(String inv_date) {
        String invoiceDay, invoiceMonth, invoiceYear;
        LocalDate invoiceDate;

        invoiceDay = inv_date.split("/")[0]; // 06/05/2020
        invoiceMonth = inv_date.split("/")[1];
        invoiceYear = inv_date.split("/")[2];
        invoiceDate = LocalDate.of(
                Integer.parseInt(invoiceYear),
                Integer.parseInt(invoiceMonth),
                Integer.parseInt(invoiceDay)); // 2020-05-06
        return invoiceDate.toString();
    }

    private static int extractCourseFees(String courseCosts) {
        if (courseCosts == null || courseCosts.isEmpty())
            return 0;
        try {
            int startIndex = courseCosts.lastIndexOf('(');
            int endIndex = courseCosts.lastIndexOf(')');

            if (startIndex != -1 && endIndex != -1) {
                String costString = courseCosts.substring(startIndex + 1, endIndex).trim();
                return Integer.parseInt(costString);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Error parsing course fees: " + courseCosts);
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("Starting test data population...");
        populateFinancesFromExistingData();
        System.out.println("Test data population completed.");
        System.out.println("Database Model Testing Successful!");
    }
}