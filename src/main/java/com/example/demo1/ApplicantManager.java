package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class ApplicantManager {
    public ObservableList<ApplicantDetails> applicantList = FXCollections.observableArrayList();

    //Used for test file
    protected String getApplicantsFilePath() {
        return "src/main/resources/com/example/demo1/APPLICANTS.csv";
    }
    //Used for test file
    protected String getValidApplicantsFilePath() {
        return "src/main/resources/com/example/demo1/VALID_APPLICANTS.csv";
    }
    //Used for test file
    protected String getInvalidApplicantsFilePath() {
        return "src/main/resources/com/example/demo1/INVALID_APPLICANTS.csv";
    }

    // Debugging: Print when list is initialized
    public ApplicantManager() {
        System.out.println("ApplicantManager initialized. Current applicants: " + applicantList.size());
    }

    //Used to process entries from original APPLICANTS.csv file
    public void processApplicants() {
        applicantList.clear(); // Clear existing data to prevent duplication

        File validFile = new File("src/main/resources/com/example/demo1/VALID_APPLICANTS.csv");

        // Check if VALID_APPLICANTS.csv exists and isn't empty, then load directly
        if (validFile.exists() && validFile.length() > 0) {
            System.out.println("✅ VALID_APPLICANTS.csv exists and is not empty. Loading existing applicants...");
            loadValidApplicants();
            return;
        }

        System.out.println("🔄 Processing APPLICANTS.csv for the first time...");

        String line;
        int invalidCount = 0;
        StringBuilder incompleteLog = new StringBuilder();

        try (
                BufferedReader bufferRead = new BufferedReader(new FileReader("src/main/resources/com/example/demo1/APPLICANTS.csv"));
                BufferedWriter validWriter = new BufferedWriter(new FileWriter(validFile));
                BufferedWriter incompleteWriter = new BufferedWriter(new FileWriter("src/main/resources/com/example/demo1/INVALID_APPLICANTS.csv"))
        ) {
            // Write header to VALID_APPLICANTS.csv (exactly 20 columns)
            validWriter.write("Unique ID,Applicant Name,Date of Birth,Date of Application,Certificate,Grade,Email,Contact Number,Address Line 1,Address Line 2,City,State,Postcode,Institution,Graduation Date,Comments,Profile Picture Path,Passport Picture Path,Document Path,Applicant Folder Name");
            validWriter.newLine();

            // Write header to INVALID_APPLICANTS.csv (for incomplete applicants)
            incompleteWriter.write("Unique ID,Applicant Name,Date of Birth,Date of Application,Certificate,Grade");
            incompleteWriter.newLine();

            bufferRead.readLine(); // Skip header in APPLICANTS.csv

            while ((line = bufferRead.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                //prevents loss of data, even if it consists of empty values.
                String[] values = line.split(",", -1); // helps keeps empty fields instead of dropping

                if (values.length != 4) {
                    System.out.println("⚠ Skipping malformed line (expected exactly 4 columns): " + line);
                    continue;
                }

                // Extract original 4 fields from APPLICANTS.csv
                String name = values[0].trim();
                String applicationDate = values[1].trim();
                String certificate = values[2].trim();
                String grade = values[3].trim();

                String dob = "-"; // Placeholder for DOB (unknown from original file)
                String email = "N/A";
                String contactNumber = "N/A";
                String address1 = "N/A";
                String address2 = "N/A";
                String city = "N/A";
                String state = "N/A";
                String postcode = "N/A";
                String institution = "N/A";
                String graduationDate = "N/A";
                String comments = "N/A";
                String profilePicturePath = "N/A";
                String passportPicturePath = "N/A";
                String documentPath = "N/A";
                String applicantFolderName = "N/A";


                boolean isIncomplete = name.isEmpty() || applicationDate.isEmpty() || certificate.isEmpty() || grade.isEmpty();

                // Generate unique ID based on name and date of birth
                String uniqueID = ApplicantDetails.generateUniqueID(name, dob);

                //when original file is missing fields
                if (isIncomplete) {
                    incompleteWriter.write(String.join(",", uniqueID, name, dob, applicationDate, certificate, grade));
                    incompleteWriter.newLine();
                    incompleteLog.append("❌ Invalid: ").append(name).append("\n----------------------------------------\n");
                    invalidCount++;
                    continue;
                }

                // Write complete data to VALID_APPLICANTS.csv
                validWriter.write(String.join(",",
                        uniqueID, name, dob, applicationDate, certificate, grade,
                        email, contactNumber, address1, address2, city, state, postcode,
                        institution, graduationDate, comments, profilePicturePath, passportPicturePath, documentPath,
                        applicantFolderName
                ));
                validWriter.newLine();

                // Add applicant details to observable list
                ApplicantDetails applicant = new ApplicantDetails(name, dob, applicationDate, certificate, grade);
                applicant.setUniqueID(uniqueID);
                applicant.setApplicantFolderName(applicantFolderName);
                applicantList.add(applicant);
            }

            System.out.println("✅ Applicants processed successfully.");
            System.out.println("✅ Valid applicants: " + applicantList.size());

            if (invalidCount > 0) {
                System.out.println("\n📌 Moved to INVALID_APPLICANTS.csv:\n" + incompleteLog);
            } else {
                System.out.println("✅ No incomplete applicants found.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error processing applicants: " + e.getMessage());
        }
    }

    public void displayApplicants() {
        for (ApplicantDetails applicant : applicantList) {
            System.out.println(applicant);
        }
        System.out.println("--------------------------------------------------------------");
    }

    public void saveApplicants() {
        if (applicantList.isEmpty()) {
            System.out.println("No applicants found to save.");
            return;
        }

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter("src/main/resources/com/example/demo1/VALID_APPLICANTS.csv"))) {
            // FIX: Ensure fields are in the correct order
            bufferWrite.write("Unique ID,Applicant Name,Date of Birth,Date of Application,Certificate,Grade,Email,Contact Number,Address Line 1,Address Line 2,City,State,Postcode,Institution,Graduation Date,Comments,Profile Picture Path,Passport Picture Path,Document Path,Applicant Folder Name,Submitted");
            bufferWrite.newLine();

            for (ApplicantDetails applicant : applicantList) {
                // Convert isSubmitted to a string ("true"/"false")
                String submittedStr = String.valueOf(applicant.isSubmitted());
                // Ensure values align with the correct column headers
                bufferWrite.write(String.join(",",
                        applicant.getUniqueID(),
                        applicant.getFullName(),
                        applicant.getDateOfBirth() != null ? applicant.getDateOfBirth() : "-",
                        applicant.getDateOfApplication() != null ? applicant.getDateOfApplication() : "-",
                        applicant.getCertificate() != null ? applicant.getCertificate() : "N/A",
                        applicant.getGrade() != null ? applicant.getGrade() : "N/A",
                        applicant.getEmail() != null ? applicant.getEmail() : "N/A",
                        applicant.getContactNumber() != null ? applicant.getContactNumber() : "N/A",
                        applicant.getAddress1() != null ? applicant.getAddress1() : "N/A",
                        applicant.getAddress2() != null ? applicant.getAddress2() : "N/A",
                        applicant.getCity() != null ? applicant.getCity() : "N/A",
                        applicant.getCounty() != null ? applicant.getCounty() : "N/A",
                        applicant.getPostcode() != null ? applicant.getPostcode() : "N/A",
                        applicant.getInstitution() != null ? applicant.getInstitution() : "N/A",
                        applicant.getGraduationDate() != null ? applicant.getGraduationDate() : "N/A",
                        applicant.getComments() != null ? applicant.getComments() : "N/A",
                        applicant.getProfilePicturePath() != null ? applicant.getProfilePicturePath() : "N/A",
                        applicant.getPassportPicturePath() != null ? applicant.getPassportPicturePath() : "N/A",
                        applicant.getDocumentPath() != null ? applicant.getDocumentPath() : "N/A",
                        applicant.getApplicantFolderName(),
                        submittedStr
                ));
                bufferWrite.newLine();
            }

            System.out.println("✅ VALID_APPLICANTS.csv successfully updated.");
        } catch (IOException e) {
            System.out.println("❌ Error saving applicants: " + e.getMessage());
        }
    }

    public void loadValidApplicants() {
        applicantList.clear(); // Ensure fresh data load

        String line;
        try (BufferedReader bufferRead = new BufferedReader(new FileReader("src/main/resources/com/example/demo1/VALID_APPLICANTS.csv"))) {
            String header = bufferRead.readLine(); // Skip header
            boolean hasSubmittedColumn = header != null && header.contains("Submitted");

            while ((line = bufferRead.readLine()) != null) {
                System.out.println("📌 Reading Line: " + line);

                // If the file now has 21 columns, we expect 21 values
                String[] values = line.split(",", -1);
                if (hasSubmittedColumn && values.length != 21) {
                    System.out.println("⚠ Skipping invalid line (incorrect field count): " + line);
                    continue;
                } else if (!hasSubmittedColumn && values.length != 20) {
                    System.out.println("⚠ Skipping invalid line (incorrect field count): " + line);
                    continue;
                }

                // Correct Field Mapping (assuming the Submitted column is the 21st if present)
                String uniqueID = values[0].trim();
                String name = values[1].trim();
                String dob = values[2].trim();
                String applicationDate = values[3].trim();
                String certificate = values[4].trim();
                String grade = values[5].trim();
                String email = values[6].trim();
                String contactNumber = values[7].trim();
                String address1 = values[8].trim();
                String address2 = values[9].trim();
                String city = values[10].trim();
                String state = values[11].trim();
                String postcode = values[12].trim();
                String institution = values[13].trim();
                String graduationDate = values[14].trim();
                String comments = values[15].trim();
                String profilePicturePath = values[16].trim();
                String passportPicturePath = values[17].trim();
                String documentPath = values[18].trim();
                String applicantFolderName = values[19].trim();
                boolean submitted = false;
                if (hasSubmittedColumn) {
                    submitted = Boolean.parseBoolean(values[20].trim());
                }

                // Create ApplicantDetails object
                ApplicantDetails applicant = new ApplicantDetails(name, dob, applicationDate, certificate, grade);
                applicant.setUniqueID(uniqueID);
                applicant.setApplicantFolderName(applicantFolderName);
                applicant.setEmail(email);
                applicant.setContactNumber(contactNumber);
                applicant.setAddress1(address1);
                applicant.setAddress2(address2);
                applicant.setCity(city);
                applicant.setCounty(state);
                applicant.setPostcode(postcode);
                applicant.setInstitution(institution);
                applicant.setGraduationDate(graduationDate);
                applicant.setComments(comments);
                applicant.setProfilePicturePath(profilePicturePath.equals("N/A") ? "" : profilePicturePath);
                applicant.setPassportPicturePath(passportPicturePath.equals("N/A") ? "" : passportPicturePath);
                applicant.setDocumentPath(documentPath.equals("N/A") ? "" : documentPath);
                applicant.setApplicantFolderName(applicantFolderName.equals("N/A") ? "" : applicantFolderName);
                // Set the submitted status
                applicant.setSubmitted(submitted);

                // Add to list
                applicantList.add(applicant);
                System.out.println("✅ Applicant Loaded: " + name + " (" + uniqueID + ")");
            }

            System.out.println("✅ Valid applicants loaded from VALID_APPLICANTS.csv: " + applicantList.size());
        } catch (IOException e) {
            System.out.println("❌ Error loading VALID_APPLICANTS.csv: " + e.getMessage());
        }
    }

    //Method created solely for integration purposes
    public void writeSubmittedApplicantsToFile() {
        String outputPath = "src/main/resources/com/example/demo1/SUBMITTED_APPLICANTS.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // ✅ Same header as VALID_APPLICANTS.csv
            writer.write("Unique ID,Applicant Name,Date of Birth,Date of Application,Certificate,Grade,Email,Contact Number,Address Line 1,Address Line 2,City,State,Postcode,Institution,Graduation Date,Comments,Profile Picture Path,Passport Picture Path,Document Path,Applicant Folder Name");
            writer.newLine();

            for (ApplicantDetails applicant : applicantList) {
                if (!applicant.isSubmitted()) continue;

                writer.write(String.join(",",
                        safe(applicant.getUniqueID()),
                        safe(applicant.getFullName()),
                        safe(applicant.getDateOfBirth()),
                        safe(applicant.getDateOfApplication()),
                        safe(applicant.getCertificate()),
                        safe(applicant.getGrade()),
                        safe(applicant.getEmail()),
                        safe(applicant.getContactNumber()),
                        safe(applicant.getAddress1()),
                        safe(applicant.getAddress2()),
                        safe(applicant.getCity()),
                        safe(applicant.getCounty()),
                        safe(applicant.getPostcode()),
                        safe(applicant.getInstitution()),
                        safe(applicant.getGraduationDate()),
                        safe(applicant.getComments()),
                        safe(applicant.getProfilePicturePath()),
                        safe(applicant.getPassportPicturePath()),
                        safe(applicant.getDocumentPath()),
                        safe(applicant.getApplicantFolderName())
                ));
                writer.newLine();
            }

            System.out.println("✅ SUBMITTED_APPLICANTS.csv written successfully.");
        } catch (IOException e) {
            System.err.println("❌ Error writing SUBMITTED_APPLICANTS.csv: " + e.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }

    // Delete entries from submitted applicants when user deletes entry from dashboard
    public void deleteFromSubmittedApplicants(String uniqueID) {
        File submittedFile = new File("src/main/resources/com/example/demo1/SUBMITTED_APPLICANTS.csv");
        File tempFile = new File("src/main/resources/com/example/demo1/TEMP_SUBMITTED_APPLICANTS.csv");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(submittedFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    writer.write(line);
                    writer.newLine();
                    firstLine = false;
                    continue;
                }

                if (!line.startsWith(uniqueID + ",")) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    System.out.println("🗑️ Deleted from SUBMITTED_APPLICANTS: " + uniqueID);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error deleting from SUBMITTED_APPLICANTS.csv: " + e.getMessage());
            return;
        }

        // Replace original file
        try {
            Files.move(tempFile.toPath(), submittedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.err.println("❌ Error replacing file: " + ex.getMessage());
        }
    }
    // Delete applicant folder when applicant is removed from database
    public void deleteApplicantFolder(String folderName) {
        File folder = new File("applicants", folderName);
        if (folder.exists() && folder.isDirectory()) {
            deleteDirectoryRecursively(folder);
            System.out.println("Deleted folder: " + folder.getAbsolutePath());
        }
    }

    private void deleteDirectoryRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectoryRecursively(child);
                }
            }
        }
        file.delete();
    }

    // Log deleted applicants for other components
    public void logDeletedApplicant(String uniqueID, String fullName) {
        File logFile = new File("src/main/resources/com/example/demo1/DELETED_APPLICANTS.log");
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true))) {
            logWriter.write(uniqueID + "," + fullName);
            logWriter.newLine();
        } catch (IOException e) {
            System.err.println("Error logging deleted applicant: " + e.getMessage());
        }
    }
}


