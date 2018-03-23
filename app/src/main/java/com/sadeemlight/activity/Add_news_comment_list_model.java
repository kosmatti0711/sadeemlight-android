package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Add_news_comment_list_model {

    private String name, title, date, frd_img_id;

    public Add_news_comment_list_model() {
    }

    public Add_news_comment_list_model(String title,String name, String date, String frd_img_id) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.frd_img_id = frd_img_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String points) {
        this.date = date;
    }

    public String getFrd_img_id() {
        return frd_img_id;
    }

    public void setFrd_img_id(String frd_img_id) {
        this.frd_img_id = frd_img_id;
    }
}
