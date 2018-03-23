package com.sadeemlight.venus_uis.utils;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by VENUS on 12/14/2016.
 */

public class GlobalProfile
{

    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

  //  public static String baseApiUrl = "http://192.168.1.132/counter/counterapi/";
    public static String baseApiUrl = "http://www.smsaruae.net/tsbeeh/counter/counterapi/";

    public static void init(Context parent)
    {
    }

    public static void appendResport(String msg)
    {
        String dirpath = GlobalFunction.getTempFolderPath();
        String filepath = dirpath + "/report.txt";

        File t = new File(filepath);
        try {

            FileWriter writer = new FileWriter(t, true);
            writer.append(msg);
            writer.append("\n\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
