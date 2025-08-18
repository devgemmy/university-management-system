package com.example.course;

public class course {
	private String courseId;
    private String title;
    private int kisMode;
    private int stages;

    public course(String courseId, String title, int kisMode, int stages) {
        this.courseId = courseId;
        this.title = title;
        this.kisMode = kisMode;
        this.stages = stages;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public int getKisMode() { return kisMode; }
    public int getStages() { return stages; }

   

}
