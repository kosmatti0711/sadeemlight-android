package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 16/8/2016.
 */
public class Home_grid_model {

    private int title;
    private int img_icon_id;

    public Home_grid_model() {
    }

    public Home_grid_model(int title, int img_icon_id) {
        this.title = title;
        this.img_icon_id = img_icon_id;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getImg_icon_id() {
        return img_icon_id;
    }

    public void setImg_icon_id(int img_icon_id) {
        this.img_icon_id = img_icon_id;
    }
}
