package com.sadeemlight.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sadeemlight.R;
import com.sadeemlight.util.ConnectivityReceiver;
import com.sadeemlight.venus_model.ModelMessage;
import com.universalvideoview.UniversalVideoView;

import org.json.JSONObject;

import java.io.File;

import com.sadeemlight.config.ConstValue;
import cz.msebera.android.httpclient.Header;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.venus_uis.VideoPreviewActivity;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by VENUS on 5/26/2017.
 */

public class ChatAttatchViewMedia  implements UniversalVideoView.VideoViewCallback, PopupWindow.OnDismissListener
{


    View mRootView;
    Context mParent;
    ChatActivity mParentFragment;

    View mLayoutVideo;
    ImageView mAttachImage;
    ImageView mVideoThumb;

    String m_filepath = "";
    String m_typeString = "";
    int m_mediaType;
    public ChatAttatchViewMedia(Fragment parent)
    {
        mParent = parent.getContext();
        mParentFragment = (ChatActivity)parent;
        mRootView = mParentFragment.mRootView.findViewById(R.id.layout_viewmedia);

        initView();
    }

    public void initView()
    {
        mLayoutVideo = mRootView.findViewById(R.id.video_layout);
        mAttachImage = (ImageView) mRootView.findViewById(R.id.attach_image);
        mVideoThumb = (ImageView) mRootView.findViewById(R.id.image_video_thumb);

        mRootView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mVideoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPreviewActivity.showActivity(mParent, m_filepath);
            }
        });

        mRootView.findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String access_token = mParentFragment.sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
                final String fromName = mParentFragment.sessionManagement.getUserDetails().get(ConstValue.KEY_NAME);
                sendAttachment(mParent, fromName, access_token, m_typeString, m_filepath, new OnSendAttachmentSuccessListener() {
                    @Override
                    public void onSendAttachemntSucess() {

                        dismiss();
                        mParentFragment.updateMessageList();
                    }

                    @Override
                    public void onSendAttachemntFailed() {
                        dismiss();
                        final ModelMessage sendMessage = new ModelMessage(
                                "",
                                fromName,
                                "",
                                GlobalFunction.getCurrentDateTime(),
                                "1",
                                m_filepath,
                                m_typeString,
                                "0",
                                false);

                        if(ChatOfflineService.instance != null)
                        {
                            ChatOfflineService.instance.addOfflineChat(sendMessage);
                        }

                        mParentFragment.refreshList();
                    }
                });
            }
        });
        dismiss();
    }

    public void onViewMedia(String file_path, int mediaType) {
        m_filepath = file_path;
        m_mediaType = mediaType;
        if(mediaType == ChatAttatchChooseMedia.ATTACH_MEDIA_IMAGE)
        {
            mAttachImage.setVisibility(View.VISIBLE);
            mVideoThumb.setVisibility(View.INVISIBLE);

            try
            {
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap bitmap = GlobalFunction.loadImageWithShrink(file_path);//BitmapFactory.decodeFile(takenPhotoUri.getPath());

                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = GlobalFunction.rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = GlobalFunction.rotateImage(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = GlobalFunction.rotateImage(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        break;
                }
                mAttachImage.setImageBitmap(bitmap);
                m_typeString = "image";
            }
            catch (Exception e)
            {
                GlobalFunction.outputToast(mParent, "Failed to capture image");
                e.printStackTrace();
            }
        }
        else if(mediaType == ChatAttatchChooseMedia.ATTACH_MEDIA_VIDEO)
        {
            mAttachImage.setVisibility(View.VISIBLE);
            mVideoThumb.setVisibility(View.VISIBLE);

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(m_filepath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            mAttachImage.setImageBitmap(thumb);
            m_typeString = "video";
        }
        else if(mediaType == ChatAttatchChooseMedia.ATTACH_MEDIA_MUSIC)
        {
            mAttachImage.setVisibility(View.VISIBLE);
            mVideoThumb.setVisibility(View.VISIBLE);

            mAttachImage.setImageResource(R.drawable.music_icon);
            m_typeString = "audio";
        }

    }

    public void dismiss()
    {
        mRootView.setVisibility(View.GONE);
    }

    public void show()
    {
        mRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    public interface OnSendAttachmentSuccessListener
    {
        public void onSendAttachemntSucess();
        public void onSendAttachemntFailed();
    }

    public static void sendAttachment(final Context context, String fromName,  String access_token, String type, String file_path, final OnSendAttachmentSuccessListener listener)
    {
        if(type.equals("") == true)
        {
            return;
        }

        if(ConnectivityReceiver.isConnected() == false)
        {
            listener.onSendAttachemntFailed();
            return;
        }

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        asyncHttpClient.addHeader("authorization", "Bearer " + access_token);

        RequestParams params = new RequestParams();

        params.put("message", "");
        params.put("type", type);

        try
        {
            params.put("attachment", new File(file_path));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            GlobalFunction.outputToast(context, "File not found");
            return;
        }

        final Progress_dialog pd = new Progress_dialog(context);
        pd.showProgressbar();
        asyncHttpClient.post(ConstValue.SEND_MESSAGE_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                long sendTime = 0;
                pd.dismissProgress();
                try
                {
                    JSONObject jsonData = new JSONObject(paramString);

                    boolean bRet = jsonData.getBoolean("Status");
                    String message = jsonData.getString("Message");
                    sendTime = jsonData.getJSONObject("Data").getLong("Time");

                    //bRet = true;
                    if(bRet == true)
                    {
                        listener.onSendAttachemntSucess();
                    }
                    else
                    {
                        GlobalFunction.outputToast(context, message);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismissProgress();

                listener.onSendAttachemntFailed();

                if (responseBody == null)
                {
                    GlobalFunction.outputToast(context, "Connection Error");
                    return;
                }
                String paramString = new String(responseBody);
                long sendTime = 0;

                try
                {
                    JSONObject jsonData = new JSONObject(paramString);

                    boolean bRet = jsonData.getBoolean("Status");
                    String message = jsonData.getString("Message");

                    GlobalFunction.outputToast(context, message);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    GlobalFunction.outputToast(context, paramString);
                }
            }
        });
    }


    @Override
    public void onDismiss()
    {
        m_filepath = "";
        m_typeString = "";
        m_mediaType = -1;
    }
}
