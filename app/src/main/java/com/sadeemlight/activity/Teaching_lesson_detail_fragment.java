package com.sadeemlight.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.rtoshiro.view.video.FullscreenVideoView;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.sadeemlight.R;

import java.io.IOException;

/**
 * Created by Rajesh Dabhi on 7/9/2016.
 */
public class Teaching_lesson_detail_fragment extends /*YouTubePlayerSupportFragment*/ Fragment {

    public static final String API_KEY = "AIzaSyB5KY1MmGiqKl82AvD_ySH46KpXPrQioL4";

    TextView title;
    WebView detail;
    //VideoView vv_teach;
    FullscreenVideoView vv_teach;

    YouTubePlayerSupportFragment youTubePlayerFragment;

    public Teaching_lesson_detail_fragment() {
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
        View view = inflater.inflate(R.layout.teaching_lesson_detail, container, false);

        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        ((MainActivity) getActivity()).setTitle(R.string.tab_teaching);

        title = (TextView) view.findViewById(R.id.tv_teaching_detail_title);
        //vv_teach = (VideoView) view.findViewById(R.id.vv_teaching);
        vv_teach = (FullscreenVideoView) view.findViewById(R.id.vv_teaching);
        detail = (WebView) view.findViewById(R.id.wv_teaching_detail);

        try {
            String gettitle = getArguments().getString("lesson_name");
            String getdetail = getArguments().getString("lesson_text");
            final String getVideo_link = getArguments().getString("video_link");

            title.setText(gettitle);

            detail.getSettings().setJavaScriptEnabled(true);
            detail.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            detail.setVisibility(View.GONE);

            if (!getdetail.contentEquals("")) {
                vv_teach.setVisibility(View.GONE);
                detail.setVisibility(View.VISIBLE);

                String getExtension = getdetail.substring(getdetail.lastIndexOf("."));

                Log.e("extension: ", getExtension);

                if (getExtension.contentEquals(".pdf") || getExtension.contentEquals(".pptx")) {
                    detail.loadUrl("http://docs.google.com/gview?embedded=true&url=" + getdetail);
                }
            }

            if (!getVideo_link.contentEquals(""))
            {
                //detail.loadUrl(getdetail);
                if (getVideo_link.contains("youtube"))
                {
                    vv_teach.setVisibility(View.GONE);

                    //final String link = getVideo_link.replace("https://www.youtube.com/watch?v=", "");

                    /*FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                    transaction.add(R.id.youtube_view, youTubePlayerFragment).commit();

                    youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                            if (!wasRestored) {
                                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                                player.loadVideo(link);
                                player.play();
                            }
                            *//*if (wasRestored) {
                                player.play();
                            } else {
                                player.loadVideo(link);
                            }*//*
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                            // YouTube error
                            String errorMessage = error.toString();
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                            Log.d("errorMessage:", errorMessage);
                        }
                    });*/

                    Intent startplay = new Intent(getActivity(), News_video_youtube_activity.class);
                    startplay.putExtra("video_link",getVideo_link);
                    startActivity(startplay);

                }
                else
                {
                    vv_teach.setVisibility(View.VISIBLE);
                    try
                    {
                        Uri video = Uri.parse(getVideo_link);
                        vv_teach.setVideoURI(video);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;
    }
}
