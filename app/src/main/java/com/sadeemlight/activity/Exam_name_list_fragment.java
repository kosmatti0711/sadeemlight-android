package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sadeemlight.Models.ExamNameListModel;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.ExamAdapter;
import com.sadeemlight.adapter.LibraryAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelSubject;

/**
 * Created by Rajesh Dabhi on 8/9/2016.
 */
public class Exam_name_list_fragment extends Fragment {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    RecyclerView rvExamNameList;
    TextView tv_title;
    ExamAdapter mAdapter;
    List<ExamNameListModel> listAdapter = new ArrayList<>();
    Session_management sessionManagement;
    String getSubject_id,getexam_subject;

    public Exam_name_list_fragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.exam_name_list, container, false);

        sessionManagement = new Session_management(getActivity());
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();
        try {
            //TODO get page settings from shared preferences
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "exam_name", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((MainActivity) getActivity()).setTitle(R.string.action_exam);
        //TODO bind the Recycler view
        rvExamNameList = (RecyclerView)view.findViewById(R.id.rvExamNameList);
        tv_title = (TextView) view.findViewById(R.id.tv_exam_name_title);


        try
        {
            getexam_subject = getArguments().getString("subjectName");
            getSubject_id = getArguments().getString("subject_id");
            tv_title.setText(getexam_subject);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        if(ConnectivityReceiver.isConnected())
        {
            new getExamname().execute();
        }
        else
        {
            addData();
        }

        return view;
    }

    public class getExamname extends AsyncTask<Void, Void, Void>
    {
        JSONArray jsonArray = null;
        String response = "";
        Progress_dialog pd = new Progress_dialog(getActivity());

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
            String class_id = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASSID);
            ServiceHandler sh = new ServiceHandler();
            String jsonSTR = null;
            String url  = ConstValue.EXAM_SUBJECT_URL +  getSubject_id;
            try
            {
                jsonSTR = sh.makeServiceCallWithTokeOk(url ,ConstValue.GET,null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                listAdapter.clear();
                listarray.clear();
                Log.e("response: ", jsonSTR);
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    jsonArray = dataObject.getJSONArray("Exams");
                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", c.getString("exam_id"));
                        map.put("exam_title", c.getString("exam_title"));
                        map.put("count", c.getString("count"));

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

            if(response == "true")
            {
                try
                {
                    settings.edit().putString("sadeemlight" + "exam_name", ObjectSerializer.serialize(listarray)).commit();
                }
                catch (IOException e)
                {
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
        listAdapter.clear();
        for (int i = 0; i < listarray.size(); i++)
        {
            int show_count = 0;
            try {
                show_count = Integer.parseInt(listarray.get(i).get("count"));
            }
            catch (Exception e){}
            listAdapter.add(new ExamNameListModel(Integer.parseInt(listarray.get(i).get("id"))
                    ,getexam_subject
                    ,listarray.get(i).get("exam_title"),
                    show_count));
        }
        mAdapter = new ExamAdapter(getActivity(),listAdapter);
        rvExamNameList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExamNameList.setAdapter(mAdapter);
    }
}
