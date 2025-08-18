package com.example.dashboard;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.text.DecimalFormat;

public class PopulateOrderHistory {
    private static DatabaseModel dbModel = new DatabaseModel();
    /*
    public static void main(String[] args) throws SQLException {
        orderHistory.put(8110, 23.00);
        orderHistory.put(3440, 46.00);
        orderHistory.put(7685, 72.00);
        setOrderHistory(orderHistory);
    }
    */

    // Insert order history from Restaurant (Integration)
    public static void setOrderHistory(HashMap<Integer, Double> orderHistory) throws SQLException {
        // System.out.println("Finance collected: " + orderHistory);
        HashMap<String, String> studInvPair = new HashMap<>();
        String getFKeysQuery = """
            SELECT invoice_id, student_id FROM FINANCES
        """;

        try (
            Connection conn = dbModel.getConnection();
            PreparedStatement fkeystmt = conn.prepareStatement(getFKeysQuery);
            ResultSet fkeyrs = fkeystmt.executeQuery()
        ) {
            while(fkeyrs.next()) {
                studInvPair.put(fkeyrs.getString("invoice_id"), fkeyrs.getString("student_id"));
            }
            // System.out.println("invoiceIDs and student IDs are collected");
        } catch (SQLException ex) {
            dbModel.showError("Database error: " + ex.getMessage());
            return;
        }

        if (studInvPair.isEmpty()) {
            System.out.println("No student-invoice pairs found.");
            return;
        }
        List<Map.Entry<String, String>> studEntries = new ArrayList<>(studInvPair.entrySet());
        Random random = new Random();
        int randomIndex = random.nextInt(studEntries.size());

        Map.Entry<String, String> randomEntry = studEntries.get(randomIndex);
        System.out.println("Randomly selected entry:");
        System.out.println("Invoie ID: " + randomEntry.getKey() + ", Student ID: " + randomEntry.getValue());

        String insertQuery = """
            INSERT INTO ORDERS (invoice_id, student_id, order_history, order_total) VALUES (?, ?, ?, ?)
        """;

        int orderNum = -1;
        double orderTot = 0.0;
        System.out.println("Provided order history entries:");
        for (Map.Entry<Integer, Double> order : orderHistory.entrySet()) {
            if (orderNum == -1) orderNum = order.getKey();
            if (orderTot == 0.0) orderTot = order.getValue();
            DecimalFormat df = new DecimalFormat("0.00");
            String orderTotalFormat = df.format(orderTot);
            String orderNumFormat = "#Order " + orderNum;
            System.out.println("Order number: " + orderNumFormat + ", Total: " + orderTotalFormat);

            try (
                    Connection conn = dbModel.getConnection();
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery)
            ) {
                insertStmt.setString(1, randomEntry.getKey());   // invoice_id
                insertStmt.setString(2, randomEntry.getValue()); // student_id
                insertStmt.setString(3, orderNumFormat);         // order_history
                insertStmt.setString(4, orderTotalFormat);       // order_total

                // Execute the insert
                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("A new row was inserted into ORDERS!");
                }
            } catch (SQLException ex) {
                dbModel.showError("Database error while inserting to ORDERS: " + ex.getMessage());
            }

        }
    }
}

//CREATE TABLE IF NOT EXISTS ORDERS (
//        invoice_id TEXT,
//        student_id TEXT,
//        order_history TEXT,
//        order_total REAL
//);
