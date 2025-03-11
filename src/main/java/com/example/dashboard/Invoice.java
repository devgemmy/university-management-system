package com.example.dashboard;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Invoice {
    private final SimpleStringProperty invoiceID;
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty institutionDetails;
    private final SimpleStringProperty courseList;
    // private final HashMap<String, String> institutionDetails;
    // private final HashMap<String, String> courseList;
    private final SimpleIntegerProperty courseInvFees;
    // private final HashMap<String, Double> sportsActivityList;
    private final SimpleDoubleProperty totalSportsCost;
    // private final HashMap<String, Double> foodItemsList;
    private final SimpleDoubleProperty totalFoodCost;
    private final SimpleStringProperty invoiceDate;

    public Invoice(
            String invID,
            String sName,
            String invDate,
            int cInvFees,
            double tSprCost,
            double tFdCost,
            String instDetails,
            String cList
            // ,HashMap<String, String> cList,
            // HashMap<String, String> instDetails,
            // HashMap<String, Double> sActivityList,
            // HashMap<String, Double> fItemsList
    ) {
        this.invoiceID = new SimpleStringProperty(invID);
        this.studentName = new SimpleStringProperty(sName);
        this.invoiceDate = new SimpleStringProperty(invDate);
        this.courseInvFees = new SimpleIntegerProperty(cInvFees);
        this.totalSportsCost = new SimpleDoubleProperty(tSprCost);
        this.totalFoodCost = new SimpleDoubleProperty(tFdCost);
        this.institutionDetails = new SimpleStringProperty(instDetails);
        this.courseList = new SimpleStringProperty(cList);
        // this.courseList = cList;
        // this.institutionDetails = instDetails;
        // this.sportsActivityList = sActivityList;
        // this.foodItemsList = fItemsList;
    }

    // Getters
    public SimpleStringProperty getInvoiceID() {
        return invoiceID;
    }
    public SimpleStringProperty getStudentName() {
        return studentName;
    }
    public SimpleIntegerProperty getCourseInvFees() {
        return courseInvFees;
    }
    public SimpleStringProperty getInvoiceDate() {
        return invoiceDate;
    }
    public SimpleDoubleProperty getTotalSportsCost() {
        return totalSportsCost;
    }
    public SimpleDoubleProperty getTotalFoodCost() {
        return totalFoodCost;
    }
//    public HashMap<String, String> getCourseList() {
//        return courseList;
//    }
//    public HashMap<String, Double> getSportsActivityList() {
//        return sportsActivityList;
//    }
//    public HashMap<String, Double> getFoodItemsList() {
//        return foodItemsList;
//    }
//    public HashMap<String, String> getInstitutionDetails() {
//        return institutionDetails;
//    }
}
