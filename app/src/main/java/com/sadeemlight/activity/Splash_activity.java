package com.sadeemlight.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sadeemlight.R;

import com.sadeemlight.fcm.MyFirebaseMessagingService;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 2/8/2016.
 */
public class Splash_activity extends AppCompatActivity {

    Session_management sessionManagement;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        GlobalFunction.init(this);
        sessionManagement = new Session_management(Splash_activity.this);
        // thread use for hold next process some time.
        Thread th = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    // start activity after 2000 milisecond
                    if (sessionManagement.isLoggedIn() && true) {
                        Intent main_activity = new Intent(Splash_activity.this, MainActivity.class);
                        startActivity(main_activity);

                        finish();
                    }else {
                        Intent startactivity = new Intent(Splash_activity.this, Login_activity.class);
                        startActivity(startactivity);
                        finish();
                    }
                }
            }
        };
        th.start();

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
