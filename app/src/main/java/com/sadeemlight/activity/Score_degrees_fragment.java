package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 19/8/2016.
 */
public class Score_degrees_fragment extends Fragment implements View.OnClickListener {

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    ImageView student_icon;
    TextView student_name, classname, fail, pass, daily_degree, monthly_degree;

    Session_management sessionManagement;

    public Score_degrees_fragment() {
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
        View view = inflater.inflate(R.layout.degrees_activity, container, false);

        sessionManagement = new Session_management(getActivity());

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_score);
        ((MainActivity)getActivity()).setTabindicatorfm();

        student_icon = (ImageView) view.findViewById(R.id.iv_degree_student_icon);
        student_name = (TextView) view.findViewById(R.id.tv_degree_student_name);
        classname = (TextView) view.findViewById(R.id.tv_degree_class);
        fail = (TextView) view.findViewById(R.id.tv_degree_fail);
        pass = (TextView) view.findViewById(R.id.tv_degree_pass);
        daily_degree = (TextView) view.findViewById(R.id.tv_degrees_daily);
        monthly_degree = (TextView) view.findViewById(R.id.tv_degrees_monthly);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        ImageLoader.getInstance().init(config);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_login_icon)
                .showImageForEmptyUri(R.drawable.ic_login_icon)
                .showImageOnFail(R.drawable.ic_login_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK), student_icon, options);

        String get_studentname = sessionManagement.getUserDetails().get(ConstValue.KEY_NAME);
        String get_classname = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME);
        String getdivision = sessionManagement.getUserDetails().get(ConstValue.KEY_DIVISION_NAME);

        student_name.setText(get_studentname);

        StringBuilder sb = new StringBuilder();
        sb.append(getActivity().getString(R.string.attend_class)+": " + get_classname + "\n");
        sb.append(getActivity().getString(R.string.attend_div)+": "+ getdivision + "\n");

        classname.setText(sb.toString());

        daily_degree.setOnClickListener(this);
        monthly_degree.setOnClickListener(this);

        if(ConnectivityReceiver.isConnected()){
            new getScore().execute();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_degrees_daily) {

            Fragment fm = new Daily_degrees_fragment();
            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

        } else if (view.getId() == R.id.tv_degrees_monthly) {

            Fragment fm = new Monthly_degree_fragment();
            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
    }

    public class getScore extends AsyncTask<Void, Void, Void>{

        Progress_dialog pd = new Progress_dialog(getActivity());

        String getFail,getpass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String student_id = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.SCORE_URL+student_id, ConstValue.GET);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.SCORE_URL,ConstValue.GET,null
                ,sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                Log.e("response: ", jsonSTR);

                try {

                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    JSONObject jsonArray = jsonObject.getJSONObject("Data");

                    response = jsonObject.getString("Status");


                    getFail = jsonArray.getString("total_failed");

                    getpass = jsonArray.getString("total_success");

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

                fail.setText(getFail);
                pass.setText(getpass);
            }
        }
    }
}
