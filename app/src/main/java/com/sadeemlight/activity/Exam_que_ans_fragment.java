package com.sadeemlight.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Circle_textview;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 8/9/2016.
 */
public class Exam_que_ans_fragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    private List<ModelParamsPair> params = new ArrayList<>();


    TextView tv_qus, tv_qus_title, title_header/*, tv_prev, tv_next*/;
    RadioButton ans_1, ans_2, ans_3, ans_4;
    RadioGroup ans_group;
    Button btn_ans;

    int exam_id;

    int examListposition = 0;

    ArrayList<String> list_true_ans = new ArrayList<>();
    ArrayList<String> list_false_ans = new ArrayList<>();

    String ansvalue = "";
    int totalans_complite = 0;

    String getsubject, getexamname;

    Session_management sessionManagement;

    public Exam_que_ans_fragment() {
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
        View view = inflater.inflate(R.layout.exam_que_ans_activity, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Do your operation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        ((MainActivity) getActivity()).setTitle(R.string.action_exam);

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "exam_qus_ans", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_qus = (TextView) view.findViewById(R.id.tv_exam_que_ans_q);
        tv_qus_title = (TextView) view.findViewById(R.id.tv_exam_que_ans_title);
        title_header = (TextView) view.findViewById(R.id.tv_exam_que_ans_titleheader);
        btn_ans = (Button) view.findViewById(R.id.btn_exam_que_ans);
        /*tv_prev = (TextView) view.findViewById(R.id.tv_exam_que_ans_prev);
        tv_next = (TextView) view.findViewById(R.id.tv_exam_que_ans_next);*/
        ans_1 = (RadioButton) view.findViewById(R.id.rb_ans_1);
        ans_2 = (RadioButton) view.findViewById(R.id.rb_ans_2);
        ans_3 = (RadioButton) view.findViewById(R.id.rb_ans_3);
        ans_4 = (RadioButton) view.findViewById(R.id.rb_ans_4);
        ans_group = (RadioGroup) view.findViewById(R.id.rg_exam_ans);

        Circle_textview ct = new Circle_textview();

        tv_qus.setBackgroundDrawable(ct.drawCircle(getActivity(), 30, 30,
                getActivity().getResources().getColor(R.color.colorPrimary)));

        try
        {
            exam_id = getArguments().getInt("examId");

            if(getArguments().containsKey("subjectName"))
            {
                getsubject = getArguments().getString("subjectName");
                getexamname = getArguments().getString("examTitle");
                title_header.setText(getsubject + " / " + getexamname);
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        if (ConnectivityReceiver.isConnected())
        {
            new getExamqusanslist().execute();
            btn_ans.setOnClickListener(this);
        }

        /*tv_prev.setOnClickListener(this);
        tv_next.setOnClickListener(this);*/

        ans_group.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {

        if (!ansvalue.contentEquals(""))
        {
            ans_group.clearCheck();

            if (totalans_complite < listarray.size())
            {
                if (listarray.get(examListposition).get("currect_ans").contentEquals(ansvalue))
                {
                    list_true_ans.add("true");
                }
                else
                {
                    list_false_ans.add("false");
                }
                totalans_complite++;
                ansvalue = "";
            }

        /*if (view.getId() == R.id.tv_exam_que_ans_prev) {

            if (examListposition > 0) {
                examListposition--;

                refreshList();
            }
        } else if (view.getId() == R.id.tv_exam_que_ans_next) {

            if (examListposition < m_offlineData.size() - 1) {
                examListposition++;

                refreshList();
            } else {
                Toast.makeText(getActivity(), "Exam finish", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "true: " + list_true_ans.size() + ", false:" + list_false_ans.size(), Toast.LENGTH_SHORT).show();
            }
        }*/

            if (view.getId() == R.id.btn_exam_que_ans)
            {
                if (examListposition < listarray.size() - 1)
                {
                    examListposition++;
                    addData();
                }
                else
                {

                    String getstudentid = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

                    params.add(new ModelParamsPair("exam_id", String.valueOf(exam_id)));
                    params.add(new ModelParamsPair("student_id", getstudentid));
                    params.add(new ModelParamsPair("total_correct", "" + list_true_ans.size()));
                    params.add(new ModelParamsPair("total_wrong", "" + list_false_ans.size()));

                    new submitResult().execute();

                    Toast.makeText(getActivity(), "Exam finish", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "true: " + list_true_ans.size() + ", false:" + list_false_ans.size(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            Toast.makeText(getActivity(), "Please select answer", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
        int id = radioGroup.getCheckedRadioButtonId();

        if (id == R.id.rb_ans_1)
        {
            ansvalue = "a";
        }
        else if (id == R.id.rb_ans_2)
        {
            ansvalue = "b";
        }
        else if (id == R.id.rb_ans_3)
        {
            ansvalue = "c";
        }
        else if (id == R.id.rb_ans_4)
        {
            ansvalue = "d";
        }
    }

    public class getExamqusanslist extends AsyncTask<Void, Void, Void>
    {
        JSONArray jsonArray = null;
        String response = "";
        String title = "";
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
            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.EXAM_QUS_ANS_URL + exam_id, ConstValue.GET);
            String jsonSTR = null;
            try
            {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.EXAM_QUS_ANS_URL + exam_id,ConstValue.GET, null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                listarray.clear();
                Log.e("response: ", jsonSTR);
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    jsonArray = dataObject.getJSONArray("Question");
                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", c.getString("question_id"));
                        map.put("qus_name", c.getString("question"));
                        map.put("ans_a", c.getString("a"));
                        map.put("ans_b", c.getString("b"));
                        map.put("ans_c", c.getString("c"));
                        map.put("ans_d", c.getString("d"));
                        map.put("currect_ans", c.getString("correct_answer"));

                        listarray.add(map);
                    }

                    if(dataObject.has("Exam"))
                    {
                        JSONObject category = dataObject.getJSONObject("Exam");
                        String subjectname = category.getString("subject_name");
                        String examname = category.getString("exam_title");
                        title = subjectname + "/ " + examname;
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
                    settings.edit().putString("sadeemlight" + "exam_qus_ans", ObjectSerializer.serialize(listarray)).commit();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                addData();

                if(title.contentEquals("") == false)
                {
                    title_header.setText(title);
                }
            }
        }
    }

    public void addData()
    {
        if(listarray != null && examListposition < listarray.size())
        {
            tv_qus_title.setText(listarray.get(examListposition).get("qus_name"));
            ans_1.setText(listarray.get(examListposition).get("ans_a"));
            ans_2.setText(listarray.get(examListposition).get("ans_b"));
            ans_3.setText(listarray.get(examListposition).get("ans_c"));
            ans_4.setText(listarray.get(examListposition).get("ans_d"));
        }
        else if(listarray != null && listarray.size() == 0)
        {
            try
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
            }catch (Exception e){}

        }
    }

    public class submitResult extends AsyncTask<Void, Void, Void>
    {
        JSONArray jsonArray = null;
        String response = "";
        Progress_dialog pd = new Progress_dialog(getActivity());
        String resultresponse = "";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            ServiceHandler sh = new ServiceHandler();
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.EXAM_SUBMIT_RESULT_URL,ConstValue.POST,params,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("response: ", jsonSTR);

            if (jsonSTR != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    jsonArray = jsonObject.getJSONArray("Code");
                    resultresponse = jsonObject.getString("Status");
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

            StringBuilder sb = new StringBuilder();
            sb.append(getActivity().getString(R.string.exam_dialog_name) + getexamname + "\n");
            sb.append(getActivity().getString(R.string.exam_dialog_subject)+ getsubject + "\n");
            sb.append(getActivity().getString(R.string.exam_dialog_que) + listarray.size() + "\n");
            sb.append(getActivity().getString(R.string.exam_dialog_c_ans) + list_true_ans.size() + "\n");
            sb.append(getActivity().getString(R.string.exam_dialog_ic_ans) + list_false_ans.size() + "\n");

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.exam_dialog_result);
            alert.setMessage(sb.toString());
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Fragment fm = new NewsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fm).commit();
                }
            });

            alert.show();
        }
    }
}
