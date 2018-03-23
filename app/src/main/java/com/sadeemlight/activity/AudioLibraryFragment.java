package com.sadeemlight.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sadeemlight.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.AudioLessonListAdapter;
import com.sadeemlight.adapter.AudioSubjectListAdapter;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelAudioLesson;
import com.sadeemlight.venus_model.ModelAudioSubject;

/**
 * Created by Rajesh Dabhi on 2/8/2016.
 */
public class AudioLibraryFragment extends Fragment
{
    public interface OnChooseSubjectListener
    {
        public void onChooseSubject(ModelAudioSubject subject);
    }

    public interface OnChooseLessonListener
    {
        public void onChooseLesson(ModelAudioLesson lesson);
    }

    // arraylist list variable for store data;
    ArrayList<HashMap<String, String>> mSubjectOfflineData = new ArrayList<>();


    // store m_offlineData
    public SharedPreferences m_settings;

    ModelAudioSubject m_selSubject;

    AudioSubjectListAdapter m_subjectAdapter;
    List<ModelAudioSubject> m_subjectList = new ArrayList<>();
    GridView mSubjectGridView;

    View mLessonRootView;
    AudioLessonListAdapter m_lessonAdapter;
    List<ModelAudioLesson> m_lessonList = new ArrayList<>();
    ListView mLessonListView;

    private SwipeRefreshLayout swipeRefreshLayout;

    Session_management sessionManagement;

    public AudioLibraryFragment() {
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
        View rootView = inflater.inflate(R.layout.audiolibrary_activity, container, false);

        sessionManagement = new Session_management(getActivity());

        initSubjectView(rootView);
        initLessonView(rootView);
        loadOfflineSubjectData();

        if (ConnectivityReceiver.isConnected())
        {
            new AudioLibraryFragment.getAudioSubject().execute();
        }

        return rootView;
    }


    @Override
    public void onPause()
    {
        super.onPause();
        releasePlayer();
    }

    public void initSubjectView(View rootView)
    {
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    if( ((MainActivity)getActivity()).mShowStudentLayout.isVisible())
                    {
                        ((MainActivity)getActivity()).mShowStudentLayout.hideStudentList();
                    }
                    else
                    {
                        if(mLessonRootView.getVisibility() == View.VISIBLE)
                        {
                            mLessonRootView.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Fragment fm = new NewsFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fm).commit();
                        }
                    }

