package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.view.video.FullscreenVideoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.activity.Add_news_comment_fragment;
import com.sadeemlight.venus_model.ModelNews;
import com.sadeemlight.activity.News_video_youtube_activity;
import com.sadeemlight.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.ImageViewActivity;

/**
 * Created by Rajesh Dabhi on 6/8/2016.
 */
public class NewsAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = NewsAdapter.class.getSimpleName();
    private ModelFilter ModelFilter;
    private List<ModelNews> allModelItemsArray = new ArrayList<>();
    private List<ModelNews> filteredModelItemsArray = new ArrayList<>();

    private Context context;
    private List<ModelNews> m_baseData = new ArrayList<>();
    private DisplayImageOptions options, options_private, option_public;

    private List<ModelParamsPair> params = new ArrayList<>();

    Session_management session_management;
    Boolean isLike;

    public static final String API_KEY = "AIzaSyB5KY1MmGiqKl82AvD_ySH46KpXPrQioL4";

    LayoutInflater mInflater;
    ImageLoader imageLoader;
    String id;

    private FragmentActivity m_parent;

    public NewsAdapter(Context context)
    {
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();

        this.context = context;
        m_parent = (FragmentActivity) context;

        try
        {

            session_management = new Session_management(context);

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .build();
            ImageLoader.getInstance().init(config);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(null)
                    .showImageForEmptyUri(null)
                    .showImageOnFail(null)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.NONE)
                    .build();

            options_private = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_menu_school)
                    .showImageForEmptyUri(R.drawable.ic_menu_school)
                    .showImageOnFail(R.drawable.ic_menu_school)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.NONE)
                    .build();

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return m_baseData.size();
    }

    @Override
    public Object getItem(int position) {
        return m_baseData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<ModelNews> data)
    {
        m_baseData = data;

        allModelItemsArray = new ArrayList<>();
        allModelItemsArray.addAll(m_baseData);

        filteredModelItemsArray = new ArrayList<>();
        filteredModelItemsArray.addAll(allModelItemsArray);

        getFilter();

        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_items, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ModelNews m = m_baseData.get(position);

        viewHolder.txtTitle.setText(m.getTitle());
        viewHolder.txtDatetime.setText(m.getDate_time());
        viewHolder.txtMsg.setText(m.getMessage());
        viewHolder.txtLike.setText(m.getLikes());
        viewHolder.txtView.setText(m.getViews());
        viewHolder.txtComment.setText(m.getComments());
        String ty = m.getNews_type();
        if (ty.contains("homework")){
            viewHolder.newsTools.setVisibility(View.GONE);
        }else if (ty.contains("Scores")){
            viewHolder.newsTools.setVisibility(View.GONE);
        }else {
            viewHolder.newsTools.setVisibility(View.VISIBLE);
        }
        // txtMsg has links specified by putting <a> tags in the string
        // resource.  By default these links will appear but not
        // respond to user input.  To make them active, you need to
        // call setMovementMethod() on the TextView object.
        viewHolder.txtMsg.setMovementMethod(LinkMovementMethod.getInstance());

        String school_image_url = session_management.getUserDetails().get(ConstValue.KEY_SCHOOL_IMAGE);

        if (m.getNews_type().contentEquals("School"))
        {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(school_image_url, viewHolder.iv_profile, options_private);
        }
        else
        {
            viewHolder.iv_profile.setImageResource(R.drawable.app_logo);
        }

        if (!m.getMsg_img_id().contentEquals("")) {
            viewHolder.iv_msg_img.setVisibility(View.VISIBLE);
            viewHolder.iv_msg_img.setTag(m);
            viewHolder.vv.setVisibility(View.GONE);
            viewHolder.iv_play.setVisibility(View.GONE);
            viewHolder.rl_video.setVisibility(View.VISIBLE);
            //viewHolder.youTubeThumbnailView.setVisibility(View.GONE);
            viewHolder.iv_share_divider.setVisibility(View.GONE);
            viewHolder.iv_share.setVisibility(View.GONE);


            // Setting all values in listview
            imageLoader.displayImage(m.getMsg_img_id(), viewHolder.iv_msg_img, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    viewHolder.iv_msg_img.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    viewHolder.iv_msg_img.setVisibility(View.GONE);
                }
            });
        }


        if (!m.getYoutube_url().contentEquals(""))
        {
            //make icon visible
            viewHolder.iv_play.setVisibility(View.VISIBLE);
            viewHolder.iv_share_divider.setVisibility(View.VISIBLE);
            viewHolder.iv_share.setVisibility(View.VISIBLE);
        }

        if (m.getMsg_img_id().contentEquals("") && m.getYoutube_url().contentEquals(""))
        {
            viewHolder.iv_msg_img.setVisibility(View.GONE);
            viewHolder.vv.setVisibility(View.GONE);
            viewHolder.iv_play.setVisibility(View.GONE);
            viewHolder.rl_video.setVisibility(View.GONE);
            //viewHolder.youTubeThumbnailView.setVisibility(View.GONE);
            viewHolder.iv_share_divider.setVisibility(View.GONE);
            viewHolder.iv_share.setVisibility(View.GONE);
        }

        viewHolder.iv_msg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (m.getYoutube_url().contentEquals("")) {
                    loadPhoto(viewHolder.iv_msg_img, 50, 50,m.getMsg_img_id());
                }
            }
        });


        if (m.getIsliked().contentEquals("true")) {
            viewHolder.iv_globlelike.setImageResource(R.drawable.ic_news_select_52);
        } else {
            viewHolder.iv_globlelike.setImageResource(R.drawable.ic_news_13);
        }

        viewHolder.rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.clear();

                final String news_id = m.getNews_id();

                String student_id = session_management.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

                params.add(new ModelParamsPair("news_id", news_id));
                params.add(new ModelParamsPair("student_id", student_id));
                params.add(new ModelParamsPair("news_type", m.getNews_type()));

                if (m.getIsliked().contentEquals("true")) {

                    if (ConnectivityReceiver.isConnected()) {
                        new insert_unlike(m).execute();
                    } else {
                        Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
                    }

                    m.setIsliked("false");

                } else {

                    if (ConnectivityReceiver.isConnected()) {
                        new insert_like(m).execute();
                    } else {
                        Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
                    }

                    m.setIsliked("true");
                }
            }
        });


        if (m.getIsviewed().contentEquals("true"))
        {
            viewHolder.iv_globleviews.setImageResource(R.drawable.ic_news_select_54);
        }
        else
        {
            viewHolder.iv_globleviews.setImageResource(R.drawable.ic_news_15);

            viewHolder.rl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    params.clear();

                    final String news_id = m.getNews_id();

                    String student_id = session_management.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

                    params.add(new ModelParamsPair("news_id", news_id));
                    params.add(new ModelParamsPair("student_id", student_id));
                    params.add(new ModelParamsPair("news_type", m.getNews_type()));

                    if (ConnectivityReceiver.isConnected()) {
                        new insert_view(m).execute();
                    } else {
                        Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        viewHolder.rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String news_id = m.getNews_id();

                Bundle args;
                Fragment fm = new Add_news_comment_fragment();
                args = new Bundle();

                args.putString("news_id", news_id);
                args.putString("news_title", m.getTitle());
                args.putString("news_detail", m.getMessage());
                args.putString("news_type", m.getNews_type());

                fm.setArguments(args);
                FragmentManager fmf = m_parent.getSupportFragmentManager();
                fmf.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

            }
        });


        /*final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {

                if (!m.getYoutube_url().contentEquals("")) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    viewHolder.iv_play.setVisibility(View.VISIBLE);
                } else {
                    youTubeThumbnailView.setVisibility(View.GONE);
                    viewHolder.iv_play.setVisibility(View.GONE);
                }
                //convertView.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        viewHolder.youTubeThumbnailView.initialize(API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                String yourl = m.getYoutube_url().toString();

                String id = yourl.replace("https://www.youtube.com/watch?v=", "");

                youTubeThumbnailLoader.setVideo(id);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //write something for failure
            }
        });*/

        /*youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yourl = m.getYoutube_url().toString();

                String id = yourl.replace("https://www.youtube.com/watch?v=", "");

                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, API_KEY, id);
                context.startActivity(intent);
            }
        });*/

        viewHolder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = m.getYoutube_url();

                //getTaskurl = "";
                //if youtube video
                if (link.contains("youtube")) {
                    //viewHolder.vv.setVisibility(View.GONE);
                    //viewHolder.youTubeThumbnailView.setVisibility(View.VISIBLE);
                    final String youTUBE_URL = link.replace("https://www.youtube.com/watch?v=", "");

                    /*viewHolder.youTubeView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                            if (!wasRestored) {

//                                String id = youTUBE_URL.replace("https://www.youtube.com/watch?v=","");

                                youTubePlayer.cueVideo(youTUBE_URL); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                            if (errorReason.isUserRecoverableError()) {
                                Log.e(TAG, "onInitializationFailure: user recoverable error");
//                                errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
                            } else {
                                String error = String.format("error: ", errorReason.toString());
//                                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });*/

                    /*Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, API_KEY, youTUBE_URL);
                    context.startActivity(intent);*/

                    Intent startplay = new Intent(context, News_video_youtube_activity.class);

                    startplay.putExtra("video_link", link);
                    startplay.putExtra("total_like", m.getLikes());
                    startplay.putExtra("total_views", m.getViews());
                    startplay.putExtra("total_comment", m.getComments());
                    startplay.putExtra("is_like", m.getIsliked());
                    startplay.putExtra("is_view", m.getIsviewed());
                    startplay.putExtra("news_id", m.getNews_id());
                    startplay.putExtra("news_type", m.getNews_type());
                    startplay.putExtra("news_title", m.getTitle());
                    startplay.putExtra("news_detail", m.getMessage());

                    context.startActivity(startplay);

                } else {
                    viewHolder.iv_msg_img.setVisibility(View.GONE);
                    viewHolder.iv_play.setVisibility(View.GONE);
                    viewHolder.vv.setVisibility(View.VISIBLE);
                    //viewHolder.youTubeThumbnailView.setVisibility(View.GONE);

                    /*MediaController mediaController = new MediaController(context);
                    mediaController.setAnchorView(viewHolder.vv);
                    Uri video = Uri.parse(link);
                    viewHolder.vv.setMediaController(mediaController);
                    viewHolder.vv.setVideoURI(video);
                    viewHolder.vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            //pd.dismissProgress();
                            viewHolder.vv.start();
                        }
                    });
                    viewHolder.vv.requestFocus();*/

                    Uri video = Uri.parse(link);

                    try {
                        viewHolder.vv.setVideoURI(video);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        viewHolder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getShareurl = m.getYoutube_url();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am watching this video ." + getShareurl);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });

        return convertView;
    }
    //FullscreenVideoView vv;
    class ViewHolder {

        //YouTubePlayerView youTubeView;
        TextView txtTitle;
        TextView txtDatetime;
        TextView txtMsg;
        TextView txtLike;
        TextView txtView;
        TextView txtComment;
        ImageView iv_profile;
        ImageView iv_msg_img;
        ImageView iv_share;
        ImageView iv_share_divider;

        RelativeLayout rl_like;
        RelativeLayout rl_comment;
        RelativeLayout rl_view;

        //VideoView vv;
        FullscreenVideoView vv;
        RelativeLayout rl_video;
        RelativeLayout newsTools;
        ImageView iv_play;
        ImageView iv_globlelike, iv_globleviews;

        //YouTubeThumbnailView youTubeThumbnailView;


        public ViewHolder(View convertView) {

            txtTitle = (TextView) convertView.findViewById(R.id.tv_title);
            txtDatetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            txtMsg = (TextView) convertView.findViewById(R.id.tv_message);
            txtLike = (TextView) convertView.findViewById(R.id.tv_likes);

            txtView = (TextView) convertView.findViewById(R.id.tv_views);
            txtComment = (TextView) convertView.findViewById(R.id.tv_comments);
            iv_profile = (ImageView) convertView.findViewById(R.id.iv_profile);
            iv_msg_img = (ImageView) convertView.findViewById(R.id.iv_message_img);
            //youTubeThumbnailView = (YouTubeThumbnailView) convertView.findViewById(R.id.youtube_thumbnail);
            rl_video = (RelativeLayout) convertView.findViewById(R.id.rl_news_img_video);
            newsTools = (RelativeLayout) convertView.findViewById(R.id.newsTools);
            //vv = (VideoView) convertView.findViewById(R.id.videoView2);
            vv = (FullscreenVideoView) convertView.findViewById(R.id.videoView2);
            iv_play = (ImageView) convertView.findViewById(R.id.iv_news_play);
            iv_share = (ImageView) convertView.findViewById(R.id.iv_news_share);
            iv_share_divider = (ImageView) convertView.findViewById(R.id.iv_news_divider);

            rl_like = (RelativeLayout) convertView.findViewById(R.id.rl_news_like);
            rl_comment = (RelativeLayout) convertView.findViewById(R.id.rl_news_comment);
            rl_view = (RelativeLayout) convertView.findViewById(R.id.rl_news_views);


            iv_globlelike = (ImageView) convertView.findViewById(R.id.iv_news_list_likes);
            iv_globleviews = (ImageView) convertView.findViewById(R.id.iv_news_list_views);

            //youTubeView = (YouTubePlayerView) convertView.findViewById(R.id.youtube_view);

        }

    }

    public class insert_like extends AsyncTask<Void, Void, Void> {

        String get_total_likes = "";
        String response="";
        String message="";
        private ModelNews model;

        public insert_like(ModelNews model) {
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_likes = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String newType = model.getNews_type();

            String url;
            if(newType.contentEquals("school_news"))
            {
                url = ConstValue.BASE_URL_NEW + "school/news/" + model.getNews_id() + "/like";
            }
            else
            {
                url = ConstValue.BASE_URL_NEW + "news/" + model.getNews_id() + "/like";
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

                    JSONObject jsonData = jsonObject.getJSONObject("Data");

                    get_total_likes = jsonData.getString("TotalLikes").toString();
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

            if (response.contentEquals("true"))
            {
                isLike = true;
                model.setLikes(get_total_likes);
                model.setIsliked("true");
                notifyDataSetChanged();
            }
        }
    }

    public class insert_unlike extends AsyncTask<Void, Void, Void>
    {
        String get_total_likes = "";
        String response;
        String message;
        private ModelNews model;

        public insert_unlike(ModelNews model) {
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_likes = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String newType = model.getNews_type();

            String url;
            if(newType.contentEquals("school_news"))
            {
                url = ConstValue.BASE_URL_NEW + "school/news/" + model.getNews_id() + "/unlike";
            }
            else
            {
                url = ConstValue.BASE_URL_NEW + "news/" + model.getNews_id() + "/unlike";
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

                    JSONObject jsonData = jsonObject.getJSONObject("Data");

                    get_total_likes = jsonData.getString("TotalLikes").toString();
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

            if (response.contentEquals("true"))
            {
                isLike = false;
                model.setLikes(get_total_likes);
                model.setIsliked("false");
                notifyDataSetChanged();
            }
        }
    }

    public class insert_view extends AsyncTask<Void, Void, Void> {

        String get_total_view="";
        String response ="";
        String message ="";
        private ModelNews model;

        public insert_view(ModelNews model) {
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";
            get_total_view = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = session_management.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String newType = model.getNews_type();

            String url;
            if(newType.contentEquals("school_news"))
            {
                url = ConstValue.BASE_URL_NEW + "school/news/" + model.getNews_id() + "/view";
            }
            else
            {
                url = ConstValue.BASE_URL_NEW + "news/" + model.getNews_id() + "/view";
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

                    JSONObject jsonData = jsonObject.getJSONObject("Data");

                    get_total_view = jsonData.getString("TotalLikes").toString();
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

            if (response.contentEquals("true"))
            {
                model.setViews(get_total_view);
                model.setIsviewed("true");
                notifyDataSetChanged();
            }
        }
    }


    @Override
    public Filter getFilter()
    {
        if (ModelFilter == null)
        {
            ModelFilter = new ModelFilter();
        }

        return ModelFilter;
    }

    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ModelNews> filteredItems = new ArrayList<ModelNews>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    ModelNews m = allModelItemsArray.get(i);
                    if (m.getTitle().toLowerCase().contains(constraint))
                        filteredItems.add(m);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

            filteredModelItemsArray = (ArrayList<ModelNews>) results.values;
            notifyDataSetChanged();
            m_baseData.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                m_baseData.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }

    private void loadPhoto(ImageView imageView, int width, int height,String imageUrl) {

        if (imageView.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ImageViewActivity.showImageByBitmap(context, bitmap);
        }else if (imageUrl != null){
            ImageViewActivity.showImageByUrl(context,imageUrl);
        }
    }

}
