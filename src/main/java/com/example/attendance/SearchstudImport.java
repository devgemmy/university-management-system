package com.example.attendance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.Arrays;

public class SearchstudImport {
	// Global lock object to serialize DB write operations.
	private static final Object DB_LOCK = new Object();

	public static ResultSet ReadFromDB2(String textField) throws SQLException {

		//try (Connection c = DriverManager.getConnection(url)) {
		String driver = "jdbc:sqlite";
		//C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
		String db = "UMS Data.db";
		String url = driver + ":" + db;
		Connection c = DriverManager.getConnection(url);
		System.out.println("Made connection to db");

		//		 String sql = "SELECT StudentID, CourseID, SessionDate, Attendance FROM ATTENDANCE " +
		//                 "WHERE (StudentID LIKE ? OR CourseID LIKE ?) " +
		//                 "AND SessionDate BETWEEN ? AND ?";

		//		String sql = "SELECT StudentID, CourseID, " +
		//                "substr(SessionDate, 1, 2) || '-' || substr(SessionDate, 3, 2) || '-' || substr(SessionDate, 5, 4) AS FormattedDate, " +
		//                "Attendance " +
		//                "FROM ATTENDANCE " +
		//                "WHERE (StudentID LIKE ? OR CourseID LIKE ?) " +
		//                "AND date(substr(SessionDate, 5, 4) || '-' || substr(SessionDate, 3, 2) || '-' || substr(SessionDate, 1, 2)) " +
		//                "BETWEEN date(?) AND date(?)";

		String sql = "SELECT StudentID, CourseID, SessionDate, Attendance FROM ATTENDANCE " +
				"WHERE StudentID LIKE ? OR CourseID LIKE ? OR SessionDate LIKE ? OR Attendance LIKE ? ";

		PreparedStatement stmnt = c.prepareStatement(sql);
		String searchValue = "%" + textField + "%";

		stmnt.setString(1, searchValue);
		stmnt.setString(2, searchValue);
		stmnt.setString(3, searchValue);
		stmnt.setString(4, searchValue);

		ResultSet results = stmnt.executeQuery();


		//SELECT * FROM INSTITUTION WHERE LEGAL_NAME LIKE "%user_input%"
		//		String sql = "SELECT TITLE, CourseID, LEGAL_NAME, PUBUKPRN FROM COURSE WHERE TITLE LIKE ? OR CourseID LIKE ? OR LEGAL_NAME LIKE ? OR PUBUKPRN LIKE ? '%" + textField + "%'";
		//		PreparedStatement stmnt = c.prepareStatement(sql);
		//		ResultSet results = stmnt.executeQuery();

		//		while (results.next()) {
		//			String name = results.getString("LEGAL_NAME");
		//			System.out.println(name);
		//		}

		return results; 
	}

