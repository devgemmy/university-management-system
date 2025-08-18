package com.example.course;

import com.example.attendance.SearchstudImport;
import com.example.dashboard.PopulateStudentCourse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;

public class CourseAssigner {

    public static void assignCourses(String applicantFilePath, String courseFilePath, String outputFilePath) {
        List<String[]> applicants = new ArrayList<>();
        List<String[]> courses = new ArrayList<>();
        Set<String> alreadyAssigned = new HashSet<>();
        List<String> existingAssignments = new ArrayList<>();
        Random random = new Random();

        // Read previously assigned applicants (if file exists)
        File assignedFile = new File(outputFilePath);
        if (assignedFile.exists()) {
            try (BufferedReader existingReader = new BufferedReader(new FileReader(assignedFile))) {
                String line;
                boolean inDeletedSection = false;
                while ((line = existingReader.readLine()) != null) {
                    if (line.startsWith("----")) {
                        inDeletedSection = true;
                        break; // stop before deleted section
                    }
                    existingAssignments.add(line);
                    if (!line.startsWith("ApplicantID")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 1) {
                            alreadyAssigned.add(parts[0]); // ApplicantID
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading existing assigned courses.");
                e.printStackTrace();
            }
        }

        // Read applicants
        try (BufferedReader reader = new BufferedReader(new FileReader(applicantFilePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                applicants.add(line.split(","));
            }

            // Read courses
            try (BufferedReader courseReader = new BufferedReader(new FileReader(courseFilePath))) {
                courseReader.readLine(); // skip header
                String courseLine;
                while ((courseLine = courseReader.readLine()) != null) {
                    courses.add(courseLine.split(","));
                }

                // Open writer (overwrite if no file, else append new entries)
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

                    // Write previously saved assignments
                    if (existingAssignments.isEmpty()) {
                        writer.write("ApplicantID,CourseID,CourseTitle,StudentID\n");
                    }
                    for (String assignment : existingAssignments) {
                        writer.write(assignment);
                        writer.newLine();
                    }

                    // Assign course to new applicants
                    for (String[] applicant : applicants) {
                        String applicantID = applicant[0];

                        if (alreadyAssigned.contains(applicantID)) {
                            continue; // skip already assigned
                        }

                        String dateOfApplication = applicant[3];
                        String year = dateOfApplication.split("/")[2].substring(2);
                        String rawID = applicantID.substring(3);
                        String shortID = rawID.substring(0, 4);
                        String studentID = year + shortID + applicantID.substring(0,2);

                        String[] course = courses.get(random.nextInt(courses.size()));
                        String courseTitle = course[0];
                        String courseID = course[1];

                        writer.write(String.join(",", applicantID, courseID, courseTitle, studentID));
                        writer.newLine();

                        // Send to Attendance
                        SearchstudImport.insertStudent(studentID, courseID);

                        // write a procedure to get the applicant for Finance
                        String studentName = applicant[1];
                        System.out.println("Student Name: " + studentName);

                        // Send to Finance
                        //System.out.println("Student ID: " + studentID + "\nCourse ID: " + courseID + "\nStudent Name: " + studentName);
                        PopulateStudentCourse.getStudentDetails(studentID, courseID, studentName);
                    }


                    // Append deleted applicants (if any)
                    File deletedLog = new File("src/main/resources/com/example/demo1/DELETED_APPLICANTS.log");
                    if (deletedLog.exists() && deletedLog.length() > 0) {
                        writer.newLine();
                        writer.write("---- Deleted Applicants ----");
                        writer.newLine();
                        try (BufferedReader logReader = new BufferedReader(new FileReader(deletedLog))) {
                            String logLine;
                            while ((logLine = logReader.readLine()) != null) {
                                writer.write("Deleted: " + logLine);
                                writer.newLine();
                            }
                        }
                    }

                    System.out.println("Courses assigned and written to: " + outputFilePath);

                } catch (IOException e) {
                    System.err.println("Failed to write output CSV");
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } catch (IOException e) {
                System.err.println("Failed to read COURSE.csv");
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Failed to read SUBMITTED_APPLICANTS.csv");
            e.printStackTrace();
        }
    }

    /**
     * Deletes the assignment record from ASSIGNED_COURSES.csv that matches the provided applicantID.
     * If found, it also calls SearchstudImport.deleteStudent to remove the attendance record.
     */
    public static void deleteAssignment(String applicantID, String assignedFilePath) {
        File assignedFile = new File(assignedFilePath);
        File tempFile = new File(assignedFilePath + ".temp");
        boolean recordFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(assignedFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Read and write header for the active file.
            String header = reader.readLine();
            writer.write(header);
            writer.newLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // If the line is empty or a log section, write it back.
                if (line.trim().isEmpty() || line.startsWith("----")) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                // Check if this line belongs to the applicant.
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(applicantID)) {
                    String courseID = parts[1];
                    String studentID = parts[3];

                    // Archive this line into ARCHIVED_ASSIGNED_COURSES.csv.
                    File archiveFile = new File("src/main/resources/com/example/course/ARCHIVED_ASSIGNED_COURSES.csv");
                    boolean writeHeader = false;
                    if (!archiveFile.exists() || archiveFile.length() == 0) {
                        writeHeader = true;
                    }
                    try (BufferedWriter archiveWriter = new BufferedWriter(new FileWriter(archiveFile, true))) {
                        if (writeHeader) {
                            archiveWriter.write("ApplicantID,CourseID,CourseTitle,StudentID");
                            archiveWriter.newLine();
                        }
                        archiveWriter.write(line);
                        archiveWriter.newLine();
                    } catch (IOException ioEx) {
                        System.err.println("Error archiving assignment: " + ioEx.getMessage());
                    }

                    System.out.println("Archiving assignment for ApplicantID: " + applicantID
                            + " | StudentID: " + studentID + " | CourseID: " + courseID);

                    // Call the attendance module to archive the record.
                    SearchstudImport.archiveStudent(studentID, courseID);
                    // Call the finance module to archive the record.
                    PopulateStudentCourse.archiveInvoice(studentID);
                    recordFound = true;

                    // Do not write this line to the active (temp) file.
                } else {
                    // Write the line unchanged to the temp file.
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error deleting assignment from " + assignedFilePath);
            e.printStackTrace();
            return;
        }

        // Replace the original ASSIGNED_COURSES.csv file with the updated temp file.
        try {
            Files.move(tempFile.toPath(), assignedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error replacing the assignment file.");
            e.printStackTrace();
        }

        if (recordFound) {
            System.out.println("Assignment record for ApplicantID " + applicantID + " has been deleted.");
        } else {
            System.out.println("No assignment record found for ApplicantID " + applicantID);
        }
    }

    public static void main(String[] args) {
        assignCourses(
                "src/main/resources/com/example/demo1/SUBMITTED_APPLICANTS.csv",
                "src/main/resources/com/example/course/COURSE.csv",
                "src/main/resources/com/example/course/ASSIGNED_COURSES.csv"
        );
    }
}