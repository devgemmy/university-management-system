package com.example.demo1;

import java.util.UUID;

public class ApplicantDetails {
    // Instance variables
    private String uniqueID;
    private String fullName;
    private String dateOfBirth;
    private String email;
    private String contactNumber;
    private String address1;
    private String address2;
    private String city;
    private String county;
    private String postcode;
    private String institution;
    private String certificate;
    private String grade;
    private String graduationDate;
    private String dateOfApplication;

    // Uploaded files and file handling
    private String applicantFolderName;
    private String profilePicturePath;
    private String passportPicturePath;
    private String documentPath; // Stores path of uploaded PDF file

    // Comments
    private String comments;

    // Submission status
    private boolean isSubmitted = false;

    //Constructor for NEW applicants (requires DOB & generates Unique ID)
    public ApplicantDetails(String fullName, String dateOfBirth, String dateOfApplication, String certificate, String grade) {
        this.uniqueID = generateUniqueID(fullName, dateOfBirth);
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.dateOfApplication = dateOfApplication;
        this.certificate = certificate;
        this.grade = grade;
        this.applicantFolderName = generateRandomFolderName();
    }



    // Getters
    public String getUniqueID() { return this.uniqueID; }
    public String getFullName() { return this.fullName; }
    public String getDateOfBirth() { return this.dateOfBirth; }
    public String getDateOfApplication() { return this.dateOfApplication; }
    public String getCertificate() { return this.certificate; }
    public String getGrade() { return this.grade; }
    public String getEmail() { return this.email; }
    public String getContactNumber() { return this.contactNumber; }
    public String getAddress1() { return this.address1; }
    public String getAddress2() { return this.address2; }
    public String getCity() { return this.city; }
    public String getCounty() { return this.county; }
    public String getPostcode() { return this.postcode; }
    public String getInstitution() { return this.institution; }
    public String getGraduationDate() { return this.graduationDate; }
    public String getProfilePicturePath() { return this.profilePicturePath; }
    public String getPassportPicturePath() { return this.passportPicturePath; }
    public String getDocumentPath() { return this.documentPath; }
    public String getComments() { return this.comments; }
    public String getApplicantFolderName() { return applicantFolderName; }
    public boolean isSubmitted() {return isSubmitted;}

    //Status logic: Tracks "Active" vs "Inactive" & "Complete" vs "Incomplete"
    public String getStatus() {
        boolean missingFields = isBlank(fullName) ||
                isBlank(dateOfBirth) ||
                isBlank(email) ||
                isBlank(contactNumber) ||
                isBlank(address1) ||
                isBlank(city) ||
                isBlank(postcode) ||
                isBlank(certificate) ||
                isBlank(grade);

        if (isSubmitted) {
            return missingFields ? "Inactive, Incomplete" : "Inactive, Complete";
        } else {
            return missingFields ? "Active, Incomplete" : "Active, Complete";
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || value.trim().equals("-");
    }

    // Setters
    public void setUniqueID(String uniqueID) { this.uniqueID = uniqueID; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setDateOfApplication(String dateOfApplication) { this.dateOfApplication = dateOfApplication; }
    public void setCertificate(String certificate) { this.certificate = certificate; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setEmail(String email) { this.email = email; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public void setCity(String city) { this.city = city; }
    public void setCounty(String county) { this.county = county; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setInstitution(String institution) { this.institution = institution; }
    public void setGraduationDate(String graduationDate) { this.graduationDate = graduationDate; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }
    public void setPassportPicturePath(String passportPicturePath) { this.passportPicturePath = passportPicturePath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public void setComments(String comments) { this.comments = comments; }
    public void setSubmitted(boolean submitted) { this.isSubmitted = submitted; }
    public void setApplicantFolderName(String applicantFolderName) { this.applicantFolderName = applicantFolderName; }

    // Unique ID Generator
    public static String generateUniqueID(String fullName, String dateOfBirth) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "UNKNOWN"; // Prevents empty names from breaking ID generation
        }

        String[] nameParts = fullName.trim().split("\\s+");
        if (nameParts.length < 2) {
            return "INVALID"; // Ensures at least first and last name exist
        }

        String firstName = nameParts[0]; // First word (First Name)
        String lastName = nameParts[nameParts.length - 1]; // Last word (Last Name)

        // Extract first and last letters
        char firstLetter = Character.toUpperCase(firstName.charAt(0));
        char lastLetter = Character.toUpperCase(lastName.charAt(lastName.length() - 1));

        // If DOB is missing, just use initials
        if (dateOfBirth == null || dateOfBirth.trim().isEmpty() || dateOfBirth.equals("-")) {
            return firstLetter + "" + lastLetter + "-UNKNOWN";
        }

        // Ensure DOB is valid
        if (!dateOfBirth.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return firstLetter + "" + lastLetter + "-INVALID"; // Return INVALID if date format is wrong
        }

        // Convert DOB to `DDMMYYYY`
        String dobFormatted = dateOfBirth.replaceAll("/", "");

        return firstLetter + "" + lastLetter + "-" + dobFormatted;
    }

    public static String generateRandomFolderName() {
        return UUID.randomUUID().toString().substring(0, 8); // 8-character unique folder name
    }

    //String representation of the object
    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s", fullName, dateOfApplication, certificate, grade);
    }
}