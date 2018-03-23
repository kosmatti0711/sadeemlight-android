package com.sadeemlight.activity;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelMessage;
import com.sadeemlight.venus_uis.utils.Defines;
import com.sadeemlight.venus_uis.utils.FileUtil;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class ChatOfflineService extends Service {

    public static ChatOfflineService instance = null;


    private static int m_RunReferCount = 0;
    public static List<ModelMessage> m_offlineMessage = new ArrayList<>();
    private static final Object lock = new Object();

    private Session_management sessionManagement;
    public SharedPreferences settings;
    private Timer m_timer = new Timer();
    private TimerTask timerPositionCheck = null;
    private boolean m_isUploading = false;
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();


    public static void startService(Context context)
    {
        if(m_RunReferCount == 0)
        {
            context.startService(new Intent(context, ChatOfflineService.class));
        }

        m_RunReferCount++;
    }

    public static void stopService(Context context)
    {
        m_RunReferCount--;

        if(m_RunReferCount < 0)
        {
            m_RunReferCount = 0;
        }

        if(m_RunReferCount == 0) {
            context.stopService(new Intent(context, ChatOfflineService.class));
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Defines.APP_LOG_TITLE, "create offline service");

        if(m_timer != null)
        {
            m_timer.cancel();
            m_timer = null;
        }

        m_timer = new Timer();
        timerPositionCheck= new TimerTask()
            {
                public void run()
                {
                    Message message = mHandler.obtainMessage();
                    message.sendToTarget();
                }
            };
        m_timer.schedule(timerPositionCheck, 1000, 5000); // 0초후 첫실행, 3초마다 계속실행

        return super.onStartCommand(intent, flags, startId);
    }

    Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message message)
        {
            ChatOfflineService.this.onCheckOffline();
        }
    };



    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    @Override
    public void onCreate()
    {
        m_isUploading = false;
        sessionManagement = new Session_management(this);
        settings = this.getSharedPreferences("MAIN_PREF", 0);

        instance = this;
        loadOfflineData();
    }

    @Override
    public void onDestroy(){
        Log.d(Defines.APP_LOG_TITLE, "destroy offline service");
        m_timer.cancel();
        m_timer = null;

        instance = null;
        saveOfflineData();
    }

    public boolean onCheckOffline()
    {
        Log.d(Defines.APP_LOG_TITLE, "check offline service");
        if(ConnectivityReceiver.isConnected() == false)
        {
            return false;
        }

        if(m_isUploading == true)
        {
            return false;
        }

        if(m_offlineMessage.size() > 0)
        {
            ModelMessage message = m_offlineMessage.get(0);

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);

            //syncClient.setTimeout(TIME_OUT);
            asyncHttpClient.setConnectTimeout(30000);
            asyncHttpClient.setResponseTimeout(30000);
            asyncHttpClient.setMaxRetriesAndTimeout(3, 10000);

            asyncHttpClient.addHeader("authorization", "Bearer " + access_token);

            RequestParams params = new RequestParams();

            params.put("message", message.message);
            params.put("type", message.type);

            if(message.type.contentEquals("message") == false)
            {
                try
                {
                    params.put("attachment", new File(message.file));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    removeFirstMessage();
                    return false;
                }
            }

            m_isUploading = true;

            Log.d(Defines.APP_LOG_TITLE, "sending message - " + message.message + "-" + message.file);

            asyncHttpClient.post(ConstValue.SEND_MESSAGE_URL, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String paramString = new String(responseBody);
                    long sendTime = 0;

                    try
                    {
                        JSONObject jsonData = new JSONObject(paramString);

                        boolean bRet = jsonData.getBoolean("Status");
                        String message = jsonData.getString("Message");
                        sendTime = jsonData.getJSONObject("Data").getLong("Time");

                        if(m_offlineMessage.size() > 0) {
                            removeFirstMessage();
                        }

                        sendSuccessBroadcast();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Log.d(Defines.APP_LOG_TITLE, "sending message -success");
                    m_isUploading = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    m_isUploading = false;
                    Log.d(Defines.APP_LOG_TITLE, "sending message - failed");
                }
            });

        }

        return true;
    }

    public void sendSuccessBroadcast()
    {
        Log.d(Defines.APP_LOG_TITLE, "offlinemessage_sent");
        Intent updates = new Intent("sadeem_notification");
        updates.putExtra("type", "offlinemessage_sent");
        sendBroadcast(updates);
    }

    public void removeFirstMessage()
    {
        if(m_offlineMessage.size() > 0)
        {
            ModelMessage message = m_offlineMessage.get(0);
            if(message.file.equals("") == false)
            {
                try
                {
                    new File(message.file).delete();
                }
                catch (Exception ex)
                {

                }
            }

            m_offlineMessage.remove(0);

            saveOfflineData();
        }
    }

    public boolean addOfflineChat(ModelMessage message)
    {
        synchronized (lock)
        {
            if(message.file.equals("") == false)
            {
                try
                {
                    String dir_path = getTempDir();
                    String filename = GlobalFunction.getCurTimeString() + "_" + message.file.substring(message.file.lastIndexOf("."));

                    String file_path  = dir_path + File.separator + filename;
                    FileUtil.copySingleFile(new File(message.file), new File(file_path));
                    message.file = file_path;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
            }

            message.offlinemessage = true;
            m_offlineMessage.add(message);

            saveOfflineData();
        }

        return true;
    }

    public static String getTempDir()
    {
        String tempPath = "";
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/sadeemlight/temp_offline");

        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.d("App", "failed to create directory");
                return "";
            }
        }

        tempPath = dir.getAbsolutePath();

        return tempPath;
    }

    public void loadOfflineData()
    {
        ArrayList<HashMap<String, String>> offlineData = null;

        String t1 = settings.getString("sadeemlight" + "offlinechat", "");
        try
        {
            offlineData = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(t1);
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }

        if(offlineData == null)
        {
            offlineData = new ArrayList<HashMap<String, String>>();
        }

        m_offlineMessage = new ArrayList<>();
        for (int i = 0; i < offlineData.size(); i++)
        {
            ModelMessage message = new ModelMessage();

            message.id =  offlineData.get(i).get("message_id");
            message.fromName = offlineData.get(i).get("fromName");
            message.message = offlineData.get(i).get("message");
            message.msg_time = offlineData.get(i).get("msg_time");

            message.isSelf = Boolean.parseBoolean(offlineData.get(i).get("is_sender"));

            message.file = offlineData.get(i).get("file");
            message.type = offlineData.get(i).get("type");

            message.is_read = Integer.parseInt(offlineData.get(i).get("is_read"));
            message.loaded = Boolean.parseBoolean(offlineData.get(i).get("loaded"));
            message.offlinemessage = true;

            m_offlineMessage.add(message);
        }
    }

    public void saveOfflineData()
    {
        ArrayList<HashMap<String, String>> offlineData;
        offlineData = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < m_offlineMessage.size(); i++)
        {
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("message_id", m_offlineMessage.get(i).id);
            map.put("is_sender", String.valueOf(m_offlineMessage.get(i).isSelf));
            map.put("msg_time",  m_offlineMessage.get(i).msg_time);
            map.put("message", m_offlineMessage.get(i).message);
            map.put("file", m_offlineMessage.get(i).file);
            map.put("type", m_offlineMessage.get(i).type);
            map.put("is_read", String.valueOf(m_offlineMessage.get(i).is_read));
            map.put("loaded", String.valueOf(m_offlineMessage.get(i).loaded));
            map.put("fromName", m_offlineMessage.get(i).fromName);

            offlineData.add(map);
        }

        try {
            settings.edit().putString("sadeemlight" + "offlinechat", ObjectSerializer.serialize(offlineData)).commit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
