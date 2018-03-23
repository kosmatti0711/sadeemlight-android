package com.sadeemlight.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sadeemlight.MyApplication;

/**
 * Created by Rajesh Dabhi on 17/8/2016.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting() && activeNetwork.isConnected();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        /*boolean is_connect = false;

        if(activeNetwork != null
                && activeNetwork.isConnectedOrConnecting()){
            try {
                if (InetAddress.getByName("www.google.com").isReachable(2000)) {
                    // host reachable
                    is_connect = true;
                } else {
                    // host not reachable
                    is_connect = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return is_connect;*/

        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting() && activeNetwork.isConnected();

    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
