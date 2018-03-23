package com.sadeemlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;

import com.github.rtoshiro.view.video.FullscreenVideoView;


import com.sadeemlight.R;
import com.sadeemlight.adapter.NewsAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelNews;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;
import com.sadeemlight.venus_uis.utils.HttpUtil;
import com.sadeemlight.venus_uis.xlistview.XListView;

/**
 * Created by Rajesh Dabhi on 2/8/2016.
 */
public class NewsFragment extends Fragment {

    // arraylist list variable for store data;
    private ArrayList<HashMap<String, String>> m_offlineData = new ArrayList<>();
    private List<ModelNews> m_newsListData = new ArrayList<>();
    // store m_offlineData
    public SharedPreferences settings;

    private int m_page = 1;
    private int m_lastPage = 0;
    private XListView m_listView;
    private NewsAdapter m_adapter;

    private Session_management sessionManagement;
    private Boolean exit = false;

    public NewsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_activity, container, false);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_news);

        ((MainActivity) getActivity()).setTabindicator("news");

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (exit)
                    {
                        ((MainActivity) getActivity()).setfinish();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Press Back again to Exit.",
                                Toast.LENGTH_SHORT).show();
                        exit = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exit = false;
                            }
                        }, 3 * 1000);
                    }
                    return true;
                }
                return false;
            }
        });

        sessionManagement = new Session_management(getActivity());

        m_listView = (XListView) view.findViewById(R.id.lv_news_list);
        m_listView.setRecyclerListener(new AbsListView.RecyclerListener()
        {
            @Override
            public void onMovedToScrapHeap(View view)
            {
                FullscreenVideoView vv = (FullscreenVideoView) view.findViewById(R.id.videoView2);
                try
                {
                    if(vv.isPlaying())
                    {
                        vv.pause();
                    }
                }
                catch (Exception ex)
                {

                }

            }
        });

        m_adapter = new NewsAdapter(getContext());
        m_listView.setAdapter(m_adapter);
        m_listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (ConnectivityReceiver.isConnected())
                {
                    m_page = 1;
                    new getNews().execute(0);

                    Intent updates = new Intent("sadeem_notification");
                    updates.putExtra("type", "show_tab");
                    getActivity().sendBroadcast(updates);
                }
            }

            @Override
            public void onLoadMore() {
                if (ConnectivityReceiver.isConnected())
                {
                    new getNews().execute(1);

                    Intent updates = new Intent("sadeem_notification");
                    updates.putExtra("type", "hide_tab");
                    getActivity().sendBroadcast(updates);
                }
            }
        });

        m_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    Log.d(Defines.APP_LOG_TITLE, "SCROLLING DOWN TRUE");
                    ((MainActivity)getActivity()).showTablayout(false);
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    Log.d(Defines.APP_LOG_TITLE, "SCROLLING UP TRUE");
                    if(firstVisibleItem == 0)
                    {
                        ((MainActivity)getActivity()).showTablayout(true);
                    }
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        loadOfflineData();
        return view;
    }

    public void loadOfflineData() {
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        m_offlineData = new ArrayList<HashMap<String, String>>();

        try {
            m_offlineData = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("sadeemlight" + "news", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < m_offlineData.size(); i++)
        {
            m_newsListData.add(new ModelNews(
                    m_offlineData.get(i).get("id"),
                    m_offlineData.get(i).get("newstitle"),
                    m_offlineData.get(i).get("newsdate"),
                    m_offlineData.get(i).get("newsdetail"),
                    m_offlineData.get(i).get("totallike"),
                    m_offlineData.get(i).get("totalview"),
                    m_offlineData.get(i).get("totalcomment"),
                    m_offlineData.get(i).get("newslink"),
                    m_offlineData.get(i).get("isliked"),
                    m_offlineData.get(i).get("isviewed"),
                    m_offlineData.get(i).get("youtube_url"),
                    m_offlineData.get(i).get("news_type")));
        }

        m_adapter.updateData(m_newsListData);

        m_page = 1;
        new getNews().execute(0);
    }

    public void saveOfflineData() {
        try
        {
            settings.edit().putString("sadeemlight" + "news", ObjectSerializer.serialize(m_offlineData)).commit();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class getNews extends AsyncTask<Integer, Void, Void> {
        String response = "";
        String message = "";
        int code = 0;
        int selPage = 1;
        ArrayList<HashMap<String, String>> newMapList = new ArrayList<>();

        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            selPage = m_page;
            m_listView.setEnabled(false);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Integer... params)
        {
            int isLoadMore = params[0];

            if(isLoadMore == 0)
            {
                m_page = 1;
                selPage = 1;
            }
            else
            {
                if(m_lastPage > 0)
                {
                    if(m_page < m_lastPage)
                    {
                        selPage = m_page + 1;
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    m_page = 1;
                    selPage = 1;
                }
            }

            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.NEWS_URL + "?page=" + selPage;

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            //String jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {

                Log.d(Defines.APP_LOG_TITLE, jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");
                    code = jsonObject.getInt("Code");

                    m_lastPage = jsonObject.getJSONObject("Data").getInt("last_page");
                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("news");


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject data = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("id", data.getString("news_id"));

                        Date date = null;
                        String showdate = null;
                        String inputPattern = "yyyy-MM-dd HH:mm:ss";
                        String outputPattern = "dd-MMM";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        date = inputFormat.parse(data.getString("news_date").toString());
                        showdate = outputFormat.format(date);

                        String newslink = data.getString("news_link");
                        if(URLUtil.isValidUrl(newslink) == false)
                        {
                            newslink = "";
                        }

                        String youtubeurl = data.getString("youtube_url");
                        if(URLUtil.isValidUrl(youtubeurl) == false)
                        {
                            youtubeurl = "";
                        }

                        map.put("newsdate", showdate);
                        map.put("newstitle", data.getString("news_title"));
                        map.put("newsdetail", data.getString("news_detail"));
                        map.put("newslink", newslink);
                        map.put("totalcomment", data.getString("total_comment"));
                        map.put("totallike", data.getString("total_like"));
                        map.put("totalview", data.getString("total_view"));
                        map.put("isliked", data.getString("is_liked"));
                        map.put("isviewed", data.getString("is_viewed"));
                        map.put("youtube_url", youtubeurl);
                        map.put("news_type", data.getString("type"));

                        newMapList.add(map);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                catch (ParseException e)
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
                //pd.dismissProgress();
                m_listView.setEnabled(true);

                m_listView.stopRefresh();
                m_listView.stopLoadMore();

                if(code == 402)
                {
                    Log.d(Defines.APP_LOG_TITLE, "offlinemessage_sent");
                    Intent updates = new Intent("sadeem_notification");
                    updates.putExtra("type", "expire_alert");
                    getActivity().sendBroadcast(updates);

                    GlobalFunction.outputToast(getActivity(), message);
                    return;
                }

                if (response.contentEquals("true"))
                {
                    m_page = selPage;

                    if(m_page == 1)
                    {
                        m_newsListData.clear();
                        m_offlineData = newMapList;
                        saveOfflineData();
                    }

                    for (int i = 0; i < newMapList.size(); i++)
                    {
                        m_newsListData.add(new ModelNews(
                                newMapList.get(i).get("id"),
                                newMapList.get(i).get("newstitle"),
                                newMapList.get(i).get("newsdate"),
                                newMapList.get(i).get("newsdetail"),
                                newMapList.get(i).get("totallike"),
                                newMapList.get(i).get("totalview"),
                                newMapList.get(i).get("totalcomment"),
                                newMapList.get(i).get("newslink"),
                                newMapList.get(i).get("isliked"),
                                newMapList.get(i).get("isviewed"),
                                newMapList.get(i).get("youtube_url"),
                                newMapList.get(i).get("news_type")));
                    }

                    if(m_page == m_lastPage)
                    {
                        m_listView.setPullLoadEnable(false);
                    }
                    else
                    {
                        m_listView.setPullLoadEnable(true);
                    }

                    m_adapter.updateData(m_newsListData);
                }
            }
            catch (Exception ex){}
        }
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        ((MainActivity)getActivity()).onCreateMenu_venus(menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {

                m_adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("sadeem_notification"));

        MainActivity.notfNewCount = 0;
        ((MainActivity)getActivity()).updateNotfCounts();

        if (ConnectivityReceiver.isConnected())
        {
            m_page = 1;
            new getNews().execute(0);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("news"))
            {
                Log.i(ConstValue.APPTITLE, "newsfragments_news");
                if (ConnectivityReceiver.isConnected())
                {
                    new getNews().execute(0);
                }

                MainActivity.notfNewCount = 0;
                ((MainActivity)getActivity()).updateNotfCounts();
            }
        }
    };
}
