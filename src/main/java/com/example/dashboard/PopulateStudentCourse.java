package com.example.dashboard;

import java.sql.*;
import java.util.Map;

import static com.example.dashboard.PopulateFinanceTable.getCourseSelection;

public class PopulateStudentCourse {
    private static DatabaseModel dbModel = new DatabaseModel();

    /*
    public static void main(String[] args) throws SQLException {
        // Case 0 (OLD)
        // String studentID = "210810GO-ADF", courseID = "QV54-2000", studentName = "Aisha Dunya Freira";
        // Case 1 (ADDED)
        String studentID = "241205FG-PA", courseID = "BSRDIF-B821", studentName = "Patricia Asikpo";
        // Case 2 (NEW)
        // String studentID = "251004XD-OO", courseID = "K00676", studentName = "Zitara Uche";
        // Case 3 (NEW)
        // String studentID = "251203IS-FA", courseID = "NTAD162_1", studentName = "Frances Asikpo";

        getStudentDetails(studentID, courseID, studentName);
    }
    */

    // Insert student entry from CourseAssigner (Integration)
    public static void getStudentDetails(String studentID, String courseID, String studentName) throws SQLException {
        // System.out.println("Student ID: " + studentID + "\nCourse ID: " + courseID + "\nStudent Name: " + studentName);
        String checkStudentQuery = """
              SELECT student_id, student_name FROM STUDENTS WHERE student_id = ? OR student_name = ?
            """;
        String insertStudentQuery = """
              INSERT INTO STUDENTS (student_id, student_name, course_id) VALUES (?, ?, ?);
            """;
        try (
                Connection conn = dbModel.getConnection();
                PreparedStatement stmt = conn.prepareStatement(checkStudentQuery)
        ) {
            stmt.setString(1, studentID);
            stmt.setString(2, studentName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   System.out.println("This student " + studentName + " has already been enrolled.");
                } else {
                    System.out.println("Storing new student details ...");
                    try (PreparedStatement pstmt = conn.prepareStatement(insertStudentQuery)) {
                        pstmt.setString(1, studentID);
                        pstmt.setString(2, studentName);
                        pstmt.setString(3, courseID);
                        pstmt.executeUpdate();
                        System.out.println("New student: " + studentName + " has been enrolled.");
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void archiveInvoice(String studentID) {
        String createArchiveTable = """
            CREATE TABLE IF NOT EXISTS INV_ARCHIVES (
                  invoice_id TEXT PRIMARY KEY,
                  student_id TEXT,
                  student_name TEXT,
                  course_details TEXT,
                  sports_activity TEXT,
                  food_items TEXT,
                  institution_id TEXT,
                  institution_name TEXT,
                  invoice_date TEXT,
                  order_history TEXT,
                  order_totals REAL
              );
         """;

        String insertArchivedInv = """
            INSERT INTO INV_ARCHIVES (
                invoice_id,
                student_id,
                student_name,
                course_details,
                sports_activity,
                food_items,
                institution_id,
                institution_name,
                invoice_date,
                order_history,
                order_totals
            )
            SELECT
                f.invoice_id,
                st.student_id,
                st.student_name,
                f.course_details,
                f.sports_activity,
                f.food_items,
                f.institution_id,
                f.institution_name,
                f.invoice_date,
                o.order_history,
                o.order_totals
            FROM
                FINANCES f
            LEFT JOIN
                STUDENTS st ON f.student_id = st.student_id
            LEFT JOIN
                ORDERS o ON f.invoice_id = o.invoice_id
            WHERE
                st.student_id = ?
        """;
        // SQL to delete the record from the active tables.
        String deleteFinanceRec = "DELETE FROM FINANCES WHERE student_id = ?";
        String deleteStudentRec = "DELETE FROM STUDENTS WHERE student_id = ?";
        String deleteOrderRec = "DELETE FROM ORDERS WHERE student_id = ?";

        try (
                Connection conn = dbModel.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            // Ensure the archive table exists.
            stmt.execute(createArchiveTable);
            // Archive the record: Copy it into INV_ARCHIVES
            try (PreparedStatement invArchive = conn.prepareStatement(insertArchivedInv)) {
                invArchive.setString(1, studentID);
                int inserted = invArchive.executeUpdate();
                if (inserted > 0) {
                    System.out.println("Invoice archived in INV_ARCHIVES for StudentID: " + studentID);
                } else {
                    System.out.println("No matching record found in FINANCES to archive StudentID: " + studentID);
                }
            }

            // Delete the record from the FINANCES table.
            try (PreparedStatement invDelete = conn.prepareStatement(deleteFinanceRec)) {
                invDelete.setString(1, studentID);
                int rowsAffected = invDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Deleted finance record for StudentID: " + studentID + " from FINANCES table.");
                } else {
                    System.out.println("No matching finance record found for deletion in FINANCES for StudentID: " + studentID);
                }
            }

            // Delete the record from the STUDENTS table.
            try (PreparedStatement studDelete = conn.prepareStatement(deleteStudentRec)) {
                studDelete.setString(1, studentID);
                int rowsAffected = studDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Deleted student record for StudentID: " + studentID + " from STUDENTS table.");
                } else {
                    System.out.println("No matching student record found for deletion in STUDENTS for StudentID: " + studentID);
                }
            }

            // Delete the record from the ORDERS table.
            try (PreparedStatement orderDelete = conn.prepareStatement(deleteOrderRec)) {
                orderDelete.setString(1, studentID);
                int rowsAffected = orderDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Deleted order history record for StudentID: " + studentID + " from ORDERS table.");
                } else {
                    System.out.println("No matching order history record found for deletion in ORDERS for StudentID: " + studentID);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error archiving record: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

