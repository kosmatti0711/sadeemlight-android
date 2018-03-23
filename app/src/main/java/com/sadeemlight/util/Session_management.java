package com.sadeemlight.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sadeemlight.activity.Login_activity;

import java.util.HashMap;

import com.sadeemlight.config.ConstValue;

/**
 * Created by Rajesh Dabhi on 5/8/2016.
 */
public class Session_management {

    SharedPreferences prefs;
    SharedPreferences setting;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor setting_editor;

    Context context;

    int PRIVATE_MODE = 0;

    public Session_management(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(ConstValue.PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

        setting = context.getSharedPreferences("MAIN_PREF", PRIVATE_MODE);
        setting_editor = setting.edit();
    }

    public void createLoginSession(String name, String email, String student_id, String login_as,
                                   String class_id, String bithdate, String gender, String img_link,
                                   String schoolname, String school_id, String classname, String division,
                                   String lang, String division_id, String city_id, String city_name,
                                   String points,String school_image, String parent_id, String access_token) {

        editor.putBoolean(ConstValue.IS_LOGIN, true);
        editor.putString(ConstValue.KEY_NAME, name);
        editor.putString(ConstValue.KEY_EMAIL, email);
        editor.putString(ConstValue.KEY_LANG, lang);

        editor.putString(ConstValue.KEY_STUDENT_ID, student_id);
        editor.putString(ConstValue.KEY_LOGINAS, login_as);
        editor.putString(ConstValue.KEY_CLASSID, class_id);
        editor.putString(ConstValue.KEY_BIRTHDATE, bithdate);
        editor.putString(ConstValue.KEY_GENDER, gender);
        editor.putString(ConstValue.KEY_IMAGELINK, img_link);
        editor.putString(ConstValue.KEY_SCHOOL_ID, school_id);
        editor.putString(ConstValue.KEY_SCHOOL_NAME, schoolname);
        editor.putString(ConstValue.KEY_CLASS_NAME, classname);
        editor.putString(ConstValue.KEY_DIVISION_NAME, division);

        editor.putString(ConstValue.KEY_DIVISION_ID, division_id);
        editor.putString(ConstValue.KEY_CITY_ID, city_id);
        editor.putString(ConstValue.KEY_CITY_NAME, city_name);
        editor.putString(ConstValue.KEY_POINTS, points);
        editor.putString(ConstValue.KEY_SCHOOL_IMAGE, school_image);
        editor.putString(ConstValue.KEY_PARENT_ID, parent_id);

        editor.putString(ConstValue.KEY_TOKEN, "");
        editor.putString(ConstValue.KEY_ACCESSTOKEN, access_token);

        editor.commit();
    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {
            Intent loginsucces = new Intent(context, Login_activity.class);
            // Closing all the Activities
            loginsucces.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            loginsucces.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(loginsucces);
        }
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(ConstValue.KEY_NAME, prefs.getString(ConstValue.KEY_NAME, null));

        // user email id
        user.put(ConstValue.KEY_EMAIL, prefs.getString(ConstValue.KEY_EMAIL, null));

        // user language
        user.put(ConstValue.KEY_LANG, prefs.getString(ConstValue.KEY_LANG, null));

        user.put(ConstValue.KEY_STUDENT_ID, prefs.getString(ConstValue.KEY_STUDENT_ID, null));

        user.put(ConstValue.KEY_LOGINAS, prefs.getString(ConstValue.KEY_LOGINAS, null));

        user.put(ConstValue.KEY_CLASSID, prefs.getString(ConstValue.KEY_CLASSID, null));

        user.put(ConstValue.KEY_BIRTHDATE, prefs.getString(ConstValue.KEY_BIRTHDATE, null));

        user.put(ConstValue.KEY_GENDER, prefs.getString(ConstValue.KEY_GENDER, null));

        user.put(ConstValue.KEY_IMAGELINK, prefs.getString(ConstValue.KEY_IMAGELINK, null));

        user.put(ConstValue.KEY_SCHOOL_ID, prefs.getString(ConstValue.KEY_SCHOOL_ID, null));

        user.put(ConstValue.KEY_SCHOOL_NAME, prefs.getString(ConstValue.KEY_SCHOOL_NAME, null));

        user.put(ConstValue.KEY_CLASS_NAME, prefs.getString(ConstValue.KEY_CLASS_NAME, null));

        user.put(ConstValue.KEY_DIVISION_NAME, prefs.getString(ConstValue.KEY_DIVISION_NAME, null));

        user.put(ConstValue.KEY_DIVISION_ID, prefs.getString(ConstValue.KEY_DIVISION_ID, null));

        user.put(ConstValue.KEY_CITY_ID, prefs.getString(ConstValue.KEY_CITY_ID, null));

        user.put(ConstValue.KEY_CITY_NAME, prefs.getString(ConstValue.KEY_CITY_NAME, null));

        user.put(ConstValue.KEY_POINTS, prefs.getString(ConstValue.KEY_POINTS, null));

        user.put(ConstValue.KEY_TOKEN, prefs.getString(ConstValue.KEY_TOKEN, null));

        user.put(ConstValue.KEY_ACCESSTOKEN, prefs.getString(ConstValue.KEY_ACCESSTOKEN, null));

        user.put(ConstValue.KEY_SCHOOL_IMAGE, prefs.getString(ConstValue.KEY_SCHOOL_IMAGE, null));

        user.put(ConstValue.KEY_PARENT_ID, prefs.getString(ConstValue.KEY_PARENT_ID, null));

        // return user
        return user;
    }

    public void setUserDetail(String name, String data) {
        if (name.contentEquals(ConstValue.KEY_IMAGELINK)) {
            editor.putString(ConstValue.KEY_IMAGELINK, data);
        } else if(name.contentEquals(ConstValue.KEY_TOKEN)){
            editor.putString(ConstValue.KEY_TOKEN, data);
        }
        editor.apply();
    }

    public void setAlluserDetail(String name, String email, String student_id,
                                 String class_id, String bithdate, String gender, String img_link,
                                 String schoolname, String school_id, String classname, String division,
                                 String division_id, String city_id, String city_name,
                                 String points,String school_image, String access_token){

        editor.putBoolean(ConstValue.IS_LOGIN, true);
        editor.putString(ConstValue.KEY_NAME, name);
        editor.putString(ConstValue.KEY_EMAIL, email);

        editor.putString(ConstValue.KEY_STUDENT_ID, student_id);
        editor.putString(ConstValue.KEY_CLASSID, class_id);
        editor.putString(ConstValue.KEY_BIRTHDATE, bithdate);
        editor.putString(ConstValue.KEY_GENDER, gender);
        editor.putString(ConstValue.KEY_IMAGELINK, img_link);
        editor.putString(ConstValue.KEY_SCHOOL_ID, school_id);
        editor.putString(ConstValue.KEY_SCHOOL_NAME, schoolname);
        editor.putString(ConstValue.KEY_CLASS_NAME, classname);
        editor.putString(ConstValue.KEY_DIVISION_NAME, division);

        editor.putString(ConstValue.KEY_DIVISION_ID, division_id);
        editor.putString(ConstValue.KEY_CITY_ID, city_id);
        editor.putString(ConstValue.KEY_CITY_NAME, city_name);
        editor.putString(ConstValue.KEY_POINTS, points);
        editor.putString(ConstValue.KEY_SCHOOL_IMAGE, school_image);

        editor.putString(ConstValue.KEY_ACCESSTOKEN, access_token);

        editor.apply();
    }

    public void logoutSession() {
        setting_editor.clear();
        setting_editor.commit();

        editor.clear();
        editor.commit();

        Intent logout = new Intent(context, Login_activity.class);
        // Closing all the Activities
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(logout);
    }

    public void clearOffline(){
        setting_editor.clear();
        setting_editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn() {
        return prefs.getBoolean(ConstValue.IS_LOGIN, false);
    }

}
