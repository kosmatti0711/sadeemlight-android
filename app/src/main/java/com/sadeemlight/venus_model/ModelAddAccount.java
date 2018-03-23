package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 21/9/2016.
 */
public class ModelAddAccount {

    public String name  = "",
            school_name  = "",
            frd_img_id  = "",
            student_id  = "",
            access_token = "";


    public ModelAddAccount() {
    }

    public ModelAddAccount(String name, String school_name, String student_id, String frd_img_id, String access_token) {
        this.name = name;
        this.school_name = school_name;
        this.student_id = student_id;
        this.frd_img_id = frd_img_id;
        this.access_token = access_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getFrd_img_id() {
        return frd_img_id;
    }

    public void setFrd_img_id(String frd_img_id) {
        this.frd_img_id = frd_img_id;
    }

}
