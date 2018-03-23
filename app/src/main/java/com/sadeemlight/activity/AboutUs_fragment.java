package com.sadeemlight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.sadeemlight.R;

import com.sadeemlight.util.LocaleHelper;

public class AboutUs_fragment extends Fragment {

    WebView webView;

    public AboutUs_fragment() {
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

        ((MainActivity) getActivity()).setTitle(R.string.side_menu_about);

        webView = (WebView) view.findViewById(R.id.webView);

        String getlang = LocaleHelper.getLanguage(getActivity());

        webView.getSettings().setJavaScriptEnabled(true);
        //webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //"file:///android_asset/sample.html"
        if(getlang.contentEquals("en")){
            webView.loadUrl("file:///android_asset/en.htm");
        }else if(getlang.contentEquals("ar")){
            webView.loadUrl("file:///android_asset/ar.htm");
        }

        return view;
    }
}
