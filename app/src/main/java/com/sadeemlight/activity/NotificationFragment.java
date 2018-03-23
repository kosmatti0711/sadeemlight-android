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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

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

import com.sadeemlight.adapter.NotificationAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelNotification;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 31/8/2016.
 */
public class NotificationFragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;



    private NotificationAdapter adapter;
    private List<ModelNotification> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NotificationFragment() {
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
        View view = inflater.inflate(R.layout.notification_activity, container, false);
        setHasOptionsMenu(true);

        sessionManagement = new Session_management(getActivity());

        ((MainActivity)getActivity()).setTitle(R.string.side_menu_notification);
        ((MainActivity)getActivity()).setTabindicatorfm();

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "notification", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lv = (ListView) view.findViewById(R.id.lv_notific);
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
                        m_subjectList.clear();
                        refreshList();
                    } else {*/
                        new getNotification().execute();
                    //}
                } else {
                    addData();
                }
            }
        });

        lv.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {

            new getNotification().execute();
        } else {
            addData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle args;
        Fragment fm = new NotificationDetailFragment();
        args = new Bundle();

        args.putString("title",listarray.get(i).get("notific_title"));
        args.putString("msg_time",listarray.get(i).get("notific_date"));
        args.putString("detail",listarray.get(i).get("notific_detail"));
        args.putString("image",listarray.get(i).get("notific_image"));

        fm.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
    }

    public class getNotification extends AsyncTask<Void, Void, Void>{
        String response = "";
        String message = "";
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd.showProgressbar();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            ServiceHandler sh = new ServiceHandler();
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.NOTIFICATION_URL, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);



                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    String inputPattern = "yyyy-MM-dd HH:mm:ss";
                    String outputPattern = "dd-MMM h:mm";
                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    Date date = null;
                    String str = null;

                    jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Notifications");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("notification_id"));

                        date = inputFormat.parse(c.getString("notification_date").toString());
                        str = outputFormat.format(date);

                        map.put("notific_date", str);
                        map.put("notific_id", c.getString("notification_id"));
                        map.put("notific_title", c.getString("notification_title"));
                        map.put("notific_detail", c.getString("notification_detail"));
                        map.put("notific_status", c.getString("notification_status"));
                        map.put("notific_image", c.getString("notification_image"));

                        listarray.add(map);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e){
                    e.printStackTrace();
                    Log.e("datas: ", e.toString());
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pd.dismissProgress();
            swipeRefreshLayout.setRefreshing(false);

            if(response.contentEquals("true")) {

                Log.e("response","true");

                try {
                    settings.edit().putString("sadeemlight" + "notification", ObjectSerializer.serialize(listarray)).commit();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                addData();
            }else{
                GlobalFunction.outputToast(getContext(), message);
                addData();
            }
        }
    }

    public void addData() {
        list_adapter.clear();

        for (int i = 0; i < listarray.size(); i++) {

            String shortdetail = listarray.get(i).get("notific_detail");

            if(shortdetail.length() > 20)
                shortdetail = shortdetail.substring(0, 20)+"...";

            list_adapter.add(new ModelNotification(listarray.get(i).get("notific_title"),
                    listarray.get(i).get("notific_date"),
                    shortdetail));

        }

        adapter = new NotificationAdapter(getActivity(),list_adapter);

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

            if(type.contentEquals("notification")){
                if (ConnectivityReceiver.isConnected()) {
                    new getNotification().execute();
                } else {
                    addData();
                }
            }
        }
    };
}

