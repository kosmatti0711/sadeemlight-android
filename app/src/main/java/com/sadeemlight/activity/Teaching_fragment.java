package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sadeemlight.adapter.Teaching_grid_adapter;
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
public class Teaching_fragment extends Fragment implements AdapterView.OnItemClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    private Teaching_grid_adapter adapter;
    private List<Teaching_grid_model> list_adapter = new ArrayList<>();

    GridView gv_subject;

    int colors[] = {R.color.grid_pinkdark, R.color.grid_pink, R.color.grid_lime,
            R.color.grid_4, R.color.grid_5, R.color.grid_6};

    Session_management sessionManagement;

    public Teaching_fragment() {
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
        View view = inflater.inflate(R.layout.teaching_activity, container, false);

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

        ((MainActivity) getActivity()).setTitle(R.string.tab_teaching);
        ((MainActivity) getActivity()).setTabindicator("teaching");

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "teaching", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gv_subject = (GridView) view.findViewById(R.id.gv_teaching);
        gv_subject.setOnItemClickListener(this);
        gv_subject.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem = 0;
            private int scrollstate = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                scrollstate = scrollstate;
                Log.d(Defines.APP_LOG_TITLE, "state=" +scrollstate );
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    Log.d(Defines.APP_LOG_TITLE, "SCROLLING DOWN TRUE");
                    if(firstVisibleItem >= 6)
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

        if (ConnectivityReceiver.isConnected()) {
            new getTeachingsubject().execute();
        } else {
            addData();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Fragment fragment = null;
        Bundle args;

        fragment = new Teaching_level_fragment();
        args = new Bundle();
        args.putString("subject_id", listarray.get(i).get("subject_id"));
        args.putString("subjectName", listarray.get(i).get("subject_name"));

        fragment.setArguments(args);

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    public class getTeachingsubject extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String class_id = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASSID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.TEACHING_URL + class_id, ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.TEACHING_URL,ConstValue.GET,null,
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

                    jsonArray = dataObject.getJSONArray("Subjects");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("subject_id", c.getString("subject_id"));
                        map.put("subject_name", c.getString("subject_name"));

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

            if (response.contentEquals("true")) {

                try {
                    settings.edit().putString("sadeemlight" + "teaching", ObjectSerializer.serialize(listarray)).commit();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                addData();
            }else {
                addData();
            }
        }
    }

    public void addData() {
        list_adapter.clear();

        for (int i = 0; i < listarray.size(); i++) {

            Random random = new Random();
            int r_id = random.nextInt(5 - 0) + 0;
            int randome_color = colors[r_id];

            list_adapter.add(new Teaching_grid_model(listarray.get(i).get("subject_name"), randome_color));

        }

        adapter = new Teaching_grid_adapter(getActivity(), list_adapter);

        gv_subject.setAdapter(adapter);
    }
}
