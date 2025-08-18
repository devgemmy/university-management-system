package com.example.demo1;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardControllerTest extends ApplicationTest {

    private DashboardController dashboardController;
    private TableView<ApplicantDetails> tableView;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/dashboard.fxml"));
        Parent root = loader.load();
        dashboardController = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();

        // Get TableView reference after initialization
        tableView = dashboardController.getTable();

        // Debugging: Ensure tableView is not null
        assertNotNull(tableView, "TableView should be initialized in DashboardController");
    }

    @BeforeEach
    public void setUp() {
        // Ensure controller initializes properly
        dashboardController.initialize(null, null);

        // Debugging: Check if applicants list is populated
        ObservableList<ApplicantDetails> applicants = dashboardController.getApplicantManager().applicantList;
        assertNotNull(applicants, "Applicant list should be initialized before tests.");
        assertFalse(applicants.isEmpty(), "Applicant list should not be empty.");
    }

    @Test
    public void testTableIsPopulated() {
        // Ensure that the TableView is not null
        assertNotNull(tableView, "TableView should be initialized");

        // Verify the TableView contains applicants
        ObservableList<ApplicantDetails> applicants = tableView.getItems();
        assertNotNull(applicants, "Applicants list should not be null");
        assertEquals(96, applicants.size(), "Expected 96 applicants from CSV file");
    }

    @Test
    public void testApplicantDetails() {
        ObservableList<ApplicantDetails> applicants = tableView.getItems();

        // Ensure there are enough applicants
        assertNotNull(applicants, "Applicants list should not be null");
        assertEquals(96, applicants.size(), "Expected 96 applicants from CSV file");

        // Debugging: Print first 5 applicants
        System.out.println("DEBUG: Checking first 5 applicants:");
        for (int i = 0; i < Math.min(5, applicants.size()); i++) {
            System.out.println(applicants.get(i).getFullName() + " | " + applicants.get(i).getDateOfBirth());
        }

        // Validate first applicant
        ApplicantDetails firstApplicant = applicants.get(0);
        assertEquals("John Smith", firstApplicant.getFullName(), "First applicant's name mismatch");
        assertEquals("14/07/2020", firstApplicant.getDateOfApplication(), "Mismatch in Date of Application");
        assertEquals("National Senior Certificate", firstApplicant.getCertificate(), "Mismatch in certificate");
        assertEquals("Fail", firstApplicant.getGrade(), "Mismatch in grade");

        // Validate second applicant
        ApplicantDetails secondApplicant = applicants.get(1);
        assertEquals("Emily Johnson", secondApplicant.getFullName(), "Second applicant's name mismatch");
        assertEquals("05/03/2022", secondApplicant.getDateOfApplication(), "Mismatch in Date of Application");
        assertEquals("VCE (Victorian Certificate of Education)", secondApplicant.getCertificate(), "Mismatch in certificate");
        assertEquals("A+", secondApplicant.getGrade(), "Mismatch in grade");
    }
}