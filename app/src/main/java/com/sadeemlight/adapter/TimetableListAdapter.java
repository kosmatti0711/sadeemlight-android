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
import com.sadeemlight.venus_model.ModelTimetable;
import com.sadeemlight.venus_uis.ordinalnumbers.OrdinalNumbers;

import java.util.List;

/**
 * Created by Rajesh Dabhi on 1/9/2016.
 */
public class TimetableListAdapter extends BaseAdapter {

    private Context context;
    private List<ModelTimetable> mlistItems;

    public TimetableListAdapter(Context context, List<ModelTimetable> Items) {
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

        ModelTimetable m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.timetable_item,null);

        TextView subjectName = (TextView) convertView.findViewById(R.id.text_subjectname);
        TextView teacherName = (TextView) convertView.findViewById(R.id.text_teacher);
        TextView lesson_no = (TextView) convertView.findViewById(R.id.text_lessonno);

        subjectName.setText(m.subjectName);
        teacherName.setText(!m.teacherName.contentEquals("null") ? m.teacherName  :"" );
        lesson_no.setText(OrdinalNumbers.getInstance().format(m.lesson_no));

        return convertView;
    }
}
