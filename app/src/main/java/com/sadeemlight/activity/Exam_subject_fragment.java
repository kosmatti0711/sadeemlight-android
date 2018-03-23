package com.sadeemlight.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Rajesh Dabhi on 8/9/2016.
 */
public class Exam_subject_fragment extends Fragment implements AdapterView.OnItemClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    private Teaching_grid_adapter adapter;
    private List<Teaching_grid_model> list_adapter = new ArrayList<>();

    GridView gv_exam_subject;

    int colors[] = {R.color.grid_pinkdark, R.color.grid_pink, R.color.grid_lime,
            R.color.grid_4, R.color.grid_5, R.color.grid_6};

    Session_management sessionManagement;

    public Exam_subject_fragment() {
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
        View view = inflater.inflate(R.layout.exam_subject_activity, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    Fragment fm = new NewsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fm).commit();

                    ((MainActivity)getActivity()).mShowStudentLayout.hideStudentList();
                    return true;
                }
                return false;
            }
        });

        ((MainActivity) getActivity()).setTitle(R.string.action_exam);

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "exam_subject", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gv_exam_subject = (GridView) view.findViewById(R.id.gv_exam_subject);
        gv_exam_subject.setOnItemClickListener(this);

        if (ConnectivityReceiver.isConnected()) {
            new getExamsubject().execute();
        } else {
            addData();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        int show_count = 0;
        try
        {
            show_count = Integer.parseInt(listarray.get(i).get("count"));

            if(show_count == 0)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.exam_dialog_result);
                alert.setMessage(getContext().getString(R.string.exam_dialog_emtpyresult));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                });

                alert.show();
                return;
            }
        }
        catch (Exception e){}

        Fragment fragment = null;
        Bundle args;

        fragment = new Exam_name_list_fragment();
        args = new Bundle();
        args.putString("subject_id", listarray.get(i).get("subject_id"));
        args.putString("subjectName", listarray.get(i).get("subject_name"));

        fragment.setArguments(args);

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    public class getExamsubject extends AsyncTask<Void, Void, Void>
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
            try
            {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.EXAM_SUBJECT_URL,ConstValue.GET,null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


            if (jsonSTR != null)
            {
                list_adapter.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);
                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("Exams");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("subject_id", c.getString("subject_id"));
                        map.put("subject_name", c.getString("subject_name"));
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

            if (response.contentEquals("true"))
            {
                try
                {
                    settings.edit().putString("sadeemlight" + "exam_subject", ObjectSerializer.serialize(listarray)).commit();
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
        list_adapter.clear();

        for (int i = 0; i < listarray.size(); i++)
        {

            Random random = new Random();
            int r_id = random.nextInt(5 - 0) + 0;
            int randome_color = colors[r_id];

            list_adapter.add(new Teaching_grid_model(listarray.get(i).get("subject_name"), randome_color));

        }

        adapter = new Teaching_grid_adapter(getActivity(), list_adapter);

        gv_exam_subject.setAdapter(adapter);
    }
}
