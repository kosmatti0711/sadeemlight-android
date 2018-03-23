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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.Voice_lesson_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Voice_lesson_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    private List<ModelParamsPair> params = new ArrayList<>();

    String response;

    ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Voice_lesson_list_adapter adapter;
    private List<Voice_lesson_list_model> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    public Voice_lesson_fragment() {
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
        View view = inflater.inflate(R.layout.voice_lesson_activity, container, false);
        setHasOptionsMenu(true);

        sessionManagement = new Session_management(getActivity());

        ((MainActivity)getActivity()).setTitle(R.string.side_menu_voice);
        ((MainActivity)getActivity()).setTabindicatorfm();

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "voice", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv = (ListView) view.findViewById(R.id.lv_voice_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (ConnectivityReceiver.isConnected()) {
                    /*if (m_offlineData.size() > 0) {
                        refreshList();
                    } else {*/
                        new getVoicelesson().execute();
                    //}
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

            new getVoicelesson().execute();
        } else {
            addData();
        }
    }

    public class getVoicelesson extends AsyncTask<Void, Void, Void> {

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

            String class_id = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASSID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.VOICE_LESSON_URL + class_id, ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.VOICE_LESSON_URL + class_id,ConstValue.GET,null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("VoiceLecture");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("voice_lecture_id"));
                        map.put("lesson_name", c.getString("subject_name"));
                        map.put("lesson_link", c.getString("lecture_link"));

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

                try {
                    settings.edit().putString("sadeemlight" + "voice", ObjectSerializer.serialize(listarray)).commit();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                addData();
            }else{
                addData();
            }
        }
    }

    public void addData() {
        list_adapter.clear();

        for (int i = 0; i < listarray.size(); i++) {

            list_adapter.add(new Voice_lesson_list_model(listarray.get(i).get("lesson_name"),
                    listarray.get(i).get("lesson_link")));

        }

        adapter = new Voice_lesson_list_adapter(getActivity(), list_adapter);

        lv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);

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
        adapter.stopPlay();
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

            if(type.contentEquals("voice_lesson")){
                if (ConnectivityReceiver.isConnected()) {
                    new getVoicelesson().execute();
                } else {
                    addData();
                }
            }
        }
    };
}
