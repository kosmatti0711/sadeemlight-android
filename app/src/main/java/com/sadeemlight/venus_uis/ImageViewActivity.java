package com.sadeemlight.venus_uis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sadeemlight.R;
import com.sadeemlight.util.ZoomableImageView;
import com.sadeemlight.venus_uis.utils.GlobalFunction;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ImageViewActivity extends AppCompatActivity {
    public static Bitmap m_image;
    public static String m_path = null;

    private ZoomableImageView m_imagePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        m_imagePanel = (ZoomableImageView) findViewById(R.id.idcImagePanel);

        if(m_path != null)
        {
            m_image = null;
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.default_image2)
                    .showImageForEmptyUri(R.drawable.default_image2)
                    .showImageOnFail(R.drawable.default_image2)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.NONE)
                    .build();

            ImageLoader.getInstance().displayImage(m_path, m_imagePanel, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    m_image = null;
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    m_image = loadedImage;
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            Picasso.with(this).load(m_path).into(m_imagePanel);
        }
        else if(m_image != null)
        {
            m_imagePanel.setImageBitmap(m_image);
        }

        findViewById(R.id.button_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShareBitmap();
            }
        });
    }

    public void saveShareBitmap()
    {
        final CharSequence[] items = { this.getString(R.string.Save),
                this.getString(R.string.Share),
                this.getString(R.string.Cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.ShareImage));
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int item)
            {
                if (item == 0)
                {
                    saveBitmap();
                }
                else if (item == 1) {
                    shareBitmap();
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public boolean saveBitmap()
    {
        if(m_image != null)
        {
            MediaStore.Images.Media.insertImage(getContentResolver(), m_image, "SadeemLight", "SadeemLight");
        }
        else
        {
            return false;
        }

        return true;
    }

    public boolean shareBitmap()
    {
        if(m_image == null) return false;

        OutputStream output;
        // Create a new folder AndroidBegin in SD Card
        File dir = new File(GlobalFunction.getTempFolderPath());
        // Create a name for the saved image
        File file = new File(dir, "temp_share.png");

        try {

            // Share Intent
            Intent share = new Intent(Intent.ACTION_SEND);

            // Type of file to share
            share.setType("image/*");

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            m_image.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

            // Locate the image to Share
            Uri uri = Uri.fromFile(file);

            // Pass the image into an Intnet
            share.putExtra(Intent.EXTRA_STREAM, uri);

            // Show the social share chooser list
            startActivity(Intent.createChooser(share, getString(R.string.Share)));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void showImageByBitmap(Context parent, Bitmap img)
    {
        m_path = null;
        m_image = img;
        Intent m = new Intent(parent, ImageViewActivity.class);
        parent.startActivity(m);
    }

    public static void showImageByUrl(Context parent, String path)
    {
        m_path = path;
        m_image = null;
        Intent m = new Intent(parent, ImageViewActivity.class);
        parent.startActivity(m);
    }


}
