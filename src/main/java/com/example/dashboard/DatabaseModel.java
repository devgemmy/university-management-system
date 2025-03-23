package com.example.dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.Month;
import java.util.stream.Collectors;

public class DatabaseModel {
    // Singleton instance and database connection
    private static DatabaseModel instance;
    private Connection connection;
    @SuppressWarnings("unused")
    private final String dbPath;
    private static final String DB_URL = "jdbc:sqlite:UMS-DB.db";

    public DatabaseModel() {
        String currentDir = System.getProperty("user.dir");
        this.dbPath = Paths.get(currentDir, "UMS-DB.db").toString();
    }

    public static DatabaseModel getInstance() {
        if (instance == null) {
            instance = new DatabaseModel();
        }
        return instance;
    }

    @SuppressWarnings("exports")
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @SuppressWarnings("exports")
    public Connection getConnection() throws SQLException {
        // if (connection == null || connection.isClosed()) {
        try {
            // String url = "jdbc:sqlite:" + dbPath;
            // connection = DriverManager.getConnection(url);
            connection = DriverManager.getConnection(DB_URL + "?busy_timeout=30000");
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
        // }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public void createFinancesTable() {
        String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS "FINANCES" (
                        "invoiceID" TEXT,
                        "studentName" TEXT,
                        "courseID" TEXT,
                        "courseName" TEXT,
                        "courseInvFees" INT,
                        "sportsActivity" TEXT,
                        "totalSportsCost" REAL,
                        "foodItems" Text,
                        "totalFoodCost" REAL,
                        "institutionID" TEXT,
                        "institutionName" TEXT,
                        "invoiceDate" TEXT,
                        PRIMARY KEY("invoiceID")
                    )
                """;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("FINANCES table created successfully or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating FINANCES table: " + e.getMessage());
        }
    }

    public void populateFinancesFromExistingData() {
        try (Connection conn = getConnection()) {
            // First, check if FINANCES table is empty
            String countQuery = "SELECT COUNT(*) FROM FINANCES";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(countQuery)) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("FINANCES table already contains data. Skipping population.");
                    return;
                }
            }

            // Query to join INVOICES, INSTITUTION, and KISCOURSE tables
            String selectDataSQL = """
                        SELECT
                            i.INVOICE_ID,
                            i.STUDENT_NAME,
                            k.KISCOURSEID,
                            k.TITLE as COURSE_NAME,
                            i.COURSE_FEES,
                            i.SPORTS_ACTIVITIES,
                            i.SPORTS_TOTAL_COST,
                            i.FOOD_ITEMS,
                            i.FOOD_TOTAL_COST,
                            inst.UKPRN,
                            inst.INSTITUTION_NAME,
                            i.INVOICE_DATE
                        FROM INVOICES i
                        LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
                        LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
                    """;

            String insertSQL = """
                        INSERT INTO FINANCES (
                            invoiceID, studentName, courseID, courseName, courseInvFees,
                            sportsActivity, totalSportsCost, foodItems, totalFoodCost,
                            institutionID, institutionName, invoiceDate
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (Statement selectStmt = conn.createStatement();
                    ResultSet rs = selectStmt.executeQuery(selectDataSQL);
                    PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                while (rs.next()) {
                    insertStmt.setString(1, rs.getString("INVOICE_ID"));
                    insertStmt.setString(2, rs.getString("STUDENT_NAME"));
                    insertStmt.setString(3, rs.getString("KISCOURSEID"));
                    insertStmt.setString(4, rs.getString("COURSE_NAME"));
                    insertStmt.setInt(5, rs.getInt("COURSE_FEES"));
                    insertStmt.setString(6, rs.getString("SPORTS_ACTIVITIES"));
                    insertStmt.setDouble(7, rs.getDouble("SPORTS_TOTAL_COST"));
                    insertStmt.setString(8, rs.getString("FOOD_ITEMS"));
                    insertStmt.setDouble(9, rs.getDouble("FOOD_TOTAL_COST"));
                    insertStmt.setString(10, rs.getString("UKPRN"));
                    insertStmt.setString(11, rs.getString("INSTITUTION_NAME"));
                    insertStmt.setString(12, rs.getString("INVOICE_DATE"));

                    insertStmt.executeUpdate();
                }
                System.out.println("Data successfully populated into FINANCES table.");
            }
        } catch (SQLException e) {
            System.err.println("Error populating FINANCES table: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection();
        }
    }

    // CRUD Operations for FINANCES table
    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM FINANCES";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }
        }
        return invoices;
    }

