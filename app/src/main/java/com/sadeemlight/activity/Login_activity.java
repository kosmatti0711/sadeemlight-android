package com.sadeemlight.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.gcm.GCMRegistrationIntentService;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.LocaleHelper;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 5/8/2016.
 */
public class Login_activity extends Activity implements View.OnClickListener {

    Button btn_login;
    EditText et_user, et_password;
    LinearLayout ll_choose_lang;
    TextView tv_policy;

    String selected_lang = "english";

    String student_id, student_name, login_as, class_id, birth_date, gender, username,
            imagelink, response, classname, school_id, schoolname, division,
            division_id, city_id, city_name, points,school_image, parent_id, access_token;

    String get_username = "", get_password = "";
    String error_msg;

    Session_management sessionManagement;

    List<ModelParamsPair> params = new ArrayList<>();

    View view;

    String fcm_token = "";

    //Creating a broadcast receiver for com.sadeemlight.gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sessionManagement = new Session_management(Login_activity.this);

        ll_choose_lang = (LinearLayout) findViewById(R.id.ll_choose_lang);
        et_user = (EditText) findViewById(R.id.et_login_username);
        et_password = (EditText) findViewById(R.id.et_login_password);
        tv_policy = (TextView) findViewById(R.id.tv_login_privacy);

        btn_login = (Button) findViewById(R.id.btn_login);

        intializeReciver();

        checkConnection();

        if (sessionManagement.isLoggedIn()) {
//            Intent main_activity = new Intent(Login_activity.this, MainActivity.class);
//            startActivity(main_activity);
//
//            finish();
        }

        btn_login.setOnClickListener(this);
        ll_choose_lang.setOnClickListener(this);
        tv_policy.setOnClickListener(this);

