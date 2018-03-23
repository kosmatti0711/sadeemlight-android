package com.sadeemlight.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.activity.LibraryDetailFragment;
import com.sadeemlight.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.venus_model.ModelBook;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by Rajesh Dabhi on 20/8/2016.
 */
public class LibraryDetailsAdapter extends BaseAdapter implements View.OnClickListener
{
    public static final int[] background_image_id = {
            R.drawable.library_book1,
            R.drawable.library_book2,
            R.drawable.library_book3,
            R.drawable.library_book4,
            R.drawable.library_book5,
            R.drawable.library_book6,
    };

    private LibraryDetailFragment m_parent;
    private Context mConext;
    private List<ModelBook> m_baseData = new ArrayList<>();

    public LibraryDetailsAdapter(Context context, LibraryDetailFragment parent)
    {
        this.mConext = context;
        this.m_parent = parent;
    }

    @Override
    public int getCount() {
        return m_baseData.size();
    }

    @Override
    public Object getItem(int position) {
        return m_baseData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ModelBook m = m_baseData.get(position);
        final ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) mConext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.library_list_details,null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.model = m;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.library_book)
                .showImageForEmptyUri(R.drawable.library_book)
                .showImageOnFail(R.drawable.library_book)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        ImageLoader.getInstance().displayImage(m.cover, holder.imageCover, options);

        int backId = background_image_id[position % background_image_id.length];
        holder.imageBack.setImageResource(backId);
        holder.bookName.setText(m.book_name);
        holder.bookCounter.setText(GlobalFunction.formatValueString(m.readers));

        holder.bookName.setOnClickListener(this);

        return convertView;
    }

    public void updateData(List<ModelBook> data)
    {
        m_baseData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.text_bookname)
        {
            ViewHolder holder = (ViewHolder) v.getTag();
            ModelBook model = holder.model;

            new getBook().execute(model.book_id);
        }
    }

    public class getBook extends AsyncTask<String, Void, Void> {

        Progress_dialog pd = new Progress_dialog(mConext);
        String response = "";
        String message = "";
        ModelBook data = new ModelBook();

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String bookid = "";
            if(params != null)
            {
                for(String s : params)
                {
                    bookid = s;
                    break;
                }
            }

            response = "";

            String access_token = m_parent.sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            ServiceHandler sh = new ServiceHandler();

            String url = "";

            if(m_parent.m_subjectID.equals("public"))
            {
                url = ConstValue.LIBRARY_BOOK_PUBLIC_URL + bookid;
            }
            else
            {
                url = ConstValue.LIBRARY_BOOK_PRIVATE_URL + bookid;
            }

            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.GET, null, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null)
            {
                Log.d(ConstValue.APPTITLE, jsonSTR);

                try {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    JSONObject bookData = jsonObject.getJSONObject("Data").getJSONObject("Book");

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                    data.book_id = bookData.getString("book_id");
                    data.book_date = bookData.getString("book_date");
                    data.book_name = bookData.getString("book_name");
                    data.book_link = bookData.getString("book_link");
                    data.cover = bookData.getString("cover");
                    data.readers = bookData.getInt("reader");
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
            pd.dismissProgress();
            if (response == "true")
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.book_link));
                mConext.startActivity(browserIntent);
            }
            else
            {
                Toast.makeText(mConext, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ViewHolder
    {
        ModelBook model;

        TextView  bookName;
        TextView  bookCounter;
        ImageView imageBack;
        ImageView imageCover;

        public ViewHolder(View convertView)
        {
            bookName = (TextView) convertView.findViewById(R.id.text_bookname);
            bookCounter = (TextView) convertView.findViewById(R.id.text_counter);
            imageBack = (ImageView) convertView.findViewById(R.id.image_back);
            imageCover = (ImageView) convertView.findViewById(R.id.image_cover);
            bookName.setTag(this);
            bookCounter.setTag(this);
            imageBack.setTag(this);
            imageCover.setTag(this);
        }
    }
}
