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

import com.sadeemlight.venus_model.ModelSubject;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class LibraryAdapter extends BaseAdapter implements Filterable {

    private ModelFilter ModelFilter;
    private List<ModelSubject> allModelItemsArray;
    private List<ModelSubject> filteredModelItemsArray;

    private Context context;
    private List<ModelSubject> mlistItems;

    public LibraryAdapter(Context context, List<ModelSubject> Items) {
        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<ModelSubject>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<ModelSubject>();
        filteredModelItemsArray.addAll(allModelItemsArray);
        getFilter();
    }

    @Override
    public int getCount() {
        return mlistItems.size() + 1;
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

        if(position == 0)
        {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.library_list_publicitems,null);
        }
        else
        {
            ModelSubject m = mlistItems.get(position-1);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.library_list_privateitems,null);

            TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_library_list_item_title);
            //TextView txtsubtitle = (TextView) convertView.findViewById(R.id.tv_library_list_item_subtitle);

            txtTitle.setText(m.getName());
            //txtsubtitle.setText(m.getName());
        }

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
                ArrayList<ModelSubject> filteredItems = new ArrayList<ModelSubject>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    ModelSubject m = allModelItemsArray.get(i);
                    if (m.getId().toLowerCase().contains(constraint))
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

            filteredModelItemsArray = (ArrayList<ModelSubject>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
