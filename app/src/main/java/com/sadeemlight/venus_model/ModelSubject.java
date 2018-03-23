package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class ModelSubject {

    public String id;
    public String name;

    public ModelSubject() {
    }

    public ModelSubject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
