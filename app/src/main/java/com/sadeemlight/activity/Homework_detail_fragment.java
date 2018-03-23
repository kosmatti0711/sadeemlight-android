package com.sadeemlight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sadeemlight.R;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 18/8/2016.
 */
public class Homework_detail_fragment extends Fragment {

    TextView title,date,classname,detail;

    Session_management sessionManagement;

    public Homework_detail_fragment() {
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
        View view = inflater.inflate(R.layout.home_work_detail_activity, container, false);

        sessionManagement = new Session_management(getActivity());

        title = (TextView) view.findViewById(R.id.tv_homework_detail_title);
        date = (TextView) view.findViewById(R.id.tv_homework_detail_date);
        classname = (TextView) view.findViewById(R.id.tv_homework_detail_class);
        detail = (TextView) view.findViewById(R.id.tv_homework_detail_detail);

        try {

            String matirialname = getArguments().getString("matirialname");
            String getdate = getArguments().getString("msg_time");
            String getclassname = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME);
            String getdetail = getArguments().getString("detail");

            title.setText(matirialname);
            date.setText(getdate);
            classname.setText("Class "+getclassname);
            detail.setText(getdetail);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return view;
    }
}
