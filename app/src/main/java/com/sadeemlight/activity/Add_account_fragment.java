package com.sadeemlight.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.AddAccountAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelAddAccount;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 10/9/2016.
 */
public class Add_account_fragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    ListView lv;
    EditText et_username, et_password;
    Button add_account;

    private AddAccountAdapter adapter;
    private List<ModelAddAccount> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    String getparent_id, get_student_id;

    String getstudent_id, student_name, login_as, getclass_id, birth_date, gender, username,
            imagelink, classname, school_id, schoolname, division,
            division_id, city_id, city_name, points, school_image, access_token;

    public Add_account_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_account_activity, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_addacount);

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(
                    settings.getString("sadeemlight" + "addaccount",
                            ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv = (ListView) view.findViewById(R.id.lv_show_students);
        et_username = (EditText) view.findViewById(R.id.et_addaccount_username);
        et_password = (EditText) view.findViewById(R.id.et_addaccount_password);
        add_account = (Button) view.findViewById(R.id.btn_add_account);

        add_account.setOnClickListener(this);

        getparent_id = sessionManagement.getUserDetails().get(ConstValue.KEY_PARENT_ID);
        get_student_id = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

        if (ConnectivityReceiver.isConnected())
        {
            new getAccountlist().execute();
        }
        else
        {
            addData();
        }

        lv.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        String getusername = et_username.getText().toString();
        String getpassword = et_password.getText().toString();

        if (!getusername.contentEquals("") && !getpassword.contentEquals(""))
        {
            new addAccount().execute();
        }
        else
        {
            Toast.makeText(getActivity(), "Please fill detail", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        sessionManagement.clearOffline();

        getstudent_id = listarray.get(i).get("id");
        student_name = listarray.get(i).get("student_name");
        login_as = listarray.get(i).get("login_as");
        getclass_id = listarray.get(i).get("class_id");
        classname = listarray.get(i).get("class_name");
        division = listarray.get(i).get("division_name");
        school_id = listarray.get(i).get("school_id");
        schoolname = listarray.get(i).get("school_name");
        birth_date = listarray.get(i).get("birth_date");
        gender = listarray.get(i).get("gender");
        username = listarray.get(i).get("username");
        imagelink = listarray.get(i).get("student_image");

        division_id = listarray.get(i).get("division_id");
        city_id = listarray.get(i).get("city_id");
        city_name = listarray.get(i).get("city_name");
        points = listarray.get(i).get("points");
        school_image = listarray.get(i).get("school_image");
        access_token = listarray.get(i).get("access_token");

        sessionManagement.setAlluserDetail(student_name, "", getstudent_id, getclass_id, birth_date
                , gender, imagelink, schoolname, school_id, classname, division,
                division_id, city_id, city_name, points,
                school_image, access_token);

        ((MainActivity) getActivity()).setfinish();

        Intent refresh = new Intent(getActivity(), MainActivity.class);
        startActivity(refresh);

    }

    String message = "";
    public class getAccountlist extends AsyncTask<Void, Void, Void> {

        String response;
        String error_msg;

        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            ServiceHandler sh = new ServiceHandler();

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String url = ConstValue.ACCOUNT_LIST_URL + "?parent_id=" + getparent_id;
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(Defines.APP_LOG_TITLE, url);
            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.d(Defines.APP_LOG_TITLE, jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");
                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Accounts");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("student_id"));
                        map.put("student_name", c.getString("student_name"));
                        map.put("student_image", c.getString("student_image"));
                        map.put("login_as", c.getString("login_as"));
                        map.put("class_id", c.getString("class_id"));
                        map.put("class_name", c.getString("class_name"));
                        map.put("division_name", c.getString("division_name"));
                        map.put("school_id", c.getString("school_id"));
                        map.put("school_name", c.getString("school_name"));
                        map.put("birth_date", c.getString("birth_date"));
                        map.put("gender", c.getString("gender"));
                        map.put("username", c.getString("username"));
                        map.put("division_id", c.getString("division_id"));
                        map.put("city_id", c.getString("city_id"));
                        map.put("points", c.getString("points"));
                        map.put("school_image", c.getString("school_image"));
                        map.put("access_token",  c.getString("access_token"));

                        listarray.add(map);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismissProgress();

            if (response == "true")
            {
                try
                {
                    settings.edit().putString("sadeemlight" + "addaccount", ObjectSerializer.serialize(listarray)).commit();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                addData();
            }
            else
            {
                GlobalFunction.outputToast(getActivity(), message);
                addData();
            }
        }
    }

    public void addData() {

        for (int i = 0; i < listarray.size(); i++) {

            list_adapter.add(new ModelAddAccount(listarray.get(i).get("student_name"),
                    listarray.get(i).get("school_name"),
                    listarray.get(i).get("id"),
                    listarray.get(i).get("student_image"),
                    listarray.get(i).get("access_token")));

        }

        adapter = new AddAccountAdapter(getActivity(), list_adapter);

        lv.setAdapter(adapter);
    }

    public class addAccount extends AsyncTask<Void, Void, Void> {


        String response;
        String message;

        List<ModelParamsPair> params = new ArrayList<>();
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            String getusername = et_username.getText().toString();
            String getpassword = et_password.getText().toString();

            String fcm_token = FirebaseInstanceId.getInstance().getToken();

            if(fcm_token == null)
            {
                GlobalFunction.outputToast(getActivity(), "Please wait. Now fetching token...");
                return;
            }

            params.add(new ModelParamsPair("parent_id", getparent_id));
            params.add(new ModelParamsPair("student_id", get_student_id));
            params.add(new ModelParamsPair("username", getusername));
            params.add(new ModelParamsPair("password", getpassword));
            params.add(new ModelParamsPair("device_type", "Android"));
            params.add(new ModelParamsPair("device_token", fcm_token));

            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            response = "";
            message = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.ADDACCOUNT_URL, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                list_adapter.clear();
                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    if(response.contentEquals("true"))
                    {

                    }
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

            if (response.contentEquals("true")) {

                et_username.setText("");
                et_password.setText("");

                new getAccountlist().execute();
            }
            else
            {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
