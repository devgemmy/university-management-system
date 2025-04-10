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
        String invoiceId = rs.getString("invoice_id");
        String studentId = rs.getString("student_id");
        String studentName = rs.getString("student_name");
        String invoiceDate = rs.getString("invoice_date");
        double courseFees = rs.getDouble("course_inv_fees");
        double sportsCost = rs.getDouble("total_sports_cost");
        double foodCost = rs.getDouble("total_food_cost");

        // Create course details HashMap
        HashMap<String, String> courseDetails = new HashMap<>();
        courseDetails.put("courseID", rs.getString("course_id"));
        courseDetails.put("courseName", rs.getString("course_details"));
        // ourseDetails.put("courseName", rs.getString("course_name"));
        // System.out.println("courseDetails: " + courseDetails);

        // Create institution details HashMap
        HashMap<String, String> institutionDetails = new HashMap<>();
        institutionDetails.put("institutionID", rs.getString("institution_id"));
        institutionDetails.put("institutionName", rs.getString("institution_name"));

        // Parse sports activities string and create HashMap
        HashMap<String, Double> sportsActivities = new HashMap<>();
        String sportsActivitiesStr = rs.getString("sports_activity");
        if (sportsActivitiesStr != null && !sportsActivitiesStr.isEmpty()) {
            String[] activities = sportsActivitiesStr.split(";");
            for (String activity : activities) {
                try {
                    int startIndex = activity.indexOf('(');
                    if (startIndex == -1)
                        continue;

                    int endIndex = activity.indexOf(')');
                    if (endIndex == -1)
                        continue;

                    String activityName = activity.substring(0, startIndex).trim();
                    String costString = activity.substring(startIndex + 1, endIndex).trim();

                    double cost = Double.parseDouble(costString);
                    sportsActivities.put(activityName, cost);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Error parsing sports activity: " + activity);
                }
            }
        }

        // Parse food items string and create HashMap
        HashMap<String, Double> foodItems = new HashMap<>();
        String foodItemsStr = rs.getString("food_items");
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
                studentId,
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
                    SELECT SUM(course_inv_fees) as total_course_fees,
                           SUM(total_sports_cost) as total_sports_cost,
                           SUM(total_food_cost) as total_food_cost
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
                        SELECT strftime('%Y', invoice_date) as year,
                               AVG(course_inv_fees) as avg_course_fees,
                               AVG(total_sports_cost) as avg_sports_cost,
                               AVG(total_food_cost) as avg_food_cost
                        FROM FINANCES
                        GROUP BY strftime('%Y', invoice_date)
                        ORDER BY year
                    """;
        } else {
            query = """
                        SELECT strftime('%Y', invoice_date) as year,
                               AVG(course_inv_fees) as avg_course_fees,
                               AVG(total_sports_cost) as avg_sports_cost,
                               AVG(total_food_cost) as avg_food_cost
                        FROM FINANCES
                        WHERE institution_name = ?
                        GROUP BY strftime('%Y', invoice_date)
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

            queryBuilder.append(" AND strftime('%Y', invoice_date) = ? AND strftime('%m', invoice_date) = ?");
            params.add(yearPeriod);
            // Convert month name to number (e.g., "January" to "01")
            String monthNumber = String.format("%02d", Month.valueOf(monthPeriod.toUpperCase()).getValue());
            params.add(monthNumber);

            if ("First Half".equals(timeFilter)) {
                queryBuilder.append(" AND strftime('%d', invoice_date) <= '15'");
            } else if ("Second Half".equals(timeFilter)) {
                queryBuilder.append(" AND strftime('%d', invoice_date) > '15'");
            }
        }

        queryBuilder.append(" ORDER BY invoice_date");

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
                    row.put("invoiceID", rs.getString("invoice_id"));
                    row.put("studentID", rs.getString("student_id"));
                    row.put("studentName", rs.getString("student_name"));
                    row.put("courseID", rs.getString("course_id"));
                    row.put("courseName", rs.getString("course_details"));
                    row.put("courseInvFees", rs.getInt("course_inv_fees"));
                    row.put("sportsActivity", rs.getString("sports_activity"));
                    row.put("totalSportsCost", rs.getDouble("total_sports_cost"));
                    row.put("foodItems", rs.getString("food_items"));
                    row.put("totalFoodCost", rs.getDouble("total_food_cost"));
                    row.put("institutionID", rs.getString("institution_id"));
                    row.put("institutionName", rs.getString("institution_name"));
                    row.put("invoiceDate", rs.getString("invoice_date"));
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
            queryBuilder.append(" AND institution_name = ?");
            params.add(institution);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            queryBuilder.append(" AND invoice_date >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryBuilder.append(" AND invoice_date <= ?");
            params.add(endDate);
        }

        queryBuilder.append(" ORDER BY invoice_date");

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
                invoice.put("invoiceDate", rs.getString("invoice_date"));
                invoice.put("courseInvFees", rs.getDouble("course_inv_fees"));
                invoice.put("totalSportsCost", rs.getDouble("total_sports_cost"));
                invoice.put("totalFoodCost", rs.getDouble("total_food_cost"));
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
                // String insertInvoicesSQL = """
                // INSERT INTO INVOICES (
                // "Student Name", "Course Costs", "Sports Costs",
                // "Food Costs", "Date of Invoice"
                // ) VALUES (?, ?, ?, ?, ?)
                // """;

                // invoicesStmt = conn.prepareStatement(insertInvoicesSQL);

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
                // invoicesStmt.setString(1, String.valueOf(invoiceData.get("studentName")));
                // invoicesStmt.setString(2, formattedCourseCost); // Using KISCOURSEID formats
                // invoicesStmt.setString(3, formattedSportsCosts.toString());
                // invoicesStmt.setString(4, formattedFoodCosts.toString());
                // invoicesStmt.setString(5, String.valueOf(invoiceData.get("invoiceDate")));

                // int result = invoicesStmt.executeUpdate();
                // System.out.println("INVOICES insert result: " + result);

                // if (result > 0) {
                // Generate invoice ID after successful INVOICES insert
                invoiceId = "INV" + String.format("%09d", System.currentTimeMillis() % 1000000000) + "TS";

                // Step 2: Insert into FINANCES table
                String insertFinancesSQL = """
                            INSERT INTO FINANCES (
                                invoice_id, student_id, student_name, course_id, course_details, course_inv_fees,
                                sports_activity, total_sports_cost, food_items, total_food_cost,
                                institution_id, institution_name, invoice_date
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                financesStmt = conn.prepareStatement(insertFinancesSQL);

                // Set values for FINANCES
                financesStmt.setString(1, invoiceId);
                financesStmt.setString(2, String.valueOf(invoiceData.get("studentId")));
                financesStmt.setString(3, String.valueOf(invoiceData.get("studentName")));
                financesStmt.setString(4, courseId); // KISCOURSEID
                financesStmt.setString(5, String.valueOf(invoiceData.get("courseName"))); // Full course name
                financesStmt.setDouble(6, courseFees);
                financesStmt.setString(7, formattedSportsCosts.toString());
                financesStmt.setDouble(8, totalSportsCost);
                financesStmt.setString(9, formattedFoodCosts.toString());
                financesStmt.setDouble(10, totalFoodCost);
                financesStmt.setString(11, String.valueOf(invoiceData.get("institutionId")));
                financesStmt.setString(12, String.valueOf(invoiceData.get("institutionName")));
                financesStmt.setString(13, String.valueOf(invoiceData.get("invoiceDate")));

                int result = financesStmt.executeUpdate();
                System.out.println("FINANCES insert result: " + result);

                conn.commit();
                return invoiceId;
                // } else {
                // conn.rollback();
                // throw new SQLException("Failed to insert invoice record");
                // }
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
                    SELECT * FROM FINANCES WHERE invoice_id = ?
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

        String deleteFinancesQuery = "DELETE FROM FINANCES WHERE invoice_id = ?";
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
                "course_id = ?, " +
                "course_details = ?, " +
                "course_inv_fees = ?, " +
                "food_items = ?, " +
                "total_food_cost = ?, " +
                "sports_activity = ?, " +
                "total_sports_cost = ? " +
                "WHERE invoice_id = ?";
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
        String query = "SELECT SUM(course_inv_fees) as total FROM FINANCES";
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
        String query = "SELECT SUM(total_sports_cost) as total FROM FINANCES";
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
        String query = "SELECT SUM(total_food_cost) as total FROM FINANCES";
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
                    SELECT strftime('%Y', invoice_date) as year,
                           institution_name,
                           AVG(course_inv_fees) as avg_course_fees,
                           AVG(total_sports_cost) as avg_sports_cost,
                           AVG(total_food_cost) as avg_food_cost
                    FROM FINANCES
                    GROUP BY strftime('%Y', invoice_date), institution_name
                    ORDER BY year
                """;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String year = rs.getString("year");
                String institution = rs.getString("institution_name");

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
        String query = "SELECT SUM(course_inv_fees) as totalCourses, " +
                "SUM(total_sports_cost) as totalSports, " +
                "SUM(total_food_cost) as totalFood " +
                "FROM FINANCES WHERE institution_name = ?";

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

    // public void syncInvoicesToFinances() {
    // String selectSQL = """
    // SELECT
    // i.INVOICE_ID,
    // i."Student Name" as studentName,
    // i.COURSE_ID as courseID,
    // k.TITLE as courseName,
    // CAST(i."Course Costs" AS INTEGER) as courseInvFees,
    // i.SPORTS_ACTIVITIES as sportsActivity,
    // CAST(i."Sports Costs" AS REAL) as totalSportsCost,
    // i.FOOD_ITEMS as foodItems,
    // CAST(i."Food Costs" AS REAL) as totalFoodCost,
    // i.INSTITUTION_ID as institutionID,
    // inst.INSTITUTION_NAME as institutionName,
    // i."Date of Invoice" as invoiceDate
    // FROM INVOICES i
    // LEFT JOIN KISCOURSE k ON i.COURSE_ID = k.KISCOURSEID
    // LEFT JOIN INSTITUTION inst ON i.INSTITUTION_ID = inst.UKPRN
    // WHERE i.INVOICE_ID NOT IN (SELECT invoiceID FROM FINANCES)
    // """;

    // String insertSQL = """
    // INSERT INTO FINANCES (
    // invoice_id, student_name, course_id, course_name, course_inv_fees,
    // sports_activity, total_sports_cost, food_items, total_food_cost,
    // institution_id, institution_name, invoice_date
    // ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    // """;

    // try (Connection conn = getConnection()) {
    // conn.setAutoCommit(false);
    // try (Statement selectStmt = conn.createStatement();
    // ResultSet rs = selectStmt.executeQuery(selectSQL);
    // PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

    // System.out.println("Executing query: " + rs);
    // while (rs.next()) {
    // insertStmt.setString(1, rs.getString("invoice_id"));
    // insertStmt.setString(2, rs.getString("student_name"));
    // insertStmt.setString(3, rs.getString("course_id"));
    // insertStmt.setString(4, rs.getString("course_name"));
    // insertStmt.setInt(5, rs.getInt("course_inv_fees"));
    // insertStmt.setString(6, rs.getString("sports_activity"));
    // insertStmt.setDouble(7, rs.getDouble("total_sports_cost"));
    // insertStmt.setString(8, rs.getString("food_items"));
    // insertStmt.setDouble(9, rs.getDouble("total_food_cost"));
    // insertStmt.setString(10, rs.getString("institution_id"));
    // insertStmt.setString(11, rs.getString("institution_name"));
    // insertStmt.setString(12, rs.getString("invoice_date"));

    // insertStmt.executeUpdate();
    // }
    // conn.commit();
    // System.out.println("Successfully synced INVOICES to FINANCES");
    // } catch (SQLException e) {
    // conn.rollback();
    // throw e;
    // }
    // } catch (SQLException e) {
    // System.err.println("Error syncing INVOICES to FINANCES: " + e.getMessage());
    // }
    // }
}

/*
 * List of Methods:
 * -getInstance(): DatabaseModel
 * -connect(): Connection
 * -getConnection(): Connection
 * -closeConnection(): void
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
