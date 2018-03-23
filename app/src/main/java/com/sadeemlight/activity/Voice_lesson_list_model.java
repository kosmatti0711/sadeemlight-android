package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Voice_lesson_list_model {

    private String title, link;
    private int msg_img_id;

    public Voice_lesson_list_model() {
    }

    public Voice_lesson_list_model(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
