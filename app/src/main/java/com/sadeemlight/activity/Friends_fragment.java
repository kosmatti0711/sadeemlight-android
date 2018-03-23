package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.Friends_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.Defines;

/**
 * Created by Rajesh Dabhi on 2/8/2016.
 */
public class Friends_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Friends_list_adapter adapter;
    private List<Friends_list_model> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    public Friends_fragment() {
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
        View view = inflater.inflate(R.layout.friends_activity, container, false);
        setHasOptionsMenu(true);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    Fragment fm = new NewsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fm).commit();

                    ((MainActivity)getActivity()).mShowStudentLayout.hideStudentList();
                    return true;
                }
                return false;
            }
        });

        ((MainActivity)getActivity()).setTitle(R.string.tab_friend);
        ((MainActivity)getActivity()).setTabindicator("friends");

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "Friendlist", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv = (ListView) view.findViewById(R.id.lv_friend);
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

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (ConnectivityReceiver.isConnected()) {
                    /*if (m_offlineData.size() > 0) {
                        refreshList();
                    } else {*/
                        new getFriends().execute();
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

            new getFriends().execute();
        } else {
            addData();
        }
    }

    public class getFriends extends AsyncTask<Void, Void, Void>{

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

            String student_id = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.FRIENDS_URL + student_id+"&student_type=Friend", ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.FRIENDS_URL,ConstValue.GET,null,sessionManagement.getUserDetails()
                .get(ConstValue.KEY_ACCESSTOKEN));
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

                    jsonArray = dataObject.getJSONArray("Friend");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("student_id"));
                        map.put("name", c.getString("student_name"));
                        map.put("imglink", c.getString("student_image"));
                        map.put("points", c.getString("points"));

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

            if(response == "true"){

                try {
                    settings.edit().putString("sadeemlight" + "Friendlist", ObjectSerializer.serialize(listarray)).commit();
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

            list_adapter.add(new Friends_list_model(listarray.get(i).get("name"),
                        getActivity().getString(R.string.friend_point)+listarray.get(i).get("points"),
                    listarray.get(i).get("imglink")));

        }

        adapter = new Friends_list_adapter(getActivity(), list_adapter);

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
}
