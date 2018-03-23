package com.sadeemlight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sadeemlight.R;

/**
 * Created by Rajesh Dabhi on 5/10/2016.
 */
public class Visit_us_fragment extends Fragment {

    WebView webView;

    public Visit_us_fragment() {
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
        View view = inflater.inflate(R.layout.about_us, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.activity_visit);
        ((MainActivity) getActivity()).setTabindicatorfm();

        webView = (WebView) view.findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        try {
            String getvisitlink = getArguments().getString("link");

            webView.loadUrl(getvisitlink);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }
}
