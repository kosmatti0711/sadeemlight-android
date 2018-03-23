package com.sadeemlight.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;

import com.sadeemlight.venus_uis.ImageViewActivity;

/**
 * Created by Rajesh Dabhi on 31/8/2016.
 */
public class NotificationDetailFragment extends Fragment {

    TextView tv_title,tv_date,tv_detail;
    WebView  mWebView;
    ImageView mImageView;

    public NotificationDetailFragment() {
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
        View view = inflater.inflate(R.layout.notification_detail_activity, container, false);

        ((MainActivity)getActivity()).setTitle(R.string.side_menu_notification);

        tv_title = (TextView) view.findViewById(R.id.tv_notific_detail_title);
        tv_date = (TextView) view.findViewById(R.id.tv_notific_detail_date);
        tv_detail = (TextView) view.findViewById(R.id.tv_notific_detail_detail);
        mImageView = (ImageView) view.findViewById(R.id.notf_image);
        mWebView = (WebView)view.findViewById(R.id.webView1);
        tv_detail.setVisibility(View.GONE);


        try{

            String gettitle = getArguments().getString("title");
            String getdate = getArguments().getString("msg_time");
            String getdetail = getArguments().getString("detail");
            String getImage = getArguments().getString("image");

            if(getImage.contentEquals("") == true || getImage.contentEquals("null"))
            {
                mImageView.setVisibility(View.GONE);
            }
            else
            {
                mImageView.setVisibility(View.VISIBLE);

                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                        ImageViewActivity.showImageByBitmap(getContext(), bitmap);
                    }
                });
            }


            tv_title.setText(gettitle);
            tv_date.setText(getdate);
            tv_detail.setText(getdetail);

            if(isArabicText(getdetail))
            {
                getdetail = "<body dir=\"rtl\">\n" +
                        "\n" + getdetail + "\n" +
                        "</body>";
            }
            else
            {
                getdetail = "<body dir=\"ltl\">\n" +
                        "\n" + getdetail + "\n" +
                        "</body>";
            }

            mWebView.loadData(getdetail, "text/html; charset=UTF-8", null);

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

            ImageLoader.getInstance().displayImage(getImage, mImageView, options);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return view;
    }

    public static boolean isArabicText(String text)
    {
        if (text.matches("(?s).*[\\u0600-\\u06FF\\u0750-\\u077F\\uFB50-\\uFDFF\\uFE70‌​-\\uFEFF].*"))
        {
            return true;
        }
        else if (text.matches("(?s).*\\p{InArabic}.*"))
        {
            return true;
        }

        return false;
    }
}
