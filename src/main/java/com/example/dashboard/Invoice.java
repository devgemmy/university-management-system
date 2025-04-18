package com.example.dashboard;

import java.util.HashMap;
import java.util.Map;

public class Invoice {
    private String invoiceID;
    private String studentID;
    private String studentName;
    private Map<String, String> institutionDetails;
    private Map<String, String> courseList;
    private double courseInvFees;
    private Map<String, Double> sportsActivities;
    private double totalSportsCost;
    private Map<String, Double> foodItems;
    private double totalFoodCost;
    private String invoiceDate;

    // Constructor overloading for default and parameterized constructors
    public Invoice() {
        this.institutionDetails = new HashMap<>();
        this.courseList = new HashMap<>();
        this.sportsActivities = new HashMap<>();
        this.foodItems = new HashMap<>();
    }

    public Invoice(String invoiceId, String studentID, String studentName, String invoiceDate, double courseFees,
            double sportsCost, double foodCost, Map<String, String> courseList,
            Map<String, String> institutionDetails, Map<String, Double> sportsActivities,
            Map<String, Double> foodItems) {
        this.invoiceID = invoiceId;
        this.studentID = studentID;
        this.studentName = studentName;
        this.invoiceDate = invoiceDate;
        this.courseInvFees = courseFees;
        this.totalSportsCost = sportsCost;
        this.totalFoodCost = foodCost;
        this.courseList = new HashMap<>(courseList);
        this.institutionDetails = new HashMap<>(institutionDetails);
        this.sportsActivities = new HashMap<>(sportsActivities);
        this.foodItems = new HashMap<>(foodItems);
    }

    // Getters and setters
    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Map<String, String> getInstitutionDetails() {
        return institutionDetails;
    }

    public void setInstitutionDetails(Map<String, String> institutionDetails) {
        this.institutionDetails = new HashMap<>(institutionDetails);
    }

    public Map<String, String> getCourseList() {
        return courseList;
    }

    public void setCourseList(Map<String, String> courseList) {
        this.courseList = new HashMap<>(courseList);
    }

    public double getCourseInvFees() {
        return courseInvFees;
    }

    public void setCourseInvFees(double courseInvFees) {
        this.courseInvFees = courseInvFees;
    }

    public Map<String, Double> getSportsActivities() {
        return sportsActivities;
    }

    public void setSportsActivities(Map<String, Double> sportsActivities) {
        this.sportsActivities = new HashMap<>(sportsActivities);
        this.totalSportsCost = sportsActivities.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalSportsCost() {
        return totalSportsCost;
    }

    public Map<String, Double> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(Map<String, Double> foodItems) {
        this.foodItems = new HashMap<>(foodItems);
        this.totalFoodCost = foodItems.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalFoodCost() {
        return totalFoodCost;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}

/*
 * 
 * -invoicelD: String
 * -studentName: String
 * -institutionDetails: Map<String, String>
 * -courseList: Map<String, String>
 * -courseInvFees: double
 * -sportsActivities: Map<String, Double>
 * -totalSportsCost: double
 * -foodItems: Map<String, Double>
 * -totalFoodCost: double
 * -invoiceDate: String
 * 
 * ----------------------------------------------
 * 
 * +public Invoice(String invoiceId, String studentName, String invoiceDate,
 * double courseFees, double sportsCost, double foodCost, Map<String, String>
 * courseList, Map<String, String> institutionDetails, Map<String, Double>
 * sportsActivities, Map<String, Double> foodItems)
 * 
 * +getInvoiceID(): String
 * +setInvoiceID(): void
 * +getStudentName(): String
 * +setStudentName(): void
 * +getInstitutionDetails(): Map<String, String>
 * +setInstitutionDetails(): void
 * +getCourseList(): Map<String, String>
 * +setCourseList(): void
 * +getCourseInvFees(): double
 * +setCourseInvFees(): void
 * +getSportsActivities(): Map<String, Double>
 * +setSportsActivities(): void
 * +getTotalSportsCost(): double
 * +getFoodItems(): Map<String, Double>
 * +setFoodItems(): void
 * +getTotalFoodCost(): double
 * +getInvoiceDate(): String
 * +setInvoiceDate(): void
 * 
 */