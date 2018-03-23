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

import com.sadeemlight.activity.Daily_degrees_list_model;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 1/9/2016.
 */
public class Daily_degrees_list_adapter extends BaseAdapter implements Filterable {

    private ModelFilter ModelFilter;
    private List<Daily_degrees_list_model> allModelItemsArray;
    private List<Daily_degrees_list_model> filteredModelItemsArray;

    private Context context;
    private List<Daily_degrees_list_model> mlistItems;

    public Daily_degrees_list_adapter(Context context, List<Daily_degrees_list_model> Items) {
        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<Daily_degrees_list_model>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<Daily_degrees_list_model>();
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

        Daily_degrees_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.daily_degrees_list_items,null);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_daily_title);
        TextView txtScoretotal = (TextView) convertView.findViewById(R.id.tv_daily_totalscore);
        TextView txtScore = (TextView) convertView.findViewById(R.id.tv_daily_score);
        TextView txtResult_date = (TextView) convertView.findViewById(R.id.tv_daily_resultdate);

        txtTitle.setText(m.getTitle());
        txtScoretotal.setText(m.getScoretotal());
        txtScore.setText(m.getScore());
        txtResult_date.setText(m.getResult_date());

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
                ArrayList<Daily_degrees_list_model> filteredItems = new ArrayList<Daily_degrees_list_model>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    Daily_degrees_list_model m = allModelItemsArray.get(i);
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

            filteredModelItemsArray = (ArrayList<Daily_degrees_list_model>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
