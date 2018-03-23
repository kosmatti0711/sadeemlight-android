package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 12/8/2016.
 */
public class Attendance_fragment extends Fragment implements View.OnClickListener {

    /*// arraylist list variable for store data;
    ArrayList<HashMap<String, String>> m_offlineData = new ArrayList<>();*/

    boolean is_today_attend = false;

    int total_attends, total_absent, total_off;

    Progress_dialog pd;

    TextView view_archive,
            student_name,
            class_div,
            attend_status,
            attend_date_time,
            tv_total_Attend,
            tv_total_Absent,
            tv_total_off;
    ImageView iv_attend_student_icon, attend_status_icon;

    Session_management sessionManagement;

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray;

    // store m_offlineData
    public SharedPreferences settings;

    private DisplayImageOptions options;

    public Attendance_fragment() {
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
        View view = inflater.inflate(R.layout.attendance_activity, container, false);

        sessionManagement = new Session_management(getActivity());
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "attendance", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));

            total_attends = settings.getInt("sadeemlight" + "attendance-Attend", 0);
            total_absent = settings.getInt("sadeemlight" + "attendance-Absent", 0);
            total_off = settings.getInt("sadeemlight" + "attendance-Legally", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pd = new Progress_dialog(getActivity());

        ((MainActivity)getActivity()).setTitle(R.string.side_menu_attend);
        ((MainActivity)getActivity()).setTabindicatorfm();

        student_name = (TextView) view.findViewById(R.id.tv_attend_student_name);
        class_div = (TextView) view.findViewById(R.id.tv_attend_class_div);
        attend_status = (TextView) view.findViewById(R.id.tv_attend_status);
        view_archive = (TextView) view.findViewById(R.id.tv_attend_view_archive);
        attend_date_time = (TextView) view.findViewById(R.id.tv_attend_date_time);
        tv_total_Attend = (TextView) view.findViewById(R.id.tv_attend_total_attend);
        tv_total_Absent = (TextView) view.findViewById(R.id.tv_attend_total_absent);
        tv_total_off = (TextView) view.findViewById(R.id.tv_attend_total_off);
        iv_attend_student_icon = (ImageView) view.findViewById(R.id.iv_attend_student_icon);
        attend_status_icon = (ImageView) view.findViewById(R.id.iv_attend_status_icon);

        student_name.setText(sessionManagement.getUserDetails().get(ConstValue.KEY_NAME));

        String getclassname = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME);
        String getdivision = sessionManagement.getUserDetails().get(ConstValue.KEY_DIVISION_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append(getActivity().getString(R.string.attend_class)+": " + getclassname + "\n");
        sb.append(getActivity().getString(R.string.attend_div)+": "+ getdivision + "\n");

        class_div.setText(sb.toString());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_login_icon)
                .showImageForEmptyUri(R.drawable.ic_login_icon)
                .showImageOnFail(R.drawable.ic_login_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK), iv_attend_student_icon, options);

        view_archive.setOnClickListener(this);

        addData();
        if (ConnectivityReceiver.isConnected())
        {
            new attendToday().execute();
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        Fragment fm = new Archive_fragment();
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
    }

    public class attendToday extends AsyncTask<Void, Void, Void>
    {
        JSONArray jsonArray = null;
        String response = "";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            response = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.ATTEND_TODAY_URL,ConstValue.GET,null,access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    jsonArray = dataObject.getJSONArray("Attendance");
                    response = jsonObject.getString("Status");
                    JSONObject statistics = dataObject.getJSONObject("Statistics");

                    listarray.clear();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        is_today_attend = true;

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.optString("ID"));
                        map.put("msg_time", c.optString("Date"));
                        map.put("attend_status", c.optString("attendance_status"));

                        listarray.add(map);
                    }

                    total_off = Integer.valueOf(statistics.getString("Legally"));
                    total_absent = Integer.valueOf(statistics.getString("Absent"));
                    total_attends = Integer.valueOf(statistics.getString("Attend"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            try
            {
                pd.dismissProgress();

                Log.e("validtoday: ", response);

                if (response.contentEquals("true"))
                {
                    Log.e("validtoday: ", "true");

                    try
                    {
                        settings.edit().putString("sadeemlight" + "attendance", ObjectSerializer.serialize(listarray)).commit();
                        settings.edit().putInt("sadeemlight" + "attendance-Attend", total_attends).commit();
                        settings.edit().putInt("sadeemlight" + "attendance-Absent", total_absent).commit();
                        settings.edit().putInt("sadeemlight" + "attendance-Legally", total_off).commit();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    String nofoundmsg = getActivity().getString(R.string.attendance_nofoundrecord);
                    if (is_today_attend)
                    {
                        Log.e("validtoday: ", "true1");

                        for (int i = 0; i < listarray.size(); i++)
                        {
                            if(!listarray.get(i).get("attend_status").contentEquals(""))
                            {
                                attend_date_time.setText(listarray.get(i).get("attend_status"));

                                if (listarray.get(i).get("attend_status").contentEquals("Attend"))
                                {
                                    attend_status.setText(R.string.attend_attend);
                                    attend_status_icon.setImageResource(R.drawable.ic_attend_39);
                                    Log.e("status: ", "attend");
                                }
                                else if (listarray.get(i).get("attend_status").contentEquals("Absent"))
                                {
                                    attend_status.setText(R.string.attend_absent);
                                    attend_status_icon.setImageResource(R.drawable.ic_absent_32);
                                    Log.e("status: ", "absent");
                                }
                                else if (listarray.get(i).get("attend_status").contentEquals("Legally"))
                                {
                                    attend_status.setText(R.string.attend_off);
                                    attend_status_icon.setImageResource(R.drawable.ic_off_46);
                                    Log.e("status: ", "off");
                                }
                            }
                            else
                            {
                                attend_date_time.setText(nofoundmsg);
                                attend_status.setText("");
                            }
                        }
                    }
                    else
                    {
                        Log.e("validtoday: ", "false");
                        attend_date_time.setText(nofoundmsg);
                        attend_status.setText("");
                    }

                    tv_total_Attend.setText("" + total_attends);
                    tv_total_Absent.setText("" + total_absent);
                    tv_total_off.setText("" + total_off);
                }
            }catch (Exception e){}
        }
    }

    public void addData()
    {
        try
        {
            for (int i = 0; i < listarray.size(); i++)
            {
                if (!listarray.get(i).get("attend_status").contentEquals(""))
                {
                    attend_date_time.setText(listarray.get(i).get("attend_status"));

                    if (listarray.get(i).get("attend_status").contentEquals("Attend"))
                    {
                        attend_status.setText(R.string.attend_attend);
                        attend_status_icon.setImageResource(R.drawable.ic_attend_39);
                        Log.e("status: ", "attend");
                    }
                    else if (listarray.get(i).get("attend_status").contentEquals("Absent"))
                    {
                        attend_status.setText(R.string.attend_absent);
                        attend_status_icon.setImageResource(R.drawable.ic_absent_32);
                        Log.e("status: ", "absent");
                    }
                    else if (listarray.get(i).get("attend_status").contentEquals("Legally"))
                    {
                        attend_status.setText(R.string.attend_off);
                        attend_status_icon.setImageResource(R.drawable.ic_off_46);
                        Log.e("status: ", "off");
                    }
                }
                else
                {
                    attend_date_time.setText("No Offline Record.");
                    attend_status.setText("");
                }
            }

            tv_total_Attend.setText("" + total_attends);
            tv_total_Absent.setText("" + total_absent);
            tv_total_off.setText("" + total_off);
        }
        catch (Exception e){}
    }
}
