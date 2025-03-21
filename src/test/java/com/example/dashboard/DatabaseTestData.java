package com.example.dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class for populating test data in the database
 */
public class DatabaseTestData {
    private static final int MAX_RETRIES = 3;
    private static final Random random = new Random();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static Connection getConnection() throws SQLException {
        String dbPath = System.getProperty("user.dir") + "/UMS-DB.db";
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    private static String generateRandomInvoiceId() {
        String prefix = "INV";
        String numbers = String.format("%09d", Math.abs(random.nextInt(1000000000)));
        String suffix = UUID.randomUUID().toString().substring(0, 2).toUpperCase();
        return prefix + numbers + suffix;
    }

    private static LocalDate generateRandomDate() {
        int year = 2020 + random.nextInt(5); // 2020-2024
        int month = 1 + random.nextInt(12); // 1-12
        int day = 1 + random.nextInt(28); // 1-28 (to avoid invalid dates)
        return LocalDate.of(year, month, day);
    }

    private static double generateRandomAmount(double min, double max) {
        return Math.round((min + (max - min) * random.nextDouble()) * 100.0) / 100.0;
    }

    public static void populateTestData() {
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount < MAX_RETRIES) {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // Generate and insert multiple invoices
                    for (int i = 0; i < 50; i++) {
                        String invoiceId = generateRandomInvoiceId();
                        LocalDate invoiceDate = generateRandomDate();

                        // Insert into INVOICES table
                        String insertInvoiceSQL = """
                                    INSERT INTO INVOICES (
                                        INVOICE_ID, STUDENT_NAME, INVOICE_DATE
                                    ) VALUES (?, ?, ?)
                                """;

                        try (PreparedStatement stmt = conn.prepareStatement(insertInvoiceSQL)) {
                            stmt.setString(1, invoiceId);
                            stmt.setString(2, "Student " + (i + 1));
                            stmt.setString(3, invoiceDate.format(dateFormatter));
                            stmt.executeUpdate();
                        }

                        // Insert into FINANCES table
                        String insertFinanceSQL = """
                                    INSERT INTO FINANCES (
                                        INVOICE_ID, STUDENT_NAME, COURSE_NAME, COURSE_FEE,
                                        SPORTS_ACTIVITIES, SPORTS_COSTS, FOOD_ITEMS, FOOD_COSTS,
                                        INVOICE_DATE, INSTITUTION_ID, INSTITUTION_NAME
                                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """;

                        try (PreparedStatement stmt = conn.prepareStatement(insertFinanceSQL)) {
                            double courseFee = generateRandomAmount(1000, 35000);
                            double sportsCost = generateRandomAmount(50, 500);
                            double foodCost = generateRandomAmount(20, 200);

                            stmt.setString(1, invoiceId);
                            stmt.setString(2, "Student " + (i + 1));
                            stmt.setString(3, "Course " + (i % 10 + 1));
                            stmt.setDouble(4, courseFee);
                            stmt.setString(5, "Activity " + (i % 5 + 1));
                            stmt.setDouble(6, sportsCost);
                            stmt.setString(7, "Food Item " + (i % 3 + 1));
                            stmt.setDouble(8, foodCost);
                            stmt.setString(9, invoiceDate.format(dateFormatter));
                            stmt.setString(10, "INST" + (10000000 + i));
                            stmt.setString(11, "University " + (i % 5 + 1));
                            stmt.executeUpdate();
                        }
                    }

                    // Commit all changes
                    conn.commit();
                    success = true;
                    System.out.println("Test data populated successfully!");

                } catch (SQLException e) {
                    // Rollback on error
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                    }
                    throw e;
                }
            } catch (SQLException e) {
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    System.err.println("Attempt " + retryCount + " failed. Retrying...");
                    try {
                        Thread.sleep(1000 * retryCount); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println(
                            "Error populating test data after " + MAX_RETRIES + " attempts: " + e.getMessage());
                }
            }
        }
    }

    private static void clearExistingData() {
        try (Connection conn = getConnection()) {
            // Clear INVOICES table
            try (Statement stmt = conn.createStatement();) {
                stmt.executeUpdate("DELETE FROM INVOICES");
                System.out.println("Cleared INVOICES table");
            }

            // Clear FINANCES table
            try (Statement stmt = conn.createStatement();) {
                stmt.executeUpdate("DELETE FROM FINANCES");
                System.out.println("Cleared FINANCES table");
            }

        } catch (SQLException e) {
            System.err.println("Error clearing existing data: " + e.getMessage());
        }
    }

    private static void createTablesIfNotExist() {
        try (Connection conn = getConnection()) {
            // Create INVOICES table
            try (Statement stmt = conn.createStatement();) {
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS INVOICES (
                                INVOICE_ID TEXT PRIMARY KEY,
                                STUDENT_NAME TEXT,
                                INVOICE_DATE TEXT
                            )
                        """);
            }

            // Create FINANCES table
            try (Statement stmt = conn.createStatement();) {
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS FINANCES (
                                INVOICE_ID TEXT PRIMARY KEY,
                                STUDENT_NAME TEXT,
                                COURSE_NAME TEXT,
                                COURSE_FEE REAL,
                                SPORTS_ACTIVITIES TEXT,
                                SPORTS_COSTS REAL,
                                FOOD_ITEMS TEXT,
                                FOOD_COSTS REAL,
                                INVOICE_DATE TEXT,
                                INSTITUTION_ID TEXT,
                                INSTITUTION_NAME TEXT,
                                FOREIGN KEY(INVOICE_ID) REFERENCES INVOICES(INVOICE_ID)
                            )
                        """);
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting test data population...");
        populateTestData();
        System.out.println("Test data population completed.");
    }
}