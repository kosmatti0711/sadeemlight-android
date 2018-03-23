package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.activity.Add_news_comment_list_model;
import com.sadeemlight.R;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 26/8/2016.
 */
public class Add_news_comment_adapter extends BaseAdapter {

    private Context context;
    private List<Add_news_comment_list_model> mlistItems;
    private DisplayImageOptions options;

    public Add_news_comment_adapter(Context context, List<Add_news_comment_list_model> Items) {
        this.context = context;
        this.mlistItems = Items;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_login_icon)
                .showImageForEmptyUri(R.drawable.ic_login_icon)
                .showImageOnFail(R.drawable.ic_login_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
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

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        Add_news_comment_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.add_news_comment_list_item,null);

        TextView txtName = (TextView) convertView.findViewById(R.id.tv_comment_list_name);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_comment_list_title);
        TextView txtDate = (TextView) convertView.findViewById(R.id.tv_comment_list_date);
        ImageView iv_frd_img = (ImageView) convertView.findViewById(R.id.iv_comment_list_icon);

        txtTitle.setText(m.getTitle());
        txtName.setText(m.getName());
        txtDate.setText(m.getDate());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage( m.getFrd_img_id(), iv_frd_img, options);

        return convertView;
    }
}
