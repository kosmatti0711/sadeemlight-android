package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 12/8/2016.
 */
public class Archive_list_model {

    private String date, status;
    private int img_id;

    public Archive_list_model() {
    }

    public Archive_list_model(String date, String status, int img_id) {
        this.date = date;
        this.status = status;
        this.img_id = img_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

}
