package com.sadeemlight.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sadeemlight.activity.MainActivity;
import com.sadeemlight.R;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.venus_uis.utils.Defines;

import org.json.JSONObject;

/**
 * Created by Rajesh Dabhi on 15/7/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {

    protected NotificationManager mNotificationManager;

    public static int count = 1,
            simple_count = 1,
            newscount = 1,
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
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        String title = data.getString("title");
        String subtitle = data.getString("subtitle");
        String tickerText = data.getString("tickerText");
        String vibrate = data.getString("vibrate");
        String sound = data.getString("sound");
        String largeIcon = data.getString("largeIcon");
        String smallIcon = data.getString("smallIcon");

        //String count = data.getString("count");

        String type = "";
        m_param1 = "";
        try
        {
            String custom = data.getString("custom");
            JSONObject json = new JSONObject(custom);
            JSONObject payload = json.getJSONObject("payload");
            type = payload.getString("type");

            if(type.contentEquals("expire_alert") == true)
            {
                m_param1 = "2017-09-09";
                m_param1 = payload.getString("expire_date");
                m_param2 = payload.getString("phone");
            }
        }
        catch (Exception ex)
        {

        }

        Log.d(Defines.APP_LOG_TITLE, "From: " + from);
        Log.d(Defines.APP_LOG_TITLE, "ModelMessage: " + message);
        Log.d(Defines.APP_LOG_TITLE, "type: " + type);

        //Displaying a notiffication with the message
        //sendNotification(message);

        Log.d(Defines.APP_LOG_TITLE, data.toString());
        showBigNotification(message, type);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message,
                                  String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;

        String settype = "";
        if (type != null)
        {
            if (type.contentEquals("news"))
            {
                requestCode = 01;
            } else if (type.contentEquals("homework")) {
                requestCode = 102;
            } else if (type.contentEquals("voice_lesson")) {
                requestCode = 254;
            } else if (type.contentEquals("notification")) {
                requestCode = 365;
            } else if (type.contentEquals("attendance")) {
                requestCode = 487;
            } else if (type.contentEquals("score")) {
                requestCode = 564;
            } else if (type.contentEquals("message")) {
                requestCode = 648;
            } else if (type.contentEquals("timetable")) {
                requestCode = 779;
            } else if (type.contentEquals("library")) {
                requestCode = 862;
            } else if (type.contentEquals("attendance_archive")) {
                requestCode = 549;
            } else if (type.contentEquals("daily_degrees")) {
                requestCode = 159;
            } else if (type.contentEquals("monthly_degrees")) {
                requestCode = 312;
            } else if (type.contentEquals("homework_archive")) {
                requestCode = 468;
            } else if (type.contentEquals("homework_today")) {
                requestCode = 965;
            }

            settype = " :" + type;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound2 = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.tone_notific);
        Log.e("sounds:", "" + getPackageName() + "," + sound2);

        /*try {
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound2);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notific_icon_2)
                .setContentTitle("Sadeemlight" + settype)
                .setContentText(message)
                .setSound(sound2)
                .setAutoCancel(true)
                .setGroup("Sadeemlight")
                .setGroupSummary(true)
                .setContentIntent(pendingIntent);

        PowerManager pm = (PowerManager) getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm
                .newWakeLock(
                        (PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                        "TAG");
        wakeLock.acquire(1000);

        //notificationManager.notify(0, noBuilder.build()); //0 = ID of notification

        if (type != null) {
            if (type.contentEquals("news")) {
                notificationManager.notify(01, noBuilder.build());
            } else if (type.contentEquals("homework")) {
                notificationManager.notify(102, noBuilder.build());
            } else if (type.contentEquals("voice_lesson")) {
                notificationManager.notify(254, noBuilder.build());
            } else if (type.contentEquals("notification")) {
                notificationManager.notify(365, noBuilder.build());
            } else if (type.contentEquals("attendance")) {
                notificationManager.notify(487, noBuilder.build());
            } else if (type.contentEquals("score")) {
                notificationManager.notify(564, noBuilder.build());
            } else if (type.contentEquals("message")) {
                notificationManager.notify(648, noBuilder.build());
            } else if (type.contentEquals("timetable")) {
                notificationManager.notify(779, noBuilder.build());
            } else if (type.contentEquals("library")) {
                notificationManager.notify(862, noBuilder.build());
            } else if (type.contentEquals("attendance_archive")) {
                notificationManager.notify(549, noBuilder.build());
            } else if (type.contentEquals("daily_degrees")) {
                notificationManager.notify(159, noBuilder.build());
            } else if (type.contentEquals("monthly_degrees")) {
                notificationManager.notify(312, noBuilder.build());
            } else if (type.contentEquals("homework_archive")) {
                notificationManager.notify(468, noBuilder.build());
            } else if (type.contentEquals("homework_today")) {
                notificationManager.notify(965, noBuilder.build());
            } else {
                notificationManager.notify(111, noBuilder.build());
            }
            updateActivity(this, type);
        } else {
            notificationManager.notify(111, noBuilder.build());
        }
    }

    private void showBigNotification(String message, String type)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;
        String settype = "";
        if (type != null)
        {
            if (type.contentEquals("news"))
            {
                count = newscount;
                requestCode = 01;
            }
            else if (type.contentEquals("homework"))
            {
                count = home_count;
                requestCode = 102;
            }
            else if (type.contentEquals("voice_lesson"))
            {
                count = voice_count;
                requestCode = 254;
            }
            else if (type.contentEquals("notification"))
            {
                count = noti_count;
                requestCode = 365;
            }
            else if (type.contentEquals("attendance"))
            {
                count = atted_count;
                requestCode = 487;
            }
            else if (type.contentEquals("score"))
            {
                count = score_count;
                requestCode = 564;
            }
            else if (type.contentEquals("message"))
            {
                count = msg_count;
                requestCode = 648;
            }
            else if (type.contentEquals("timetable"))
            {
                count = time_count;
                requestCode = 779;
            }
            else if (type.contentEquals("library"))
            {
                count = lib_count;
                requestCode = 862;
            }
            else if (type.contentEquals("attendance_archive"))
            {
                count = archi_count;
                requestCode = 549;
            }
            else if (type.contentEquals("daily_degrees"))
            {
                count = daily_count;
                requestCode = 159;
            }
            else if (type.contentEquals("monthly_degrees"))
            {
                count = month_count;
                requestCode = 312;
            }
            else if (type.contentEquals("homework_archive"))
            {
                count = home_arc_count;
                requestCode = 468;
            }
            else if (type.contentEquals("homework_today"))
            {
                count = home_to_count;
                requestCode = 965;
            }
            else
            {
                count = simple_count;
            }

            settype = " :" + type;
        }
        else
        {
            count = simple_count;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound2 = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.tone_notific);
        Log.e("sounds:", "" + getPackageName() + "," + sound2);

        /*NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle("Sadeemlight");
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());*/

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        //mBuilder.setSound(sound2);

        Notification notification = mBuilder.setSmallIcon(R.drawable.ic_notific_icon_2).setTicker("Sadeemlight")
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
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),"TAG");
        wakeLock.acquire(1000);
        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.tone_notific);
        //notification.defaults = Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (type != null)
        {
            if (type.contentEquals("news"))
            {
                Log.i(ConstValue.APPTITLE, "gcm_news");
                newscount++;
                MainActivity.notfNewCount++;
                notificationManager.notify(01, notification);
            }
            else if (type.contentEquals("homework"))
            {
                home_count++;
                notificationManager.notify(102, notification);
            }
            else if (type.contentEquals("voice_lesson"))
            {
                voice_count++;
                notificationManager.notify(254, notification);
            }
            else if (type.contentEquals("notification"))
            {
                noti_count++;
                notificationManager.notify(365, notification);
            }
            else if (type.contentEquals("attendance"))
            {
                atted_count++;
                notificationManager.notify(487, notification);
            }
            else if (type.contentEquals("score"))
            {
                score_count++;
                notificationManager.notify(564, notification);
            }
            else if (type.contentEquals("message"))
            {
                Log.i(ConstValue.APPTITLE, "gcm_message");
                msg_count++;
                MainActivity.notfMessageCount++;
                notificationManager.notify(648, notification);
            }
            else if (type.contentEquals("timetable"))
            {
                time_count++;
                notificationManager.notify(779, notification);
            }
            else if (type.contentEquals("library"))
            {
                lib_count++;
                notificationManager.notify(862, notification);
            }
            else if (type.contentEquals("attendance_archive"))
            {
                archi_count++;
                notificationManager.notify(549, notification);
            }
            else if (type.contentEquals("daily_degrees"))
            {
                daily_count++;
                notificationManager.notify(159, notification);
            }
            else if (type.contentEquals("monthly_degrees"))
            {
                month_count++;
                notificationManager.notify(312, notification);
            }
            else if (type.contentEquals("homework_archive"))
            {
                home_arc_count++;
                notificationManager.notify(468, notification);
            }
            else if (type.contentEquals("homework_today"))
            {
                home_to_count++;
                notificationManager.notify(965, notification);
            }
            else
            {
                simple_count++;
                notificationManager.notify(111, notification);
            }
            updateActivity(this, type);
        }
        else
        {
            simple_count++;
            notificationManager.notify(111, notification);
        }
    }

    public void updateActivity(Context context, String type)
    {
        Intent updates = new Intent("sadeem_notification");
        updates.putExtra("type", type);
        updates.putExtra("param1", m_param1);
        updates.putExtra("param2", m_param2);
        context.sendBroadcast(updates);
    }

}
