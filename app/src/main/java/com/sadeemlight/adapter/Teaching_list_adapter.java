package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sadeemlight.R;
import com.sadeemlight.activity.Teaching_list_model;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 7/9/2016.
 */
public class Teaching_list_adapter extends BaseAdapter {

    private Context context;
    private List<Teaching_list_model> mlistItems;

    public Teaching_list_adapter(Context context, List<Teaching_list_model> Items) {
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

        Teaching_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.teaching_list_items,null);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_teaching_book_title);

        txtTitle.setText(m.getTitle());

        return convertView;
    }
}
