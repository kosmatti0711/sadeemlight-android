package com.sadeemlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.Archive_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.Defines;

/**
 * Created by Rajesh Dabhi on 12/8/2016.
 */
public class Archive_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    private Archive_list_adapter adapter;
    private List<Archive_list_model> list_adapter = new ArrayList<>();

    ListView lv_archive;
    private SwipeRefreshLayout swipeRefreshLayout;

    Session_management sessionManagement;

    public Archive_fragment() {
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
        View view = inflater.inflate(R.layout.attendance_absent_archive_list, container, false);
        setHasOptionsMenu(true);

        sessionManagement = new Session_management(getActivity());

        ((MainActivity)getActivity()).setTabindicatorfm();
        ((MainActivity)getActivity()).setTitle(R.string.side_menu_attend);

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "archivelist", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv_archive = (ListView) view.findViewById(R.id.lv_archive);
        lv_archive.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    Log.d(Defines.APP_LOG_TITLE, "SCROLLING DOWN TRUE");
                    ((MainActivity)getActivity()).showTablayout(false);
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    Log.d(Defines.APP_LOG_TITLE, "SCROLLING UP TRUE");
                    if(firstVisibleItem == 0)
                    {
                        ((MainActivity)getActivity()).showTablayout(true);
                    }
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (ConnectivityReceiver.isConnected())
                {
                    /*if (m_offlineData.size() > 0) {
                        refreshList();
                    } else {*/
                        new attendList().execute();
                   // }
                }
                else
                {
                    addData();
                }
            }
        });

        return view;
    }

    @Override
    public void onRefresh()
    {
        Intent updates = new Intent("sadeem_notification");
        updates.putExtra("type", "show_tab");
        getActivity().sendBroadcast(updates);

        if (ConnectivityReceiver.isConnected())
        {
            new attendList().execute();
        }
        else
        {
            addData();
        }
    }

    class attendList extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.ATTEND_LIST_URL + sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID), ConstValue.GET);
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.ATTEND_LIST_URL,ConstValue.GET,null,access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {
                listarray.clear();
                list_adapter.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    JSONObject ibject = jsonObject.getJSONObject("Data");

                    jsonArray = ibject.getJSONArray("Attendance");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("ID"));
                        map.put("msg_time", c.getString("Date"));
                        map.put("attend_status", c.getString("attendance_status"));

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
            //pd.dismissProgress();
            swipeRefreshLayout.setRefreshing(false);

            if (response == "true") {
                Log.e("status: ", "yes");

                try {
                    settings.edit().putString("sadeemlight" + "archivelist", ObjectSerializer.serialize(listarray)).commit();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                addData();
            } else {
                addData();
            }

        }
    }

    public void addData() {
        try
        {
            list_adapter.clear();

            for (int i = 0; i < listarray.size(); i++) {
                Log.e("status: ", "hi");
                if (listarray.get(i).get("attend_status").contentEquals("Attend")) {
                    list_adapter.add(new Archive_list_model("" + listarray.get(i).get("msg_time"), "(" + getResources().getString(R.string.attend_attend) + ")", R.drawable.ic_attend_39));
                    Log.e("status: ", "attend");
                } else if (listarray.get(i).get("attend_status").contentEquals("Absent")) {
                    list_adapter.add(new Archive_list_model("" + listarray.get(i).get("msg_time"), "(" + getResources().getString(R.string.attend_absent) + ")", R.drawable.ic_absent_32));
                    Log.e("status: ", "absent");
                } else if (listarray.get(i).get("attend_status").contentEquals("Legally")) {
                    list_adapter.add(new Archive_list_model("" + listarray.get(i).get("msg_time"), "(" + getResources().getString(R.string.attend_off) + ")", R.drawable.ic_off_46));
                    Log.e("status: ", "off");
                }
            }

            adapter = new Archive_list_adapter(getActivity(), list_adapter);
            lv_archive.setAdapter(adapter);
        }
        catch (Exception e){}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        ((MainActivity)getActivity()).onCreateMenu_venus(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return false;
            }
        });

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

            if (type.contentEquals("attendance_archive")) {
                if (ConnectivityReceiver.isConnected()) {
                    new attendList().execute();
                } else {
                    addData();
                }
            }
        }
    };
}
