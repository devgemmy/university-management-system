package com.example.course;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	 private static final String URL = "jdbc:sqlite:src/main/resources/com/example/course/courseselec.db";
	

	    public static Connection connect() {
	        try {
	            return DriverManager.getConnection(URL);
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
}
