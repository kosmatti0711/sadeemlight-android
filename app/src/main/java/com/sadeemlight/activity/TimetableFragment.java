package com.sadeemlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.TimetableListAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.LocaleHelper;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelTimetable;
import com.sadeemlight.venus_uis.utils.Defines;

/**
 * Created by Rajesh Dabhi on 1/9/2016.
 */
public class TimetableFragment extends Fragment {

    // arraylist list variable for store data;
    public static String[] WEEKDAYS = {
            "sat", "sun", "mon", "tue", "wed", "thu", "fri"
    };

    HashMap<String, ArrayList<HashMap<String, String>>> m_offlineData = new HashMap<>();

    // store m_offlineData
    public SharedPreferences settings;

    private TimetableListAdapter m_adapter;

    ListView mListView;
    TabLayout mWeekTab;
    SwipeRefreshLayout swipeRefreshLayout;

    Session_management sessionManagement;

    String m_selWeekday;
    String getlang;

    public TimetableFragment() {
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
        View view = inflater.inflate(R.layout.timetable_fragment, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_timetable);
        ((MainActivity) getActivity()).setTabindicatorfm();

        sessionManagement = new Session_management(getActivity());

        initView(view);
        loadOfflineData();

        new getTimetablelist().execute();

        return view;
    }

    public void initView(View view)
    {
        mListView = (ListView) view.findViewById(R.id.lv_weekly);
        mWeekTab = (TabLayout) view.findViewById(R.id.tabs_day);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectivityReceiver.isConnected()) {
                    new getTimetablelist().execute();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        getlang = LocaleHelper.getLanguage(getActivity());
        if(getlang.contentEquals("en"))
        {
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_sat));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_sun));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_mon));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_tue));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_wed));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_thu));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_fri));
        }
        else if(getlang.contentEquals("ar"))
        {
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_fri));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_thu));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_wed));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_tue));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_mon));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_sun));
            mWeekTab.addTab(mWeekTab.newTab().setText(R.string.weekly_sat));
        }

        m_selWeekday = WEEKDAYS[0];
        if(getlang.contentEquals("en"))
        {
            mWeekTab.getTabAt(0).select();
        }
        else if(getlang.contentEquals("ar"))
        {
            mWeekTab.getTabAt(6).select();
        }

        mWeekTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mWeekTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));

                Log.e("Tabs:",tab.getText().toString());

                if (tab.getText() == getResources().getString(R.string.weekly_sat))
                {
                    m_selWeekday = "sat";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_sun)) {
                    m_selWeekday = "sun";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_mon)) {
                    m_selWeekday = "mon";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_tue)) {
                    m_selWeekday = "tue";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_wed)) {
                    m_selWeekday = "wed";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_thu)) {
                    m_selWeekday = "thu";
                }
                else if (tab.getText() == getResources().getString(R.string.weekly_fri)) {
                    m_selWeekday = "fri";
                }

                Log.e("day_name: ", m_selWeekday);

                updateList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

    }

    public void loadOfflineData()
    {
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);

        try
        {
            m_offlineData = (HashMap<String, ArrayList<HashMap<String, String>>>)
                    ObjectSerializer.deserialize(settings.getString("sadeemlight" + "timetable", ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(m_offlineData == null)
        {
            m_offlineData = new HashMap<>();
        }

        updateList();
    }

    public void saveOfflineData()
    {
        try
        {
            settings.edit().putString("sadeemlight" + "timetable", ObjectSerializer.serialize(m_offlineData)).commit();
            Log.d(Defines.APP_LOG_TITLE, m_offlineData.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public class getTimetablelist extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());
        String response = "";
        String message = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //pd.showProgressbar();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.TIMETABLEALL_URL;
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                m_offlineData.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    //response = jsonObject.getString("Status");
                    //message = jsonObject.getString("Message");

                    m_offlineData = new HashMap<>();

                    for (int week = 0; week < WEEKDAYS.length; week++)
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray(WEEKDAYS[week]);
                        ArrayList<HashMap<String, String>> dayTable = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject c = jsonArray.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("id", c.getString("subject_id"));
                            map.put("subject_name", c.getString("subject_name"));
                            map.put("teacher_name", c.getString("teacher_name"));
                            map.put("order", c.getString("order"));

                            dayTable.add(map);
                        }

                        m_offlineData.put(WEEKDAYS[week], dayTable);
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
            //pd.dismissProgress();
            swipeRefreshLayout.setRefreshing(false);

            saveOfflineData();
            updateList();
        }
    }

    public void updateList()
    {
        List<ModelTimetable> baseData = new ArrayList<>();

        ArrayList<HashMap<String, String>> dayTable = m_offlineData.get(m_selWeekday);

        if(dayTable != null)
        {
            for (int i = 0; i < dayTable.size(); i++)
            {
                baseData.add(new ModelTimetable(
                        dayTable.get(i).get("subject_id"),
                        dayTable.get(i).get("order"),
                        dayTable.get(i).get("teacher_name"),
                        dayTable.get(i).get("subject_name")));
            }
        }

        m_adapter = new TimetableListAdapter(getActivity(), baseData);
        mListView.setAdapter(m_adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("sadeem_notification"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("timetable")) {
                if (ConnectivityReceiver.isConnected()) {
                    new getTimetablelist().execute();
                } else {
                    updateList();
                }
            }
        }
    };
}
