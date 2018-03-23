package com.sadeemlight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.adapter.Home_grid_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 16/8/2016.
 */
public class Home_fragment extends Fragment implements AdapterView.OnItemClickListener {

    GridView gridMenu;

    private Home_grid_adapter adapter;
    private List<Home_grid_model> list_adapters = new ArrayList<>();

    Session_management sessionManagement;

    public Home_fragment() {
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
        View view = inflater.inflate(R.layout.home_menu_activity, container, false);

        sessionManagement = new Session_management(getActivity());

        gridMenu = (GridView) view.findViewById(R.id.gv_home_menu);

        String LoginAs = sessionManagement.getUserDetails().get(ConstValue.KEY_LOGINAS);

        list_adapters.clear();

        list_adapters.add(new Home_grid_model(R.string.side_menu_homework, R.drawable.ic_menu_homework));
        list_adapters.add(new Home_grid_model(R.string.side_menu_voice, R.drawable.ic_menu_audiobook));
        list_adapters.add(new Home_grid_model(R.string.side_menu_notification, R.drawable.ic_menu_notification));
        list_adapters.add(new Home_grid_model(R.string.side_menu_attend, R.drawable.ic_menu_attendance));
        list_adapters.add(new Home_grid_model(R.string.side_menu_score, R.drawable.ic_menu_score));

        if (!LoginAs.contentEquals("Student"))
        {
            list_adapters.add(new Home_grid_model(R.string.side_menu_message, R.drawable.ic_menu_message));
        }
        list_adapters.add(new Home_grid_model(R.string.side_menu_timetable, R.drawable.ic_menu_schedule));
        list_adapters.add(new Home_grid_model(R.string.side_menu_library, R.drawable.ic_menu_library));

        if (!LoginAs.contentEquals("Student"))
        {
            list_adapters.add(new Home_grid_model(R.string.side_menu_addacount, R.drawable.ic_menu_addaccount));
        }

        list_adapters.add(new Home_grid_model(R.string.side_menu_about, R.drawable.ic_menu_aboutus));
        list_adapters.add(new Home_grid_model(R.string.side_menu_elearning, R.drawable.ic_menu_elearning));
        list_adapters.add(new Home_grid_model(R.string.side_menu_etest, R.drawable.ic_menu_etests));

        adapter = new Home_grid_adapter(getActivity(), list_adapters);

        gridMenu.setAdapter(adapter);

        gridMenu.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        int listtext = list_adapters.get(i).getTitle();

        Fragment fragment = null;
        Bundle args;

        /*if (listtext == R.string.side_menu_news) {

            setFragmentItemPosition(0);
            getSupportActionBar().setId(R.string.side_menu_news);
            tabLayout.setVisibility(View.VISIBLE);
        } else*/
        if (listtext == R.string.side_menu_homework) {

            fragment = new Homework_activity();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_voice) {
            fragment = new AudioLibraryFragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_notification) {
            fragment = new NotificationFragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_attend) {
            fragment = new Attendance_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_score) {
            fragment = new Score_degrees_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_message) {
            fragment = new ChatActivity();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_timetable) {
            // Handle the action
            fragment = new TimetableFragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_library) {
            fragment = new LibraryFragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_addacount) {
            // Handle the action
            fragment = new Add_account_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_about) {
            fragment = new AboutUs_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_elearning) {
            fragment = new Teaching_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_etest) {
            fragment = new Exam_subject_fragment();
            args = new Bundle();
            fragment.setArguments(args);
        } else if (listtext == R.string.side_menu_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " APP");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }
}
