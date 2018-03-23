package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ak.sh.ay.musicwave.MusicWave;
import com.sadeemlight.venus_model.ModelMessage;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sadeemlight.venus_uis.ImageViewActivity;
import com.sadeemlight.venus_uis.VideoPreviewActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Rajesh Dabhi on 3/8/2016.
 */
public class ChatAdapter extends BaseAdapter {

    static MediaPlayer audioPlayer;
    static Visualizer mVisualizer;
    static ViewHolder selItem = null;
    static final int HOUR = 60*60*1000;
    static final int MINUTE = 60*1000;
    static final int SECOND = 1000;

    private Context context;
    private List<ModelMessage> messagesItems = new ArrayList<>();

    public ChatAdapter(Context context)
    {
        this.context = context;
    }

    public void updateData(List<ModelMessage> navDrawerItems)
    {
        this.messagesItems = navDrawerItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ChatAdapter.ViewHolder holder;
        final ModelMessage model = messagesItems.get(position);

        // Identifying the message owner
        if (model.isSelf == true)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.chat_item_right,null);

            holder = new ChatAdapter.ViewHolder(convertView);
            holder.model = model;
            convertView.setTag(holder);

            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            lblFrom.setText(model.fromName);
        }
        else
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.chat_item_left,null);

            holder = new ChatAdapter.ViewHolder(convertView);
            holder.model = model;
            convertView.setTag(holder);

            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            lblFrom.setText("School Management");
        }

        holder.model = model;
        holder.txtMsg.setText(model.message);

        holder.lbldate.setText(getDateNTime(model.msg_time, "msg_time"));
        holder.time1.setText(getDateNTime(model.msg_time, "time"));
        holder.time2.setText(getDateNTime(model.msg_time, "time"));
        holder.time3.setText(getDateNTime(model.msg_time, "time"));

        if(model.type.contentEquals("message"))
        {
            holder.layoutText.setVisibility(View.VISIBLE);
            holder.layoutImage.setVisibility(View.GONE);
            holder.layoutMusic.setVisibility(View.GONE);

            showCheckMessageState(holder);

        }
        else if (model.type.contentEquals("image"))
        {
            holder.layoutText.setVisibility(View.GONE);
            holder.layoutImage.setVisibility(View.VISIBLE);
            holder.thumbPlay.setVisibility(View.GONE);
            holder.layoutMusic.setVisibility(View.GONE);

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.default_image2)
                    .showImageForEmptyUri(R.drawable.default_image2)
                    .showImageOnFail(R.drawable.default_image2)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.NONE)
                    .build();

            String image_url = model.file;
            if(URLUtil.isHttpsUrl(model.file) == false  && URLUtil.isHttpUrl(model.file) == false)
            {
                image_url = "file://" + model.file;
            }

            //ImageLoader.getInstance().displayImage(image_url, holder.messageImage, options);
            Picasso.with(context).load(image_url).resize(500,500).placeholder(R.drawable.default_image2)
                    .into(holder.messageImage);

            holder.layoutImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewActivity.showImageByUrl(context, model.file);
                }
            });

            showCheckMessageState(holder);
        }
        else if(model.type.contentEquals("audio"))
        {
            holder.layoutText.setVisibility(View.GONE);
            holder.layoutImage.setVisibility(View.GONE);
            holder.layoutMusic.setVisibility(View.VISIBLE);

            showCheckMessageState(holder);



            class playAudioPrepare extends AsyncTask<Void, Void, Void> {

                MediaPlayer audioPlayer1;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    holder.audioPlayButton.setImageResource(R.drawable.message_audio_wait);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        String fileurl = model.file;
                        if(URLUtil.isHttpsUrl(model.file) == false  && URLUtil.isHttpUrl(model.file) == false)
                        {
                            fileurl = "file://" + model.file;
                        }

                        audioPlayer1 = new MediaPlayer();
                        audioPlayer1.reset();
                        audioPlayer1.setDataSource(fileurl);
                        audioPlayer1.prepare();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    int durationInMillis = audioPlayer1.getDuration();
                    int curVolume = audioPlayer1.getCurrentPosition();

                    int durationHour = durationInMillis/HOUR;
                    int durationMint = (durationInMillis%HOUR)/MINUTE;
                    int durationSec = (durationInMillis%MINUTE)/SECOND;

                    int currentHour = curVolume/HOUR;
                    int currentMint = (curVolume%HOUR)/MINUTE;
                    int currentSec = (curVolume%MINUTE)/SECOND;

                    String desc = String.format("%02d:%02d/%02d:%02d", currentMint, currentSec, durationMint, durationSec);
                    holder.textMusicDesc.setText(desc);

                    audioPlayer1.release();
                    audioPlayer1 = null;
                    holder.audioPlayButton.setImageResource(R.drawable.message_audio_play);
                }
            }


            new playAudioPrepare().execute();
            holder.textMusicDesc.setText("__:__/__:__");


            holder.audioPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    playAudioChatting(holder, model);
                }
            });

        }
        else if(model.type.contentEquals("video"))
        {
            holder.layoutText.setVisibility(View.GONE);
            holder.layoutImage.setVisibility(View.VISIBLE);
            holder.thumbPlay.setVisibility(View.VISIBLE);
            holder.layoutMusic.setVisibility(View.GONE);

            holder.thumbPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileurl = model.file;
                    if(URLUtil.isHttpsUrl(model.file) == false  && URLUtil.isHttpUrl(model.file) == false)
                    {
                        fileurl = "file://" + model.file;
                    }

                    VideoPreviewActivity.showActivity(context, fileurl);
                }
            });

            showCheckMessageState(holder);
        }
        else
        {
            holder.layoutText.setVisibility(View.GONE);
            holder.layoutImage.setVisibility(View.VISIBLE);
            holder.thumbPlay.setVisibility(View.VISIBLE);
            holder.layoutMusic.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void showCheckMessageState(ViewHolder holder)
    {
        ModelMessage model = holder.model;

        if(model.type.contentEquals("message"))
        {
            if(model.offlinemessage == false)
            {
                if(model.loaded == true && holder.loadCheck1!=null)
                {
                    holder.loadCheck1.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if(holder.loadOffline1 != null)
                {
                    holder.loadOffline1.setVisibility(View.VISIBLE);
                }

                if(holder.loadCheck1!=null)
                {
                    holder.loadCheck1.setVisibility(View.INVISIBLE);
                    holder.loadNormal1.setVisibility(View.INVISIBLE);
                }
            }
        }
        else if(model.type.contentEquals("video") || model.type.contentEquals("image"))
        {
            if(model.offlinemessage == false)
            {
                if(model.loaded == true && holder.loadCheck2!=null)
                {
                    holder.loadCheck2.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if(holder.loadOffline2 != null)
                {
                    holder.loadOffline2.setVisibility(View.VISIBLE);
                }

                if(holder.loadCheck2!=null)
                {
                    holder.loadCheck2.setVisibility(View.INVISIBLE);
                    holder.loadNormal2.setVisibility(View.INVISIBLE);
                }
            }
        }
        else if(model.type.contentEquals("audio"))
        {
            if(model.offlinemessage == false)
            {
                if(model.loaded == true && holder.loadCheck3!=null)
                {
                    holder.loadCheck3.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if(holder.loadOffline3 != null)
                {
                    holder.loadOffline3.setVisibility(View.VISIBLE);
                }

                if(holder.loadCheck3!=null)
                {
                    holder.loadCheck3.setVisibility(View.INVISIBLE);
                    holder.loadNormal3.setVisibility(View.INVISIBLE);
                }
            }
        }
        else
        {
            if(model.offlinemessage == false)
            {
                if(model.loaded == true && holder.loadCheck1!=null)
                {
                    holder.loadCheck1.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if(holder.loadOffline1 != null)
                {
                    holder.loadOffline1.setVisibility(View.VISIBLE);
                }

                if(holder.loadCheck1!=null)
                {
                    holder.loadCheck1.setVisibility(View.INVISIBLE);
                    holder.loadNormal1.setVisibility(View.INVISIBLE);
                }
            }
        }


    }

    public static void releaseAudio()
    {
        if(audioPlayer != null)
        {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();

            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    public static void playAudioChatting(final ViewHolder holder, final ModelMessage model)
    {
        final Runnable seekRunThread = new Runnable() {

            @Override
            public void run() {

                if(audioPlayer != null && audioPlayer.isPlaying() &&
                        selItem !=null && selItem.equals(holder))
                {
                    int durationInMillis = audioPlayer.getDuration();
                    int curVolume = audioPlayer.getCurrentPosition();

                    int durationHour = durationInMillis/HOUR;
                    int durationMint = (durationInMillis%HOUR)/MINUTE;
                    int durationSec = (durationInMillis%MINUTE)/SECOND;

                    int currentHour = curVolume/HOUR;
                    int currentMint = (curVolume%HOUR)/MINUTE;
                    int currentSec = (curVolume%MINUTE)/SECOND;

                    String desc = String.format("%02d:%02d/%02d:%02d",currentMint, currentSec, durationMint, durationSec);
                    holder.textMusicDesc.setText(desc);
                    holder.textMusicDesc.postDelayed(this,100);
                }
            }
        };

        if(audioPlayer != null)
        {
            if(selItem !=null)
            {
                if(selItem.equals(holder))
                {
                    if(audioPlayer.isPlaying())
                    {
                        audioPlayer.pause();
                        mVisualizer.setEnabled(false);
                        int durationInMillis = audioPlayer.getDuration();
                        holder.textMusicDesc.setText(String.format("00:00/%02d:%02d", (durationInMillis%HOUR)/MINUTE, (durationInMillis%MINUTE)/SECOND));
                    }
                    else
                    {
                        audioPlayer.seekTo(0);
                        audioPlayer.start();
                        mVisualizer.setEnabled(true);
                        holder.textMusicDesc.post(seekRunThread);
                    }

                    return;
                }
                else
                {
                    releaseAudio();
                }

            }
            else
            {
                releaseAudio();
            }
        }


        holder.audioPlayButton.setImageResource(R.drawable.message_audio_wait);
        holder.audioPlayButton.post( new Runnable() {

            @Override
            public void run() {

                try {
                    String fileurl = model.file;
                    if(URLUtil.isHttpsUrl(model.file) == false  && URLUtil.isHttpUrl(model.file) == false)
                    {
                        fileurl = "file://" + model.file;
                    }

                    audioPlayer = new MediaPlayer();
                    audioPlayer.reset();
                    audioPlayer.setDataSource(fileurl);
                    audioPlayer.prepare();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                audioPlayer.start();
                audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mVisualizer.setEnabled(false);
                    }
                });

                mVisualizer = new Visualizer(audioPlayer.getAudioSessionId());
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                mVisualizer.setDataCaptureListener(
                        new Visualizer.OnDataCaptureListener() {
                            public void onWaveFormDataCapture(Visualizer visualizer,  byte[] bytes, int samplingRate) {
                                holder.musicWave.updateVisualizer(bytes);
                            }

                            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                            }
                        }, Visualizer.getMaxCaptureRate(), true, false);
                mVisualizer.setEnabled(true);

                selItem = holder;
                holder.textMusicDesc.post(seekRunThread);
                holder.audioPlayButton.setImageResource(R.drawable.message_audio_play);
            }
        });
    }

    class ViewHolder
    {
        ModelMessage model;

        View layoutText;
        TextView txtMsg;
        TextView lbldate;
        View  layoutImage;
        ImageView messageImage;
        ImageView thumbPlay;
        ImageView audioPlayButton;

        View  layoutMusic;
        TextView textMusicDesc;
        MusicWave musicWave;

        TextView time1;
        TextView time2;
        TextView time3;

        ImageView loadCheck1;
        ImageView loadCheck2;
        ImageView loadCheck3;
        ImageView loadNormal1;
        ImageView loadNormal2;
        ImageView loadNormal3;
        ImageView loadOffline1;
        ImageView loadOffline2;
        ImageView loadOffline3;



        public ViewHolder(View convertView)
        {

            txtMsg = (TextView) convertView.findViewById(R.id.text_message);
            lbldate = (TextView) convertView.findViewById(R.id.lbldate);
            time1 = (TextView) convertView.findViewById(R.id.text_time1);
            time2 = (TextView) convertView.findViewById(R.id.text_time2);
            time3 = (TextView) convertView.findViewById(R.id.text_time3);

            layoutText = convertView.findViewById(R.id.layout_text);
            layoutImage = convertView.findViewById(R.id.layout_image);
            layoutMusic  = convertView.findViewById(R.id.layout_music);

            messageImage = (ImageView)convertView.findViewById(R.id.message_image);
            thumbPlay = (ImageView)convertView.findViewById(R.id.message_video_thumb);
            textMusicDesc = (TextView)convertView.findViewById(R.id.text_music_desc);
            audioPlayButton = (ImageView)convertView.findViewById(R.id.button_music_play);
            musicWave = (MusicWave) convertView.findViewById(R.id.musicWave);

            loadCheck1 = (ImageView)convertView.findViewById(R.id.img_loaded_check1);
            loadCheck2 = (ImageView)convertView.findViewById(R.id.img_loaded_check2);
            loadCheck3 = (ImageView)convertView.findViewById(R.id.img_loaded_check3);
            loadNormal1 = (ImageView)convertView.findViewById(R.id.img_loaded_normal1);
            loadNormal2 = (ImageView)convertView.findViewById(R.id.img_loaded_normal2);
            loadNormal3 = (ImageView)convertView.findViewById(R.id.img_loaded_normal3);
            loadOffline1 = (ImageView)convertView.findViewById(R.id.img_loaded_offline1);
            loadOffline2 = (ImageView)convertView.findViewById(R.id.img_loaded_offline2);
            loadOffline3 = (ImageView)convertView.findViewById(R.id.img_loaded_offline3);

            if(loadCheck1 != null)
            {
                loadCheck1.setVisibility(View.INVISIBLE);
                loadCheck2.setVisibility(View.INVISIBLE);
                loadCheck3.setVisibility(View.INVISIBLE);

                loadOffline1.setVisibility(View.INVISIBLE);
                loadOffline2.setVisibility(View.INVISIBLE);
                loadOffline3.setVisibility(View.INVISIBLE);
            }



            byte[] initvalue = new byte[]{-127, -127, -127, -127, -127, -127, -127, -127, -127};
            musicWave.updateVisualizer(initvalue);

            layoutImage.setTag(this);
        }
    }

    public String getDateNTime(String datetimeString, String what)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = inputFormat.parse(datetimeString);
        } catch (ParseException e) {
            return "";
        }

        if(date == null)
        {
            return "";
        }

        if(what.contentEquals("msg_time") == true)
        {
            return dateFormat.format(date);
        }
        else if(what.contentEquals("time") == true)
        {
            return timeFormat.format(date);
        }

        return "";
    }
}

