package com.sadeemlight.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 24/9/2016.
 */
public class News_video_youtube_activity extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener,
        View.OnClickListener {

    private static final int RECOVERY_REQUEST = 1;
    //private YouTubePlayerView youTubeView;
    YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    public String youTUBE_URL = "";

    //Button share;

    TextView tv_like, tv_comment, tv_view, tv_title;
    ImageView iv_share, iv_like, iv_view;
    RelativeLayout rl_like, rl_view, rl_comment;
    LinearLayout ll_youtube;

    private List<ModelParamsPair> params = new ArrayList<>();
    JSONArray jsonArray = null;
    String response;
    String get_total_likes, get_total_comment, get_total_view;

    Session_management session_management;

    String getis_view;
    String getis_like;
    String getnews_id="";
    String getnews_title;
    String getnews_detail;
    String getnews_type;

    boolean islike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_video_youtube);

        session_management = new Session_management(this);

        //youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

        tv_like = (TextView) findViewById(R.id.tv_youtube_likes);
        tv_comment = (TextView) findViewById(R.id.tv_youtube_comments);
        tv_view = (TextView) findViewById(R.id.tv_youtube_views);
        tv_title = (TextView) findViewById(R.id.tv_youtube_title);
        iv_share = (ImageView) findViewById(R.id.iv_youtube_share);
        iv_like = (ImageView) findViewById(R.id.iv_youtube_likes);
        iv_view = (ImageView) findViewById(R.id.iv_youtube_views);
        rl_comment = (RelativeLayout) findViewById(R.id.rl_youtube_comment);
        rl_like = (RelativeLayout) findViewById(R.id.rl_youtube_like);
        rl_view = (RelativeLayout) findViewById(R.id.rl_youtube_views);
        ll_youtube = (LinearLayout) findViewById(R.id.ll_youtube);

        youTubePlayerSupportFragment.initialize(ConstValue.API_KEY, this);

        try {
            youTUBE_URL = getIntent().getStringExtra("video_link");
            String gettotal_likes = getIntent().getStringExtra("total_like");
            String gettotal_views = getIntent().getStringExtra("total_views");
            String gettotal_comments = getIntent().getStringExtra("total_comment");
            getis_view = getIntent().getStringExtra("is_view");
            getis_like = getIntent().getStringExtra("is_like");
            getnews_id = getIntent().getStringExtra("news_id");
            getnews_title = getIntent().getStringExtra("news_title");
            getnews_detail = getIntent().getStringExtra("news_detail");
            getnews_type = getIntent().getStringExtra("news_type");


            Log.e("youtube_url:", "" + youTUBE_URL);

            if (getnews_id == null) {
                Log.e("empty:","true");

                tv_title.setVisibility(View.GONE);
                ll_youtube.setVisibility(View.GONE);
            } else {
                Log.e("empty:","false");
                tv_title.setVisibility(View.VISIBLE);
                ll_youtube.setVisibility(View.VISIBLE);

                tv_like.setText(gettotal_likes);
                tv_view.setText(gettotal_views);
                tv_comment.setText(gettotal_comments);
                tv_title.setText(getnews_title);

                if (getis_like.contentEquals("true")) {
                    islike = true;
                    iv_like.setImageResource(R.drawable.ic_news_select_52);
                } else {
                    islike = false;
                    iv_like.setImageResource(R.drawable.ic_news_13);
                }

                if (getis_view.contentEquals("true")) {
                    iv_view.setImageResource(R.drawable.ic_news_select_54);
                } else {
                    iv_view.setImageResource(R.drawable.ic_news_15);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        rl_like.setOnClickListener(this);
        rl_view.setOnClickListener(this);
        rl_comment.setOnClickListener(this);
        iv_share.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        String student_id = session_management.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

        int id = view.getId();

        if (id == R.id.iv_youtube_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am watching this video ." + youTUBE_URL);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.rl_youtube_like) {
            if (ConnectivityReceiver.isConnected()) {
                params.clear();

                params.add(new ModelParamsPair("news_id", getnews_id));
                params.add(new ModelParamsPair("student_id", student_id));
                params.add(new ModelParamsPair("news_type", getnews_type));

                if (islike) {
                    new insert_unlike().execute();
                } else {
                    new insert_like().execute();
                }
            }
        } else if (id == R.id.rl_youtube_views) {

            if (ConnectivityReceiver.isConnected()) {
                params.clear();

                params.add(new ModelParamsPair("news_id", getnews_id));
                params.add(new ModelParamsPair("student_id", student_id));
                params.add(new ModelParamsPair("news_type", getnews_type));

                new insert_view().execute();
            }

        } else if (id == R.id.rl_youtube_comment) {

            Intent comment = new Intent(News_video_youtube_activity.this, MainActivity.class);
            comment.putExtra("news_id", getnews_id);
            comment.putExtra("news_title", getnews_title);
            comment.putExtra("news_detail", getnews_detail);
            comment.putExtra("news_type", getnews_type);
            startActivity(comment);

        }

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            String id = youTUBE_URL.replace("https://www.youtube.com/watch?v=", "");

            player.cueVideo(id); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("error: ", errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery actionyoutube_view
            getYouTubePlayerProvider().initialize(ConstValue.API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubePlayerSupportFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
    }

    public class insert_like extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_likes = "";

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.NEWS_LIKE_URL, ConstValue.POST, params);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.NEWS_LIKE_URL+getnews_id+"/like",ConstValue.GET,null,session_management
                .getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("adapterresponse: ", jsonSTR);

            if (jsonSTR != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("TotalLikes");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        //HashMap<String, String> map = new HashMap<String, String>();

                        //map.put("totallike", c.getString("total_like"));

                        get_total_likes = c.getString("total_like").toString();

                        //m_baseDataMap.add(map);
                        //Log.e("postresponse ","true1"+response+","+get_total_likes);
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

            if (response.contentEquals("true")) {

                Log.e("postresponse ", "true");

                islike = true;
                iv_like.setImageResource(R.drawable.ic_news_select_52);
                tv_like.setText(get_total_likes);
            }
        }
    }

    public class insert_unlike extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_likes = "";

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.NEWS_UNLIKE_URL, ConstValue.POST, params);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.NEWS_UNLIKE_URL + getnews_id + "/unlike",ConstValue.GET,
                        null,session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("adapterresponse: ", jsonSTR);

            if (jsonSTR != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = dataObject.getJSONArray("TotalLikes");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        //HashMap<String, String> map = new HashMap<String, String>();

                        //map.put("totallike", c.getString("total_like"));

                        get_total_likes = c.getString("total_like").toString();

                        //m_baseDataMap.add(map);
                        //Log.e("postresponse ","true1"+response+","+get_total_likes);
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

            if (response.contentEquals("true")) {

                Log.e("postresponse ", "true");

                islike = false;
                iv_like.setImageResource(R.drawable.ic_news_13);
                tv_like.setText(get_total_likes);
            }
        }
    }

    public class insert_view extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_view = "";

            ServiceHandler sh = new ServiceHandler();

            //String jsonSTR = sh.makeServiceCall(ConstValue.NEWS_VIEW_URL, ConstValue.POST, params);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.NEWS_VIEW_URL+getnews_id+"/view",ConstValue.POST,null,
                        session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("adapterresponse: ", jsonSTR);

            if (jsonSTR != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");

                    jsonArray = jsonObject.getJSONArray("TotalView");

                    response = jsonObject.getString("Status");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        get_total_view = c.getString("total_view").toString();

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

            if (response.contentEquals("true")) {

                Log.e("postresponse ", "true2"/* + getdata*/);

                tv_view.setText(get_total_view);
            }
        }
    }

}
