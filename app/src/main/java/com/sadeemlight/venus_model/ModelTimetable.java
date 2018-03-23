package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 1/9/2016.
 */
public class ModelTimetable {

    public String subject_id;
    public String teacherName;
    public String subjectName;
    public int lesson_no;

    public ModelTimetable() {
    }

    public ModelTimetable(String id, String lesson_no, String teacher, String subject) {
        this.lesson_no = Integer.parseInt(lesson_no);
        this.subjectName = subject;
        this.teacherName = teacher;
        this.subject_id = id;
    }

}
