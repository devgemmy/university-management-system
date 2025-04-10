package com.example.dashboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Statement;
// import java.sql.DriverManager;
// import java.nio.file.Paths;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

public class DatabaseModelTest {
    protected static final String[] STUDENT_NAMES = {
            "Oliver Bennett", "Emma Sanders", "Liam Peterson", "Sophia Ramirez", "Elijah Foster",
            "Isabella Collins", "Jackson Reed", "Mia Scott", "Aiden Barnes", "Lily Richardson",
            "Caleb Hayes", "Ava Coleman", "Nathan Stewart", "Zoe James", "Sebastian Wright",
            "Madison Torres", "Evan Parker", "Grace Hughes", "Carter Green", "Emily Nelson",
            "Julian Brooks", "Hannah Simmons", "Wyatt Turner", "Natalie Jenkins", "Gabriel Murphy",
            "Lucy Bryant", "Dylan Ross", "Victoria Patterson", "Leo Edwards", "Scarlett Mitchell"
    };

    // private static final String DB_URL = "jdbc:sqlite:"
    // + Paths.get(System.getProperty("user.dir"), "UMS-DB.db").toString();

    // private Connection getConnection() throws SQLException {
    // return DriverManager.getConnection(DB_URL);
    // }

    private static class InstitutionInfo {
        String ukprn;
        String name;
        List<CourseInfo> courses;

        InstitutionInfo(String ukprn, String name) {
            this.ukprn = ukprn;
            this.name = name;
            this.courses = new ArrayList<>();
        }
    }

    private static class CourseInfo {
        String id;
        String title;
        double fee;

        CourseInfo(String id, String title) {
            this.id = id;
            this.title = title;
            // Set a random fee between £9,000 and £25,000
            this.fee = 9000 + new Random().nextInt(25001);
        }
    }

