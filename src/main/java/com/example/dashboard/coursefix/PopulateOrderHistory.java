//package com.example.dashboard;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//
//public class PopulateOrderHistory {
//    // private static DatabaseModel dbModel = new DatabaseModel();
//
//    public static void main(String[] args) throws SQLException {
//        // Case 1
//        HashMap<Integer, Double> orderHistory = new HashMap<>();
//        // meal_orders, order_totals
//        orderHistory.put(5992, 90.00);
//        orderHistory.put(8110, 23.00);
//        orderHistory.put(3440, 46.00);
//        orderHistory.put(7685, 72.00);
//
//        setOrderHistory(orderHistory);
//    }
//
//    // Insert order history from Restaurant (Integration)
//    private static void setOrderHistory(HashMap<Integer, Double> orderHistory) {
//
//    }
//}
//
////CREATE TABLE IF NOT EXISTS ORDERS (
////        invoice_id TEXT,
////        student_id TEXT,
////        order_history TEXT,
////        order_total REAL
////);
//
//// DashboardController.java
//// DatabaseModel.java
//// Invoice.java
//// PopulateFinanceTable.java
//// ViewInvoiceController.java
//// invoice-details-view.fxml
//// UMS-DB.db
//
////The UMS Database has 14 columns:
////invoice_id TEXT
////student_id TEXT
////student_name TEXT
////course_id TEXT
////course_details TEXT
////course_inv_fees TEXT
////sports_activity TEXT
////total_sports_cost TEXT
////food_items TEXT
////total_food_cost TEXT
////institution_id TEXT
////institution_name TEXT
////invoice_date TEXT
////course_names TEXT