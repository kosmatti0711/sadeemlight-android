package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.venus_model.ModelAddAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 10/9/2016.
 */
public class AddAccountAdapter extends BaseAdapter {
    private List<ModelParamsPair> params = new ArrayList<>();

    private Context context;
    private List<ModelAddAccount> mlistItems;
    private DisplayImageOptions options;
    public int nameColor = R.color.dark_text_color;

    boolean allowDelete = true;

    Session_management sessionManagement;

    int getlistposition;

    private FragmentActivity myContext;
    String delAccessToken = "";

    public AddAccountAdapter(Context context, List<ModelAddAccount> Items) {
        this.context = context;
        myContext=(FragmentActivity) context;
        this.mlistItems = Items;

        sessionManagement = new Session_management(context);

        try {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
            ImageLoader.getInstance().init(config);

            options = new DisplayImageOptions.Builder()
                    //.showImageOnLoading(R.drawable.ic_login_icon)
                    //.showImageForEmptyUri(R.drawable.ic_login_icon)
                    //.showImageOnFail(R.drawable.ic_login_icon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.NONE)
                    .build();
        } catch (NullPointerException e) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        final ModelAddAccount m = mlistItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.add_account_list_items, null);

        TextView txtName = (TextView) convertView.findViewById(R.id.tv_addaccount_name);
        ImageView iv_frd_img = (ImageView) convertView.findViewById(R.id.iv_addaccount_icon);
        TextView txtSchool = (TextView) convertView.findViewById(R.id.tv_addaccount_school);
        ImageView iv_delete = (ImageView) convertView.findViewById(R.id.iv_addaccount_delete);

        txtName.setText(m.getName());
        txtName.setTextColor(context.getResources().getColor(nameColor));
        txtSchool.setText(m.getSchool_name());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(m.getFrd_img_id(), iv_frd_img, options);


        final String getparent_id = sessionManagement.getUserDetails().get(ConstValue.KEY_PARENT_ID);

        Fragment fr = myContext.getSupportFragmentManager().findFragmentById(R.id.container);

        String fm_name = fr.getClass().getSimpleName();

        Log.e("namescontext",fm_name);

        if(allowDelete) {

            iv_delete.setVisibility(View.VISIBLE);

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    params.clear();

                    getlistposition = position;

                    String get_student_id = m.getStudent_id();

                    //params.add(new ModelParamsPair("parent_id", getparent_id));
                    //params.add(new ModelParamsPair("student_id", get_student_id));

                    params.add(new ModelParamsPair("parent_id", getparent_id));
                    delAccessToken = m.access_token;

                    new deleteAccount().execute();
                }
            });
        }else{
            iv_delete.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setAllowDelete(boolean b)
    {
        allowDelete = b;
    }
    public class deleteAccount extends AsyncTask<Void, Void, Void> {
        String response = "";
        String message = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            ServiceHandler sh = new ServiceHandler();

            String access_token = delAccessToken;//sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(ConstValue.ACCOUNT_DELETE, ConstValue.DELETE, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    if(response.contentEquals("true"))
                    {

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response.contentEquals("true"))
            {

                mlistItems.remove(getlistposition);
                notifyDataSetChanged();
            }
        }
    }
}