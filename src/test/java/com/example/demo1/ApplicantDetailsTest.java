package com.example.demo1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ApplicantDetailsTest {

    private ApplicantDetails applicantDetails;

    @BeforeEach
    void setUp() {
        applicantDetails = new ApplicantDetails("John Doe", "01/01/1990", "01/01/2025", "BSc", "A");
    }

    @Test
    void testGetters() {
        assertEquals("John Doe", applicantDetails.getFullName());
        assertEquals("01/01/1990", applicantDetails.getDateOfBirth());
        assertEquals("01/01/2025", applicantDetails.getDateOfApplication());
        assertEquals("BSc", applicantDetails.getCertificate());
        assertEquals("A", applicantDetails.getGrade());
        assertNotNull(applicantDetails.getUniqueID());
        assertNotNull(applicantDetails.getApplicantFolderName());
    }

    @Test
    void testSetters() {
        applicantDetails.setEmail("john.doe@example.com");
        applicantDetails.setContactNumber("1234567890");
        applicantDetails.setAddress1("123 Main St");
        applicantDetails.setCity("Anytown");
        applicantDetails.setPostcode("12345");

        assertEquals("john.doe@example.com", applicantDetails.getEmail());
        assertEquals("1234567890", applicantDetails.getContactNumber());
        assertEquals("123 Main St", applicantDetails.getAddress1());
        assertEquals("Anytown", applicantDetails.getCity());
        assertEquals("12345", applicantDetails.getPostcode());
    }

    @Test
    void testGenerateUniqueID() {
        String uniqueID = ApplicantDetails.generateUniqueID("Jane Smith", "12/31/1992");
        assertEquals("JH-12311992", uniqueID);

        uniqueID = ApplicantDetails.generateUniqueID("Jane Smith", null);
        assertEquals("JH-UNKNOWN", uniqueID);

        uniqueID = ApplicantDetails.generateUniqueID(null, "12/31/1992");
        assertEquals("UNKNOWN", uniqueID);

        uniqueID = ApplicantDetails.generateUniqueID("Jane", "12/31/1992");
        assertEquals("INVALID", uniqueID);

        uniqueID = ApplicantDetails.generateUniqueID("Jane Smith", "12-31-1992");
        assertEquals("JH-INVALID", uniqueID);
    }

    @Test
    void testGetStatus() {
        // Initially status should be Active, Incomplete
        assertEquals("Active, Incomplete", applicantDetails.getStatus());

        // Complete the details
        applicantDetails.setEmail("john.doe@example.com");
        applicantDetails.setContactNumber("1234567890");
        applicantDetails.setAddress1("123 Main St");
        applicantDetails.setCity("Anytown");
        applicantDetails.setPostcode("12345");

        // Now status should be Active, Complete
        assertEquals("Active, Complete", applicantDetails.getStatus());

        // Mark as submitted
        applicantDetails.setSubmitted(true);

        // Now status should be Inactive, Complete
        assertEquals("Inactive, Complete", applicantDetails.getStatus());
    }
}