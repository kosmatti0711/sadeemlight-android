package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sadeemlight.venus_model.ModelAudioSubject;

import com.sadeemlight.activity.AudioLibraryFragment;
import com.sadeemlight.R;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class AudioSubjectListAdapter extends BaseAdapter  {

    private Context context;
    private List<ModelAudioSubject> mlistItems;
    private AudioLibraryFragment.OnChooseSubjectListener m_listener;

    private Handler mhandler = new Handler();

    public AudioSubjectListAdapter(Context context, List<ModelAudioSubject> Items) {

        this.context = context;
        this.mlistItems = Items;
    }

    public void setChooseSubjectListener(AudioLibraryFragment.OnChooseSubjectListener listener)
    {
        m_listener = listener;
    }

    @Override
    public int getCount() {
        return mlistItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ModelAudioSubject m = mlistItems.get(position);

        ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.audio_subject_item,null);

            holder = new AudioSubjectListAdapter.ViewHolder(convertView);
            holder.model = m;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.model = m;
        holder.txtTitle.setText(m.subject_name);
        holder.txtTitle.setBackgroundColor(m.getColor());

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_listener != null)
                {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    m_listener.onChooseSubject(holder.model);
                }
            }
        });

        return convertView;
    }

    class ViewHolder
    {
        ModelAudioSubject model;
        TextView txtTitle;
        public ViewHolder(View convertView)
        {
            txtTitle = (TextView) convertView.findViewById(R.id.text_subjectname);
            txtTitle.setTag(this);
        }
    }

}
