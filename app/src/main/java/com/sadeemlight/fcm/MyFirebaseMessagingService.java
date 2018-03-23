package com.sadeemlight.fcm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sadeemlight.R;
import com.sadeemlight.activity.MainActivity;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.FileUtil;
import com.sadeemlight.venus_uis.utils.GlobalFunction;
import com.sadeemlight.venus_uis.utils.GlobalProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Created by VENUS on 2017-09-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    public static final int REQUSTCODE_ONLINE_EXAM = 02;
    public static final int REQUSTCODE_ONLINE_EXAM_QUESTOIN = 201;
    public static final int REQUSTCODE_ONLINE_LESSON = 03;
    public static final int REQUSTCODE_ONLINE_LESSON_CATEGORY = 301;


    protected NotificationManager mNotificationManager;

    public static int count = 1,
            simple_count = 1,
            newscount = 1,
            exam_count = 1,
            teaching_count = 1,
            home_count = 1,
            voice_count = 1,
            noti_count = 1,
            atted_count = 1,
            score_count = 1,
            msg_count = 1,
            time_count = 1,
            lib_count = 1,
            archi_count = 1,
            daily_count = 1,
            month_count = 1,
            home_arc_count = 1,
            home_to_count = 1;

    public String m_param1 = "";
    public String m_param2 = "";

    //This method will be called on every new message received

    // [START receive_message]

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();

        //Getting the message from the bundle
        String message = data.get("message");
        String title = data.get("title");
        String subtitle = data.get("subtitle");
        String tickerText = data.get("tickerText");
        String vibrate = data.get("vibrate");
        String sound = data.get("sound");
        String largeIcon = data.get("largeIcon");
        String smallIcon = data.get("smallIcon");

        //String count = data.getString("count");



        String type = "";
        m_param1 = "";
        JSONObject notf_data = null;
        try
        {
            final String custom = data.get("custom");
            JSONObject json = new JSONObject(custom);
            JSONObject payload = json.getJSONObject("payload");
            type = payload.getString("type");

            notf_data = payload;

            if(type.contentEquals("expire_alert") == true)
            {
                m_param1 = "2017-09-09";
                m_param1 = payload.getString("expire_date");
                m_param2 = payload.getString("phone");
            }

           // if(MainActivity.G_MAINACTIVTY != null)
            {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
        catch (Exception ex){}

        {
            File dir = null;

            if(Environment.getExternalStorageDirectory() != null)
            {
                dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sadeemlight");
            }


            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.d("SAEEMLIGHT", "failed to create directory");
                }
            }

            String file_path = dir.getAbsolutePath() + "/test.txt";

            String file_contents = message + "\n" + data.get("custom");

            Log.d("SAEEMLIGHT", file_path);
            Log.d("SAEEMLIGHT", file_contents);

            FileUtil.writeFileWithString(new File(file_path), file_contents);
        }


        Log.d(Defines.APP_LOG_TITLE, data.toString());
        showBigNotification(message, type, notf_data);
    }


    private void showBigNotification(String message, String type, JSONObject notf_data) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;
        String settype = "";
        if (type != null)
        {
            if (type.contentEquals("news"))
            {
                count = newscount++;
                requestCode = 01;
            }
            else if (type.contentEquals("online_exam"))
            {
                count = exam_count++;
                requestCode = REQUSTCODE_ONLINE_EXAM;
            }
            else if (type.contentEquals("online_exam_question"))
            {
                count = exam_count++;

                try {
                    m_param1 = notf_data.getString("exam_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("param1", m_param1);
                requestCode = REQUSTCODE_ONLINE_EXAM_QUESTOIN;
            }
            else if (type.contentEquals("online_lesson"))
            {
                count = teaching_count++;
                try {
                    m_param1 = notf_data.getString("lesson_category_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("param1", m_param1);

                requestCode = REQUSTCODE_ONLINE_LESSON;
            }
            else if (type.contentEquals("online_lesson_category"))
            {
                count = teaching_count++;
                requestCode = REQUSTCODE_ONLINE_LESSON_CATEGORY;
            }
            else if (type.contentEquals("homework"))
            {
                count = home_count++;
                requestCode = 102;
            }
            else if (type.contentEquals("voice_lesson")) {
                count = voice_count++;
                requestCode = 254;
            } else if (type.contentEquals("notification")) {
                count = noti_count++;
                requestCode = 365;
            } else if (type.contentEquals("attendance")) {
                count = atted_count++;
                requestCode = 487;
            } else if (type.contentEquals("score")) {
                count = score_count++;
                requestCode = 564;
            } else if (type.contentEquals("message")) {
                count = msg_count++;
                requestCode = 648;
            } else if (type.contentEquals("timetable")) {
                count = time_count++;
                requestCode = 779;
            } else if (type.contentEquals("library")) {
                count = lib_count++;
                requestCode = 862;
            } else if (type.contentEquals("attendance_archive")) {
                count = archi_count++;
                requestCode = 549;
            } else if (type.contentEquals("daily_degrees")) {
                count = daily_count++;
                requestCode = 159;
            } else if (type.contentEquals("monthly_degrees")) {
                count = month_count++;
                requestCode = 312;
            } else if (type.contentEquals("homework_archive")) {
                count = home_arc_count++;
                requestCode = 468;
            } else if (type.contentEquals("homework_today")) {
                count = home_to_count++;
                requestCode = 965;
            }else{
                count = simple_count++;
            }

            settype = " :" + type;
        }
        else
        {
            count = simple_count++;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound2 = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.tone_notific);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Notification notification = mBuilder.setSmallIcon(R.drawable.app_logo).setTicker("Sadeemlight")
                .setAutoCancel(true)
                .setContentTitle("Sadeemlight" + settype)
                .setContentIntent(pendingIntent)
                .setNumber(count)
                /*.setStyle(bigPictureStyle)*/
                .setContentText(message)
                /*.setSound(sound2, AudioManager.STREAM_NOTIFICATION)*/
                .setLights(Color.RED, 3000, 3000)//0xff00ff00, 300, 100 or Color.RED, 3000, 3000
                .build();


        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm  .newWakeLock( (PowerManager.SCREEN_BRIGHT_WAKE_LOCK  | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire(1000);

        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.tone_notific);
        //notification.defaults = Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(requestCode, notification);

        if (type != null)
        {
            updateActivity(this, type);
        }
    }

    public void updateActivity(Context context, String type) {
        Intent updates = new Intent("sadeem_notification");
        updates.putExtra("type", type);
        updates.putExtra("param1", m_param1);
        updates.putExtra("param2", m_param2);
        context.sendBroadcast(updates);
    }
}
