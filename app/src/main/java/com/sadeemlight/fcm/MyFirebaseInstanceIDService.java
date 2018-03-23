package com.sadeemlight.fcm;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {

    private Session_management sessionManagement;
    public SharedPreferences settings;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sessionManagement = new Session_management(this);
        settings = getSharedPreferences("MAIN_PREF", 0);
    }
    // [START refresh_token]
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(Defines.APP_LOG_TITLE, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token)
    {
        // Add custom implementation, as needed.
        String old = settings.getString("sadeemlight-" + "devicetoken", "");

        settings.edit().putString("sadeemlight-" + "devicetoken", token).apply();
        if(old.contentEquals(""))
        {
            return;
        }

        List<ModelParamsPair> params = new ArrayList<>();
        params.add(new ModelParamsPair("old", old));
        params.add(new ModelParamsPair("new", token));

        new sendRegistrationToServer().execute(params);
    }

    public class sendRegistrationToServer extends AsyncTask<List<ModelParamsPair>, Void, Void>
    {
        String response = "";
        String message = "";
        int code = 0;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(List<ModelParamsPair>... listParam)
        {
            List<ModelParamsPair> params = listParam[0];
            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.BASE_URL_NEW + "token/refresh";

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.optString("Message");
                    code = jsonObject.getInt("Code");

                    JSONObject obj = jsonObject.getJSONObject("Data");


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response.contentEquals("true"))
            {

            }
            else
            {
                GlobalFunction.outputToast(MyFirebaseInstanceIDService.this, message);
            }
        }
    }
}
