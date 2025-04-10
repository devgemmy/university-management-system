package com.example.dashboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class InvoiceGenerationUnitTest {

    @Test
    public void testInvoiceGeneration() {
        try (Connection conn = DatabaseModel.connect()) {
            assertNotNull(conn, "Database connection should not be null");
            conn.setAutoCommit(false);

            try {
                // Insert into INVOICES table
                // String insertInvoicesSQL = """
                // INSERT INTO INVOICES (
                // "Student Name", "Course Costs", "Sports Costs",
                // "Food Costs", "Date of Invoice"
                // ) VALUES (?, ?, ?, ?, ?)
                // """;

                // try (PreparedStatement invoicesStmt =
                // conn.prepareStatement(insertInvoicesSQL)) {
                // invoicesStmt.setString(1, "Test Student");
                // invoicesStmt.setString(2, "COMP101 (9250.00)");
                // invoicesStmt.setString(3, "150.00");
                // invoicesStmt.setString(4, "75.00");
                // invoicesStmt.setString(5, LocalDate.now().toString());

                // int invoicesResult = invoicesStmt.executeUpdate();
                // assertEquals(1, invoicesResult, "INVOICES insert should be successful");
                // }

                String invoiceId = "INV" + String.format("%09d", System.currentTimeMillis() % 1000000000) + "TS";
                // Insert into FINANCES table
                String insertFinancesSQL = """
                            INSERT INTO FINANCES (
                                invoice_id, student_id, student_name, course_id, course_details, course_inv_fees,
                                sports_activity, total_sports_cost, food_items, total_food_cost,
                                institution_id, institution_name, invoice_date
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement financesStmt = conn.prepareStatement(insertFinancesSQL)) {
                    financesStmt.setString(1, invoiceId);
                    financesStmt.setString(1, "252007-TS");
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

                    int financesResult = financesStmt.executeUpdate();
                    assertEquals(1, financesResult, "FINANCES insert should be successful");
                }

                conn.commit();
                System.out.println("Successfully generated invoice: " + invoiceId);

            } catch (SQLException e) {
                conn.rollback();
                fail("Error during invoice generation: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            fail("Database connection error: " + e.getMessage());
        }
    }
}