package com.sadeemlight.venus_model;

/**
 * Created by Rajesh Dabhi on 3/8/2016.
 */
public class ModelMessage {

    public String id;
    public String fromName="";
    public String message="";
    public boolean isSelf = true;
    public String msg_time = "";
    public String type = "";
    public String file = "";
    public int is_read = 1;
    public boolean loaded = true;
    public boolean offlinemessage = false;

    //private Boolean isSelf;

    public ModelMessage() {
    }

    public ModelMessage(String id, String fromName, String message, String date, String isSelf, String file, String type, String is_read, boolean loaded)
    {
        this.id = id;
        this.fromName = fromName;
        this.message = message;
        this.msg_time = date;
        this.isSelf = isSelf.contentEquals("1");
        this.type = type;
        this.file = file;
        this.is_read = Integer.parseInt(is_read);
        this.loaded = loaded;
    }


}
