package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 31/8/2016.
 */
public class ModelNotification {

    private String title, date_time,detail;
    private int msg_img_id;

    public ModelNotification() {
    }

    public ModelNotification(String title, String date_time, String detail) {
        this.title = title;
        this.date_time = date_time;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
