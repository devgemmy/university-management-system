package com.example.dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceTest {
    private DatabaseController databaseController;
    private String testInvoiceId;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize database controller
        databaseController = DatabaseController.getInstance();

        // Create test invoice data
        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("studentName", "Test Student");
        invoiceData.put("courseId", "COMP101");
        invoiceData.put("courseName", "Computer Science");
        invoiceData.put("courseFees", 9250.0);
        invoiceData.put("institutionId", "10007777");
        invoiceData.put("institutionName", "Test University");
        invoiceData.put("invoiceDate", "2024-03-16");

        // Add sports activities
        Map<String, String> sportsActivities = new HashMap<>();
        sportsActivities.put("Flight archery", "22.85");
        sportsActivities.put("Baseball racing", "20.24");
        invoiceData.put("sportsActivities", sportsActivities);

        // Add food items
        Map<String, String> foodItems = new HashMap<>();
        foodItems.put("Cod Fish", "5.25");
        foodItems.put("Caesar Wrap", "14.78");
        invoiceData.put("foodItems", foodItems);

        // Generate test invoice
        testInvoiceId = databaseController.generateInvoice(invoiceData);
        assertNotNull(testInvoiceId, "Test invoice should be created successfully");
    }

    @Test
    void testDeleteInvoice() {
        // Verify the invoice exists before deletion
        Invoice invoice = databaseController.getInvoiceById(testInvoiceId);
        assertNotNull(invoice, "Test invoice should exist before deletion");
        assertEquals("Test Student", invoice.getStudentName(), "Student name should match");

        // Delete the invoice
        boolean result = databaseController.deleteInvoice(testInvoiceId);
        assertTrue(result, "Invoice deletion should be successful");

        // Verify the invoice no longer exists
        Invoice deletedInvoice = databaseController.getInvoiceById(testInvoiceId);
        assertNull(deletedInvoice, "Invoice should not exist after deletion");
    }
}