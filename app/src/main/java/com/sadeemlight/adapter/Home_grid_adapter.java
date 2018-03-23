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

import com.sadeemlight.activity.Home_grid_model;
import com.sadeemlight.R;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 16/8/2016.
 */
public class Home_grid_adapter extends BaseAdapter {

    private Context context;
    private List<Home_grid_model> mlistItems;

    public Home_grid_adapter(Context context, List<Home_grid_model> Items) {
        this.context = context;
        this.mlistItems = Items;
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

        Home_grid_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.home_menu_grid_item,null);

        ImageView nav_icon = (ImageView) convertView.findViewById(R.id.iv_home_item_icon);
        TextView nav_txt = (TextView) convertView.findViewById(R.id.tv_home_item_title);

        nav_txt.setText(m.getTitle());
        nav_icon.setImageResource(m.getImg_icon_id());

        return convertView;
    }
}