                    return true;
                }
                return false;
            }
        });

        ((MainActivity)getActivity()).setTitle(R.string.side_menu_voice);
        ((MainActivity)getActivity()).setTabindicatorfm();

        mSubjectGridView = (GridView) rootView.findViewById(R.id.gv_subjectlist);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectivityReceiver.isConnected())
                {
                    new AudioLibraryFragment.getAudioSubject().execute();
                }
            }
        });

        ((TextView)rootView.findViewById(R.id.text_schoolname)).setText(sessionManagement.getUserDetails().get(ConstValue.KEY_SCHOOL_NAME));
        ((TextView)rootView.findViewById(R.id.text_classname)).setText(sessionManagement.getUserDetails().get(ConstValue.KEY_CLASS_NAME));
    }

    public void loadOfflineSubjectData()
    {
        m_settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        try {
            mSubjectOfflineData = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(
                    m_settings.getString("sadeemlight" + "audiolibrary", ""));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(mSubjectOfflineData == null)
        {
            mSubjectOfflineData = new ArrayList<HashMap<String, String>>();
        }

        addSubjectData();
    }

    public class getAudioSubject extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());
        String response = "";
        String message = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
            //pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            String class_id = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASSID);
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            ServiceHandler sh = new ServiceHandler();

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.AUDIOLIBRARY_LIST, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {
                try {

                    m_subjectList.clear();
                    mSubjectOfflineData.clear();

                    Log.d(ConstValue.APPTITLE, jsonSTR);
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Subjects");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("subject_id", c.getString("subject_id"));
                        map.put("subject_name", c.getString("subject_name"));

                        mSubjectOfflineData.add(map);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pd.dismissProgress();
            swipeRefreshLayout.setRefreshing(false);

            if (response == "true")
            {
                try
                {
                    m_settings.edit().putString("sadeemlight" + "audiolibrary", ObjectSerializer.serialize(mSubjectOfflineData)).commit();
                    addSubjectData();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addSubjectData()
    {
        m_subjectList.clear();

        for (int i = 0; i < mSubjectOfflineData.size(); i++)
        {
            m_subjectList.add(new ModelAudioSubject(
                    mSubjectOfflineData.get(i).get("subject_id"),
                    mSubjectOfflineData.get(i).get("subject_name")));
        }

        m_subjectAdapter = new AudioSubjectListAdapter(getActivity(), m_subjectList);
        m_subjectAdapter.setChooseSubjectListener(new OnChooseSubjectListener() {
            @Override
            public void onChooseSubject(ModelAudioSubject subject) {
                m_selSubject = subject;

                mLessonRootView.setVisibility(View.VISIBLE);
                if(ConnectivityReceiver.isConnected())
                {
                    new AudioLibraryFragment.getAudioLesson().execute();
                }
                else
                {
                    loadOfflineLessonData(subject);
                }
            }
        });
        mSubjectGridView.setAdapter(m_subjectAdapter);
    }

    public void loadOfflineLessonData(ModelAudioSubject selSubject)
    {
        List<ModelAudioLesson> lessonList = new ArrayList<>();
        try
        {
            ArrayList<HashMap<String, String>> lessonOfflineData  = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(
                    m_settings.getString("sadeemlight" + "audiolibrary-" + selSubject.subject_id, ""));

            for (int i = 0; i < lessonOfflineData.size(); i++)
            {

                ModelAudioLesson data = new ModelAudioLesson();

                data.lectureId = lessonOfflineData.get(i).get("voice_lecture_id");
                data.subjectName = lessonOfflineData.get(i).get("subject_name");
                data.lectureLink = lessonOfflineData.get(i).get("lecture_link");
                data.lectureImageLink = lessonOfflineData.get(i).get("cover");

                lessonList.add(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        m_lessonAdapter.updateData(lessonList);
    }

    public class getAudioLesson extends AsyncTask<Void, Void, Void> {

        Progress_dialog pd = new Progress_dialog(getActivity());
        String response = "";
        String message = "";
        List<ModelAudioLesson> lessonList = new ArrayList<>();
        ArrayList<HashMap<String, String>> lessonOfflineData = new ArrayList<>();

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            response = "";

            String class_id = sessionManagement.getUserDetails().get(ConstValue.KEY_CLASSID);
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.VOIDLECTURE_LIST + m_selSubject.subject_id;
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(ConstValue.APPTITLE, url);
            Log.d(ConstValue.APPTITLE, jsonSTR);

            if (jsonSTR != null)
            {
                Log.d(ConstValue.APPTITLE, jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");
                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("VoiceLecture");


                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);

                        ModelAudioLesson data = new ModelAudioLesson();

                        data.lectureId = c.getString("voice_lecture_id");
                        data.subjectName = c.getString("subject_name");
                        data.lectureLink = c.getString("lecture_link");
                        data.lectureImageLink = c.getString("cover");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("voice_lecture_id", c.getString("voice_lecture_id"));
                        map.put("subject_name", c.getString("subject_name"));
                        map.put("lecture_link", c.getString("lecture_link"));
                        map.put("cover", c.getString("cover"));

                        lessonOfflineData.add(map);
                        lessonList.add(data);
                    }
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
            pd.dismissProgress();


            if (response == "true")
            {
                try
                {
                    m_settings.edit().putString("sadeemlight" + "audiolibrary-" + m_selSubject.subject_id,
                            ObjectSerializer.serialize(lessonOfflineData)).commit();
                    m_lessonAdapter.updateData(lessonList);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ImageView m_butVolume;
    ImageView m_butPlay;
    TextView  m_lessonName;
    SeekBar m_seekBar;

    ModelAudioLesson m_selLesson;

    MediaPlayer m_player;
    AudioManager m_audioManager;
    boolean isFirstLessonClick = true;

    public void initLessonView(View rootView)
    {
        mLessonRootView = rootView.findViewById(R.id.root_lesson);
        mLessonListView = (ListView) rootView.findViewById(R.id.list_audio_lesson);
        m_seekBar = (SeekBar)rootView.findViewById(R.id.seek_audiolesson);
        m_lessonAdapter = new AudioLessonListAdapter(getContext());
        mLessonListView.setAdapter(m_lessonAdapter);
        mLessonRootView.setVisibility(View.INVISIBLE);

        m_lessonName = (TextView) rootView.findViewById(R.id.text_lessonname);
        m_butVolume = (ImageView) rootView.findViewById(R.id.button_volume);
        m_butPlay = (ImageView) rootView.findViewById(R.id.button_play);

        m_lessonAdapter.setChooseLessonListener(new OnChooseLessonListener() {
            @Override
            public void onChooseLesson(ModelAudioLesson lesson) {
                setSelLesson(lesson);
            }
        });

        m_butPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_player != null)
                {
                    playLesson();
                }
            }
        });

        m_butVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustVolume();
            }
        });
    }
    public void setSelLesson(ModelAudioLesson lesson)
    {
        releasePlayer();

        m_selLesson = lesson;

        m_lessonName.setText(m_selLesson.subjectName);
        m_player = new MediaPlayer();
        m_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        m_audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        new playAudioPrepare().execute();

        isFirstLessonClick = true;

        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //m_player.stop();
                m_seekBar.setProgress(0);
                m_butPlay.setImageResource(R.drawable.ic_audio_play);
            }
        });
    }

    public void releasePlayer()
    {
        if(m_player != null)
        {
            m_player.stop();
            m_player.release();
            m_player = null;
        }
    }

    public void playLesson()
    {
        if(m_player.isPlaying() == false)
        {
            m_butPlay.setImageResource(R.drawable.ic_audio_pause);
            m_player.start();
            onRunSeekbar();
        }
        else
        {
            m_player.pause();
            m_butPlay.setImageResource(R.drawable.ic_audio_play);
        }
    }

    public class playAudioPrepare extends AsyncTask<Void, Void, Void> {
        Progress_dialog pd = new Progress_dialog(getActivity());
        ProgressDialog pd1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd.showProgressbar();
            pd1 = ProgressDialog.show(getActivity(), "", "");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                m_player.reset();
                m_player.setDataSource(m_selLesson.lectureLink);
                m_player.prepare();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            m_seekBar.setMax(m_player.getDuration());
            m_seekBar.setProgress(0);
            //pd.dismissProgress();
            pd1.dismiss();

            if(isFirstLessonClick)
            {
                playLesson();
                isFirstLessonClick = false;
            }
        }
    }

    Runnable seekRunThread = new Runnable() {
        @Override
        public void run() {

            if(m_player != null)
            {
                if(m_player.isPlaying())
                {
                    int currentposition = m_player.getCurrentPosition();
                    m_seekBar.setProgress(currentposition);
                    m_seekBar.postDelayed(this,100);
                }
            }
        }
    };
    public void onRunSeekbar()
    {
        m_seekBar.postDelayed(seekRunThread, 10);
    }

    public void adjustVolume()
    {
        Context context = getContext();
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);

        final SeekBar seek = new SeekBar(context);
        seek.setProgressDrawable(context.getResources().getDrawable(R.drawable.apptheme_scrubber_progress_horizontal_holo_light));
        seek.setThumb(context.getResources().getDrawable(R.drawable.apptheme_scrubber_control_selector_holo_light));
        seek.setMax(m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seek.setProgress(m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        popDialog.setIcon(R.drawable.ic_audio_volume);
        popDialog.setTitle("Volume Control");
        popDialog.setView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();
    }
}