    public static void populateTestData() {
        DatabaseModel dbController = DatabaseModel.getInstance();
        Connection conn = null;
        Random random = new Random();
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
                Map<String, InstitutionInfo> institutions = loadInstitutionsAndCourses(conn);
                List<String> availableSports = getAvailableSports(conn);
                List<String> availableFoods = getAvailableFoods(conn);
                String createFinancesSQL = """
                            CREATE TABLE IF NOT EXISTS FINANCES (
                                "invoice_id" TEXT PRIMARY KEY,
                                "student_id" TEXT,
                                "student_name" TEXT,
                                "course_id" TEXT,
                                "course_details" TEXT,
                                "course_inv_fees" INT,
                                "sports_activity" TEXT,
                                "total_sports_cost" REAL,
                                "food_items" TEXT,
                                "total_food_cost" REAL,
                                "institution_id" TEXT,
                                "institution_name" TEXT,
                                "invoice_date" TEXT,
                            )
                        """;

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createFinancesSQL);
                }

                // Now add new records
                for (int i = 0; i < 30; i++) {
                    // Generate random date between 2020-2025
                    int year = 2020 + random.nextInt(6);
                    int month = 1 + random.nextInt(12);
                    int day = 1 + random.nextInt(28);
                    LocalDate invoiceDate = LocalDate.of(year, month, day);

                    String invoiceId = String.format("INV%09d%s",
                            100000000 + random.nextInt(900000000),
                            generateRandomLetters());

                    String studentId = String.format("STU%04d%s",
                            1000 + random.nextInt(9000),
                            generateRandomLetters());

                    // Select random institution and course
                    List<InstitutionInfo> institutionList = new ArrayList<>(institutions.values());
                    InstitutionInfo institution = institutionList.get(random.nextInt(institutionList.size()));
                    CourseInfo course = institution.courses.get(random.nextInt(institution.courses.size()));

                    // Generate sports activities string
                    StringBuilder sportsStr = new StringBuilder();
                    double totalSportsCost = 0.0;
                    int numSports = 1 + random.nextInt(2);
                    Set<Integer> usedSportsIndices = new HashSet<>();

                    for (int j = 0; j < numSports && j < availableSports.size(); j++) {
                        int sportIndex;
                        do {
                            sportIndex = random.nextInt(availableSports.size());
                        } while (usedSportsIndices.contains(sportIndex));
                        usedSportsIndices.add(sportIndex);

                        double sportCost = 20 + random.nextInt(31); // Random cost between £20-£50
                        if (sportsStr.length() > 0)
                            sportsStr.append(";");
                        sportsStr.append(String.format("%s (%.2f)",
                                availableSports.get(sportIndex), sportCost));
                        totalSportsCost += sportCost;
                    }

                    // Generate food items string
                    StringBuilder foodStr = new StringBuilder();
                    double totalFoodCost = 0.0;
                    int numFoods = 1 + random.nextInt(2);
                    Set<Integer> usedFoodIndices = new HashSet<>();

                    for (int j = 0; j < numFoods && j < availableFoods.size(); j++) {
                        int foodIndex;
                        do {
                            foodIndex = random.nextInt(availableFoods.size());
                        } while (usedFoodIndices.contains(foodIndex));
                        usedFoodIndices.add(foodIndex);

                        double foodCost = 5 + random.nextInt(26); // Random cost between £5-£30
                        if (foodStr.length() > 0)
                            foodStr.append(";");
                        foodStr.append(String.format("%s (%.2f)",
                                availableFoods.get(foodIndex), foodCost));
                        totalFoodCost += foodCost;
                    }

                    // Insert into FINANCES
                    String insertFinanceSQL = """
                                INSERT OR IGNORE INTO FINANCES (
                                    invoice_id, student_id, student_name, course_id, course_details, course_inv_fees,
                                    sports_activity, total_sports_cost, food_items, total_food_cost,
                                    institution_id, institution_name, invoice_date
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;

                    try (PreparedStatement pstmt = conn.prepareStatement(insertFinanceSQL)) {
                        pstmt.setString(1, invoiceId);
                        pstmt.setString(2, studentId);
                        pstmt.setString(3, STUDENT_NAMES[i]);
                        pstmt.setString(4, course.id);
                        pstmt.setString(5, course.title);
                        pstmt.setDouble(6, course.fee);
                        pstmt.setString(7, sportsStr.toString());
                        pstmt.setDouble(8, totalSportsCost);
                        pstmt.setString(9, foodStr.toString());
                        pstmt.setDouble(10, totalFoodCost);
                        pstmt.setString(11, institution.ukprn);
                        pstmt.setString(12, institution.name);
                        pstmt.setString(13, invoiceDate.toString());
                        pstmt.executeUpdate();
                    }

                    // // Insert into INVOICES
                    // String insertInvoiceSQL = """
                    // INSERT INTO INVOICES (
                    // "Student Name", "Course Costs", "Sports Costs",
                    // "Food Costs", "Date of Invoice"
                    // ) VALUES (?, ?, ?, ?, ?)
                    // """;

                    // try (PreparedStatement pstmt = conn.prepareStatement(insertInvoiceSQL)) {
                    // pstmt.setString(1, STUDENT_NAMES[i]);
                    // pstmt.setString(2, String.format("%s (%.2f)", course.id, course.fee));
                    // pstmt.setString(3, sportsStr.toString());
                    // pstmt.setString(4, foodStr.toString());
                    // pstmt.setString(5, invoiceDate.toString());
                    // pstmt.executeUpdate();
                    // }
                }

                conn.commit();
                System.out.println("Test data populated successfully!");
                return; // Success, exit the retry loop

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

    private static String generateRandomLetters() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        return "" +
                letters.charAt(random.nextInt(letters.length())) +
                letters.charAt(random.nextInt(letters.length()));
    }

    private static Map<String, InstitutionInfo> loadInstitutionsAndCourses(Connection conn) throws SQLException {
        Map<String, InstitutionInfo> institutions = new HashMap<>();

        // Load institutions
        String institutionQuery = "SELECT UKPRN, LEGAL_NAME FROM INSTITUTION";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(institutionQuery)) {
            while (rs.next()) {
                String ukprn = rs.getString("UKPRN");
                String name = rs.getString("LEGAL_NAME");
                institutions.put(ukprn, new InstitutionInfo(ukprn, name));
            }
        }
        // Load courses for each institution
        String courseQuery = "SELECT KISCOURSEID, TITLE, PUBUKPRN FROM KISCOURSE WHERE PUBUKPRN = ?";
        for (InstitutionInfo institution : institutions.values()) {
            try (PreparedStatement pstmt = conn.prepareStatement(courseQuery)) {
                pstmt.setString(1, institution.ukprn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        CourseInfo course = new CourseInfo(
                                rs.getString("KISCOURSEID"),
                                rs.getString("TITLE"));
                        institution.courses.add(course);
                    }
                    // System.out.println("Course Query ran successfully ✅");
                } catch (SQLException e) {
                    // System.err.println("❌ Error retrieving courses: " + e.getMessage());
                }
            }
        }
        return institutions;
    }

    private static List<String> getAvailableSports(Connection conn) throws SQLException {
        List<String> sports = new ArrayList<>();
        String query = "SELECT \"Sports Activities\" FROM SPORTS";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sports.add(rs.getString(1));
            }
        }
        return sports;
    }

    private static List<String> getAvailableFoods(Connection conn) throws SQLException {
        List<String> foods = new ArrayList<>();
        String query = "SELECT \"Food Item\" FROM FOODS";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                foods.add(rs.getString(1));
            }
        }
        return foods;
    }

    public static void main(String[] args) {
        System.out.println("Starting test data population...");
        populateTestData();
        System.out.println("Test data population completed.");
        System.out.println("Database Model Testing Successful!");
    }
}