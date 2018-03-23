package com.sadeemlight.Models;

/**
 * Created by mohammedsalah on 11/25/17.
 */

public class ModelParamsPair {

    private String key;
    private String value;

    public ModelParamsPair(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
