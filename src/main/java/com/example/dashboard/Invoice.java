package com.example.dashboard;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;

public class Invoice {
    private final SimpleStringProperty invoiceID;
    private final SimpleStringProperty studentName;
    private final HashMap<String, String> institutionDetails;
    private final HashMap<String, String> courseList;
    private final SimpleDoubleProperty courseInvFees;
    private final HashMap<String, Double> sportsActivityList;
    private final SimpleDoubleProperty totalSportsCost;
    private final HashMap<String, Double> foodItemsList;
    private final SimpleDoubleProperty totalFoodCost;
    private final SimpleStringProperty invoiceDate;

    public Invoice(
            String invID,
            String sName,
            String invDate,
            double cInvFees,
            double tSprCost,
            double tFdCost,
            HashMap<String, String> cList,
            HashMap<String, String> instDetails,
            HashMap<String, Double> sActivityList,
            HashMap<String, Double> fItemsList) {
        this.invoiceID = new SimpleStringProperty(invID);
        this.studentName = new SimpleStringProperty(sName);
        this.invoiceDate = new SimpleStringProperty(invDate);
        this.courseInvFees = new SimpleDoubleProperty(cInvFees);
        this.totalSportsCost = new SimpleDoubleProperty(tSprCost);
        this.totalFoodCost = new SimpleDoubleProperty(tFdCost);
        this.courseList = cList;
        this.institutionDetails = instDetails;
        this.sportsActivityList = sActivityList;
        this.foodItemsList = fItemsList;
    }

    // Standard Java Getters
    public String getInvoiceID() {
        return invoiceID.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getInvoiceDate() {
        return invoiceDate.get();
    }

    public double getCourseInvFees() {
        return courseInvFees.get();
    }

    public double getTotalSportsCost() {
        return totalSportsCost.get();
    }

    public double getTotalFoodCost() {
        return totalFoodCost.get();
    }

    public HashMap<String, String> getInstitutionDetails() {
        return institutionDetails;
    }

    public HashMap<String, String> getCourseList() {
        return courseList;
    }

    public HashMap<String, Double> getSportsActivityList() {
        return sportsActivityList;
    }

    public HashMap<String, Double> getFoodItemsList() {
        return foodItemsList;
    }

}
