package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 18/8/2016.
 */
public class Homework_list_model {

    private String title, date_time,classname,detail;
    private int msg_img_id;

    public Homework_list_model() {
    }

    public Homework_list_model(String title, String date_time, String classname, String detail) {
        this.title = title;
        this.date_time = date_time;
        this.classname = classname;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
