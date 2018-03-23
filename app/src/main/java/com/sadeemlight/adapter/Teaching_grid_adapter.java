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
import com.sadeemlight.activity.Teaching_grid_model;

import java.util.List;

import com.sadeemlight.util.Circle_textview;

/**
 * Created by Rajesh Dabhi on 2/9/2016.
 */
public class Teaching_grid_adapter extends BaseAdapter {

    private Context context;
    private List<Teaching_grid_model> mlistItems;

    Circle_textview ct;

    public Teaching_grid_adapter(Context context, List<Teaching_grid_model> Items) {
        this.context = context;
        this.mlistItems = Items;

        ct = new Circle_textview();
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

        Teaching_grid_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.teaching_grid_items,null);

        //ImageView nav_icon = (ImageView) convertView.findViewById(R.id.iv_home_item_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_teaching_grid_subject);

        txtTitle.setText(m.getTitle());
        txtTitle.setBackgroundDrawable(ct.drawCircle(context, 80, 80,
                context.getResources().getColor (m.getColor())));

        /*if(m.getId().contentEquals("Maths")) {
            txtTitle.setText(m.getId());
            txtTitle.setBackgroundResource(R.drawable.shape_round_textview_gird_1);

        }else if(m.getId().contentEquals("English")) {
            txtTitle.setText(m.getId());
            txtTitle.setBackgroundResource(R.drawable.shape_round_textview_gird_2);

        } else if(m.getId().contentEquals("Science")){
            txtTitle.setText(m.getId());
            txtTitle.setBackgroundResource(R.drawable.shape_round_textview_gird_3);
        }*/

        //nav_icon.setImageResource(m.getImg_icon_id());

        return convertView;
    }
}
