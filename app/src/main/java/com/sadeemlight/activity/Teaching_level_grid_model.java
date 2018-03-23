package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 6/9/2016.
 */
public class Teaching_level_grid_model {

    private String title;
    private int color;

    public Teaching_level_grid_model() {
    }

    public Teaching_level_grid_model(String title, int color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
