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
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.activity.Friends_list_model;
import com.sadeemlight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class Friends_list_adapter extends BaseAdapter implements Filterable {

    private ModelFilter ModelFilter;
    private List<Friends_list_model> allModelItemsArray;
    private List<Friends_list_model> filteredModelItemsArray;

    private Context context;
    private List<Friends_list_model> mlistItems;
    private DisplayImageOptions options;

    public Friends_list_adapter(Context context, List<Friends_list_model> Items) {
        this.context = context;
        this.mlistItems = Items;

        this.allModelItemsArray = new ArrayList<Friends_list_model>();
        allModelItemsArray.addAll(Items);
        this.filteredModelItemsArray = new ArrayList<Friends_list_model>();
        filteredModelItemsArray.addAll(allModelItemsArray);
        getFilter();

        try {
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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

        Friends_list_model m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.friends_list_items,null);

        TextView txtName = (TextView) convertView.findViewById(R.id.tv_friend_name);
        TextView txtPoints = (TextView) convertView.findViewById(R.id.tv_friend_point);
        ImageView iv_frd_img = (ImageView) convertView.findViewById(R.id.iv_friend_icon);

        txtName.setText(m.getName());
        txtPoints.setText(m.getPoints());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage( m.getFrd_img_id(), iv_frd_img, options);

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
                ArrayList<Friends_list_model> filteredItems = new ArrayList<Friends_list_model>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    Friends_list_model m = allModelItemsArray.get(i);
                    if (m.getName().toLowerCase().contains(constraint)
                            || m.getPoints().toLowerCase().contains(constraint) )
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

            filteredModelItemsArray = (ArrayList<Friends_list_model>) results.values;
            notifyDataSetChanged();
            mlistItems.clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                mlistItems.add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
