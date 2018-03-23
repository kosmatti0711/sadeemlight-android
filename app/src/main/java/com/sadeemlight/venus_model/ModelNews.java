package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 6/8/2016.
 */
public class ModelNews {

    private String news_id, title, date_time,message,likes,views,comments,img_id,
            isliked,isviewed,msg_img_id, youtube_url, news_type;

    public ModelNews() {
    }

    public ModelNews(String news_id, String title, String date_time, String message, String likes,
                     String views, String comments/*, String img_id*/, String msg_img_id,
                     String isliked, String isviewed, String youtube_url, String news_type) {
        this.news_id = news_id;
        this.title = title;
        this.date_time = date_time;
        this.message = message;
        this.likes = likes;
        this.views = views;
        this.comments = comments;
        this.img_id = img_id;
        this.msg_img_id = msg_img_id;
        this.isliked = isliked;
        this.isviewed = isviewed;
        this.youtube_url = youtube_url;
        this.news_type = news_type;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getMsg_img_id() {
        return msg_img_id;
    }

    public void setMsg_img_id(String msg_img_id) {
        this.msg_img_id = msg_img_id;
    }

    public String getIsliked() {
        return isliked;
    }

    public void setIsliked(String isliked) {
        this.isliked = isliked;
    }

    public String getIsviewed() {
        return isviewed;
    }

    public void setIsviewed(String isviewed) {
        this.isviewed = isviewed;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }

    public String getNews_type() {
        return news_type;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }
}
