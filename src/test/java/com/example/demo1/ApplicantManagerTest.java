package com.example.demo1;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicantManagerTest {

    private ApplicantManager applicantManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        applicantManager = new ApplicantManager();

        // Ensure VALID_APPLICANTS.csv is cleared before running tests
        File validApplicantsFile = new File("src/main/resources/com/example/demo1/VALID_APPLICANTS.csv");
        if (validApplicantsFile.exists()) {
            validApplicantsFile.delete(); // Clear existing data
        }

        // Override file paths in ApplicantManager for testing
        applicantManager = new ApplicantManager() {
            @Override
            protected String getApplicantsFilePath() {
                return "src/main/resources/com/example/demo1/APPLICANTS.csv"; // Read-only file
            }

            @Override
            protected String getValidApplicantsFilePath() {
                return "src/main/resources/com/example/demo1/VALID_APPLICANTS.csv";
            }

            @Override
            protected String getInvalidApplicantsFilePath() {
                return "src/main/resources/com/example/demo1/INVALID_APPLICANTS.csv";
            }
        };
    }

    @Test
    public void testProcessApplicants() {
        applicantManager.processApplicants();
        ObservableList<ApplicantDetails> applicants = applicantManager.applicantList;

        System.out.println("🔍 Applicants found: " + applicants.size()); // Debugging

        //  Ensure applicants were processed (based on actual CSV data)
        assertTrue(applicants.size() > 0, "No applicants found after processing!");

        // Check first few applicants from real `APPLICANTS.csv`
        ApplicantDetails firstApplicant = applicants.get(0);
        assertEquals("John Smith", firstApplicant.getFullName(), "First applicant name mismatch");
        assertEquals("14/07/2020", firstApplicant.getDateOfApplication(), "First applicant date mismatch");

        ApplicantDetails secondApplicant = applicants.get(1);
        assertEquals("Emily Johnson", secondApplicant.getFullName(), "Second applicant name mismatch");
        assertEquals("05/03/2022", secondApplicant.getDateOfApplication(), "Second applicant date mismatch");
    }

    @Test
    public void testSaveAndLoadValidApplicants() {
        applicantManager.processApplicants();
        applicantManager.saveApplicants();

        // Clear and reload from VALID_APPLICANTS.csv
        applicantManager.applicantList.clear();
        applicantManager.loadValidApplicants();

        System.out.println("🔍 Reloaded Applicants: " + applicantManager.applicantList.size()); // Debugging

        // Check applicants exist after reload
        assertTrue(applicantManager.applicantList.size() > 0, "No applicants found after reload!");

        // Verify specific applicants
        ApplicantDetails firstApplicant = applicantManager.applicantList.get(0);
        assertEquals("John Smith", firstApplicant.getFullName(), "Mismatch after reload");

        ApplicantDetails secondApplicant = applicantManager.applicantList.get(1);
        assertEquals("Emily Johnson", secondApplicant.getFullName(), "Mismatch after reload");
    }

    @Test
    public void testInvalidApplicants() {
        applicantManager.processApplicants();
        ObservableList<ApplicantDetails> applicants = applicantManager.applicantList;

        System.out.println("🔍 Total Applicants After Processing: " + applicants.size());

        // Ensure valid applicants were processed
        assertTrue(applicants.size() > 0, "No valid applicants processed!");

        // Ensure `INVALID_APPLICANTS.csv` exists
        File invalidApplicantsFile = new File("src/main/resources/com/example/demo1/INVALID_APPLICANTS.csv");
        assertTrue(invalidApplicantsFile.exists(), "Invalid applicants file should be created");
    }
}