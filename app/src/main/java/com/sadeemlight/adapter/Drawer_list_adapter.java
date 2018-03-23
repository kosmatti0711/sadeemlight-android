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

import com.sadeemlight.activity.Drawer_list_model;
import com.sadeemlight.R;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 11/8/2016.
 */
public class Drawer_list_adapter extends BaseAdapter {

    private Context context;
    private List<Drawer_list_model> mlistItems;

    public Drawer_list_adapter(Context context, List<Drawer_list_model> Items) {
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

        Drawer_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.drawer_list_item,null);

        ImageView nav_icon = (ImageView) convertView.findViewById(R.id.iv_drawer_icon);
        TextView nav_txt = (TextView) convertView.findViewById(R.id.tv_drawer_txt);
        TextView nav_number = (TextView) convertView.findViewById(R.id.tv_drawer_number);

        nav_txt.setText(m.getTitle());
        nav_icon.setImageResource(m.getImg_icon_id());

        if(position == 0){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.border_color));
        }

        if(m.getNumber() == ""){
            nav_number.setVisibility(View.GONE);

        }else{
            nav_number.setVisibility(View.VISIBLE);

            nav_number.setText(m.getNumber());
        }

        return convertView;
    }
}
