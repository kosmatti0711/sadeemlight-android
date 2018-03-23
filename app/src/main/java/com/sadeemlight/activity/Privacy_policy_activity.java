package com.sadeemlight.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.sadeemlight.R;

import com.sadeemlight.util.LocaleHelper;

/**
 * Created by Rajesh Dabhi on 24/9/2016.
 */
public class Privacy_policy_activity extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_activity);

        webView = (WebView) findViewById(R.id.wv_privacy);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        String getlang = LocaleHelper.getLanguage(this);

        if(getlang.contentEquals("en")) {
            webView.loadUrl("http://engprivate.alsadeem-systems.com");
        }else{
            webView.loadUrl("http://private.alsadeem-systems.com");
        }

    }
}
