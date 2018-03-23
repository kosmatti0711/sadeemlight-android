package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sadeemlight.activity.Homework_list_model;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 18/8/2016.
 */
public class Homework_list_adapter extends BaseAdapter implements Filterable {

    private ModelFilter ModelFilter;
    private List<Homework_list_model> allModelItemsArray;
    private List<Homework_list_model> filteredModelItemsArray;

    private Context context;
    private List<Homework_list_model> mlistItems;

    public Homework_list_adapter(Context context, List<Homework_list_model> Items) {
        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<Homework_list_model>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<Homework_list_model>();
        filteredModelItemsArray.addAll(allModelItemsArray);
        getFilter();
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

        Homework_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.home_work_list_item,null);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_homework_list_title);
        TextView txtDatetime = (TextView) convertView.findViewById(R.id.tv_homework_list_date);
        TextView txtClass = (TextView) convertView.findViewById(R.id.tv_homework_item_class);
        TextView txtDetail = (TextView) convertView.findViewById(R.id.tv_homework_list_detail);

        txtTitle.setText(m.getTitle());
        txtDatetime.setText(m.getDate_time());
        txtClass.setText(m.getClassname());
        txtDetail.setText(m.getDetail());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (ModelFilter == null) {

            ModelFilter = new ModelFilter();
        }

        return ModelFilter;
    }

    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Homework_list_model> filteredItems = new ArrayList<Homework_list_model>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    Homework_list_model m = allModelItemsArray.get(i);
                    if (m.getTitle().toLowerCase().contains(constraint))
                        filteredItems.add(m);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

            filteredModelItemsArray = (ArrayList<Homework_list_model>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
