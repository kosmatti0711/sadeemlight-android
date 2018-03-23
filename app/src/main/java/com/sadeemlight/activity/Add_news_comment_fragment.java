package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import com.sadeemlight.adapter.Add_news_comment_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Add_news_comment_fragment extends Fragment implements View.OnClickListener {

    // store m_offlineData
    public SharedPreferences settings;

    String news_id,get_newstitle,get_newsdetail,get_newstype;

    TextView add_comment,news_title,sort_detail;
    ListView lv;
    Button send;
    EditText et_comment;

    private Add_news_comment_adapter adapter;
    private List<Add_news_comment_list_model> list_adapter = new ArrayList<>();

    Session_management sessionManagement;

    public Add_news_comment_fragment() {
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
        View view = inflater.inflate(R.layout.add_news_comment, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    getFragmentManager().popBackStack();
                    ((MainActivity)getActivity()).mShowStudentLayout.hideStudentList();

                    return true;
                }
                return false;
            }
        });

        sessionManagement = new Session_management(getActivity());

        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);

        news_title = (TextView) view.findViewById(R.id.tv_comment_title);
        //add_comment = (TextView) view.findViewById(R.id.tv_comment_addcomment);
        lv = (ListView) view.findViewById(R.id.lv_comment_list);
        sort_detail = (TextView) view.findViewById(R.id.tv_comment_sort_detail);
        send = (Button) view.findViewById(R.id.btn_comment_send);
        et_comment = (EditText) view.findViewById(R.id.et_comment_text);

        adapter = new Add_news_comment_adapter(getActivity(), list_adapter);
        lv.setAdapter(adapter);

        //add_comment.setOnClickListener(this);
        send.setOnClickListener(this);

        String LoginAs = sessionManagement.getUserDetails().get(ConstValue.KEY_LOGINAS);

        if(LoginAs.contentEquals("Student")){
            send.setVisibility(View.GONE);
            et_comment.setVisibility(View.GONE);
        }else {
            send.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
        }

        try
        {
            news_id = getArguments().getString("news_id");
            get_newstitle = getArguments().getString("news_title");
            get_newsdetail = getArguments().getString("news_detail");
            get_newstype = getArguments().getString("news_type");
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        String sorted_detail = get_newsdetail;

        if(get_newsdetail.length() > 100) {
            sorted_detail = get_newsdetail.substring(0, 100) + "...";
        }

        news_title.setText(get_newstitle);
        sort_detail.setText(sorted_detail);

        if(ConnectivityReceiver.isConnected())
        {
            new getCommentlist().execute();
        }

        ((MainActivity)getActivity()).mShowStudentLayout.m_bottomView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onClick(View view) {

        String getComment = et_comment.getText().toString().trim();

        if(!getComment.contentEquals(""))
        {
            if (ConnectivityReceiver.isConnected())
            {
                new insert_comment().execute(getComment);
            }
            else
            {
                Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class insert_comment extends AsyncTask<String, Void, Void> {

        String response ="";
        String message ="";
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();
            send.setClickable(false);
            send.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... listParam) {

            String getComment = listParam[0];
            response = "";
            List<ModelParamsPair> params = new ArrayList<>();
            params.add(new ModelParamsPair("comment", getComment));

            response = "";
            ServiceHandler sh = new ServiceHandler();
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);

            String url;
            if(get_newstype.contentEquals("school_news"))
            {
                url = ConstValue.BASE_URL_NEW + "school/news/" + news_id + "/comment";
            }
            else
            {
                url = ConstValue.BASE_URL_NEW + "news/" + news_id + "/comment";
            }
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    String totalComment = jsonObject.getJSONObject("Data").getString("total_comment");

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
            try
            {
                super.onPostExecute(aVoid);

                pd.dismissProgress();
                if (response.contentEquals("true"))
                {
                    et_comment.setText("");
                    new getCommentlist().execute();
                }

                send.setClickable(true);
                send.setEnabled(true);
            }
            catch (Exception ex){}
        }
    }

    public class getCommentlist extends AsyncTask<Void, Void, Void>{

        String response ="";
        String message ="";
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.showProgressbar();
            list_adapter.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            ServiceHandler sh = new ServiceHandler();
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);

            String url;
            if(get_newstype.contentEquals("school_news"))
            {
                url = ConstValue.BASE_URL_NEW + "school/news/" + news_id + "/comment";
            }
            else
            {
                url = ConstValue.BASE_URL_NEW + "news/" + news_id + "/comment";
            }
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Comments");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.optString("comment_id"));
                        map.put("commenttext", c.optString("comment_text"));
                        map.put("commentdate", c.optString("comment_date"));
                        map.put("studentname", c.optString("student_name"));
                        map.put("studentlink", c.optString("student_image"));

                        list_adapter.add(new Add_news_comment_list_model(
                                map.get("commenttext"),
                                "@"+map.get("studentname"),
                                map.get("commentdate"),
                                map.get("studentlink")));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            try
            {
                super.onPostExecute(aVoid);
                pd.dismissProgress();

                if (response.contentEquals("true"))
                {
                    adapter = new Add_news_comment_adapter(getActivity(), list_adapter);
                    adapter.notifyDataSetChanged();

                    lv.setAdapter(adapter);
                }
            }
            catch (Exception ex){}
        }
    }
}
