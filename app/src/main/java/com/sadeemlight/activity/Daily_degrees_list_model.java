package com.sadeemlight.activity;

/**
 * Created by Rajesh Dabhi on 1/9/2016.
 */
public class Daily_degrees_list_model {

    private String id, title, scoretotal,score,result_date;

    public Daily_degrees_list_model() {
    }

    public Daily_degrees_list_model(String id,String title, String scoretotal, String score, String result_date) {
        this.id = id;
        this.title = title;
        this.scoretotal = scoretotal;
        this.score = score;
        this.result_date = result_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScoretotal() {
        return scoretotal;
    }

    public void setScoretotal(String scoretotal) {
        this.scoretotal = scoretotal;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult_date() {
        return result_date;
    }

    public void setResult_date(String result_date) {
        this.result_date = result_date;
    }

}
