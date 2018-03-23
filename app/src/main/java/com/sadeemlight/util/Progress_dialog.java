package com.sadeemlight.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sadeemlight.R;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 16/8/2016.
 */
public class Progress_dialog {

    ProgressBar progressBar;
    ProgressDialog pd;
    RelativeLayout layout;

    public Progress_dialog(Context context){
        pd = new ProgressDialog(context);

        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary)/*0xFFFF0000*/,android.graphics.PorterDuff.Mode.MULTIPLY);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(context);

        rl.setGravity(Gravity.CENTER);
        rl.addView(progressBar);

        layout.addView(rl, params);

        dismissProgress();
    }

    public Progress_dialog(Context context, int style){
        pd = new ProgressDialog(context);

        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

        progressBar = new ProgressBar(context, null, style);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary)/*0xFFFF0000*/,android.graphics.PorterDuff.Mode.MULTIPLY);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Log.d(Defines.APP_LOG_TITLE, "dp-" + GlobalFunction.dpToPixel(150));


        params.topMargin = GlobalFunction.dpToPixel(150);

        RelativeLayout rl = new RelativeLayout(context);

        rl.setGravity(Gravity.CENTER_HORIZONTAL);
        rl.addView(progressBar, params);

        params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.addView(rl, params);

        dismissProgress();
    }

    public void showProgressbar(){
        /*pd.setMessage("Please Wait...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();*/

        progressBar.setVisibility(View.VISIBLE);
    }

    public void dismissProgress(){
        /*if(pd.isShowing())
            pd.dismiss();*/

        progressBar.setVisibility(View.INVISIBLE);
    }

    /*public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void dismissProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }*/
}
