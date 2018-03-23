package com.sadeemlight.venus_model;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class ModelAudioSubject {

    public String subject_id;
    public String subject_name;

    private int color = 0;

    public ModelAudioSubject() {
    }

    public ModelAudioSubject(String id, String name) {
        this.subject_id = id;
        this.subject_name = name;
    }


    public int getColor()
    {
        if(color == 0)
        {
            Random rnd = new Random();

            color = Color.argb(255, 30 + rnd.nextInt(150),30 +  rnd.nextInt(150),30 +  rnd.nextInt(150));
        }

        return color;
    }
}
