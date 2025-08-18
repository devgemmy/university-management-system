package com.example.attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchCourseImport {
	
	public static ResultSet ReadFromDB1(String textField) throws SQLException {

		String driver = "jdbc:sqlite";
		//C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
		String db = "UMS Data.db";
		String url = driver + ":" + db;
		Connection c = DriverManager.getConnection(url);

		System.out.println("Made connection to db");
		
		String sql = "SELECT TITLE, CourseID, LEGAL_NAME, PUBUKPRN FROM COURSE " +
	             "WHERE TITLE LIKE ? OR CourseID LIKE ? OR LEGAL_NAME LIKE ? OR PUBUKPRN LIKE ?";

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

}