        getPermission();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_login) {

            get_username = et_user.getText().toString();
            get_password = et_password.getText().toString();

            if (!get_username.contentEquals("") && !get_password.contentEquals("")) {

                if (get_username.contentEquals("test") && get_password.contentEquals("test")) {

                    sessionManagement.createLoginSession("test", "", "", "", "", ""
                            , "", "", "", "", "", "", selected_lang, "", "", "",
                            "","","", "");

                    Intent main_activity = new Intent(Login_activity.this, MainActivity.class);
                    startActivity(main_activity);

                    finish();
                } else {

                    fcm_token = FirebaseInstanceId.getInstance().getToken();

                    if(fcm_token == null)
                    {
                        GlobalFunction.outputToast(this, "Please wait. Now fetching token...");
                        return;
                    }

                    Log.d("key_token: ", fcm_token);

                    params.add(new ModelParamsPair("username", get_username));
                    params.add(new ModelParamsPair("password", get_password));
                    params.add(new ModelParamsPair("device_type", "Android"));
                    params.add(new ModelParamsPair("device_token", fcm_token));

                    btn_login.setEnabled(false);
                    btn_login.setClickable(false);
                    btn_login.setPressed(true);

                    if (ConnectivityReceiver.isConnected()) {

                        new checkLogin().execute();
                    }
                }
            } else {
                Toast.makeText(Login_activity.this, "Please Fill Detail.", Toast.LENGTH_SHORT).show();
            }

        } else if (view.getId() == R.id.ll_choose_lang) {
            final PopupMenu popup = new PopupMenu(this, ll_choose_lang,Gravity.CENTER);

            // set force icon in popup
            /*try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                .getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(
                                "setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            popup.getMenuInflater().inflate(R.menu.language_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int i = item.getItemId();
                    if (i == R.id.lang_en) {

                        selected_lang = "english";
                        LocaleHelper.setLocale(Login_activity.this, "en");

                        refresh_view();

                        et_user.setGravity(Gravity.LEFT);
                        et_password.setGravity(Gravity.LEFT);
                        return true;
                    } else if (i == R.id.lang_ar) {

                        selected_lang = "arabic";
                        LocaleHelper.setLocale(Login_activity.this, "ar");

                        refresh_view();

                        et_user.setGravity(Gravity.RIGHT);
                        et_password.setGravity(Gravity.RIGHT);
                        return true;
                    } else if (i == R.id.lang_ku) {
                        //do something
                        selected_lang = "Kurdish";
                        LocaleHelper.setLocale(Login_activity.this, "ckb");

                        refresh_view();

                        et_user.setGravity(Gravity.RIGHT);
                        et_password.setGravity(Gravity.RIGHT);
                        return true;
                    } else {
                        return onMenuItemClick(item);
                    }
                }
            });

            popup.show();

        }else if(view.getId() == R.id.tv_login_privacy){

            Intent policy = new Intent(Login_activity.this, Privacy_policy_activity.class);
            startActivity(policy);
        }
    }

    String api_output = "";

    public class checkLogin extends AsyncTask<Void, Void, Void> {

        String response = "";
        Progress_dialog pd = new Progress_dialog(Login_activity.this, android.R.attr.progressBarStyleLarge);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            error_msg = "";

            if(false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            ServiceHandler sh = new ServiceHandler();


            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.LOGIN_URL, ConstValue.POST, params,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }


            api_output = jsonSTR;

            if (jsonSTR != null) {

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    error_msg = jsonObject.optString("Message");

                    JSONObject obj = jsonObject.getJSONObject("Data");

                    access_token = obj.optString("access_token");
                    if(obj.has("parent_id"))
                    {
                        parent_id = obj.getString("parent_id");
                    }
                    else
                    {
                        parent_id ="";
                    }

                    login_as = obj.getString("login_as");

                    JSONObject studentObj = obj.getJSONObject("Student");
                    student_id = studentObj.getString("student_id");
                    student_name = studentObj.getString("student_name");
                    class_id = studentObj.getString("class_id");
                    classname = studentObj.getString("class_name");
                    division =  studentObj.getString("division_name");
                    school_id = studentObj.getString("school_id");
                    schoolname = studentObj.getString("school_name");
                    birth_date = studentObj.getString("birth_date");
                    gender = studentObj.getString("gender");
                    username = studentObj.getString("username");
                    imagelink = studentObj.getString("student_image");

                    division_id = studentObj.getString("division_id");
                    city_id = studentObj.getString("city_id");
                    city_name = studentObj.getString("city_name");
                    points = studentObj.getString("points");
                    school_image = studentObj.getString("school_image");
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

            pd.dismissProgress();
            Log.e("logindata: ", response + "," + imagelink + "," + student_name + "," + selected_lang + ","
                    + get_username + "," + get_password + "," + schoolname);

            btn_login.setEnabled(true);
            btn_login.setClickable(true);
            btn_login.setPressed(false);


            if (response.contentEquals("true"))
            {
                Log.e("valid: ", "true");

                sessionManagement.createLoginSession(student_name, "", student_id, login_as, class_id, birth_date
                        , gender, imagelink, schoolname, school_id, classname, division, selected_lang,
                        division_id, city_id, city_name, points,
                        school_image,parent_id, access_token);

                Intent main_activity = new Intent(Login_activity.this, MainActivity.class);
                startActivity(main_activity);

                finish();

            }
            else
            {
                Toast.makeText(Login_activity.this, ""+error_msg, Toast.LENGTH_SHORT).show();
                Log.e("valid: ", "not");
            }
        }
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        final View view = findViewById(R.id.login_acticity);

        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.YELLOW;

            Snackbar snackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG);

            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(Login_activity.this);
                    dialog.setTitle("Your data is Off");
                    dialog.setMessage("Turn on data or Wi-Fi in Settings.");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });

                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    public void refresh_view() {

        Resources resources = getResources();

        et_user.setHint(resources.getString(R.string.login_et_user));
        et_password.setHint(resources.getString(R.string.login_et_pass));
        btn_login.setText(resources.getString(R.string.login_btn_login));
    }

    public void intializeReciver() {
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");

                    Log.e("Registration token: ", token);

                    fcm_token = token;

                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void getPermission()
    {
        List<String> permissions =new ArrayList<>();

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED )
        {
            permissions.add(android.Manifest.permission.CALL_PHONE);
        }

        if(Build.VERSION.SDK_INT >18 )
        {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(android.Manifest.permission.WAKE_LOCK);
            }
        }

        if(Build.VERSION.SDK_INT >= 23)
        {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);

                permissions.add(android.Manifest.permission.CAMERA);
                permissions.add(android.Manifest.permission.RECORD_AUDIO);
            }
        }

        if(permissions.size() > 0)
        {
            String [] per = new String[permissions.size()];

            for (int i=0;i<permissions.size();i++) {
                per[i] = new String(permissions.get(i));
            }

            ActivityCompat.requestPermissions(this, per, 10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    GlobalFunction.outputToast(this, "Permission denied to read GPS");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        // If request is cancelled, the result arrays are empty.
        if(requestCode == 10)
        {
            List<String> str = new ArrayList<String>();
            int i;
            boolean bAllAlowed = true;
            for(i = 0; i < grantResults.length; i ++)
            {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    str.add(permissions[i]);
                    bAllAlowed = false;
                    // break;
                }
            }

            if(bAllAlowed == true)
            {

            }
            else
            {
                GlobalFunction.outputToast(this,"Can not run this app");
                finish();
            }
        }
        // other 'case' lines to check for other
        // permissions this app might request
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
