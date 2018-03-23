package com.sadeemlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.Homework_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.Defines;


/**
 * Created by Rajesh Dabhi on 18/8/2016.
 */
public class Homework_list_fragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    String isTodayurl = null;

    private Homework_list_adapter adapter;
    private List<Homework_list_model> list_adapter = new ArrayList<>();

    ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;

    Session_management sessionManagement;

    public Homework_list_fragment() {
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
        View view = inflater.inflate(R.layout.home_work_list_activity, container, false);
        setHasOptionsMenu(true);

        sessionManagement = new Session_management(getActivity());

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_homework);
        ((MainActivity) getActivity()).setTabindicatorfm();

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "homeworklist", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv = (ListView) view.findViewById(R.id.lv_homework);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        lv.setOnItemClickListener(this);

        try {
            isTodayurl = getArguments().getString("isToday");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (ConnectivityReceiver.isConnected()) {
                    new getHomework().execute();
                } else {
                    addData();
                }
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {

            new getHomework().execute();
        } else {
            addData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle args;
        Fragment fm = new Homework_detail_fragment();
        args = new Bundle();

        args.putString("matirialname", listarray.get(i).get("materialname"));
        args.putString("msg_time", listarray.get(i).get("homeworkdate"));
        args.putString("detail", listarray.get(i).get("detail"));

        fm.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
    }

    public class getHomework extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String division_id = sessionManagement.getUserDetails().get(ConstValue.KEY_DIVISION_ID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.HOMEWORK_URL + division_id + isTodayurl, ConstValue.GET);
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;

            if (isTodayurl != null){
                List<ModelParamsPair> params = new ArrayList<>();
                params.add(new ModelParamsPair("type","Today"));
                try {
                    jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.HOMEWORK_URL,ConstValue.GET,params,access_token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.HOMEWORK_URL,ConstValue.GET,null,access_token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("Homeworks");

                    response = jsonObject.getString("Status");

                    String inputPattern = "yyyy-MM-dd HH:mm:ss";
                    String outputPattern = "dd-MM-yyyy h:mm";
                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    Date date = null;
                    String str = null;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("homework_id"));

                        date = inputFormat.parse(c.getString("homework_date").toString());
                        str = outputFormat.format(date);

                        map.put("homeworkdate", str);
                        map.put("materialname", c.getString("subject_name"));
                        map.put("detail", c.getString("homework_description"));
                        map.put("status", c.getString("homework_status"));

                        listarray.add(map);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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

                try {
                    settings.edit().putString("sadeemlight" + "homeworklist", ObjectSerializer.serialize(listarray)).commit();
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
        list_adapter.clear();

        for (int i = 0; i < listarray.size(); i++) {

            String shortdetail = listarray.get(i).get("detail");

            if (shortdetail.length() > 20)
                shortdetail = shortdetail.substring(0, 20) + "...";

            list_adapter.add(new Homework_list_model(listarray.get(i).get("materialname"),
                    listarray.get(i).get("homeworkdate"),
                    "Class " + sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME),
                    shortdetail));

        }

        adapter = new Homework_list_adapter(getActivity(), list_adapter);

        lv.setAdapter(adapter);
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

            if (type.contentEquals("homework_archive")) {
                isTodayurl = "";

                if (ConnectivityReceiver.isConnected()) {
                    new getHomework().execute();
                } else {
                    addData();
                }
            } else if (type.contentEquals("homework_today")) {

                isTodayurl = "&homework_type=Today";

                if (ConnectivityReceiver.isConnected()) {
                    new getHomework().execute();
                } else {
                    addData();
                }
            }
        }
    };
}
