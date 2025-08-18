//package com.example.dashboard;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Invoice {
//    private String invoiceID;
//    private String studentID;
//    private String studentName;
//    private Map<String, String> institutionDetails;
//    private Map<String, Double> courseDetailsList;
//    private ArrayList<String> courseNamesList;
//    private double courseInvFees;
//    private Map<String, Double> sportsActivities;
//    private double totalSportsCost;
//    private Map<String, Double> foodItems;
//    private double totalFoodCost;
//    private String invoiceDate;
//
//    // Constructor overloading for default and parameterized constructors
//    public Invoice() {
//        this.institutionDetails = new HashMap<>();
//        this.sportsActivities = new HashMap<>();
//        this.foodItems = new HashMap<>();
//        this.courseDetailsList = new HashMap<>();
//        this.courseNamesList = new ArrayList<>();
//    }
//
//    public Invoice(
//            String invoiceId,
//            String studentID,
//            String studentName,
//            String invoiceDate,
//            double courseFees,
//            double sportsCost,
//            double foodCost,
//            ArrayList<String> courseNamesList,
//            Map<String, String> institutionDetails,
//            Map<String, Double> courseDetailsList,
//            Map<String, Double> sportsActivities,
//            Map<String, Double> foodItems) {
//        this.invoiceID = invoiceId;
//        this.studentID = studentID;
//        this.studentName = studentName;
//        this.invoiceDate = invoiceDate;
//        this.courseInvFees = courseFees;
//        this.totalSportsCost = sportsCost;
//        this.totalFoodCost = foodCost;
//        this.courseNamesList = new ArrayList<String>(courseNamesList);
//        this.institutionDetails = new HashMap<>(institutionDetails);
//        this.courseDetailsList = new HashMap<>(courseDetailsList);
//        this.sportsActivities = new HashMap<>(sportsActivities);
//        this.foodItems = new HashMap<>(foodItems);
//    }
//
//    // GETTERS AND SETTERS
//    // Invoice ID
//    public String getInvoiceID() {
//        return invoiceID;
//    }
//
//    public void setInvoiceID(String invoiceID) {
//        this.invoiceID = invoiceID;
//    }
//
//    // Student ID
//    public String getStudentID() {
//        return studentID;
//    }
//
//    public void setStudentID(String studentID) {
//        this.studentID = studentID;
//    }
//
//    // Student name
//    public String getStudentName() {
//        return studentName;
//    }
//
//    public void setStudentName(String studentName) {
//        this.studentName = studentName;
//    }
//
//    // Institution details
//    public Map<String, String> getInstitutionDetails() {
//        return institutionDetails;
//    }
//
//    public void setInstitutionDetails(Map<String, String> institutionDetails) {
//        this.institutionDetails = new HashMap<>(institutionDetails);
//    }
//
//    // Course names list
//    public ArrayList<String> getCourseNames() {
//        return courseNamesList;
//    }
//
//    public void setCourseNames(ArrayList<String> courseNamesList) {
//        this.courseNamesList = new ArrayList<>(courseNamesList);
//    }
//
//    // Course details
//    public Map<String, Double> getCourseDetailsList() {
//        return courseDetailsList;
//    }
//
//    public void setCourseDetailsList(Map<String, Double> courseDetailsList) {
//        this.courseDetailsList = new HashMap<>(courseDetailsList);
//        this.courseInvFees = courseDetailsList.values().stream().mapToDouble(Double::doubleValue).sum();
//    }
//
//    public double getCourseInvFees() {
//        return courseInvFees;
//    }
//
//    // Sports activities
//    public Map<String, Double> getSportsActivities() {
//        return sportsActivities;
//    }
//
//    public void setSportsActivities(Map<String, Double> sportsActivities) {
//        this.sportsActivities = new HashMap<>(sportsActivities);
//        this.totalSportsCost = sportsActivities.values().stream().mapToDouble(Double::doubleValue).sum();
//    }
//
//    public double getTotalSportsCost() {
//        return totalSportsCost;
//    }
//
//    // Food items
//    public Map<String, Double> getFoodItems() {
//        return foodItems;
//    }
//
//    public void setFoodItems(Map<String, Double> foodItems) {
//        this.foodItems = new HashMap<>(foodItems);
//        this.totalFoodCost = foodItems.values().stream().mapToDouble(Double::doubleValue).sum();
//    }
//
//    public double getTotalFoodCost() {
//        return totalFoodCost;
//    }
//
//    public String getInvoiceDate() {
//        return invoiceDate;
//    }
//
//    public void setInvoiceDate(String invoiceDate) {
//        this.invoiceDate = invoiceDate;
//    }
//}
//
///*
// *
// * -invoicelD: String
// * -studentName: String
// * -studentID: String
// * -invoiceDate: String
// * -courseInvFees: double
// * -sportsActivities: Map<String, Double>
// * -totalSportsCost: double
// * -foodItems: Map<String, Double>
// * -totalFoodCost: double
// * -courseDetailsList: Map<String, Double>
// * -courseNamesList: ArrayList<String>
// * -institutionDetails: Map<String, String>
// *
// * ----------------------------------------------
// *
// * +Invoice()
// * +Invoice(String invoiceId, String studentID, String studentName, String
// * invoiceDate, double courseFees, double sportsCost, double foodCost,
// * ArrayList<String> courseNamesList, Map<String, String> institutionDetails,
// * Map<String, Double> courseDetailsList, Map<String, Double> sportsActivities,
// * Map<String, Double> foodItems)
// *
// * +getInvoiceID(): String
// * +setInvoiceID(): void
// * +getStudentID(): String
// * +setStudentID(): void
// * +getStudentName(): String
// * +setStudentName(): void
// * +getInstitutionDetails(): Map<String, String>
// * +setInstitutionDetails(): void
// * +getCourseNamesList(): ArrayList<String>
// * +setCourseList(): void
// * +getCourseDetailsList(): Map<String, Double>
// * +setCourseDetailsList(): void
// * +getCourseInvFees(): double
// * +setCourseInvFees(): void
// * +getSportsActivities(): Map<String, Double>
// * +setSportsActivities(): void
// * +getTotalSportsCost(): double
// * +getFoodItems(): Map<String, Double>
// * +setFoodItems(): void
// * +getTotalFoodCost(): double
// * +getInvoiceDate(): String
// * +setInvoiceDate(): void
// *
// * ----------------------------------------------
// */
//
////package com.example.dashboard;
////
////public class ManageInvoiceArchive {
//////    invoice_id,
//////    student_id,
//////    stuent_name,
//////    course_details,
//////    sports_activity,
//////    food_items,
//////    institution_id,
//////    invoice_date,
//////    order_history,
//////    order_totals
////}
