package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sadeemlight.R;
import com.sadeemlight.activity.Voice_lesson_list_model;

import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.Progress_dialog;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Voice_lesson_list_adapter extends BaseAdapter  implements Filterable {

    private ModelFilter ModelFilter;
    private List<Voice_lesson_list_model> allModelItemsArray;
    private List<Voice_lesson_list_model> filteredModelItemsArray;

    private Context context;
    private List<Voice_lesson_list_model> mlistItems;

    //ImageView globleplaypause;
    //RelativeLayout rl_play_puse;

    MediaPlayer player;
    private AudioManager audioManager = null;
    Progress_dialog pd;
    boolean isPlaying = false;
    String url = "";


    //SeekBar globle_seek;


    LayoutInflater mInflater;

    private Handler mhandler = new Handler();

    public Voice_lesson_list_adapter(Context context, List<Voice_lesson_list_model> Items) {

        mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<Voice_lesson_list_model>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<Voice_lesson_list_model>();
        filteredModelItemsArray.addAll(allModelItemsArray);
        getFilter();

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        pd = new Progress_dialog(context);
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

        final ViewHolder viewHolder;

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.voice_lesson_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Voice_lesson_list_model m = mlistItems.get(position);

        viewHolder.img_play.setImageResource(R.drawable.ic_audio_play);
        viewHolder.txtTitle.setText(m.getTitle());

        viewHolder.img_sound.setVisibility(View.GONE);
        viewHolder.audio_progress.setVisibility(View.GONE);

        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

                switch (i) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        pd.showProgressbar();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        pd.dismissProgress();
                        break;
                }

                return false;
            }
        });

        viewHolder.img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("voiceid:", m.getLink());

                try {

                    if (!player.isPlaying()) {

                        url = m.getLink();

                        if (ConnectivityReceiver.isConnected()) {
                            new playAudio().execute();
                        } else {
                            Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
                        }

                        viewHolder.img_sound.setVisibility(View.VISIBLE);
                        viewHolder.img_play.setImageResource(R.drawable.ic_audio_pause);

                        viewHolder.audio_progress.setVisibility(View.VISIBLE);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(player != null){
                                    int currentposition = player.getCurrentPosition() / 1000;

                                    viewHolder.audio_progress.setProgress(currentposition);
                                }

                                mhandler.postDelayed(this,1000);
                            }
                        });

                        Log.e("player: ", "play");
                    } else {
                        player.pause();

                        //seekBar.setVisibility(View.GONE);
                        viewHolder.img_sound.setVisibility(View.GONE);
                        //globleplaypause.setImageResource(R.drawable.ic_audio_lesion_09);
                        viewHolder.img_play.setImageResource(R.drawable.ic_audio_play);

                        Log.e("player: ", "pause");
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

        viewHolder.img_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("player: ", "volume");

                final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);

                final SeekBar seek = new SeekBar(context);
                seek.setProgressDrawable(context.getResources().getDrawable(R.drawable.apptheme_scrubber_progress_horizontal_holo_light));
                seek.setThumb(context.getResources().getDrawable(R.drawable.apptheme_scrubber_control_selector_holo_light));
                seek.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seek.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));

                popDialog.setIcon(R.drawable.ic_audio_volume);
                popDialog.setTitle("Volume Control");
                popDialog.setView(seek);

                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                i, 0);
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
        });

        return convertView;
    }

    class ViewHolder {

        TextView txtTitle;
        ImageView img_play;
        ImageView img_sound;
        RelativeLayout rl_voice;
        SeekBar audio_progress;
        RelativeLayout rl_play_puse;

        public ViewHolder(View convertView) {

            txtTitle = (TextView) convertView.findViewById(R.id.tv_voice_list_title);
            img_play = (ImageView) convertView.findViewById(R.id.iv_voice_list_playpuse);
            img_sound = (ImageView) convertView.findViewById(R.id.iv_voice_list_sound);
            rl_play_puse = (RelativeLayout) convertView.findViewById(R.id.rl_voice_list_playpuse);
            audio_progress = (SeekBar) convertView.findViewById(R.id.seekBar_voice);
            rl_voice = (RelativeLayout) convertView.findViewById(R.id.rl_voice);
        }

    }

    public void stopPlay(){
        if(player.isPlaying()) {
            player.stop();
        }
    }

    public class playAudio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                player.reset();
                player.setDataSource(url);
                player.prepare();
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismissProgress();

            isPlaying = true;

            /*((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(player != null){
                        int currentposition = player.getCurrentPosition() / 1000;

                        globle_seek.setProgress(currentposition);
                    }

                    mhandler.postDelayed(this,1000);
                }
            });*/

        }


    }

    @Override
    public Filter getFilter() {
        if (ModelFilter == null) {

            ModelFilter = new ModelFilter();
        }

        return ModelFilter;
    }

    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Voice_lesson_list_model> filteredItems = new ArrayList<Voice_lesson_list_model>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    Voice_lesson_list_model m = allModelItemsArray.get(i);
                    if (m.getTitle().toLowerCase().contains(constraint))
                        filteredItems.add(m);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

            filteredModelItemsArray = (ArrayList<Voice_lesson_list_model>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }


}
