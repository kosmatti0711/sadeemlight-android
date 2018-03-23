package com.sadeemlight.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.MyApplication;
import com.sadeemlight.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.sadeemlight.adapter.AddAccountAdapter;
import com.sadeemlight.adapter.ChatAdapter;
import com.sadeemlight.adapter.Drawer_list_adapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.gcm.GCMRegistrationIntentService;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelAddAccount;
import com.sadeemlight.venus_uis.ShowStudentLayout;
import com.sadeemlight.venus_uis.TabEntity;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    public static int notfNewCount = 0, notfColleagueCount = 0, notfTeachingCount = 0, notfMessageCount = 0;
    public static MainActivity G_MAINACTIVTY = null;

    public Session_management sessionManagement;

    private Drawer_list_adapter adapter;
    private List<Drawer_list_model> list_adapters = new ArrayList<>();

    private AddAccountAdapter adapter2;
    private List<ModelAddAccount> list_adapters2 = new ArrayList<>();

    private DisplayImageOptions options, options2;

    ActionBarDrawerToggle toggle;

    DrawerLayout drawer;

    boolean isAccountlist = false;

    ListView lv, lv_student;
    ImageView profile;
    boolean isRunning = false;

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
    ArrayList<HashMap<String, String>> listarray_school = new ArrayList<>();
    ArrayList<HashMap<String, String>> listarray_info = new ArrayList<>();

    int[] mTitles = {R.string.tab_news, R.string.tab_friend, R.string.tab_teaching};
    ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    CommonTabLayout mTabLayout;


    // store m_offlineData
    public SharedPreferences settings;

    JSONArray jsonArray = null;

    String response;

    String getliststudent_id;

    String getstudent_id, student_name, login_as, getclass_id, birth_date, gender, username,
            imagelink, classname, school_id, schoolname, division,
            division_id, city_id, city_name, points, school_image, access_token;

    String LoginAs = "";
    String gcm_token;

    boolean isChange_setting = false;

    //Creating a broadcast receiver for com.sadeemlight.gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public void testLang() {
        Resources res = this.getResources();
        // Change locale m_settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        conf.locale = new Locale("AR");
        res.updateConfiguration(conf, dm);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_back2);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //testLang();
        setStatusBarGradiant(this);

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        sessionManagement = new Session_management(MainActivity.this);
        sessionManagement.checkLogin();

        settings = getSharedPreferences("MAIN_PREF", 0);
        listarray = new ArrayList<HashMap<String, String>>();

        try
        {
            listarray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "drawer_student_list", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        intializeReciver();

//        notfNewCount = 2;
//        notfColleagueCount = 10;
//        notfTeachingCount = 7;

        for (int i = 0; i < mTitles.length; i++)
        {
            mTabEntities.add(new TabEntity(getResources().getString(mTitles[i]), 0, 0));
        }
        mTabLayout = (CommonTabLayout)findViewById(R.id.maintab);
        mTabLayout.setTabData(mTabEntities);

        updateNotfCounts();

        Bundle notificationData = getIntent().getExtras();
        if (notificationData != null && notificationData.getString("type") != null)
        {
            checkForNotifications(notificationData);
        }
        else
        {
            Fragment fm = new NewsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

            mTabLayout.setIndicatorColor(getResources().getColor(R.color.bg_color));
        }


        checkConnection();
        if (savedInstanceState == null)
        {
            // Manually checking internet connection
        }

        /* NOTIfication Code start */
        try
        {
            String gettype = getIntent().getStringExtra("type");
            Log.e("type: ", gettype);
            if (!gettype.contentEquals("") && gettype != null)
            {
                Fragment fm = null;
                Bundle args;

                if (gettype.contentEquals("news"))
                {
                    fm = new NewsFragment();
                }
                else if (gettype.contentEquals("homework")) {
                    fm = new Homework_activity();
                } else if (gettype.contentEquals("voice_lesson")) {
                    fm = new AudioLibraryFragment();
                } else if (gettype.contentEquals("notification")) {
                    fm = new NotificationFragment();
                } else if (gettype.contentEquals("attendance")) {
                    fm = new Attendance_fragment();
                } else if (gettype.contentEquals("score")) {
                    fm = new Score_degrees_fragment();
                } else if (gettype.contentEquals("message")) {
                    fm = new ChatActivity();
                } else if (gettype.contentEquals("timetable")) {
                    fm = new TimetableFragment();
                } else if (gettype.contentEquals("library")) {
                    fm = new LibraryFragment();
                } else if (gettype.contentEquals("attendance_archive")) {
                    fm = new Archive_fragment();
                } else if (gettype.contentEquals("daily_degrees")) {
                    fm = new Daily_degrees_fragment();
                } else if (gettype.contentEquals("monthly_degrees")) {
                    fm = new Monthly_degree_fragment();
                } else if (gettype.contentEquals("homework_archive")) {
                    fm = new Homework_list_fragment();
                } else if (gettype.contentEquals("homework_today")) {
                    fm = new Homework_list_fragment();
                    args = new Bundle();
                    args.putString("isToday", "&homework_type=Today");
                    fm.setArguments(args);
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        /* Notificaton code end */

        /* commentlist code start */
        try {
            String getnews_type = getIntent().getStringExtra("news_type");
            String getnews_title = getIntent().getStringExtra("news_title");
            String getnews_detail = getIntent().getStringExtra("news_detail");
            String getnews_id = getIntent().getStringExtra("news_id");

            Log.e("type: ", getnews_type);
            if (!getnews_type.contentEquals(""))
            {
                Bundle args;
                Fragment fm = new Add_news_comment_fragment();
                args = new Bundle();

                args.putString("news_id", getnews_id);
                args.putString("news_title", getnews_title);
                args.putString("news_detail", getnews_detail);
                args.putString("news_type", getnews_type);

                fm.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        /* commentlist code end */

        mTabLayout.setOnTabSelectListener(new OnTabSelectListener()
        {
            @Override
            public void onTabSelect(int position) {
                Fragment fragment = null;
                Bundle args;

                mTabLayout.setIndicatorColor(getResources().getColor(R.color.bg_color));

                checkConnection();

                Log.e("tabClick:", "" + position);

                if (mTabLayout.getCurrentTab() == 0)
                {
                    fragment = new NewsFragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfNewCount = 0;
                }
                else if (mTabLayout.getCurrentTab() == 1)
                {
                    fragment = new Friends_fragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfColleagueCount = 0;
                }
                else if (mTabLayout.getCurrentTab() == 2)
                {
                    fragment = new Teaching_fragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfTeachingCount = 0;
                }

                updateNotfCounts();


                final Fragment finalFragment = fragment;
                Handler handler = new Handler()
                {
                    @Override
                    public void handleMessage(android.os.Message msg)
                    {
                        if (finalFragment != null)
                        {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, finalFragment).addToBackStack(null).commit();
                        }
                    }

                };
                handler.sendEmptyMessageDelayed(0, 200);
                mShowStudentLayout.hideStudentList();
            }


            @Override
            public void onTabReselect(int position)
            {

                Fragment fr = getSupportFragmentManager().findFragmentById(R.id.container);
                String fm_name = fr.getClass().getSimpleName();
                Fragment fragment = null;
                Bundle args;

             //   mTabLayout.setIndicatorColor(getResources().getColor(R.color.bg_color));

                checkConnection();

                Log.e("tabClick:", "reselected" + position);
                Log.e("tabClick:", "reselectedFM" + fm_name);
                if (mTabLayout.getCurrentTab() == 0 && !fm_name.contentEquals("NewsFragment"))
                {
                    fragment = new NewsFragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfNewCount = 0;
                }
                else if (mTabLayout.getCurrentTab() == 1 && !fm_name.contentEquals("Friends_fragment"))
                {
                    fragment = new Friends_fragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfColleagueCount = 0;
                }
                else if (mTabLayout.getCurrentTab() == 2 && !fm_name.contentEquals("Teaching_fragment"))
                {
                    fragment = new Teaching_fragment();
                    args = new Bundle();
                    fragment.setArguments(args);
                    notfTeachingCount = 0;
                }

                updateNotfCounts();

                if (fragment != null)
                {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

                }
                mShowStudentLayout.hideStudentList();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);

                Progress_dialog pd = new Progress_dialog(MainActivity.this);
                pd.dismissProgress();

                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        lv = (ListView) findViewById(R.id.list_view_inside_nav);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                try {

                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Fragment fr = getSupportFragmentManager().findFragmentById(R.id.container);

                    final String fm_name = fr.getClass().getSimpleName();
                    Log.e("backstack: ", ": " + fm_name);

                    if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                    {

                        if (fm_name.contentEquals("NewsFragment")
                                || fm_name.contentEquals("Friends_fragment")
                                || fm_name.contentEquals("Teaching_fragment"))
                        {

                            // unlock drawer swipe
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                            //show hamburger
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            toggle.syncState();
                            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    drawer.openDrawer(GravityCompat.START);
                                }
                            });

                        }
                        else
                        {
                            // lock drawer swipe
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                            toolbar.setNavigationOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (fm_name.contentEquals("Homework_activity")
                                            || fm_name.contentEquals("Voice_lesson_fragment")
                                            || fm_name.contentEquals("NotificationFragment")
                                            || fm_name.contentEquals("Attendance_fragment")
                                            || fm_name.contentEquals("Score_degrees_fragment")
                                            || fm_name.contentEquals("TimetableFragment")
                                            || fm_name.contentEquals("LibraryFragment")
                                            || fm_name.contentEquals("AboutUs_fragment")
                                            || fm_name.contentEquals("SuggestionFragment")
                                            || fm_name.contentEquals("Exam_subject_fragment")
                                            || fm_name.contentEquals("Add_account_fragment")
                                            || fm_name.contentEquals("Settings_fragment")
                                            || fm_name.contentEquals("Visit_us_fragment")) {

                                        Fragment fm = new NewsFragment();
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

                                    }
                                    else if (fm_name.contentEquals("ChatActivity"))
                                    {

                                        Fragment fm = new NewsFragment();
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

                                        mTabLayout.setCurrentTab(0);
                                        mTabLayout.setIndicatorColor(getResources().getColor(R.color.bg_color));
                                        mTabLayout.setVisibility(View.VISIBLE);
                                        mShowStudentLayout.m_bottomView.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        onBackPressed();
                                    }
                                }
                            });
                        }

                    }
                    else
                    {
                        //show hamburger
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                drawer.openDrawer(GravityCompat.START);
                            }
                        });
                    }

                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this).build();
        ImageLoader.getInstance().init(config);

        final ImageView iv_school_back = (ImageView) findViewById(R.id.iv_nav_header_school_icon);

        lv_student = (ListView) findViewById(R.id.list_view_student_inside_nav);

        LoginAs = sessionManagement.getUserDetails().get(ConstValue.KEY_LOGINAS);

        listDown();

        profile = (ImageView) findViewById(R.id.iv_nav_header_icon);

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
        imageLoader.displayImage(sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK),profile, options);

        options2 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray)
                .showImageForEmptyUri(R.color.gray)
                .showImageOnFail(R.color.gray)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        String school_img = sessionManagement.getUserDetails().get(ConstValue.KEY_SCHOOL_IMAGE);

        ImageLoader imageLoader1 = ImageLoader.getInstance();
        imageLoader1.displayImage(school_img,
                iv_school_back, options2);

        Log.e("school_image: ", school_img);

        TextView student_name = (TextView) findViewById(R.id.tv_nav_header_studentname);
        TextView school_name = (TextView) findViewById(R.id.tv_nav_header_schoolname);

        SharedPreferences sp_getStudentname = getSharedPreferences(ConstValue.PREFS_NAME, 0);

        student_name.setText(sp_getStudentname.getString(ConstValue.KEY_NAME, null).toString());
        school_name.setText(sessionManagement.getUserDetails().get(ConstValue.KEY_SCHOOL_NAME));

        ImageView iv_school = (ImageView) findViewById(R.id.iv_nav_school);

        iv_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getSchool_info().execute();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getstudent_info().execute();
            }
        });

        lv.setOnItemClickListener(this);
        lv_student.setOnItemClickListener(this);

        initShowStudentLayout();
    }

    private void loadPhoto(ImageView imageView, int width, int height) {

        ImageView tempImageView = imageView;
        ImageButton btn_cancle;

        final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_image_popup,
                (ViewGroup) ((Activity) this).findViewById(R.id.rl_dialog));

        ImageView image = (ImageView) layout.findViewById(R.id.iv_image_dialog);
        btn_cancle = (ImageButton) layout.findViewById(R.id.ib_cancle);
        image.setImageDrawable(tempImageView.getDrawable());

        final AlertDialog dialog;

        imageDialog.setView(layout);

        dialog = imageDialog.create();

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void listUp() {

        lv_student.setVisibility(View.VISIBLE);
        lv.setVisibility(View.GONE);

        list_adapters.clear();

        if (ConnectivityReceiver.isConnected()) {
            new getAccountlist().execute();
        } else {
            addData();
        }
    }

    public void listDown() {

        lv_student.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);

        list_adapters.clear();
        list_adapters2.clear();

        list_adapters.add(new Drawer_list_model(R.string.side_menu_news, "", R.drawable.ic_menu_news));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_homework, "", R.drawable.ic_menu_homework));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_voice, "", R.drawable.ic_menu_audiobook));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_notification, "", R.drawable.ic_menu_notification));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_attend, "", R.drawable.ic_menu_attendance));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_score, "", R.drawable.ic_menu_score));

        if (!LoginAs.contentEquals("Student"))
        {
            list_adapters.add(new Drawer_list_model(R.string.side_menu_message, "", R.drawable.ic_menu_message));
        }

        list_adapters.add(new Drawer_list_model(R.string.side_menu_timetable, "", R.drawable.ic_menu_schedule));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_library, "", R.drawable.ic_menu_library));

        if (!LoginAs.contentEquals("Student")) {
            list_adapters.add(new Drawer_list_model(R.string.side_menu_addacount, "", R.drawable.ic_menu_addaccount));
        }

        list_adapters.add(new Drawer_list_model(R.string.side_menu_about, "", R.drawable.ic_menu_aboutus));
        list_adapters.add(new Drawer_list_model(R.string.side_menu_share, "", R.drawable.ic_menu_share1));

        adapter = new Drawer_list_adapter(MainActivity.this, list_adapters);

        lv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(mShowStudentLayout.isVisible())
        {
            mShowStudentLayout.hideStudentList();
        }
        else
        {
            super.onBackPressed();
        }

        mTabLayout.setVisibility(View.VISIBLE);
        mShowStudentLayout.m_bottomView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        onCreateMenu_venus(menu);
        return true;
    }

    public void onCreateMenu_venus(final Menu menu)
    {
        String LoginAs = sessionManagement.getUserDetails().get(ConstValue.KEY_LOGINAS);

        MenuItem item = menu.findItem(R.id.action_message);
        MenuItemCompat.setActionView(item, R.layout.menu_view_notf);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        messageNotfView = (MsgView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        notifCount.findViewById(R.id.img_menuicon).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MainActivity.notfMessageCount = 0;
                Fragment fm = new ChatActivity();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
                updateNotfCounts();
                mShowStudentLayout.hideStudentList();
            }
        });
        updateNotfCounts();

        if (LoginAs.contentEquals("Student"))
        {
            item.setVisible(false);
        }
        else
        {
            item.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        mTabLayout.setIndicatorColor(getResources().getColor(R.color.colorPrimary));

        Fragment fm = null;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home)
        {
            fm = new Home_fragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
            mTabLayout.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.menu_logout)
        {
            new logout().execute();
        }
        else if (id == R.id.menu_setting)
        {

            fm = new Settings_fragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else if (id == R.id.menu_suggest)
        {
            fm = new SuggestionFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
            mTabLayout.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.menu_etest) {

            fm = new Exam_subject_fragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
            mTabLayout.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.menu_elearning)
        {
            fm = new Teaching_fragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
            updateNotfCounts();
            mTabLayout.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.action_message)
        {
            fm = new ChatActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
            updateNotfCounts();
        }

        if(fm != null)
        {
            mShowStudentLayout.hideStudentList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFragmentItemPosition(int position)
    {
        sessionManagement.checkLogin();
        Fragment fragment = null;
        Bundle args;
        switch (position)
        {
            case 0:
                fragment = new NewsFragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 1:
                fragment = new Homework_activity();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new AudioLibraryFragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 3:
                fragment = new NotificationFragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 4:
                fragment = new Attendance_fragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 5:
                fragment = new Score_degrees_fragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 6:
                fragment = new ChatActivity();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 7:
                fragment = new TimetableFragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 8:
                fragment = new LibraryFragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 9:
                fragment = new Add_account_fragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
            case 10:
                fragment = new AboutUs_fragment();
                args = new Bundle();
                fragment.setArguments(args);
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            mShowStudentLayout.hideStudentList();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Handle navigation view item clicks here.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        mTabLayout.setIndicatorColor(getResources().getColor(R.color.colorPrimary));

        // Manually checking internet connection
        checkConnection();
        if (adapterView.getId() == R.id.list_view_inside_nav)
        {

            int listtext = list_adapters.get(i).getTitle();

            if (listtext == R.string.side_menu_news)
            {
                setFragmentItemPosition(0);
                getSupportActionBar().setTitle(R.string.side_menu_news);
                mTabLayout.setVisibility(View.VISIBLE);
            } else if (listtext == R.string.side_menu_homework)
            {
                setFragmentItemPosition(1);
                getSupportActionBar().setTitle(R.string.side_menu_homework);
                mTabLayout.setVisibility(View.VISIBLE);
            } else if (listtext == R.string.side_menu_voice) {
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(2);
                getSupportActionBar().setTitle(R.string.side_menu_voice);
            } else if (listtext == R.string.side_menu_notification) {
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(3);
                getSupportActionBar().setTitle(R.string.side_menu_notification);
            } else if (listtext == R.string.side_menu_attend) {
                setFragmentItemPosition(4);
                getSupportActionBar().setTitle(R.string.side_menu_attend);
                mTabLayout.setVisibility(View.VISIBLE);
            } else if (listtext == R.string.side_menu_score) {
                setFragmentItemPosition(5);
                getSupportActionBar().setTitle(R.string.side_menu_score);
                mTabLayout.setVisibility(View.VISIBLE);
            } else if (listtext == R.string.side_menu_message) {
                setFragmentItemPosition(6);
                getSupportActionBar().setTitle(R.string.side_menu_message);
                mTabLayout.setVisibility(View.GONE);
            } else if (listtext == R.string.side_menu_timetable) {
                // Handle the action
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(7);
                getSupportActionBar().setTitle(R.string.side_menu_timetable);
            } else if (listtext == R.string.side_menu_library) {
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(8);
                getSupportActionBar().setTitle(R.string.side_menu_library);
            } else if (listtext == R.string.side_menu_addacount) {
                // Handle the action
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(9);
                getSupportActionBar().setTitle(R.string.side_menu_addacount);
            } else if (listtext == R.string.side_menu_about) {
                mTabLayout.setVisibility(View.VISIBLE);
                setFragmentItemPosition(10);
                getSupportActionBar().setTitle(R.string.side_menu_about);
            } else if (listtext == R.string.side_menu_share) {
                sendApplink();
                mShowStudentLayout.m_bottomView.setVisibility(View.GONE);
            }

        } else if (adapterView.getId() == R.id.list_view_student_inside_nav) {
            getliststudent_id = listarray.get(i).get("id");

            //Toast.makeText(MainActivity.this, "" + getliststudent_id, Toast.LENGTH_SHORT).show();

            /*if (ConnectivityReceiver.isConnected()) {
                new getStudentlist_detail().execute();
            }*/

            sessionManagement.clearOffline();

            getstudent_id = listarray.get(i).get("id");
            student_name = listarray.get(i).get("student_name");
            login_as = listarray.get(i).get("login_as");
            getclass_id = listarray.get(i).get("class_id");
            classname = listarray.get(i).get("class_name");
            division = listarray.get(i).get("division_name");
            school_id = listarray.get(i).get("school_id");
            schoolname = listarray.get(i).get("school_name");
            birth_date = listarray.get(i).get("birth_date");
            gender = listarray.get(i).get("gender");
            username = listarray.get(i).get("username");
            imagelink = listarray.get(i).get("student_image");

            division_id = listarray.get(i).get("division_id");
            city_id = listarray.get(i).get("city_id");
            city_name = listarray.get(i).get("city_name");
            points = listarray.get(i).get("points");
            school_image = listarray.get(i).get("school_image");
            access_token = listarray.get(i).get("access_token");

            Log.e("school_image: ", school_image);

            sessionManagement.setAlluserDetail(student_name, "", getstudent_id, getclass_id, birth_date
                    , gender, imagelink, schoolname, school_id, classname, division,
                    division_id, city_id, city_name, points,
                    school_image, access_token);

            finish();

            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            startActivity(refresh);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void intializeReciver()
    {
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent)
            {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS))
                {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");

                    Log.e("Registration token: ", token);

                    gcm_token = token;

                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                }
                else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR))
                {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode)
        {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            }
            else
                {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        }
        else
        {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        Log.d(Defines.APP_LOG_TITLE, "Main Activity Create");
        ChatOfflineService.startService(this);
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));

        registerReceiver(mMessageReceiver, new IntentFilter("sadeem_notification"));

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        updateNotfCounts();

        G_MAINACTIVTY = this;
    }

    //Unregistering receiver on activity paused
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        unregisterReceiver(mMessageReceiver);
        ChatAdapter.releaseAudio();

        G_MAINACTIVTY = null;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(Defines.APP_LOG_TITLE, "Main Activity Destory");
        ChatOfflineService.stopService(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Method to manually check connection status
    private void checkConnection()
    {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected)
    {
        String message;
        int color;
        if (!isConnected)
        {
            message = "Sorry! Not connected to internet";
            color = Color.YELLOW;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG);

            snackbar.setAction("Retry", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Your data is Off");
                    dialog.setMessage("Turn on data or Wi-Fi in Settings.");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });

                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    public void setTitle(int title)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

        if (getResources().getString(title).contentEquals(getResources().getString(R.string.side_menu_message))  )
        {
            mTabLayout.setVisibility(View.GONE);
            mShowStudentLayout.m_bottomView.setVisibility(View.GONE);
        }
        else if(getResources().getString(title).contentEquals(getResources().getString(R.string.side_menu_voice))    )
        {
            mShowStudentLayout.m_bottomView.setVisibility(View.GONE);
        }
        else
        {
            mTabLayout.setVisibility(View.VISIBLE);

            if(getResources().getString(title).contentEquals(getResources().getString(R.string.side_menu_news)) ||
                    getResources().getString(title).contentEquals(getResources().getString(R.string.side_menu_addacount)) ||
                    getResources().getString(title).contentEquals(getResources().getString(R.string.tab_friend)))
            {
                mShowStudentLayout.m_bottomView.setVisibility(View.VISIBLE);
            }
            else
            {
                mShowStudentLayout.m_bottomView.setVisibility(View.GONE);
            }

        }
    }

    public void setSubTitle(String subtitle)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(subtitle);
    }

    public void setTabindicator(String tabname)
    {
        if (tabname == "news")
        {
            mTabLayout.setCurrentTab(0);
        } else if (tabname == "friends") {
            mTabLayout.setCurrentTab(1);
        } else if (tabname == "teaching") {
            mTabLayout.setCurrentTab(2);
        }
        mTabLayout.setIndicatorColor(getResources().getColor(R.color.bg_color));

        // unlock drawer swipe
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        //show hamburger
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.syncState();
    }

    public void setTabindicatorfm()
    {
        mTabLayout.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
    }

    public void setfinish() {
        this.finish(); // finish activity
    }

    public void sendApplink()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    String message = "";
    public class getAccountlist extends AsyncTask<Void, Void, Void>
    {
        Progress_dialog pd = new Progress_dialog(MainActivity.this);
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

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String getparent_id = sessionManagement.getUserDetails().get(ConstValue.KEY_PARENT_ID);
            String url = ConstValue.ACCOUNT_LIST_URL + "?parent_id=" + getparent_id;
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                list_adapters2.clear();
                listarray.clear();

                Log.e("response: ", jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");
                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Accounts");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", c.getString("student_id"));
                        map.put("student_name", c.getString("student_name"));
                        map.put("student_image", c.getString("student_image"));
                        map.put("login_as", c.getString("login_as"));
                        map.put("class_id", c.getString("class_id"));
                        map.put("class_name", c.getString("class_name"));
                        map.put("division_name", c.getString("division_name"));
                        map.put("school_id", c.getString("school_id"));
                        map.put("school_name", c.getString("school_name"));
                        map.put("birth_date", c.getString("birth_date"));
                        map.put("gender", c.getString("gender"));
                        map.put("username", c.getString("username"));
                        map.put("division_id", c.getString("division_id"));
                        map.put("city_id", c.getString("city_id"));
                        map.put("points", c.getString("points"));
                        map.put("school_image", c.getString("school_image"));
                        map.put("access_token",  c.getString("access_token"));

                        listarray.add(map);

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

            if (response == "true")
            {
                try
                {
                    settings.edit().putString("sadeemlight" + "addaccount", ObjectSerializer.serialize(listarray)).commit();
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
                GlobalFunction.outputToast(MainActivity.this, message);
                addData();
            }
        }
    }

    public void addData()
    {

        for (int i = 0; i < listarray.size(); i++)
        {
            list_adapters2.add(new ModelAddAccount(listarray.get(i).get("student_name"),
                    listarray.get(i).get("school_name"),
                    listarray.get(i).get("id"),
                    listarray.get(i).get("student_image"),
                    listarray.get(i).get("access_token")));

        }

        adapter2 = new AddAccountAdapter(MainActivity.this, list_adapters2);
        lv_student.setAdapter(adapter2);
    }

    public class getSchool_info extends AsyncTask<Void, Void, Void>
    {

        Progress_dialog pd = new Progress_dialog(MainActivity.this);

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

            String school_id = sessionManagement.getUserDetails().get(ConstValue.KEY_SCHOOL_ID);
            ServiceHandler sh = new ServiceHandler();

//            String jsonSTR = sh.makeServiceCall(ConstValue.SCHOOL_INFO_URL + school_id, ConstValue.GET);
            String jsonSTR = null;
            try
            {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.SCHOOL_INFO_URL,ConstValue.GET,null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                listarray_school.clear();
                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataobject = jsonObject.getJSONObject("Data");
                    jsonArray = jsonObject.getJSONArray("School");
                    response = jsonObject.getString("Status");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", c.getString("school_id"));
                        map.put("school_name", c.getString("school_name"));
                        map.put("school_code", c.getString("school_code"));
                        map.put("address", c.getString("address"));
                        map.put("website", c.getString("website"));
                        map.put("email", c.getString("email"));
                        map.put("phone", c.getString("phone"));
                        map.put("phone2", c.getString("phone2"));
                        map.put("school_image", c.getString("school_image"));

                        listarray_school.add(map);
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
            StringBuilder sb = new StringBuilder();

            if (response.contentEquals("true"))
            {

                sb.append("School name: " + listarray_school.get(0).get("school_name") + "\n");
                sb.append("" + "\n");
                sb.append("School code: " + listarray_school.get(0).get("school_code") + "\n");
                sb.append("" + "\n");
                sb.append("Address: " + listarray_school.get(0).get("address") + "\n");
                sb.append("" + "\n");
                sb.append("Website: " + listarray_school.get(0).get("website") + "\n");
                sb.append("" + "\n");
                sb.append("Email address: " + listarray_school.get(0).get("email") + "\n");
                sb.append("" + "\n");
                sb.append("Phone No1.: " + listarray_school.get(0).get("phone") + "\n");
                sb.append("" + "\n");
                sb.append("Phone No2.: " + listarray_school.get(0).get("phone2") + "\n");

            }
            else
            {
                sb.append("No Data Available ");
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("School Information");
            alert.setMessage(sb.toString());
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            });
            alert.setNegativeButton(R.string.activity_visit, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Bundle args = new Bundle();
                    Fragment fm = new Visit_us_fragment();
                    args.putString("link", listarray_school.get(0).get("website"));
                    fm.setArguments(args);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
                }
            });
            alert.show();
        }
    }

    public class logout extends AsyncTask<Void, Void, Void>
    {
        Progress_dialog pd = new Progress_dialog(MainActivity.this);

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

            List<ModelParamsPair> params = new ArrayList<>();

//            String student_id = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);
//            String parent_id = sessionManagement.getUserDetails().get(ConstValue.KEY_PARENT_ID);
//
//            if (parent_id.contentEquals("")) {
//                params.add(new ModelParamsPair("id", student_id));
//            } else {
//                params.add(new ModelParamsPair("id", parent_id));
//            }
//
//            params.add(new ModelParamsPair("device_type", "Android"));
            String device_token = FirebaseInstanceId.getInstance().getToken();
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            params.add(new ModelParamsPair("device_token", device_token));

            ServiceHandler sh = new ServiceHandler();

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.LOGOUT_URL, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                Log.e("response: ", jsonSTR);
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    jsonArray = jsonObject.getJSONArray("data");
                    response = jsonObject.getString("response");
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
            sessionManagement.logoutSession();
            finish();
        }
    }

    public class getstudent_info extends AsyncTask<Void, Void, Void>
    {
        Progress_dialog pd = new Progress_dialog(MainActivity.this);

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
            String student_id = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);
            ServiceHandler sh = new ServiceHandler();
            //String jsonSTR = sh.makeServiceCall(ConstValue.FRIENDS_URL + student_id, ConstValue.GET);
            String jsonSTR = null;
            try
            {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.ACCOUNT_LIST_URL,ConstValue.GET,null,
                        sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                listarray_info.clear();
                Log.e("response: ", jsonSTR);
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    JSONObject jsonData = jsonObject.getJSONObject("Friend");
                    response = jsonObject.getString("Status");

                    /*for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);*/

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("id", jsonData.getString("student_id"));
                    map.put("student_name", jsonData.getString("student_name"));
                    map.put("student_image", jsonData.getString("student_image"));
                    map.put("login_as", jsonData.getString("login_as"));
                    map.put("class_id", jsonData.getString("class_id"));
                    map.put("class_name", jsonData.getString("class_name"));
                    map.put("division_name", jsonData.getString("division_name"));
                    map.put("school_id", jsonData.getString("school_id"));
                    map.put("school_name", jsonData.getString("school_name"));
                    map.put("birth_date", jsonData.getString("birth_date"));
                    map.put("gender", jsonData.getString("gender"));
                    map.put("username", jsonData.getString("username"));
                    map.put("division_id", jsonData.getString("division_id"));
                    map.put("city_id", jsonData.getString("city_id"));
                    map.put("city_name", jsonData.getString("city_name"));
                    map.put("points", jsonData.getString("points"));
                    map.put("Attend", jsonData.getString("Attend"));
                    map.put("Absent", jsonData.getString("Absent"));
                    map.put("Legally", jsonData.getString("Legally"));

                    listarray_info.add(map);
                    //}
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

            if (response == "true")
            {
                sb.append("Class name: " + listarray_info.get(0).get("class_name") + "\n");
                sb.append("" + "\n");
                sb.append("Division code: " + listarray_info.get(0).get("division_name") + "\n");
                sb.append("" + "\n");
                sb.append("Rating: " + listarray_info.get(0).get("points") + "\n");
                sb.append("" + "\n");
                sb.append("No. of Attendance days: " + listarray_info.get(0).get("Attend") + "\n");
                sb.append("" + "\n");
                sb.append("No. of Absent days: " + listarray_info.get(0).get("Absent") + "\n");

                ImageView tempImageView = profile;
                TextView tv_detail, tv_name;
                final AlertDialog.Builder imageDialog = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_info_popup,(ViewGroup) ((Activity) MainActivity.this).findViewById(R.id.rl_info));

                final ImageView image = (ImageView) layout.findViewById(R.id.iv_info_img);
                tv_detail = (TextView) layout.findViewById(R.id.tv_info_detail);
                tv_name = (TextView) layout.findViewById(R.id.tv_info_name);
                tv_name.setText(listarray_info.get(0).get("student_name"));
                SpannableString content = new SpannableString(sb.toString());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                tv_detail.setText(content);

                image.setImageDrawable(tempImageView.getDrawable());
                imageDialog.setView(layout);
                imageDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                image.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        loadPhoto(image, 0, 0);
                    }
                });

                imageDialog.create();
                imageDialog.show();

            }
        }
    }

    MsgView messageNotfView = null;
    public void updateNotfCounts()
    {
        if(notfNewCount == 0)
        {
            mTabLayout.hideMsg(0);
        }
        else
        {
            Log.d(Defines.APP_LOG_TITLE, "show news counter = " + notfNewCount);
            mTabLayout.showMsg(0, notfNewCount);
        }

        if(notfColleagueCount == 0)
        {
            mTabLayout.hideMsg(1);
        }
        else
        {
            Log.d(Defines.APP_LOG_TITLE, "show colleague counter");
            mTabLayout.showMsg(1, notfColleagueCount);
        }

        if(notfTeachingCount == 0)
        {
            mTabLayout.hideMsg(2);
        }
        else
        {
            mTabLayout.showMsg(2, notfTeachingCount);
        }

        for (int i = 0; i < 3; i++)
        {
            mTabLayout.setMsgMargin(i, 5, 3);
        }

        if(messageNotfView != null)
        {
            if(notfMessageCount == 0)
            {
                messageNotfView.setVisibility(View.INVISIBLE);
            }
            else
            {
                Log.d(Defines.APP_LOG_TITLE, "show message counter");
                UnreadMsgUtils.show(messageNotfView, notfMessageCount);
            }
        }
    }

    ShowStudentLayout mShowStudentLayout;
    public void initShowStudentLayout()
    {
        mShowStudentLayout = new ShowStudentLayout(this);
    }

    private ArrayList<MyOnTouchListener> onTouchListeners = new  ArrayList<MyOnTouchListener>(100);
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        for (MyOnTouchListener listener : onTouchListeners)
        {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener listener)
    {
        for(int i=0;i<onTouchListeners.size();i++)
        {
            if(onTouchListeners.get(i).equals(listener))
            {
                return;
            }
        }

        onTouchListeners.add(listener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener listener)
    {
        if(onTouchListeners.size() > 0)
        {
            onTouchListeners.remove(listener);
        }
    }

    public interface MyOnTouchListener
    {
        public void onTouch(MotionEvent ev);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("news"))
            {
                Log.d(ConstValue.APPTITLE, "main_news");
                if (ConnectivityReceiver.isConnected())
                {

                }
                else
                {
                    addData();
                }
                updateNotfCounts();
            }
            else if (type.contentEquals("message"))
            {
                Log.d(ConstValue.APPTITLE, "main_message");
                updateNotfCounts();
            }
            else if(type.contentEquals("offlinemessage_sent"))
            {

            }
            else if(type.contentEquals("expire_alert"))
            {
                final String expire_date = intent.getStringExtra("param1");
                final String phone = intent.getStringExtra("param2");

                SimpleDateFormat sDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
                final String now_string = sDateFormat.format(new java.util.Date());

                if(expire_date == null || expire_date.contentEquals("") )
                {
                    new logout().execute();
                }
                else
                {
                    final CharSequence[] items = { getString(R.string.call_school),getString(R.string.close)};
                    String msg = getString(R.string.expire_msg) + " " + expire_date;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.notice));
                    builder.setTitle(getString(R.string.notice));
                    builder.setMessage(msg);
                    builder.setPositiveButton(getString(R.string.call_school), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                            {
                                return;
                            }
                            startActivity(callIntent);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(expire_date.equals(now_string) == true)
                            {
                                new logout().execute();
                            }
                            else
                            {
                                dialog.dismiss();
                            }
                        }
                    });

                    AlertDialog dlg = builder.create();
                    dlg.show();
                   // dlg.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_black));
                   // dlg.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_black));
                }
            }
            else if(type.contentEquals("hide_tab"))
            {
                mTabLayout.setVisibility(View.GONE);
            }
            else if(type.contentEquals("show_tab"))
            {
                mTabLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    public void showTablayout(boolean bShow)
    {
        if(bShow)
        {
            if(mTabLayout.getVisibility() != View.VISIBLE)
            {
                mTabLayout.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if(mTabLayout.getVisibility() == View.VISIBLE)
            {
                mTabLayout.setVisibility(View.GONE);
            }
        }
    }

    private void checkForNotifications(Bundle notificationData)
    {
        String type = notificationData.getString("type");
        Fragment notificationFragment;
        FragmentManager mManager = getSupportFragmentManager() ;
        if (type == null)
        {
            return;
        }
        else if (type.contentEquals("news"))
        {
            notificationFragment = new NewsFragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("online_lesson_category"))
        {
            notificationFragment = new Teaching_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("online_lesson"))
        {
            String lesson_id = notificationData.getString("param1");
            Bundle args;
            args = new Bundle();
            args.putString("level_id", lesson_id);

            notificationFragment = new Teaching_list_fragment();
            notificationFragment.setArguments(args);
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("online_exam"))
        {
            notificationFragment = new Exam_subject_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("online_exam_question"))
        {
            String exam_id = notificationData.getString("param1");

            notificationFragment = new Exam_que_ans_fragment();
            Bundle args;
            args = new Bundle();

            args.putInt("examId", Integer.valueOf(exam_id));

            notificationFragment.setArguments(args);
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("homework"))
        {
            notificationFragment = new Homework_activity();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("voice_lesson"))
        {
            notificationFragment = new Voice_lesson_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("notification"))
        {
            notificationFragment = new NotificationFragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("attendance"))
        {
            notificationFragment = new Attendance_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("score"))
        {
            notificationFragment = new Score_degrees_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("message"))
        {
            notificationFragment = new ChatActivity();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("timetable"))
        {
            notificationFragment = new TimetableFragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("library"))
        {
            notificationFragment = new LibraryFragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("attendance_archive"))
        {
            notificationFragment = new Archive_fragment();
            mManager.beginTransaction().replace(R.id.container, notificationFragment).commit();
        }
        else if (type.contentEquals("daily_degrees"))
        {
            Fragment fm = new Daily_degrees_fragment();
            mManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else if (type.contentEquals("monthly_degrees"))
        {
            Fragment fm = new Monthly_degree_fragment();
            mManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else if (type.contentEquals("homework_archive")) {
            Fragment fm = new Homework_list_fragment();
            mManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else if (type.contentEquals("homework_today"))
        {
            Bundle args;
            Fragment fm = new Homework_list_fragment();
            args = new Bundle();
            args.putString("isToday", "&homework_type=Today");
            fm.setArguments(args);
            mManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();
        }
        else
        {
            return;
        }
    }

}
