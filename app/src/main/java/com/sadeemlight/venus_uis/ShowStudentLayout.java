package com.sadeemlight.venus_uis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.activity.Add_account_fragment;

import de.hdodenhof.circleimageview.CircleImageView;
import com.sadeemlight.venus_model.ModelAddAccount;
import com.sadeemlight.activity.MainActivity;
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
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by VENUS on 5/10/2017.
 */

public class ShowStudentLayout
{
    MainActivity m_parent;


    public View m_rootView;
    public View m_listRootView;
    public View m_bottomView;
    public CircleImageView profileBottomImage;
    public CircleImageView profileBottomImage1;
    public TextView profileBottomName;

    ListView mListView;
    SharedPreferences settings;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
    AddAccountAdapter adapter;
    List<ModelAddAccount> list_adapter = new ArrayList<>();

    public ShowStudentLayout(MainActivity parent)
    {
        m_parent = parent;

        m_rootView = m_parent.findViewById(R.id.layout_showchild_root);
        m_listRootView = m_parent.findViewById(R.id.layout_showchild_list);
        m_bottomView = m_parent.findViewById(R.id.layout_bottom_view);
        profileBottomImage = (CircleImageView) m_parent.findViewById(R.id.img_bottom_profile);
        profileBottomImage1 = (CircleImageView) m_parent.findViewById(R.id.img_bottom_profile1);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_login_icon)
                .showImageForEmptyUri(R.drawable.ic_login_icon)
                .showImageOnFail(R.drawable.ic_login_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        profileBottomImage1.setBorderColor(m_parent.getResources().getColor(R.color.color_red));
        profileBottomImage.setBorderColor(m_parent.getResources().getColor(R.color.color_red));

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK),profileBottomImage, options);
        imageLoader.displayImage(m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK),profileBottomImage1, options);

        m_listRootView.setVisibility(View.INVISIBLE);

        m_parent.findViewById(R.id.img_bottom_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStudentList();
            }
        });
        m_parent.findViewById(R.id.button_addstudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = null;
                Bundle args;

                fragment = new Add_account_fragment();
                args = new Bundle();
                fragment.setArguments(args);

                if (fragment != null)
                {
                    FragmentManager fragmentManager = m_parent.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                    hideStudentList();
                }
            }
        });


        settings = m_parent.getSharedPreferences("MAIN_PREF", 0);
        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "addaccount", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mListView = (ListView) m_parent.findViewById(R.id.list_student);

        getparent_id = m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_PARENT_ID);
        get_student_id = m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

        updateList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String getstudent_id, student_name, login_as, getclass_id, birth_date, gender, username,
                        imagelink, classname, school_id, schoolname, division,
                        division_id, city_id, city_name, points, school_image, access_token;

                m_parent.sessionManagement.clearOffline();

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

                m_parent.sessionManagement.setAlluserDetail(student_name, "", getstudent_id, getclass_id, birth_date
                        , gender, imagelink, schoolname, school_id, classname, division,
                        division_id, city_id, city_name, points,
                        school_image, access_token);

                m_parent.setfinish();

                Intent refresh = new Intent(m_parent, MainActivity.class);
                m_parent.startActivity(refresh);
            }
        });
    }

    public void showStudentList()
    {
        if(isVisible() == true)
        {
            hideStudentList();
            return;
        }

        updateList();
        m_listRootView.setVisibility(View.VISIBLE);
    }

    public boolean isVisible()
    {
        if(m_listRootView.getVisibility() == View.VISIBLE)
        {
            return true;
        }

        return false;
    }

    public void hideStudentList()
    {
        m_listRootView.setVisibility(View.INVISIBLE);
    }

    public void updateList()
    {
        if (ConnectivityReceiver.isConnected())
        {
            new getAccountlist().execute();
        }
        else
        {
            addData();
        }
    }


    String response;
    String message;
    String getparent_id, get_student_id;
    public class getAccountlist extends AsyncTask<Void, Void, Void> {


        Progress_dialog pd = new Progress_dialog(m_parent);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            ServiceHandler sh = new ServiceHandler();

            String access_token = m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String url = ConstValue.ACCOUNT_LIST_URL + "?parent_id=" + getparent_id;
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(Defines.APP_LOG_TITLE, url);
            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.e(Defines.APP_LOG_TITLE, jsonSTR);

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
                GlobalFunction.outputToast(m_parent, message);
                addData();
            }
        }
    }

    public void addData()
    {

        for (int i = 0; i < listarray.size(); i++) {

            list_adapter.add(new ModelAddAccount(listarray.get(i).get("student_name"),
                    listarray.get(i).get("school_name"),
                    listarray.get(i).get("id"),
                    listarray.get(i).get("student_image"),
                    listarray.get(i).get("access_token")));
        }

        adapter = new AddAccountAdapter(m_parent, list_adapter);
        adapter.nameColor = R.color.color_white;
        adapter.setAllowDelete(false);

        mListView.setAdapter(adapter);
    }

    public void showBottomProfileIcon(boolean bShow)
    {
        if(bShow == true)
        {
            profileBottomImage.setVisibility(View.VISIBLE);
        }
        else
        {
            profileBottomImage.setVisibility(View.INVISIBLE);
        }

    }
}
