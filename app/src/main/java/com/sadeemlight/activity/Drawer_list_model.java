package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 11/8/2016.
 */
public class Drawer_list_model {

    private String number;
    private int img_icon_id,title;

    public Drawer_list_model() {
    }

    public Drawer_list_model(int title, String number, int img_icon_id) {
        this.title = title;
        this.number = number;
        this.img_icon_id = img_icon_id;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImg_icon_id() {
        return img_icon_id;
    }

    public void setImg_icon_id(int img_icon_id) {
        this.img_icon_id = img_icon_id;
    }

}
