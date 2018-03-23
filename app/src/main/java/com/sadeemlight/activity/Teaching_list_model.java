package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 7/9/2016.
 */
public class Teaching_list_model {

    private String title, subtitle;
    private int msg_img_id;

    public Teaching_list_model() {
    }

    public Teaching_list_model(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
