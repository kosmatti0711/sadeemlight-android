package com.sadeemlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sadeemlight.adapter.ChatAdapter;
import com.sadeemlight.config.ConstValue;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.util.ObjectSerializer;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_model.ModelMessage;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 3/8/2016.
 */
public class ChatActivity extends BaseFragment implements View.OnClickListener {

    List<ModelParamsPair> params = new ArrayList<>();

    //private Button btnSend;
    ImageView emojiButton;
    private EmojiconEditText inputMsg;
    private ImageButton btnSend;

    // Chat messages list m_subjectAdapter
    private ChatAdapter m_adapter;
    private List<ModelMessage> m_MessageData = new ArrayList<ModelMessage>();
    private ListView mListView;
    public View mRootView;


    private ArrayList<HashMap<String, String>> m_offlineData;
    public SharedPreferences settings;
    public Session_management sessionManagement;
    private long m_seenTime = -1;

    public ChatActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_activity, container, false);
        mRootView = view;
        ((MainActivity) getActivity()).setTitle(R.string.side_menu_message);
        ((MainActivity)getActivity()).setTabindicatorfm();

        initView(view);
        initEmojicon(view);

        sessionManagement = new Session_management(getActivity());
        loadOfflineData();
        return view;
    }

    public void loadOfflineData()
    {
        settings = getActivity().getSharedPreferences("MAIN_PREF", 0);
        m_offlineData = new ArrayList<HashMap<String, String>>();

        try
        {
            m_offlineData = (ArrayList<HashMap<String, String>>)
                    ObjectSerializer.deserialize(settings.getString("sadeemlight" + "chat",
                    ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        refreshList();
        if (ConnectivityReceiver.isConnected())
        {
            new getMessages().execute();
        }

        m_seenTime = settings.getLong("sadeemlight" + "chat-seentime", -1);
    }

    public void saveOfflineData()
    {
        try {
            settings.edit().putString("sadeemlight" + "chat", ObjectSerializer.serialize(m_offlineData)).commit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        settings.edit().putLong("sadeemlight" + "chat-seentime", m_seenTime);
    }

    @Override
    public void onClick(View view)
    {
        String messageText = inputMsg.getText().toString().trim();
        //Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();

        messageText = GlobalFunction.encodeMessage(messageText);

        if (!messageText.contentEquals(""))
        {
            params.clear();
            params.add(new ModelParamsPair("message", messageText));

            inputMsg.setText("");
            inputMsg.setClickable(false);

            new sendMessage().execute();
        }
    }

    public class getMessages extends AsyncTask<Void, Void, Void> {

        String response = "";
        String message = "";
        boolean schoolOnline = false;
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //pd.showProgressbar();
            mListView.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.GET_MESSAGE_URL;

            if(m_seenTime > 0)
            {
                url = url + "?time=" + m_seenTime;
            }
            else
            {
                m_offlineData.clear();
            }

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {


                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");
                    m_seenTime = jsonObject.getJSONObject("Data").getLong("Time");
                    schoolOnline = jsonObject.getJSONObject("Data").getBoolean("isSchoolOnline");
                    JSONArray jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Messages");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("message_id", c.getString("message_id"));
                        map.put("is_sender", c.getString("is_student_sender"));
                        map.put("msg_time", c.getString("message_time"));
                        map.put("message", GlobalFunction.decodeMessage(c.getString("message")));
                        map.put("file", c.getString("file"));
                        map.put("type", c.getString("type"));
                        map.put("is_read", c.getString("is_read"));

                        m_offlineData.add(map);
                    }

                    if(jsonObject.getJSONObject("Data").has("Seen"))
                    {
                        jsonArray = jsonObject.getJSONObject("Data").getJSONArray("Seen");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject c = jsonArray.getJSONObject(i);
                            int seenId = c.getInt("message_id");

                            for (int j = 0; j < m_offlineData.size(); j++)
                            {
                                HashMap<String, String> map = m_offlineData.get(j);
                                int msg_id = GlobalFunction.tryParseInt(map.get("message_id"));

                                if(seenId == msg_id)
                                {
                                    map.put("is_read", "1");
                                }
                            }
                        }
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
        protected void onPostExecute(Void aVoid)
        {
            try
            {
                super.onPostExecute(aVoid);
                pd.dismissProgress();

                if (response == "true")
                {
                    saveOfflineData();
                }

                refreshList();

                if(schoolOnline)
                {
                    ((MainActivity) getActivity()).setSubTitle(getContext().getString(R.string.online));
                }
                else
                {
                    ((MainActivity) getActivity()).setSubTitle(getContext().getString(R.string.offline));
                }

                mListView.setEnabled(true);
            }
            catch (Exception ex){}
        }
    }



    public void refreshList() {
        m_MessageData.clear();
        for (int i = 0; i < m_offlineData.size(); i++)
        {
            m_MessageData.add(new ModelMessage(
                    m_offlineData.get(i).get("message_id"),
                    sessionManagement.getUserDetails().get(ConstValue.KEY_NAME),
                    m_offlineData.get(i).get("message"),
                    m_offlineData.get(i).get("msg_time"),
                    m_offlineData.get(i).get("is_sender"),
                    m_offlineData.get(i).get("file"),
                    m_offlineData.get(i).get("type"),
                    m_offlineData.get(i).get("is_read"),
                    m_offlineData.get(i).get("is_read").contentEquals("1")));
        }

        if(ChatOfflineService.m_offlineMessage.size() > 0)
        {
            for (int i=0;i<ChatOfflineService.m_offlineMessage.size();i++)
            {
                m_MessageData.add(ChatOfflineService.m_offlineMessage.get(i));
            }
        }

        m_adapter.updateData(m_MessageData);
    }

    public void addData(ModelMessage newData)
    {
        m_MessageData.add(newData);
        m_adapter.updateData(m_MessageData);
    }

    public class sendMessage extends AsyncTask<Void, Void, Void> {

        String response = "";
        String message = "";
        long sendTime = 0;

        ModelMessage sendMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSend.setEnabled(false);

            sendMessage = new ModelMessage(
                    "",
                    sessionManagement.getUserDetails().get(ConstValue.KEY_NAME),
                    GlobalFunction.decodeMessage(params.get(0).getValue()),
                    GlobalFunction.getCurrentDateTime(),
                    "1",
                    "",
                    "message",
                    "0",
                    false);

            sendMessage.offlinemessage = true;
            addData(sendMessage);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            if(ConnectivityReceiver.isConnected() == false)
            {
                if(ChatOfflineService.instance != null)
                {
                    ChatOfflineService.instance.addOfflineChat(sendMessage);
                }

                return null;
            }

            ServiceHandler sh = new ServiceHandler();
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.SEND_MESSAGE_URL, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(ConstValue.APPTITLE, jsonSTR);

            boolean success = false;
            if (jsonSTR != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    sendTime = jsonObject.getJSONObject("Data").getInt("Time");
                    success = true;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            if(success == false)
            {
                if(ChatOfflineService.instance != null)
                {
                    ChatOfflineService.instance.addOfflineChat(sendMessage);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            btnSend.setEnabled(true);
            if (response.contentEquals("true"))
            {
                updateMessageList();
            }
            else
            {
                if(message.length() > 0) {
                    GlobalFunction.outputToast(getActivity(), message);
                }
            }
        }
    }

    public void updateMessageList() {
        Log.d(ConstValue.APPTITLE, "true");

        new getMessages().execute();

        inputMsg.setText("");
        inputMsg.setClickable(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);

        ((MainActivity) getActivity()).setSubTitle("");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("sadeem_notification"));

        MainActivity.notfMessageCount = 0;
        ((MainActivity)getActivity()).updateNotfCounts();
    }

    ChatAttatchChooseMedia mAttachChooseMedia;
    ChatAttatchViewMedia mAttachViewMedia;
    ChatAttatchRecordingView mAttachRecordView;

    public void initView(final View rootView) {
        btnSend = (ImageButton) rootView.findViewById(R.id.button_send);
        emojiButton = (ImageView) rootView.findViewById(R.id.button_emojikey);
        inputMsg = (EmojiconEditText) rootView.findViewById(R.id.inputMsg);
        mListView = (ListView) rootView.findViewById(R.id.list_view_messages);
        m_adapter = new ChatAdapter(getContext());
        mListView.setAdapter(m_adapter);


        btnSend.setOnClickListener(this);
        mAttachChooseMedia = new ChatAttatchChooseMedia(this);
        rootView.findViewById(R.id.button_attach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalFunction.hideSystemKeyboard(getActivity());
                mAttachChooseMedia.show();
            }
        });
        mAttachViewMedia  = new ChatAttatchViewMedia(this);
        mAttachRecordView = new ChatAttatchRecordingView(this, mRootView,getActivity());
        mAttachRecordView.setOnSendListener(new ChatAttatchRecordingView.OnSendRecordFile() {
            @Override
            public void onSendRecordFile(final String file_path) {

                addData(new ModelMessage(
                        "",
                        sessionManagement.getUserDetails().get(ConstValue.KEY_NAME),
                        "",
                        GlobalFunction.getCurrentDateTime(),
                        "1",
                        file_path,
                        "audio",
                        "0",
                        false));
                String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
                final String fromName = sessionManagement.getUserDetails().get(ConstValue.KEY_NAME);
                ChatAttatchViewMedia.sendAttachment(getContext(), fromName, access_token, "audio", file_path,
                        new ChatAttatchViewMedia.OnSendAttachmentSuccessListener() {
                                @Override
                                public void onSendAttachemntSucess() {
                                    updateMessageList();
                                }

                            @Override
                            public void onSendAttachemntFailed() {
                                final ModelMessage sendMessage = new ModelMessage(
                                        "",
                                        fromName,
                                        "",
                                        GlobalFunction.getCurrentDateTime(),
                                        "1",
                                        file_path,
                                        "audio",
                                        "0",
                                        false);

                                if(ChatOfflineService.instance != null)
                                {
                                    ChatOfflineService.instance.addOfflineChat(sendMessage);
                                }
                                refreshList();
                            }
                });
            }
        });

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        inputMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAttachChooseMedia.dismiss();
            }
        });
    }

    EmojIconActions emojIcon;
    public void initEmojicon(View rootView) {
        emojIcon = new EmojIconActions(getContext(), rootView, inputMsg, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        emojIcon.addEmojiconEditTextList(inputMsg);
    }

    public boolean dispatchTouchEvent(MotionEvent e)
    {
        return true;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if(type.contentEquals("message"))
            {
                Log.i(ConstValue.APPTITLE, "fragment_message");

                if (ConnectivityReceiver.isConnected())
                {
                    new getMessages().execute();
                }


                MainActivity.notfMessageCount = 0;
                ((MainActivity)getActivity()).updateNotfCounts();
            }
            else if(type.contentEquals("offlinemessage_sent"))
            {
                if (ConnectivityReceiver.isConnected())
                {
                    new getMessages().execute();
                }
            }
        }
    };


}
