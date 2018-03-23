package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sadeemlight.adapter.Teaching_level_grid_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 6/9/2016.
 */
public class Teaching_level_fragment extends Fragment implements AdapterView.OnItemClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    private Teaching_level_grid_adapter adapter;
    private List<Teaching_level_grid_model> list_adapter = new ArrayList<>();

    TextView title;
    GridView gv_level;

    int colors[] = {R.color.grid_pinkdark, R.color.grid_pink, R.color.grid_lime,
            R.color.grid_4, R.color.grid_5, R.color.grid_6};

    String getsubject, getsubject_id;

    Session_management session_management;

    public Teaching_level_fragment() {
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
        View view = inflater.inflate(R.layout.teaching_level_activity, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.tab_teaching);

        session_management = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "teaching_level", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        title = (TextView) view.findViewById(R.id.tv_modes_title);
        gv_level = (GridView) view.findViewById(R.id.gv_modes);

        try {

            getsubject_id = getArguments().getString("subject_id");
            getsubject = getArguments().getString("subjectName");

            title.setText(getsubject);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        gv_level.setOnItemClickListener(this);

        if (ConnectivityReceiver.isConnected())
        {
            new getTeachinglevel().execute();
        } else {
            addData();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Bundle args;
        Fragment fm = new Teaching_list_fragment();
        args = new Bundle();

        args.putString("subjectName", getsubject);
        args.putString("level_name", listarray.get(i).get("level_name"));
        args.putString("level_id", listarray.get(i).get("level_id"));

        fm.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
    }

    public class getTeachinglevel extends AsyncTask<Void, Void, Void>
    {
        Progress_dialog pd = new Progress_dialog(getActivity());

        String response;

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

            String class_id = "&class_id=" + session_management.getUserDetails().get(ConstValue.KEY_CLASSID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.LESSON_URL + getsubject_id + class_id, ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.LESSON_URL + getsubject_id,ConstValue.GET,null,
                        session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {

                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);
                JSONArray jsonArray = null;
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    jsonArray = dataObject.getJSONArray("Subjects");
                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("level_id", c.getString("lesson_category_id"));
                        map.put("level_name", c.getString("category_title"));
                        listarray.add(map);
                    }
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
            pd.dismissProgress();

            if (response.contentEquals("true"))
            {
                try
                {
                    settings.edit().putString("sadeemlight" + "teaching_level", ObjectSerializer.serialize(listarray)).commit();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                addData();
            }
            else
            {
                addData();
            }
        }
    }

    public void addData()
    {
        list_adapter.clear();
        for (int i = 0; i < listarray.size(); i++)
        {
            Random random = new Random();
            int r_id = random.nextInt(5 - 0) + 0;
            int randome_color = colors[r_id];

            list_adapter.add(new Teaching_level_grid_model(listarray.get(i).get("level_name"), randome_color));
        }

        adapter = new Teaching_level_grid_adapter(getActivity(), list_adapter);
        gv_level.setAdapter(adapter);
    }
}