    private Invoice createInvoiceFromResultSet(ResultSet rs) throws SQLException {
        String invoiceId = rs.getString("invoiceID");
        String studentName = rs.getString("studentName");
        String invoiceDate = rs.getString("invoiceDate");
        double courseFees = rs.getDouble("courseInvFees");
        double sportsCost = rs.getDouble("totalSportsCost");
        double foodCost = rs.getDouble("totalFoodCost");

        // Create course details HashMap
        HashMap<String, String> courseDetails = new HashMap<>();
        courseDetails.put("courseID", rs.getString("courseID"));
        courseDetails.put("courseName", rs.getString("courseName"));
        // System.out.println("courseDetails: " + courseDetails);

        // Create institution details HashMap
        HashMap<String, String> institutionDetails = new HashMap<>();
        institutionDetails.put("institutionID", rs.getString("institutionID"));
        institutionDetails.put("institutionName", rs.getString("institutionName"));

        // Parse sports activities string and create HashMap
        HashMap<String, Double> sportsActivities = new HashMap<>();
        String sportsActivitiesStr = rs.getString("sportsActivity");
        if (sportsActivitiesStr != null && !sportsActivitiesStr.isEmpty()) {
            String[] activities = sportsActivitiesStr.split(";");
            for (String activity : activities) {
                try {
                    // int startIndex = activity.lastIndexOf('(', endIndex);
                    int startIndex = activity.indexOf('(');
                    if (startIndex == -1)
                        continue;

                    // int endIndex = activity.lastIndexOf(')');
                    int endIndex = activity.indexOf(')');
                    if (endIndex == -1)
                        continue;

                    String activityName = activity.substring(0, startIndex).trim();
                    String costString = activity.substring(startIndex + 1, endIndex).trim();
                    // if (startIndex >= 0 && endIndex > startIndex) {
                    // String costString = item.substring(startIndex + 1, endIndex).trim();
                    // total += Double.parseDouble(costString);
                    // }
                    double cost = Double.parseDouble(costString);
                    sportsActivities.put(activityName, cost);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Error parsing sports activity: " + activity);
                }
            }
        }

        // Parse food items string and create HashMap
        HashMap<String, Double> foodItems = new HashMap<>();
        String foodItemsStr = rs.getString("foodItems");
        if (foodItemsStr != null && !foodItemsStr.isEmpty()) {
            String[] items = foodItemsStr.split(";");
            for (String item : items) {
                try {
                    int endIndex = item.lastIndexOf(')');
                    if (endIndex == -1)
                        continue;

                    int startIndex = item.lastIndexOf('(', endIndex);
                    if (startIndex == -1)
                        continue;

                    String itemName = item.substring(0, startIndex).trim();
                    String costString = item.substring(startIndex + 1, endIndex).trim();
                    double cost = Double.parseDouble(costString);
                    foodItems.put(itemName, cost);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Error parsing food item: " + item);
                }
            }
        }

        return new Invoice(
                invoiceId,
                studentName,
                invoiceDate,
                courseFees,
                sportsCost,
                foodCost,
                courseDetails,
                institutionDetails,
                sportsActivities,
                foodItems);
    }

