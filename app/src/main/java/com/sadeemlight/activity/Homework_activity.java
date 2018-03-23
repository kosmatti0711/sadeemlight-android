package com.sadeemlight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;

import java.util.Calendar;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 19/8/2016.
 */
public class Homework_activity extends Fragment implements View.OnClickListener {

    ImageView student_icon;

    TextView tv_archive, tv_student_name, tv_class, todaydate;
    RelativeLayout rl_homework_day;

    Session_management sessionManagement;

    private DisplayImageOptions options;

    public Homework_activity() {
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
        View view = inflater.inflate(R.layout.home_work_activity, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_homework);
        ((MainActivity)getActivity()).setTabindicatorfm();

        sessionManagement = new Session_management(getActivity());

        student_icon = (ImageView) view.findViewById(R.id.iv_homework_student_icon);
        tv_student_name = (TextView) view.findViewById(R.id.tv_homework_student_name);
        tv_class = (TextView) view.findViewById(R.id.tv_homework_class);
        tv_archive = (TextView) view.findViewById(R.id.tv_homework_archive);
        todaydate = (TextView) view.findViewById(R.id.tv_homework_today_date);
        rl_homework_day = (RelativeLayout) view.findViewById(R.id.rl_homework_today);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
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


        tv_student_name.setText(sessionManagement.getUserDetails().get(ConstValue.KEY_NAME));

        String classname = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME);
        String getdivision = sessionManagement.getUserDetails().get(ConstValue.KEY_DIVISION_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append(getActivity().getString(R.string.attend_class)+": " + classname + "\n");
        sb.append(getActivity().getString(R.string.attend_div)+": "+ getdivision + "\n");

        tv_class.setText(sb.toString());

        Calendar calendar = Calendar.getInstance();

        tv_archive.setOnClickListener(this);
        rl_homework_day.setOnClickListener(this);
        todaydate.setText(new StringBuilder().append(calendar.get(Calendar.DAY_OF_MONTH))
                .append("/").append(calendar.get(Calendar.MONTH)+1).append("/")
                .append(calendar.get(Calendar.YEAR)));

        return view;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tv_homework_archive)
        {
            Fragment fm = new Homework_list_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else if (view.getId() == R.id.rl_homework_today)
        {
            Bundle args;
            Fragment fm = new Homework_list_fragment();
            args = new Bundle();
            args.putString("isToday", "&homework_type=Today");
            fm.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
    }

}
