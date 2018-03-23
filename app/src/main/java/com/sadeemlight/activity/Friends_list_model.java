package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class Friends_list_model {

    private String name, points, frd_img_id;

    public Friends_list_model() {
    }

    public Friends_list_model(String name, String points, String frd_img_id) {
        this.name = name;
        this.points = points;
        this.frd_img_id = frd_img_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getFrd_img_id() {
        return frd_img_id;
    }

    public void setFrd_img_id(String frd_img_id) {
        this.frd_img_id = frd_img_id;
    }
}
