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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v7.widget.SearchView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.LibraryAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelSubject;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class LibraryFragment extends Fragment implements AdapterView.OnItemClickListener{

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> m_offlineData = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    GridView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private LibraryAdapter adapter;
    private List<ModelSubject> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    public LibraryFragment() {
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
        View view = inflater.inflate(R.layout.library_activity, container, false);
        setHasOptionsMenu(true);

        sessionManagement = new Session_management(getActivity());

        initView(view);
        loadOfflineData();

        new getLibrary().execute();

        return view;
    }

    public void initView(View view)
    {
        ((MainActivity)getActivity()).setTitle(R.string.side_menu_library);
        ((MainActivity)getActivity()).setTabindicatorfm();

        mListView = (GridView) view.findViewById(R.id.list_library);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectivityReceiver.isConnected()) {

                    new getLibrary().execute();
                } else {
                    addData();
                }
            }
        });

        mListView.setOnItemClickListener(this);
    }

    public void loadOfflineData()
    {
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);

        try
        {
            m_offlineData = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "libaraylist", ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(m_offlineData == null)
        {
            m_offlineData = new ArrayList<HashMap<String, String>>();
        }

        addData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int rowIndex, long l)
    {
        Bundle args;
        Fragment fm = new LibraryDetailFragment();
        args = new Bundle();

        if(rowIndex == 0)
        {
            args.putString("subject_id", "public");
        }
        else
        {
            args.putString("subject_id", m_offlineData.get(rowIndex-1).get("id"));
        }


        fm.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
    }

    public class getLibrary extends AsyncTask<Void, Void, Void>{

        Progress_dialog pd = new Progress_dialog(getActivity());
        String response = "";
        String message = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String url = ConstValue.LIBRARY_SUBJECT_URL;;
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                list_adapter.clear();
                m_offlineData.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Subjects");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("subject_id"));
                        map.put("name", c.getString("subject_name"));

                        m_offlineData.add(map);

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

            if(response == "true"){

                try {
                    settings.edit().putString("sadeemlight" + "libaraylist", ObjectSerializer.serialize(m_offlineData)).commit();
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

        for (int i = 0; i < m_offlineData.size(); i++) {
            list_adapter.add(new ModelSubject(m_offlineData.get(i).get("id"), m_offlineData.get(i).get("name")));
        }

        adapter = new LibraryAdapter(getActivity(), list_adapter);

        mListView.setAdapter(adapter);
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

            if(type.contentEquals("library")){
                if (ConnectivityReceiver.isConnected()) {
                    new getLibrary().execute();
                } else {
                    addData();
                }
            }
        }
    };
}
