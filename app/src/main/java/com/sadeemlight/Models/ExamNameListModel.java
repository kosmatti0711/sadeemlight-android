package com.sadeemlight.Models;

/**
 * Created by mohammedsalah on 12/20/17.
 */

public class ExamNameListModel {

    private int id;
    private String subjectName;
    private String examTitle;
    public int count;


    public ExamNameListModel(int id, String subjectName, String examTitle, int count) {
        this.id = id;
        this.subjectName = subjectName;
        this.examTitle = examTitle;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }
}