	public boolean processCSV(File csvFile) {
		
		System.out.println("Hi!");
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile));)
		{	
			String driver = "jdbc:sqlite";
			//C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
			String db = "UMS Data.db";
			String url = driver + ":" + db;
			Connection c = DriverManager.getConnection(url);
			System.out.println("Made connection to db"); 

			//String sql = "INSERT INTO ATTENDANCE (StudentID, CourseID, SessionDate, Attendance) VALUES (?, ?, ?, ?)";
			
			String sql = "UPDATE ATTENDANCE " +
						"SET Attendance = ? " +
						"WHERE StudentID = ? AND CourseID = ? AND SessionDate = ?";
			
			
			PreparedStatement ps = c.prepareStatement(sql);
			System.out.println(csvFile);
			
			br.readLine();
			String line;
			boolean success = false;
//			// Skip header row

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				if (data.length == 4) {
	                ps.setString(1, data[3].trim()); // StudentID
	                ps.setString(2, data[0].trim()); // CourseID
	                ps.setString(3, data[1].trim()); // SessionDate
	                ps.setString(4, data[2].trim()); // Attendance
	                int rowsAffected = ps.executeUpdate();
	                System.out.println("Updating record for StudentID: " + data[0] + " CourseID: " + data[1] + " Date: " + data[2] + " Attendance: " + data[3]);
	                
	                if (rowsAffected > 0) {
	                    System.out.println("Updated: " + Arrays.toString(data));
	                    success = true;
	                } else {
	                    System.out.println("No matching record found for: " + Arrays.toString(data));
	                }
					
					System.out.println(data[0]);
//					System.out.println("Updated: " + Arrays.toString(data));
//					success = true;
				
				} else {
					System.out.println("Invalid CSV format: " + line);
					return false;
				}
			}
			System.out.println("Successfully updated the db");
			return success;
		
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}

	// Insert student entry from Course (Integration)
	public static void insertStudent(String studentID, String courseID) {
		synchronized (DB_LOCK) {
			String driver = "jdbc:sqlite";
			String db = "UMS Data.db";
			String url = driver + ":" + db;
			String insertSql = "INSERT INTO ATTENDANCE (StudentID, CourseID, SessionDate, Attendance) VALUES (?, ?, ?, ?)";

			try (Connection c = DriverManager.getConnection(url);
				 // Create a statement to set the busy timeout.
				 Statement stmt = c.createStatement()) {

				// Set a busy timeout of 5000 ms; increase this if needed.
				stmt.execute("PRAGMA busy_timeout = 5000");

				// Check if student already exists to avoid duplicate insertion.
				String checkSql = "SELECT COUNT(*) FROM ATTENDANCE WHERE StudentID = ? AND CourseID = ?";
				try (PreparedStatement checkStmt = c.prepareStatement(checkSql)) {
					checkStmt.setString(1, studentID);
					checkStmt.setString(2, courseID);
					try (ResultSet rs = checkStmt.executeQuery()) {
						if (rs.next() && rs.getInt(1) > 0) {
							System.out.println("Student already exists in ATTENDANCE table: " + studentID + " - " + courseID);
							return; // skip insertion
						}
					}
				}

				// If not present, insert the new row.
				try (PreparedStatement ps = c.prepareStatement(insertSql)) {
					ps.setString(1, studentID);
					ps.setString(2, courseID);
					ps.setString(3, ""); // SessionDate left blank for now.
					ps.setString(4, "Course not started yet");
					ps.executeUpdate();
				}

				System.out.println("Student inserted successfully into ATTENDANCE table.");
			} catch (SQLException e) {
				System.err.println("Error inserting student into ATTENDANCE table: " + e.getMessage());
				e.printStackTrace();
			}
		} // End synchronized block.
	}

	//Delete Student entry after applicant is deleted (Integration)
	/*
	public static void deleteStudent(String studentID, String courseID) {
		String driver = "jdbc:sqlite";
		String db = "UMS Data.db";
		String url = driver + ":" + db;
		String deleteSQL = "DELETE FROM ATTENDANCE WHERE StudentID = ? AND CourseID = ?";

		try (Connection c = DriverManager.getConnection(url);
			 PreparedStatement ps = c.prepareStatement(deleteSQL)) {

			ps.setString(1, studentID);
			ps.setString(2, courseID);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Deleted attendance record for StudentID: " + studentID + ", CourseID: " + courseID);
			} else {
				System.out.println("No matching attendance record found for StudentID: " + studentID + ", CourseID: " + courseID);
			}
		} catch (SQLException e) {
			System.err.println("Error deleting from ATTENDANCE: " + e.getMessage());
			e.printStackTrace();
		}
	}
	 */

	// Archive student entry (instead of deletion)

	public static void archiveStudent(String studentID, String courseID) {
		String driver = "jdbc:sqlite";
		String db = "UMS Data.db";
		String url = driver + ":" + db;

		// SQL to ensure the archive table exists.
		String createArchiveTableSQL = "CREATE TABLE IF NOT EXISTS ARCHIVED_ATTENDANCE AS SELECT * FROM ATTENDANCE WHERE 0";
		// SQL to copy the record to the archive table.
		String insertArchiveSQL = "INSERT INTO ARCHIVED_ATTENDANCE SELECT * FROM ATTENDANCE WHERE StudentID = ? AND CourseID = ?";
		// SQL to delete the record from the active table.
		String deleteActiveSQL = "DELETE FROM ATTENDANCE WHERE StudentID = ? AND CourseID = ?";

		try (Connection c = DriverManager.getConnection(url);
			 Statement stmt = c.createStatement()) {

			// Ensure the archive table exists.
			stmt.execute(createArchiveTableSQL);

			// Archive the record: Copy it into ARCHIVED_ATTENDANCE.
			try (PreparedStatement psArchive = c.prepareStatement(insertArchiveSQL)) {
				psArchive.setString(1, studentID);
				psArchive.setString(2, courseID);
				int inserted = psArchive.executeUpdate();
				if (inserted > 0) {
					System.out.println("Record archived in ARCHIVED_ATTENDANCE for StudentID: " + studentID + ", CourseID: " + courseID);
				} else {
					System.out.println("No matching record found in ATTENDANCE for archiving for StudentID: " + studentID + ", CourseID: " + courseID);
				}
			}

			// Delete the record from the active ATTENDANCE table.
			try (PreparedStatement psDelete = c.prepareStatement(deleteActiveSQL)) {
				psDelete.setString(1, studentID);
				psDelete.setString(2, courseID);
				int rowsAffected = psDelete.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Deleted attendance record for StudentID: " + studentID + ", CourseID: " + courseID + " from ATTENDANCE table.");
				} else {
					System.out.println("No matching attendance record found for deletion in ATTENDANCE for StudentID: " + studentID + ", CourseID: " + courseID);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error archiving record: " + e.getMessage());
			e.printStackTrace();
		}
	}




	
//	public static ResultSet ReadFromDBWithDateRange(String studentId, String startDate, String endDate) throws SQLException {
//	    String driver = "jdbc:sqlite";
//	    String db = "C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db"; 
//	    String url = driver + ":" + db;
//
//	    try (Connection connection = DriverManager.getConnection(url)) {
////	        String sql = "SELECT StudentID, CourseID, SessionDate, Attendance FROM ATTENDANCE " +
////	                     "WHERE StudentID = ? AND " +
////	                     "DATE(substr(SessionDate, 7, 4) || '-' || substr(SessionDate, 4, 2) || '-' || substr(SessionDate, 1, 2)) " +
////	                     "BETWEEN DATE(?) AND DATE(?)";
//	    	
//	    	String sql = "SELECT StudentID, CourseID, SessionDate, Attendance FROM ATTENDANCE " +
//	                "WHERE StudentID = ? AND " +
//	                "DATE(substr(SessionDate, 7, 4) || '-' || substr(SessionDate, 4, 2) || '-' || substr(SessionDate, 1, 2)) " +
//	                "BETWEEN DATE(?) AND DATE(?)";
//	                //">= DATE(?) AND DATE(substr(SessionDate, 7, 4) || '-' || substr(SessionDate, 4, 2) || '-' || substr(SessionDate, 1, 2)) <= DATE(?)";
//
//	        //System.out.println("SQL Query: " + sql); 
//	        System.out.println("Student ID: " + studentId);
//	        System.out.println("Start Date: " + startDate);
//	        System.out.println("End Date: " + endDate);
//
//	        try (PreparedStatement stmnt = connection.prepareStatement(sql)) {
//	            stmnt.setString(1, studentId);
//	            stmnt.setString(2, startDate);
//	            stmnt.setString(3, endDate);
//
//	            System.out.println("Database connection successful!");
//
//	            return stmnt.executeQuery();
//	        } catch (SQLException e) {
//	            System.out.println("Error executing the query: " + e.getMessage());
//	            throw e;
//	        }
//	    } catch (SQLException e) {
//	        System.out.println("Error establishing database connection: " + e.getMessage());
//	        throw e;
//	    }
//	}
}
