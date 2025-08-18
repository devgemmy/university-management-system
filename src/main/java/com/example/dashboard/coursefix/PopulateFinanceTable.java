//package com.example.dashboard;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.sql.Statement;
//import java.util.Random;
//import java.sql.ResultSet;
//import java.util.Map;
//import java.util.HashMap;
//
//// CRUD operations for populating the FINANCES table
//// CREATE, SELECT, INSERT, DELETE
//
//public class PopulateFinanceTable {
//    public static void populateFinancesFromExistingData() {
//        DatabaseModel dbController = DatabaseModel.getInstance();
//        Connection conn = null;
//        int retryCount = 0;
//        final int MAX_RETRIES = 3;
//
//        while (retryCount < MAX_RETRIES) {
//            try {
//                if (conn != null) {
//                    try {
//                        conn.close();
//                    } catch (SQLException e) {
//
//                    }
//                }
//                conn = dbController.getConnection();
//                conn.setAutoCommit(false);
//
//                String createFinancesSQL = """
//                            CREATE TABLE IF NOT EXISTS FINANCES (
//                                invoice_id TEXT PRIMARY KEY,
//                                student_id TEXT,
//                                student_name TEXT,
//                                course_id TEXT,
//                                course_details TEXT,
//                                course_inv_fees REAL,
//                                sports_activity TEXT,
//                                total_sports_cost REAL,
//                                food_items TEXT,
//                                total_food_cost REAL,
//                                institution_id TEXT,
//                                institution_name TEXT,
//                                invoice_date TEXT
//                            );
//                        """;
//                // DROP TABLE IF EXISTS FINANCES;
//
//                // UPDATE FINANCES
//                // SET student_id = ''
//                // WHERE "Student Name" LIKE 'Mia Loveheart';
//
//                try (Statement createstmt = conn.createStatement()) {
//                    createstmt.execute(createFinancesSQL);
//                }
//
//                int initFinanceCount = 0;
//                String countQuery = "SELECT COUNT(*) FROM FINANCES";
//                try (
//                        Statement stmt = conn.createStatement();
//                        ResultSet rs = stmt.executeQuery(countQuery)) {
//                    if (rs.next() && rs.getInt(1) > 0) {
//                        initFinanceCount = rs.getInt(1);
//                        System.out.println("FINANCES table already contains data. Skipping population.");
//                        return;
//                    } else {
//                        initFinanceCount = 0;
//                    }
//                }
//
//                int originalInvCount = 96;
//                String countINVQuery = "SELECT COUNT(*) FROM INVOICES";
//                String cleanINVQuery = "DELETE FROM INVOICES WHERE \"Date of Invoice\" LIKE '%-%-% OR \"Date of Invoice\" IS NULL";
//                try (
//                        Statement countstmt = conn.createStatement();
//                        ResultSet rs = countstmt.executeQuery(countINVQuery)) {
//                    if (rs.next() && rs.getInt(1) == originalInvCount) {
//                        System.out.println(
//                                "INVOICES table already has clean data. \nGo ahead and populate the Finance table.");
//                        // Populate the FINANCES table
//                        if (initFinanceCount == 0 && originalInvCount == 96) {
//                            generateFinancesFromExistingInvoices(conn);
//                            conn.commit();
//                            String countFinanceQuery = "SELECT COUNT(*) FROM FINANCES";
//                            try (
//                                    Statement stmt = conn.createStatement();
//                                    ResultSet finrs = stmt.executeQuery(countFinanceQuery)) {
//                                if (finrs.next() && finrs.getInt(1) > 0) {
//                                    System.out.println("Finance Table Data population completed successfully!");
//                                    return;
//                                }
//                            }
//                            return; // Success, exit the retry loop
//                        } else {
//                            System.out.println("FINANCES table already contains data. Skipping population.");
//                            return;
//                        }
//
//                    } else {
//                        // Clean up the INVOICES table
//                        try (Statement cleanstmt = conn.createStatement()) {
//                            cleanstmt.execute(cleanINVQuery);
//                        }
//                        System.out.println("INVOICES table cleaned up.");
//                    }
//                }
//            } catch (SQLException e) {
//                if (conn != null) {
//                    try {
//                        conn.rollback();
//                    } catch (SQLException ex) {
//                        System.err.println("Error rolling back transaction: " + ex.getMessage());
//                    }
//                }
//                retryCount++;
//                if (retryCount < MAX_RETRIES) {
//                    System.out.println("Retrying... Attempt " + (retryCount + 1) + " of " + MAX_RETRIES);
//                    try {
//                        Thread.sleep(1000); // Wait 1 second before retrying
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
//                } else {
//                    System.err.println(
//                            "Error populating test data after " + MAX_RETRIES + " attempts: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            } finally {
//                if (conn != null) {
//                    try {
//                        conn.setAutoCommit(true);
//                        conn.close();
//                    } catch (SQLException e) {
//                        System.err.println("Error closing connection: " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }
//
//    // 5. Add Course Details and change Course Name to Course Names ✅
//    // 6. Change Course Name to Course Names in Finance table ✅
//    // 7. Change Course List Invoice class to accommodate multiple courses
//    // 8. Update Numbers on Admin Services section
//
//    private static void generateFinancesFromExistingInvoices(Connection conn) throws SQLException {
//        Random random = new Random();
//
//        String selectInvData = """
//                    SELECT
//                        "Student Name" as student_name,
//                        "Course Costs" as course_costs,
//                        "Sports Costs" as sports_costs,
//                        "Food Costs" as food_costs,
//                        "Date of Invoice" as invoice_date
//                    FROM INVOICES;
//                """;
//
//        try (
//                Statement selectInvStmt = conn.createStatement();
//                ResultSet allinvrs = selectInvStmt.executeQuery(selectInvData);
//        // Get studentID from course selection: ?
//        ) {
//            while (allinvrs.next()) {
//                String invoiceID = String.format("INV%09d%s",
//                        100000000 + random.nextInt(900000000),
//                        generateRandomLetters());
//                String studentName = allinvrs.getString("student_name");
//
//                String course_costs = allinvrs.getString("course_costs");
//                String courseID = course_costs.substring(0, course_costs.indexOf('('));
//                Map<String, String> courseDetails = getCourseSelection(conn, courseID.trim());
//
//                String courseName = courseDetails.get("courseName");
//                int courseInvFees = extractCourseFees(allinvrs.getString("course_costs"));
//                String sportsActivity = allinvrs.getString("sports_costs");
//                // totalSportsCost
//                double totalSportsCost = getTotalSportsCosts(sportsActivity);
//
//                String foodItems = allinvrs.getString("food_costs");
//                // totalFoodCost
//                double totalFoodCost = getTotalFoodCosts(foodItems);
//                String institutionID = courseDetails.get("institutionID");
//                String institutionName = courseDetails.get("institutionName");
//
//                String invoiceDate = parseDate(allinvrs.getString("invoice_date"));
//
//                String studentID = generateStudentID(invoiceDate, studentName);
//
//                // Insert formatted Invoice data into STUDENTS
//                String insertStudentQuery = """
//                          INSERT OR IGNORE INTO STUDENTS (student_id, student_name, course_id) VALUES (?, ?, ?);
//                        """;
//
//                // Check if the Student exists
//                String checkStudentIDQuery = """
//                          SELECT student_id FROM STUDENTS WHERE student_id = ?
//                        """;
//
//                // Insert formatted Invoice data into FINANCES
//                String insertFinanceSQL = """
//                            INSERT OR IGNORE INTO FINANCES (
//                                invoice_id, student_id, student_name, course_id, course_details, course_inv_fees,
//                                sports_activity, total_sports_cost, food_items, total_food_cost,
//                                institution_id, institution_name, invoice_date
//                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//                        """;
//
//                try (PreparedStatement pstmt = conn.prepareStatement(insertFinanceSQL)) {
//                    // Insert Into Finance Table
//                    pstmt.setString(1, invoiceID);
//                    pstmt.setString(2, studentID);
//                    pstmt.setString(3, studentName);
//                    pstmt.setString(4, courseID);
//                    pstmt.setString(5, courseName);
//                    pstmt.setDouble(6, courseInvFees);
//                    pstmt.setString(7, sportsActivity);
//                    pstmt.setDouble(8, totalSportsCost);
//                    pstmt.setString(9, foodItems);
//                    pstmt.setDouble(10, totalFoodCost);
//                    pstmt.setString(11, institutionID);
//                    pstmt.setString(12, institutionName);
//                    pstmt.setString(13, invoiceDate);
//                    pstmt.executeUpdate();
//
//                    try (PreparedStatement checkstmt = conn.prepareStatement(checkStudentIDQuery)) {
//                        checkstmt.setString(1, studentID);
//                        ResultSet checkStudentQ = checkstmt.executeQuery();
//                        if (!checkStudentQ.next()) {
//                            System.out.println("Storing new student details ...");
//                            try (PreparedStatement insertstmt = conn.prepareStatement(insertStudentQuery)) {
//                                // Insert Into Student Table
//                                insertstmt.setString(1, studentID);
//                                insertstmt.setString(2, studentName);
//                                insertstmt.setString(3, courseID);
//                                insertstmt.executeUpdate();
//                                System.out.println(studentName + " is now enrolled.");
//                            }
//                        }
//                    } catch (SQLException e) {
//                        System.err.println("Error checking student ID: " + e.getMessage());
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error populating FINANCES table: " + e.getMessage());
//        }
//    }
//
//    public static Map<String, String> getCourseSelection(@SuppressWarnings("exports") Connection conn, String courseID)
//            throws SQLException {
//        Map<String, String> courseDetails = new HashMap<>();
//        String courseSelectQuery = """
//                    SELECT DISTINCT
//                        k.KISCOURSEID as course_id,
//                        k.TITLE as course_name,
//                        inst.UKPRN as institution_id,
//                        inst.LEGAL_NAME as institution_name
//                    FROM KISCOURSE k
//                    LEFT JOIN INSTITUTION inst ON k.UKPRN = inst.UKPRN
//                    WHERE k.KISCOURSEID = ?;
//                """;
//
//        try (
//                PreparedStatement pstmt = conn.prepareStatement(courseSelectQuery);) {
//            pstmt.setString(1, courseID);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                courseDetails.put("courseID", rs.getString("course_id"));
//                courseDetails.put("courseName", rs.getString("course_name"));
//                courseDetails.put("institutionID", rs.getString("institution_id"));
//                courseDetails.put("institutionName", rs.getString("institution_name"));
//            }
//        } catch (SQLException e) {
//            System.err.println("Error getting courses: " + e.getMessage());
//        }
//        return courseDetails;
//    }
//
//    public static String generateStudentID(String invoiceDate, String studentFullName) {
//        String[] dateParts = invoiceDate.split("-");
//        if (dateParts.length != 3) {
//            throw new IllegalArgumentException("Invoice date must be in the format yyyy-MM-dd");
//        }
//        String year = dateParts[0];
//        String month = dateParts[1];
//        String day = dateParts[2];
//        String yearSuffix = year.substring(year.length() - 2);
//        String randomLetters = generateRandomLetters();
//        String initials = getInitials(studentFullName);
//        return String.format("%s%s%s%s%s", yearSuffix, day, month, randomLetters, initials);
//    }
//
//    private static String generateRandomLetters() {
//        Random random = new Random();
//        char letters = (char) ('A' + random.nextInt(26));
//        return letters;
//    }
//
//    private static String getInitials(String fullName) {
//        if (fullName == null || fullName.trim().isEmpty()) {
//            return "";
//        }
//        String[] names = fullName.trim().split("\\s+");
//        StringBuilder initials = new StringBuilder();
//        for (String name : names) {
//            if (!name.isEmpty()) {
//                initials.append(Character.toUpperCase(name.charAt(0)));
//            }
//        }
//        return initials.toString();
//    }
//
//    private static String parseDate(String inv_date) {
//        String invoiceDay, invoiceMonth, invoiceYear;
//        LocalDate invoiceDate;
//
//        invoiceDay = inv_date.split("/")[0]; // 06/05/2020
//        invoiceMonth = inv_date.split("/")[1];
//        invoiceYear = inv_date.split("/")[2];
//        invoiceDate = LocalDate.of(
//                Integer.parseInt(invoiceYear),
//                Integer.parseInt(invoiceMonth),
//                Integer.parseInt(invoiceDay)); // 2020-05-06
//        return invoiceDate.toString();
//    }
//
//    private static double getTotalSportsCosts(String sportsActivity) {
//        double totalSportsCost = 0.0;
//        String[] activities = sportsActivity.split(";");
//        for (String activity : activities) {
//            String[] parts = activity.split("\\(");
//            if (parts.length == 2) {
//                String costString = parts[1].replace(")", "").trim();
//                try {
//                    double cost = Double.parseDouble(costString);
//                    totalSportsCost += cost;
//                } catch (NumberFormatException e) {
//                    System.err.println("Error parsing sports cost: " + costString);
//                }
//            }
//        }
//        return totalSportsCost;
//    }
//
//    private static double getTotalFoodCosts(String foodItems) {
//        double totalFoodCosts = 0.0;
//        String[] food_items = foodItems.split(";");
//        for (String food : food_items) {
//            String[] parts = food.split("\\(");
//            if (parts.length == 2) {
//                String costString = parts[1].replace(")", "").trim();
//                try {
//                    double cost = Double.parseDouble(costString);
//                    totalFoodCosts += cost;
//                } catch (NumberFormatException e) {
//                    System.err.println("Error parsing sports cost: " + costString);
//                }
//            }
//        }
//        return totalFoodCosts;
//    }
//
//    private static int extractCourseFees(String courseCosts) {
//        if (courseCosts == null || courseCosts.isEmpty())
//            return 0;
//        try {
//            int startIndex = courseCosts.lastIndexOf('(');
//            int endIndex = courseCosts.lastIndexOf(')');
//
//            if (startIndex != -1 && endIndex != -1) {
//                String costString = courseCosts.substring(startIndex + 1, endIndex).trim();
//                return Integer.parseInt(costString);
//            }
//        } catch (NumberFormatException | IndexOutOfBoundsException e) {
//            System.err.println("Error parsing course fees: " + courseCosts);
//        }
//        return 0;
//    }
//
//    public static void main(String[] args) {
//        System.out.println("Starting test data population...");
//        populateFinancesFromExistingData();
//    }
//}
//
//// String selectSQL = """
//// SELECT
//// i."Student Name" as studentName,
//// k.KISCOURSEID as courseID,
//// k.TITLE as courseName,
//// CAST(i."Course Costs" AS INTEGER) as courseInvFees,
//// i."Sports Costs" as sportsActivity,
//// CAST(i."Sports Costs" AS REAL) as totalSportsCost,
//// i."Food Costs" as foodItems,
//// CAST(i."Food Costs" AS REAL) as totalFoodCost,
//// inst.UKPRN as institutionID,
//// inst.LEGAL_NAME as institutionName,
//// i."Date of Invoice" as invoiceDate
//// FROM INVOICES i
//// LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
//// LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
//// WHERE i.INVOICE_ID NOT IN (SELECT invoice_id FROM FINANCES)
//// """;
