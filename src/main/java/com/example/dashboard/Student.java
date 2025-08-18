package com.example.dashboard;

public class Student {
    private final javafx.beans.property.IntegerProperty index;
    private String studentID;
    private String studentName;
    private String courseID;

    public Student(int index, String studentId, String studentName, String courseId) {
        this.index = new javafx.beans.property.SimpleIntegerProperty(index);
        this.studentID = studentId;
        this.studentName = studentName;
        this.courseID = courseId;
    }

    public javafx.beans.property.IntegerProperty indexProperty() {
        return index;
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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String studentName) {
        this.courseID = courseID;
    }
}
