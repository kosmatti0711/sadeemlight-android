package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.activity.AudioLibraryFragment;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.venus_model.ModelAudioLesson;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class AudioLessonListAdapter extends BaseAdapter  {

    private Context mContext;
    private List<ModelAudioLesson> m_baseData = new ArrayList<>();
    private AudioLibraryFragment.OnChooseLessonListener m_listener;

    private Handler mhandler = new Handler();

    public AudioLessonListAdapter(Context context) {

        this.mContext = context;
    }

    public void updateData(List<ModelAudioLesson> data)
    {
        m_baseData = data;
        notifyDataSetChanged();
    }
    public void setChooseLessonListener(AudioLibraryFragment.OnChooseLessonListener listener)
    {
        m_listener = listener;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ModelAudioLesson m = m_baseData.get(position);

        final ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.audio_lesson_item,null);

            holder = new ViewHolder(convertView);
            holder.model = m;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.model = m;
        holder.lessonName.setText(m.subjectName);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_audio)
                .showImageForEmptyUri(R.drawable.default_audio)
                .showImageOnFail(R.drawable.default_audio)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        ImageLoader.getInstance().displayImage(m.lectureImageLink, holder.audioImage, options);

        holder.dotMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PopupMenu 객체 생성.
                PopupMenu popup = new PopupMenu(mContext, (View)holder.dotMenu);

                //설정한 popup XML을 inflate.
                popup.getMenuInflater().inflate(R.menu.menu_audio_lesson, popup.getMenu());
                //팝업메뉴 클릭 시 이벤트
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                shareLesson(holder);
                                break;
                            case R.id.play:
                                playLesson(holder);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        holder.butPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playLesson((ViewHolder) v.getTag());
            }
        });

        return convertView;
    }

    class ViewHolder
    {
        ModelAudioLesson model;

        ImageView audioImage;
        ImageView butPlay;
        TextView  lessonName;
        ImageView dotMenu;

        public ViewHolder(View convertView)
        {
            lessonName = (TextView) convertView.findViewById(R.id.text_lessonname);
            audioImage = (ImageView) convertView.findViewById(R.id.image_lessonmark);
            butPlay = (ImageView) convertView.findViewById(R.id.button_play);
            dotMenu = (ImageView) convertView.findViewById(R.id.button_dotmenu);
            lessonName.setTag(this);
            audioImage.setTag(this);
            butPlay.setTag(this);
            dotMenu.setTag(this);
        }
    }

    public void shareLesson(ViewHolder holder)
    {

    }
    public void playLesson(ViewHolder holder)
    {
        if(m_listener != null)
        {
            m_listener.onChooseLesson(holder.model);
        }
    }
}
