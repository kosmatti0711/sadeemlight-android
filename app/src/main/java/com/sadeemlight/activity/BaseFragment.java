package com.sadeemlight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.sadeemlight.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sadeemlight.venus_uis.utils.GlobalFunction;

/**
 * Created by VENUS on 10/22/2016.
 */

public class BaseFragment extends Fragment
{
    String mCurrentPhotoPath;
    String mCurrentVideoPath;
    Uri mVideoUri;

    public interface OnSelectMediaListener
    {
        public void onSelectImage_File(String file_path);
        public void onSelectImage_Bitmap(Bitmap image);
        public void onSelectVideo_File(String file_path);
        public void onSelectAudio_File(String file_path);
    }

    OnSelectMediaListener m_listener = null;

    protected boolean m_isPullDown = false;
    protected ProgressDialog m_waitingDlg = null;
    public void showWaitingDlg(String msg)
    {
        m_waitingDlg = ProgressDialog.show(getContext(), "", msg);
    }

    public void dismissWaitingDlg()
    {
        if(m_waitingDlg != null)
        {
            m_waitingDlg.dismiss();
            m_waitingDlg = null;
        }
    }

    public void initLocalization()
    {

    }

    public void initView()
    {

    }

    public void setOnSelectMediaListener(OnSelectMediaListener listener)
    {
        m_listener = listener;
    }

    public static final int REQUEST_IMAGE_CAMERA = 0, REQUEST_IMAGE_GALLERY = 1;
    public static final int REQUEST_VIDEO_CAMERA = 2, REQUEST_VIDEO_GALLERY = 3;
    public static final int REQUEST_AUDIO_GALLERY = 4;

