package com.sadeemlight;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.LocaleHelper;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@TargetApi(Build.VERSION_CODES.DONUT)
@ReportsCrashes(
        mailTo = "kosmatti0711@gmail.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
/**
 * Created by Rajesh Dabhi on 17/8/2016.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
       // ACRA.init(this);

        LocaleHelper.onCreate(this,LocaleHelper.getLanguage(this));
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LocaleHelper.onCreate(this,LocaleHelper.getLanguage(this));
    }
}
