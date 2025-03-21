package com.example.dashboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class InvoiceGenerationTest {
    public static void main(String[] args) {
        try {
            // Get database connection
            Connection conn = DatabaseController.connect();
            conn.setAutoCommit(false);

            try {
                // Step 1: Insert into INVOICES table first
                String insertInvoicesSQL = """
                            INSERT INTO INVOICES (
                                "Student Name", "Course Costs", "Sports Costs",
                                "Food Costs", "Date of Invoice"
                            ) VALUES (?, ?, ?, ?, ?)
                        """;

                PreparedStatement invoicesStmt = conn.prepareStatement(insertInvoicesSQL);

                // Set test data for INVOICES
                invoicesStmt.setString(1, "Test Student");
                invoicesStmt.setString(2, "COMP101 (9250.00)"); // CourseID (Cost) format
                invoicesStmt.setString(3, "150.00");
                invoicesStmt.setString(4, "75.00");
                invoicesStmt.setString(5, LocalDate.now().toString());

                int result = invoicesStmt.executeUpdate();
                System.out.println("INVOICES insert result: " + result);

                if (result > 0) {
                    // Step 2: Generate invoice ID for FINANCES table
                    String invoiceId = "INV" + String.format("%09d", System.currentTimeMillis() % 1000000000) + "TS";

                    // Step 3: Insert into FINANCES table
                    String insertFinancesSQL = """
                                INSERT INTO FINANCES (
                                    invoiceID, studentName, courseID, courseName, courseInvFees,
                                    sportsActivity, totalSportsCost, foodItems, totalFoodCost,
                                    institutionID, institutionName, invoiceDate
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;

                    PreparedStatement financesStmt = conn.prepareStatement(insertFinancesSQL);

                    // Set test data for FINANCES
                    financesStmt.setString(1, invoiceId);
                    financesStmt.setString(2, "Test Student");
                    financesStmt.setString(3, "COMP101");
                    financesStmt.setString(4, "Computer Science");
                    financesStmt.setDouble(5, 9250.00);
                    financesStmt.setString(6, "Gym (100.00); Swimming (50.00)");
                    financesStmt.setDouble(7, 150.00);
                    financesStmt.setString(8, "Meal Plan A (75.00)");
                    financesStmt.setDouble(9, 75.00);
                    financesStmt.setString(10, "10007777");
                    financesStmt.setString(11, "Test University");
                    financesStmt.setString(12, LocalDate.now().toString());

                    result = financesStmt.executeUpdate();
                    System.out.println("FINANCES insert result: " + result);

                    // If both inserts successful, commit the transaction
                    conn.commit();
                    System.out.println("Successfully generated invoice: " + invoiceId);
                } else {
                    conn.rollback();
                    System.out.println("Failed to insert into INVOICES table");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error during invoice generation: " + e.getMessage());
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}