    public void selectImage()
    {
        final CharSequence[] items = { getActivity().getString(R.string.picker_camera),
                getActivity().getString(R.string.picker_gallery),
                getActivity().getString(R.string.picker_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.picker_imagetitle));
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int item)
            {
                if (item == 0)
                {
                    onStartCamera4Image();
                }
                else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.picker_imagetitle)), REQUEST_IMAGE_GALLERY);
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void selectVideo() {
        final CharSequence[] items = { getActivity().getString(R.string.picker_camera),
                getActivity().getString(R.string.picker_gallery),
                getActivity().getString(R.string.picker_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.picker_imagetitle));
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int item)
            {
                if (item == 0)
                {
                    try {
                        onStartCamera4Video();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");

                    startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.picker_imagetitle)), REQUEST_VIDEO_GALLERY);
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void selectAudio() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("audio/*");
//
//        startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.picker_musictitle)), REQUEST_AUDIO_GALLERY);

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        // intent_upload.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent_upload, getActivity().getString(R.string.picker_musictitle)),REQUEST_AUDIO_GALLERY);

    }

    public void onStartCamera4Image() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.

        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("Error_start_camera",ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.sadeemlight.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            }
            //startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
        }


    }

    public void onStartCamera4Video() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mVideoUri = getOutputMediaFileUri(2);  // create a file to save the video in specific folder (this works for video only)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempFileUri(videoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.

        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivityForResult(intent, REQUEST_VIDEO_CAMERA);
        }
    }


    private void onSelectFromFile(Intent data) {
        String selectedImage = getFilePathFromUri(data);

        try
        {
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bitmap = GlobalFunction.loadImageWithShrink(mCurrentPhotoPath);//BitmapFactory.decodeFile(takenPhotoUri.getPath());

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = GlobalFunction.rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = GlobalFunction.rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = GlobalFunction.rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }

            if(m_listener != null)
            {
                m_listener.onSelectImage_Bitmap(bitmap);
            }
        }
        catch (Exception e)
        {
            GlobalFunction.outputToast(getActivity(), "Failed to capture image");
            e.printStackTrace();
        }

        if(m_listener != null)
        {
            m_listener.onSelectImage_File(mCurrentPhotoPath);
        }
    }

    private void onSelectFromFileGallery(Intent data) {
        String selectedImage = getFilePathFromUri(data);

        try
        {
            ExifInterface ei = new ExifInterface(selectedImage);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bitmap = GlobalFunction.loadImageWithShrink(mCurrentPhotoPath);//BitmapFactory.decodeFile(takenPhotoUri.getPath());

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = GlobalFunction.rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = GlobalFunction.rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = GlobalFunction.rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }

            if(m_listener != null)
            {
                m_listener.onSelectImage_Bitmap(bitmap);
            }
        }
        catch (Exception e)
        {
            //GlobalFunction.outputToast(getActivity(), "Failed to capture image");
            e.printStackTrace();
        }

        if(m_listener != null)
        {
            m_listener.onSelectImage_File(selectedImage);
        }
    }

    private void onSelectFromCamera(Intent data) {
        try
        {
            galleryAddPic();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //Bitmap bmb = (Bitmap) data.getExtras().get("data");

//            Uri selectedImage = getImageUri(getActivity(), bmb);
//            String realPath=getRealPathFromURI(selectedImage);
//            selectedImage = Uri.parse(realPath);

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            // convert byte array to Bitmap
//
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                    byteArray.length);


//            Uri takenPhotoUri = getTempFileUri(photoFileName);
//            // by this point we have the camera photo on disk
//            String image_filePath = takenPhotoUri.getPath();
//
//            ExifInterface ei = new ExifInterface(image_filePath);
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//
//            Bitmap bitmap = GlobalFunction.loadImageWithShrink(image_filePath);//BitmapFactory.decodeFile(takenPhotoUri.getPath());
//
//            switch(orientation)
//            {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    bitmap = GlobalFunction.rotateImage(bitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    bitmap = GlobalFunction.rotateImage(bitmap, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    bitmap = GlobalFunction.rotateImage(bitmap, 270);
//                    break;
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    break;
//            }
//
//
            if(m_listener != null)
            {
                m_listener.onSelectImage_Bitmap(bitmap);
            }

            if(m_listener != null)
            {
                m_listener.onSelectImage_File(mCurrentPhotoPath);
            }
        }
        catch (Exception e)
        {
            GlobalFunction.outputToast(getActivity(), "Failed to capture image");
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_IMAGE_GALLERY)
            {
                onSelectFromFileGallery(data);
            }
            else if (requestCode == REQUEST_IMAGE_CAMERA)
            {
                onSelectFromCamera(data);
            }
            else if (requestCode == REQUEST_VIDEO_GALLERY)
            {
                if(m_listener != null)
                {
                    m_listener.onSelectVideo_File(getFilePathFromUri(data));
                }
            }
            else if (requestCode == REQUEST_VIDEO_CAMERA)
            {
                //Uri takenPhotoUri = data.getData();
                // by this point we have the camera photo on disk
                //mCurrentVideoPath = mVideoUri.getPath();
                if(m_listener != null)
                {
                    m_listener.onSelectVideo_File(mCurrentVideoPath);
                }
            }
            else if (requestCode == REQUEST_AUDIO_GALLERY)
            {
                if(m_listener != null)
                {
                    boolean isAudioFile = false;
                    String filepath = getFilePathFromUri(data);
                    String[] audioExts = {".mp3", ".wav"};

                    String ext = filepath.substring(filepath.lastIndexOf("."));
                    ext.toLowerCase();

                    for (int i=0;i<audioExts.length;i++)
                    {
                        if(ext.contentEquals(audioExts[i]))
                        {
                            isAudioFile = true;
                            break;
                        }
                    }

                    if(isAudioFile)
                    {
                        m_listener.onSelectAudio_File(filepath);
                    }
                    else
                    {
                        GlobalFunction.outputToast(getActivity(), "Please choose audio file.");
                    }

                }
            }
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    public String getFilePathFromUri(Intent data) {
        Uri selectedUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getActivity().managedQuery(selectedUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedPath = cursor.getString(column_index);
        return selectedPath;
    }


    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type) throws IOException {

        // Check that the SDCard is mounted
        File mediaStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);;
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());
        String videoName = "VID_"+timeStamp+"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File video = File.createTempFile(videoName,".mp4",storageDir);
        mCurrentVideoPath = video.getPath();
        return video;
    }
    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type) throws IOException {
        return FileProvider.getUriForFile(getContext(),"com.sadeemlight.fileprovider",getOutputMediaFile(type));
        //return Uri.fromFile(getOutputMediaFile(2));
    }


}