    public Map<String, Double> getTotalCosts() {
        Map<String, Double> totalCosts = new HashMap<>();
        String query = """
                    SELECT SUM(courseInvFees) as total_course_fees,
                           SUM(totalSportsCost) as total_sports_cost,
                           SUM(totalFoodCost) as total_food_cost
                    FROM FINANCES
                """;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                totalCosts.put("Courses", rs.getDouble("total_course_fees"));
                totalCosts.put("Food", rs.getDouble("total_food_cost"));
                totalCosts.put("Sports", rs.getDouble("total_sports_cost"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting total costs: " + e.getMessage());
        }
        return totalCosts;
    }

    // Get average costs by year for a specific university
    public Map<String, Map<String, Double>> getAverageCostsByUniversity(String universityName) {
        Map<String, Map<String, Double>> yearlyAverageCosts = new HashMap<>();
        String query;

        if ("All Universities".equals(universityName)) {
            query = """
                        SELECT strftime('%Y', invoiceDate) as year,
                               AVG(courseInvFees) as avg_course_fees,
                               AVG(totalSportsCost) as avg_sports_cost,
                               AVG(totalFoodCost) as avg_food_cost
                        FROM FINANCES
                        GROUP BY strftime('%Y', invoiceDate)
                        ORDER BY year
                    """;
        } else {
            query = """
                        SELECT strftime('%Y', invoiceDate) as year,
                               AVG(courseInvFees) as avg_course_fees,
                               AVG(totalSportsCost) as avg_sports_cost,
                               AVG(totalFoodCost) as avg_food_cost
                        FROM FINANCES
                        WHERE institutionName = ?
                        GROUP BY strftime('%Y', invoiceDate)
                        ORDER BY year
                    """;
        }

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (!"All Universities".equals(universityName)) {
                pstmt.setString(1, universityName);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String year = rs.getString("year");
                Map<String, Double> averages = new HashMap<>();
                averages.put("Courses", rs.getDouble("avg_course_fees"));
                averages.put("Sports", rs.getDouble("avg_sports_cost"));
                averages.put("Food", rs.getDouble("avg_food_cost"));
                yearlyAverageCosts.put(year, averages);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average costs: " + e.getMessage());
        }
        return yearlyAverageCosts;
    }

    public List<Map<String, Object>> getFilteredInvoices(String timeFilter, String monthPeriod, String yearPeriod) {
        List<Map<String, Object>> result = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM FINANCES WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (timeFilter != null && !timeFilter.isEmpty() &&
                monthPeriod != null && !monthPeriod.isEmpty() &&
                yearPeriod != null && !yearPeriod.isEmpty()) {

            queryBuilder.append(" AND strftime('%Y', invoiceDate) = ? AND strftime('%m', invoiceDate) = ?");
            params.add(yearPeriod);
            // Convert month name to number (e.g., "January" to "01")
            String monthNumber = String.format("%02d", Month.valueOf(monthPeriod.toUpperCase()).getValue());
            params.add(monthNumber);

            if ("First Half".equals(timeFilter)) {
                queryBuilder.append(" AND strftime('%d', invoiceDate) <= '15'");
            } else if ("Second Half".equals(timeFilter)) {
                queryBuilder.append(" AND strftime('%d', invoiceDate) > '15'");
            }
        }

        queryBuilder.append(" ORDER BY invoiceDate");

        try (Connection conn = DatabaseModel.connect();
                PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            System.out.println("Executing query: " + queryBuilder.toString());
            System.out.println("With parameters: " + params);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("invoiceID", rs.getString("invoiceID"));
                    row.put("studentName", rs.getString("studentName"));
                    row.put("courseID", rs.getString("courseID"));
                    row.put("courseName", rs.getString("courseName"));
                    row.put("courseInvFees", rs.getInt("courseInvFees"));
                    row.put("sportsActivity", rs.getString("sportsActivity"));
                    row.put("totalSportsCost", rs.getDouble("totalSportsCost"));
                    row.put("foodItems", rs.getString("foodItems"));
                    row.put("totalFoodCost", rs.getDouble("totalFoodCost"));
                    row.put("institutionID", rs.getString("institutionID"));
                    row.put("institutionName", rs.getString("institutionName"));
                    row.put("invoiceDate", rs.getString("invoiceDate"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing getFilteredInvoices: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public List<Map<String, Object>> getFilteredInvoicesByDateRange(String institution, String startDate,
            String endDate) {
        List<Map<String, Object>> invoices = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("""
                    SELECT *
                    FROM FINANCES
                    WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (institution != null && !institution.trim().isEmpty()) {
            queryBuilder.append(" AND institutionName = ?");
            params.add(institution);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            queryBuilder.append(" AND invoiceDate >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryBuilder.append(" AND invoiceDate <= ?");
            params.add(endDate);
        }

        queryBuilder.append(" ORDER BY invoiceDate");

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            System.out.println("Executing query: " + queryBuilder.toString());
            System.out.println("With parameters: " + params);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> invoice = new HashMap<>();
                invoice.put("invoiceDate", rs.getString("invoiceDate"));
                invoice.put("courseInvFees", rs.getDouble("courseInvFees"));
                invoice.put("totalSportsCost", rs.getDouble("totalSportsCost"));
                invoice.put("totalFoodCost", rs.getDouble("totalFoodCost"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Error getting filtered invoices: " + e.getMessage());
        }
        return invoices;
    }

    public Map<String, String> getInstitutions() {
        Map<String, String> institutions = new HashMap<>();
        String query = "SELECT UKPRN, LEGAL_NAME FROM INSTITUTION";
        System.out.println("Executing query: " + query);

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String ukprn = rs.getString("UKPRN");
                String legalName = rs.getString("LEGAL_NAME");
                // System.out.println("Found institution: " + legalName + " (" + ukprn + ")");
                institutions.put(ukprn, legalName);
            }
            System.out.println("Total institutions found: " + institutions.size());
        } catch (SQLException e) {
            System.err.println("Error getting institutions: " + e.getMessage());
        }
        return institutions;
    }

    // Get courses by institution
    public Map<String, String> getCoursesByInstitution(String ukprn) {
        Map<String, String> courses = new HashMap<>();
        String query = "SELECT KISCOURSEID, TITLE FROM KISCOURSE WHERE PUBUKPRN = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ukprn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                courses.put(rs.getString("TITLE"), rs.getString("KISCOURSEID"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting courses: " + e.getMessage());
        }
        return courses;
    }

    // Get all sports activities
    public List<String> getSportsActivities() {
        List<String> sports = new ArrayList<>();
        String query = "SELECT DISTINCT \"Sports Activities\" FROM SPORTS";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                sports.add(rs.getString("Sports Activities"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting sports activities: " + e.getMessage());
        }
        return sports;
    }

    // Get all food items
    public List<String> getFoodItems() {
        List<String> foods = new ArrayList<>();
        String query = "SELECT \"Food Item\" FROM FOODS";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                foods.add(rs.getString("Food Item"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting food items: " + e.getMessage());
        }
        return foods;
    }

    public String generateInvoice(Map<String, Object> invoiceData) throws SQLException {
        Connection conn = null;
        PreparedStatement invoicesStmt = null;
        PreparedStatement financesStmt = null;
        String invoiceId = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);

            try {
                // Step 1: Insert into INVOICES table first
                String insertInvoicesSQL = """
                            INSERT INTO INVOICES (
                                "Student Name", "Course Costs", "Sports Costs",
                                "Food Costs", "Date of Invoice"
                            ) VALUES (?, ?, ?, ?, ?)
                        """;

                invoicesStmt = conn.prepareStatement(insertInvoicesSQL);

                // Format course costs as "KISCOURSEID (cost)"
                String courseId = String.valueOf(invoiceData.get("courseId"));
                double courseFees = Double.parseDouble(String.valueOf(invoiceData.get("courseFees")));
                String formattedCourseCost = String.format("%s (%.2f)", courseId, courseFees);

                // Format course list as "Course1 (cost1);Course2 (cost2)"
                // -- Future Improvement No. 5
                @SuppressWarnings("unchecked")
                Map<String, String> courseList = (Map<String, String>) invoiceData.get("courseList");
                StringBuilder formattedCourseFees = new StringBuilder();
                @SuppressWarnings("unused")
                double courseInvFees = 0.0;

                if (courseList != null && !courseList.isEmpty()) {
                    boolean first = true;
                    for (Map.Entry<String, String> entry : courseList.entrySet()) {
                        if (!first)
                            formattedCourseFees.append(";");
                        String course = entry.getKey();
                        double fees = Double.parseDouble(entry.getValue());
                        formattedCourseFees.append(String.format("%s (%.2f)", course, fees));
                        courseInvFees += fees;
                        first = false;
                    }
                }

                // Format sports activities as "Sport1 (cost1);Sport2 (cost2)"
                @SuppressWarnings("unchecked")
                Map<String, String> sportsActivities = (Map<String, String>) invoiceData.get("sportsActivities");
                StringBuilder formattedSportsCosts = new StringBuilder();
                double totalSportsCost = 0.0;

                if (sportsActivities != null && !sportsActivities.isEmpty()) {
                    boolean first = true;
                    for (Map.Entry<String, String> entry : sportsActivities.entrySet()) {
                        if (!first)
                            formattedSportsCosts.append(";");
                        String sport = entry.getKey();
                        double cost = Double.parseDouble(entry.getValue());
                        formattedSportsCosts.append(String.format("%s (%.2f)", sport, cost));
                        totalSportsCost += cost;
                        first = false;
                    }
                }

                // Format food items as "Food1 (cost1);Food2 (cost2)"
                @SuppressWarnings("unchecked")
                Map<String, String> foodItems = (Map<String, String>) invoiceData.get("foodItems");
                StringBuilder formattedFoodCosts = new StringBuilder();
                double totalFoodCost = 0.0;

                if (foodItems != null && !foodItems.isEmpty()) {
                    boolean first = true;
                    for (Map.Entry<String, String> entry : foodItems.entrySet()) {
                        if (!first)
                            formattedFoodCosts.append(";");
                        String food = entry.getKey();
                        double cost = Double.parseDouble(entry.getValue());
                        formattedFoodCosts.append(String.format("%s (%.2f)", food, cost));
                        totalFoodCost += cost;
                        first = false;
                    }
                }

                // Set values for INVOICES
                invoicesStmt.setString(1, String.valueOf(invoiceData.get("studentName")));
                invoicesStmt.setString(2, formattedCourseCost); // Using KISCOURSEID format
                invoicesStmt.setString(3, formattedSportsCosts.toString());
                invoicesStmt.setString(4, formattedFoodCosts.toString());
                invoicesStmt.setString(5, String.valueOf(invoiceData.get("invoiceDate")));

                int result = invoicesStmt.executeUpdate();
                System.out.println("INVOICES insert result: " + result);

                if (result > 0) {
                    // Generate invoice ID after successful INVOICES insert
                    invoiceId = "INV" + String.format("%09d", System.currentTimeMillis() % 1000000000) + "TS";

                    // Step 2: Insert into FINANCES table
                    String insertFinancesSQL = """
                                INSERT INTO FINANCES (
                                    invoiceID, studentName, courseID, courseName, courseInvFees,
                                    sportsActivity, totalSportsCost, foodItems, totalFoodCost,
                                    institutionID, institutionName, invoiceDate
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;

                    financesStmt = conn.prepareStatement(insertFinancesSQL);

                    // Set values for FINANCES
                    financesStmt.setString(1, invoiceId);
                    financesStmt.setString(2, String.valueOf(invoiceData.get("studentName")));
                    financesStmt.setString(3, courseId); // KISCOURSEID
                    financesStmt.setString(4, String.valueOf(invoiceData.get("courseName"))); // Full course name
                    financesStmt.setDouble(5, courseFees);
                    financesStmt.setString(6, formattedSportsCosts.toString());
                    financesStmt.setDouble(7, totalSportsCost);
                    financesStmt.setString(8, formattedFoodCosts.toString());
                    financesStmt.setDouble(9, totalFoodCost);
                    financesStmt.setString(10, String.valueOf(invoiceData.get("institutionId")));
                    financesStmt.setString(11, String.valueOf(invoiceData.get("institutionName")));
                    financesStmt.setString(12, String.valueOf(invoiceData.get("invoiceDate")));

                    result = financesStmt.executeUpdate();
                    System.out.println("FINANCES insert result: " + result);

                    conn.commit();
                    return invoiceId;
                } else {
                    conn.rollback();
                    throw new SQLException("Failed to insert invoice record");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Failed to generate invoice: " + e.getMessage());
            }
        } finally {
            if (financesStmt != null)
                try {
                    financesStmt.close();
                } catch (SQLException e) {
                }
            if (invoicesStmt != null)
                try {
                    invoicesStmt.close();
                } catch (SQLException e) {
                }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Invoice getInvoiceById(String invoiceId) {
        String query = """
                    SELECT * FROM FINANCES WHERE invoiceID = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createInvoiceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting invoice by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteInvoice(String invoiceId) {

        String deleteFinancesQuery = "DELETE FROM FINANCES WHERE invoiceID = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(deleteFinancesQuery)) {
            pstmt.setString(1, invoiceId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting invoice: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE FINANCES SET " +
                "courseID = ?, " +
                "courseName = ?, " +
                "courseInvFees = ?, " +
                "foodItems = ?, " +
                "totalFoodCost = ?, " +
                "sportsActivity = ?, " +
                "totalSportsCost = ? " +
                "WHERE invoiceID = ?";
        try (Connection conn = DatabaseModel.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Map<String, String> courseList = invoice.getCourseList();
            String courseId = courseList.get("courseID");
            String courseName = courseList.get("courseName");

            String foodItems = invoice.getFoodItems().entrySet().stream()
                    .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                    .collect(Collectors.joining("; "));

            String sportsActivities = invoice.getSportsActivities().entrySet().stream()
                    .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                    .collect(Collectors.joining("; "));

            pstmt.setString(1, courseId);
            pstmt.setString(2, courseName);
            pstmt.setDouble(3, invoice.getCourseInvFees());
            pstmt.setString(4, foodItems);
            pstmt.setDouble(5, invoice.getTotalFoodCost());
            pstmt.setString(6, sportsActivities);
            pstmt.setDouble(7, invoice.getTotalSportsCost());
            pstmt.setString(8, invoice.getInvoiceID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating invoice: " + e.getMessage());
            return false;
        }
    }

    public double calculateTotalFees() throws SQLException {
        String query = "SELECT SUM(courseInvFees) as total FROM FINANCES";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public double calculateTotalSportsFees() throws SQLException {
        String query = "SELECT SUM(totalSportsCost) as total FROM FINANCES";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public double calculateTotalFoodCosts() throws SQLException {
        String query = "SELECT SUM(totalFoodCost) as total FROM FINANCES";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    // Get average costs for all universities
    public Map<String, Map<String, Map<String, Double>>> getAllUniversitiesAverageCosts() {
        Map<String, Map<String, Map<String, Double>>> allData = new HashMap<>();
        String query = """
                    SELECT strftime('%Y', invoiceDate) as year,
                           institutionName,
                           AVG(courseInvFees) as avg_course_fees,
                           AVG(totalSportsCost) as avg_sports_cost,
                           AVG(totalFoodCost) as avg_food_cost
                    FROM FINANCES
                    GROUP BY strftime('%Y', invoiceDate), institutionName
                    ORDER BY year
                """;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String year = rs.getString("year");
                String institution = rs.getString("institutionName");

                // Initialize year map if not exists
                allData.putIfAbsent(year, new HashMap<>());

                // Create costs map for this institution
                Map<String, Double> costs = new HashMap<>();
                costs.put("Courses", rs.getDouble("avg_course_fees"));
                costs.put("Sports", rs.getDouble("avg_sports_cost"));
                costs.put("Food", rs.getDouble("avg_food_cost"));

                // Add to the data structure
                allData.get(year).put(institution, costs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all universities average costs: " + e.getMessage());
        }
        return allData;
    }

    public Map<String, Double> getTotalCostsByUniversity(String universityName) throws SQLException {
        Map<String, Double> costs = new HashMap<>();
        String query = "SELECT SUM(courseInvFees) as totalCourses, " +
                "SUM(totalSportsCost) as totalSports, " +
                "SUM(totalFoodCost) as totalFood " +
                "FROM FINANCES WHERE institutionName = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, universityName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                costs.put("Courses", rs.getDouble("totalCourses"));
                costs.put("Sports", rs.getDouble("totalSports"));
                costs.put("Food", rs.getDouble("totalFood"));
            }
        }

        return costs;
    }

    public void syncInvoicesToFinances() {
        String selectSQL = """
                    SELECT
                        i.INVOICE_ID,
                        i."Student Name" as studentName,
                        i.COURSE_ID as courseID,
                        k.TITLE as courseName,
                        CAST(i."Course Costs" AS INTEGER) as courseInvFees,
                        i.SPORTS_ACTIVITIES as sportsActivity,
                        CAST(i."Sports Costs" AS REAL) as totalSportsCost,
                        i.FOOD_ITEMS as foodItems,
                        CAST(i."Food Costs" AS REAL) as totalFoodCost,
                        i.INSTITUTION_ID as institutionID,
                        inst.INSTITUTION_NAME as institutionName,
                        i."Date of Invoice" as invoiceDate
                    FROM INVOICES i
                    LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
                    LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
                    WHERE i.INVOICE_ID NOT IN (SELECT invoiceID FROM FINANCES)
                """;

        String insertSQL = """
                    INSERT INTO FINANCES (
                        invoiceID, studentName, courseID, courseName, courseInvFees,
                        sportsActivity, totalSportsCost, foodItems, totalFoodCost,
                        institutionID, institutionName, invoiceDate
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (Statement selectStmt = conn.createStatement();
                    ResultSet rs = selectStmt.executeQuery(selectSQL);
                    PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                while (rs.next()) {
                    insertStmt.setString(1, rs.getString("INVOICE_ID"));
                    insertStmt.setString(2, rs.getString("studentName"));
                    insertStmt.setString(3, rs.getString("courseID"));
                    insertStmt.setString(4, rs.getString("courseName"));
                    insertStmt.setInt(5, rs.getInt("courseInvFees"));
                    insertStmt.setString(6, rs.getString("sportsActivity"));
                    insertStmt.setDouble(7, rs.getDouble("totalSportsCost"));
                    insertStmt.setString(8, rs.getString("foodItems"));
                    insertStmt.setDouble(9, rs.getDouble("totalFoodCost"));
                    insertStmt.setString(10, rs.getString("institutionID"));
                    insertStmt.setString(11, rs.getString("institutionName"));
                    insertStmt.setString(12, rs.getString("invoiceDate"));

                    insertStmt.executeUpdate();
                }
                conn.commit();
                System.out.println("Successfully synced INVOICES to FINANCES");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error syncing INVOICES to FINANCES: " + e.getMessage());
        }
    }
}

/*
 * List of Methods:
 * -getInstance(): DatabaseModel
 * -connect(): Connection
 * -getConnection(): Connection
 * -closeConnection(): void
 * -createFinancesTable(): void
 * -populateFinancesFromExistingData(): void
 * -testConnection(): boolean
 * -getAllInvoices(): List<Invoice>
 * -createInvoiceFromResultSet(rs: ResultSet): Invoice
 * -getTotalCosts(): Map<String, Double>
 * -getAverageCostsByUniversity(universityName: String): Map<String, Map<String,
 * Double>>
 * -getFilteredInvoices(timeFilter: String, monthPeriod: String, yearPeriod:
 * String): List<Map<String, Object>>
 * -getFilteredInvoicesByDateRange(institution: String, startDate: String,
 * endDate: String): List<Map<String, Object>>
 * -getInstitutions(): Map<String, String>
 * -getCoursesByInstitution(ukprn: String): Map<String, String>
 * -getSportsActivities(): List<String>
 * -getFoodItems(): List<String>
 * -generateInvoice(invoiceData: Map<String, Object>): String
 * -getInvoiceById(invoiceId: String): Invoice
 * -deleteInvoice(invoiceId: String): boolean
 * -updateInvoice(invoice: Invoice): boolean
 * -calculateTotalFees(): double
 * -calculateTotalSportsFees(): double
 * -calculateTotalFoodCosts(): double
 * -getAllUniversitiesAverageCosts(): Map<String, Map<String, Map<String,
 * Double>>>
 * -getTotalCostsByUniversity(universityName: String): Map<String, Double>
 * -syncInvoicesToFinances(): void
 */
