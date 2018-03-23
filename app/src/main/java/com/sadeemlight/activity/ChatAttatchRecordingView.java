package com.sadeemlight.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sadeemlight.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import com.sadeemlight.venus_uis.AudioRecorder2Mp3Util;

/**
 * Created by VENUS on 5/26/2017.
 */

public class ChatAttatchRecordingView
{
    public static final int ATTACH_MEDIA_IMAGE = 0;
    public static final int ATTACH_MEDIA_VIDEO = 1;
    public static final int ATTACH_MEDIA_MUSIC = 2;

    public interface OnSendRecordFile
    {
        public void onSendRecordFile(String file_path);
    }

    View mRootView;
    View mLayoutView;
    Context mParent;
    ChatActivity mParentFragment;
    TextView recordTime;
    AudioRecorder2Mp3Util audioRecoder;
    CircleImageView mCircle;

    OnSendRecordFile m_sendListener;
    Activity activity;


    Animation circle_animator1;
    Animation circle_animator2;
    Animation trans_animator1;
    Animation trans_animator2;

    public ChatAttatchRecordingView(Fragment parent, View view,Activity activity)
    {
        mParent = parent.getContext();
        mParentFragment = (ChatActivity)parent;
        mRootView = view;
        this.activity = activity;

        initView();
    }

    public void setOnSendListener(OnSendRecordFile listener)
    {
        m_sendListener = listener;
    }

    public void initView()
    {
        mLayoutView = mRootView.findViewById(R.id.layout_recording2);
        recordTime = (TextView)mRootView.findViewById(R.id.text_recordtime);
        mLayoutView.setVisibility(View.INVISIBLE);
        mCircle = (CircleImageView) mRootView.findViewById(R.id.image_scale1);

        mCircle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show();
                return false;
            }
        });

        circle_animator1 = AnimationUtils.loadAnimation(mParent, R.anim.scale_expand2_center);
        trans_animator1 = AnimationUtils.loadAnimation(mParent, R.anim.translate_rtl);
        circle_animator2= AnimationUtils.loadAnimation(mParent, R.anim.scale_expand2_center_reverse);
        trans_animator2 = AnimationUtils.loadAnimation(mParent, R.anim.translate_ltr);




        ready();
    }

    public void show()
    {
        recordTime.setText("00:00");
        mLayoutView.setVisibility(View.VISIBLE);

        circle_animator1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCircle.setImageDrawable(new ColorDrawable(mParent.getResources().getColor(R.color.color_blue)));
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        circle_animator1.setFillAfter(true);
        mCircle.startAnimation(circle_animator1);
        mLayoutView.startAnimation(trans_animator1);

        ((MainActivity)mParent).registerMyOnTouchListener(recordTouchListenter);
        start();
    }


    public void hide(boolean isSend)
    {
        ((MainActivity)mParent).unregisterMyOnTouchListener(recordTouchListenter);
        circle_animator2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircle.setImageDrawable(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        trans_animator2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCircle.startAnimation(circle_animator2);
        mLayoutView.startAnimation(trans_animator2);

        if(m_timer != null)
        {
            m_timer.cancel();
        }

        stop();

        if(isSend == true && m_sendListener != null)
        {
            m_sendListener.onSendRecordFile(getMp3FilePath());
        }
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = mParent.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    Timer m_timer;
    int count = 0;
    public void start()
    {
        if(m_timer != null)
        {
            m_timer.cancel();
        }

        m_timer = new Timer();
        count = 0;
        m_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                mParentFragment.getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        count++;
                        String text = String.format("%02d:%02d", count/60, count%60);
                        recordTime.setText(text);
                    }
                });
            }
        }, 1000, 1000);

        ready();

        audioRecoder.cleanFile(AudioRecorder2Mp3Util.MP3 | AudioRecorder2Mp3Util.RAW);
        audioRecoder.startRecording();
    }

    public void ready()
    {
        if(audioRecoder != null)
        {
            stop();
        }
        if (audioRecoder == null)
        {
            audioRecoder = new AudioRecorder2Mp3Util(activity, getFilename() + ".raw", getFilename() + ".mp3");
        }

    }

    public String getMp3FilePath()
    {
        String file_path = getFilename() + ".mp3";
        return file_path;
    }
    private String getFilename()
    {
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/sadeemlight/");

        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.d("App", "failed to create directory");
            }
        }

        File file = new File(dir.getAbsolutePath(), "voice_recorder");

        return file.getAbsolutePath();
    }

    public void stop()
    {
        if(audioRecoder != null)
        {
            audioRecoder.stopRecordingAndConvertFile();
            audioRecoder.cleanFile(AudioRecorder2Mp3Util.RAW);
            audioRecoder.close();
            audioRecoder = null;
        }
    }

    MainActivity.MyOnTouchListener recordTouchListenter = new MainActivity.MyOnTouchListener() {
        @Override
        public void onTouch(MotionEvent ev) {

            if(ev.getAction() == MotionEvent.ACTION_UP)
            {
                hide(true);
            }
            else if(ev.getAction() == MotionEvent.ACTION_MOVE)
            {
                float x = ev.getX();
                float y = ev.getY();
                String msg = "터치를 입력받음 : " +x+" / " +y + "";

                if(isViewContains(mCircle, (int)x, (int)y))
                {
                    msg = msg + "in";
                }
                else
                {
                    msg = msg + "out";
                    hide(false);
                }

                //GlobalFunction.outputToast(mParent, msg);
            }
        }
    };

    private boolean isViewContains(View view, int rx, int ry) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return false;
        }
        return true;
    }
}
