package com.sadeemlight.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.LibraryDetailsAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelBook;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class LibraryDetailFragment extends Fragment{

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> m_offlineData = new ArrayList<>();

    public String m_subjectID;
    // store m_offlineData
    SharedPreferences settings;
    GridView mListView;
    SwipeRefreshLayout swipeRefreshLayout;
    LibraryDetailsAdapter m_adapter;

    public Session_management sessionManagement;

    public LibraryDetailFragment()
    {
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
        View view = inflater.inflate(R.layout.library_activity, container, false);
        setHasOptionsMenu(true);

        m_subjectID = getArguments().getString("subject_id");
        sessionManagement = new Session_management(getActivity());

        initView(view);
        loadOfflineData();

        new getBook().execute();

        return view;
    }

    public void initView(View view)
    {
        ((MainActivity)getActivity()).setTitle(R.string.side_menu_library);
        ((MainActivity)getActivity()).setTabindicatorfm();

        mListView = (GridView) view.findViewById(R.id.list_library);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        m_adapter = new LibraryDetailsAdapter(getContext(), this);
        mListView.setAdapter(m_adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectivityReceiver.isConnected())
                {
                    new getBook().execute();
                }
            }
        });
    }

    public void loadOfflineData()
    {
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);

        try
        {
            m_offlineData = (ArrayList<HashMap<String, String>>)
                    ObjectSerializer.deserialize(settings.getString("sadeemlight" + "libaraybooks" + m_subjectID, ""));
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }

        if(m_offlineData == null)
        {
            m_offlineData = new ArrayList<HashMap<String, String>>();
        }

        updateList();
    }

    public void saveOfflineData()
    {
        try {
            settings.edit().putString("sadeemlight" + "libaraybooks" + m_subjectID, ObjectSerializer.serialize(m_offlineData)).commit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class getBook extends AsyncTask<Void, Void, Void>
    {

        Progress_dialog pd = new Progress_dialog(getActivity());
        String response = "";
        String message = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            ServiceHandler sh = new ServiceHandler();

            String url = "";
            if(m_subjectID.equals("public"))
            {
                url = ConstValue.LIBRARY_PUBLIC_URL;
            }
            else
            {
                url =  ConstValue.LIBRARY_PRIVATE_URL + m_subjectID;
            }

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                Log.d(ConstValue.APPTITLE, jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Books");

                    m_offlineData = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("book_id", c.getString("book_id"));
                        map.put("book_date", c.getString("book_date"));
                        map.put("book_name", c.getString("book_name"));
                        map.put("cover", c.getString("cover"));
                        map.put("readers", c.getString("readers"));

                        m_offlineData.add(map);
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pd.dismissProgress();
            swipeRefreshLayout.setRefreshing(false);

            pd.dismissProgress();

            if (response == "true")
            {
                saveOfflineData();
                updateList();
            }
            else
            {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateList()
    {
        List<ModelBook> listData = new ArrayList<>();

        for (int i = 0; i < m_offlineData.size(); i++)
        {

            ModelBook data = new ModelBook();

            data.book_id = m_offlineData.get(i).get("book_id");
            data.book_date = m_offlineData.get(i).get("book_date");
            data.book_name = m_offlineData.get(i).get("book_name");
            data.cover = m_offlineData.get(i).get("cover");
            data.readers = GlobalFunction.tryParseInt(m_offlineData.get(i).get("readers"));

            listData.add(data);
        }

        m_adapter.updateData(listData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);

        ((MainActivity)getActivity()).onCreateMenu_venus(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //m_adapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

}
