package com.sadeemlight.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sadeemlight.R;

/**
 * Created by VENUS on 5/26/2017.
 */

public class ChatAttatchChooseMedia implements BaseFragment.OnSelectMediaListener
{
    public static final int ATTACH_MEDIA_IMAGE = 0;
    public static final int ATTACH_MEDIA_VIDEO = 1;
    public static final int ATTACH_MEDIA_MUSIC = 2;

    View mRootView;
    Context mParent;
    ChatActivity mParentFragment;
    public ChatAttatchChooseMedia(Fragment parent)
    {
        mParent = parent.getContext();
        mParentFragment = (ChatActivity)parent;
        mRootView = mParentFragment.mRootView.findViewById(R.id.layout_attach);

        initView();
    }

    public void initView()
    {
        mParentFragment.setOnSelectMediaListener(this);
        mRootView.findViewById(R.id.layout_attachpicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentFragment.selectImage();
            }
        });

        mRootView.findViewById(R.id.layout_attachvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentFragment.selectVideo();
            }
        });

        mRootView.findViewById(R.id.layout_attachmusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentFragment.selectAudio();
            }
        });

        dismiss();
    }


    @Override
    public void onSelectImage_File(final String file_path) {
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                dismiss();
                mParentFragment.mAttachViewMedia.show();
                mParentFragment.mAttachViewMedia.onViewMedia(file_path, ChatAttatchChooseMedia.ATTACH_MEDIA_IMAGE);
            }
        };
        handler.sendEmptyMessageDelayed(0,100);
    }

    @Override
    public void onSelectImage_Bitmap(Bitmap image)
    {
        this.dismiss();
    }

    @Override
    public void onSelectVideo_File(final String file_path) {
        mParentFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
                mParentFragment.mAttachViewMedia.show();
                mParentFragment.mAttachViewMedia.onViewMedia(file_path, ChatAttatchChooseMedia.ATTACH_MEDIA_VIDEO);
            }
        });

    }

    @Override
    public void onSelectAudio_File(final String file_path) {

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                dismiss();
                mParentFragment.mAttachViewMedia.show();
                mParentFragment.mAttachViewMedia.onViewMedia(file_path, ChatAttatchChooseMedia.ATTACH_MEDIA_MUSIC);
            }
        };
        handler.sendEmptyMessageDelayed(0,100);
    }

    public void dismiss()
    {
        mRootView.setVisibility(View.GONE);
    }

    public void show() {
        if(mRootView.getVisibility() == View.VISIBLE)
        {
            dismiss();
        }
        else
        {
            mRootView.setVisibility(View.VISIBLE);
        }
    }
}
