package com.sadeemlight.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.Teaching_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.Defines;

/**
 * Created by Rajesh Dabhi on 6/9/2016.
 */
public class Teaching_list_fragment extends Fragment implements AdapterView.OnItemClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    private Teaching_list_adapter adapter;
    private List<Teaching_list_model> list_adapter = new ArrayList<>();

    TextView tv_title;
    ListView lv_teaching;

    String getlevel_id;

    Session_management session_management;

    public Teaching_list_fragment() {
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
        View view = inflater.inflate(R.layout.teaching_list_activity, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.tab_teaching);

        session_management = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "teaching_sesson", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_title = (TextView) view.findViewById(R.id.tv_teaching_list_titles);
        lv_teaching = (ListView) view.findViewById(R.id.lv_teaching);
        lv_teaching.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        try
        {
            getlevel_id = getArguments().getString("level_id");

            if(getArguments().containsKey("subjectName"))
            {
                String getsubject = getArguments().getString("subjectName");
                String getlevel = getArguments().getString("level_name");
                tv_title.setText(getsubject + "( " + getlevel + " )");
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        lv_teaching.setOnItemClickListener(this);

        if (ConnectivityReceiver.isConnected())
        {
            new getTeachinglist().execute();
        }
        else
        {
            addData();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(listarray.get(i).get("video_link").contentEquals("youtube"))
        {
            Intent startplay = new Intent(getActivity(), News_video_youtube_activity.class);
            startplay.putExtra("video_link",listarray.get(i).get("video_link"));
            startActivity(startplay);
        }
        else
        {
            Bundle args;
            Fragment fm = new Teaching_lesson_detail_fragment();
            args = new Bundle();

            args.putString("lesson_name", listarray.get(i).get("lesson_name"));
            args.putString("lesson_text", listarray.get(i).get("lesson_text"));
            args.putString("video_link", listarray.get(i).get("video_link"));

            fm.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
    }

    public class getTeachinglist extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());
        public String title = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();
            ;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            response = "";

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.LESSON_LIST_URL + getlevel_id, ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.LESSON_LIST_URL + getlevel_id,ConstValue.GET,null,
                        session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("Lesson");
                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("lesson_id", c.getString("lesson_id"));
                        map.put("lesson_name", c.getString("lesson_title"));
                        map.put("lesson_text", c.getString("lesson_link"));
                        map.put("video_link", c.getString("video_link"));

                        listarray.add(map);
                    }

                    if(dataObject.has("Category"))
                    {
                        JSONObject category = dataObject.getJSONObject("Category");
                        String getsubject = category.getString("subject_name");
                        String getlevel = category.getString("category_title");
                        title = getsubject + "( " + getlevel + " )";
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

            if (response.contentEquals("true"))
            {

                try {
                    settings.edit().putString("sadeemlight" + "teaching_sesson", ObjectSerializer.serialize(listarray)).commit();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if(title.contentEquals("") == false)
            {
                tv_title.setText(title);
            }

            addData();
        }
    }

    public void addData()
    {
        list_adapter.clear();
        for (int i = 0; i < listarray.size(); i++)
        {
            list_adapter.add(new Teaching_list_model(listarray.get(i).get("lesson_name")));
        }

        adapter = new Teaching_list_adapter(getActivity(), list_adapter);
        lv_teaching.setAdapter(adapter);
    }
}
