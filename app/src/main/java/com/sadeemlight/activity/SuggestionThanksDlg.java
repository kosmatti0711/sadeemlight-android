package com.sadeemlight.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sadeemlight.R;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.venus_model.ModelMessage;
import com.sadeemlight.venus_uis.VideoPreviewActivity;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by VENUS on 5/26/2017.
 */

public class SuggestionThanksDlg extends PopupWindow
{

    View mRootView;
    Context mParent;
    SuggestionFragment mParentFragment;

    public SuggestionThanksDlg(Fragment parent)
    {
        mParent = parent.getContext();
        mParentFragment = (SuggestionFragment)parent;
        mRootView = ((LayoutInflater)mParent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.suggestion_thanks_popup, null,true);

        setContentView(mRootView);
        //팝업의 크기 설정
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //팝업 뷰 터치 되도록
        setTouchable(true);
        //팝업 뷰 포커스도 주고
        setFocusable(true);
        //팝업 뷰 이외에도 터치되게 (터치시 팝업 닫기 위한 코드)
        setOutsideTouchable(true);
        //인자로 넘겨준 v 아래로 보여주기

        initView();
    }

    public void initView()
    {
        mRootView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuggestionThanksDlg.this.dismiss();
            }
        });
    }

    public void show(View v)
    {
        this.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
    }

}
