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

import com.sadeemlight.venus_model.ModelNotification;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 31/8/2016.
 */
public class NotificationAdapter extends BaseAdapter implements Filterable {

    private ModelFilter ModelFilter;
    private List<ModelNotification> allModelItemsArray;
    private List<ModelNotification> filteredModelItemsArray;

    private Context context;
    private List<ModelNotification> mlistItems;

    public NotificationAdapter(Context context, List<ModelNotification> Items) {
        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<ModelNotification>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<ModelNotification>();
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

        ModelNotification m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.notification_list_items,null);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_notific_title);
        TextView txtDatetime = (TextView) convertView.findViewById(R.id.tv_notific_date);
        TextView txtDetail = (TextView) convertView.findViewById(R.id.tv_notific_detail);

        txtTitle.setText(m.getTitle());
        txtDatetime.setText(m.getDate_time());
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
                ArrayList<ModelNotification> filteredItems = new ArrayList<ModelNotification>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    ModelNotification m = allModelItemsArray.get(i);
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

            filteredModelItemsArray = (ArrayList<ModelNotification>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
