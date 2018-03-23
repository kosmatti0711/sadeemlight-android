package com.sadeemlight.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.LocaleHelper;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.Session_management;
import com.sadeemlight.venus_uis.utils.GlobalFunction;

import okhttp3.OkHttpClient;

/**
 * Created by Rajesh Dabhi on 27/8/2016.
 */
public class Settings_fragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    /*String menu_lang[] = {"English",
            "Arabic",
            "Kurdish"};*/

    Spinner sp_lang;
    ImageView profile;

    Session_management sessionManagement;

    boolean isSettingchange = false;

    JSONArray jsonArray = null;

    List<ModelParamsPair> params = new ArrayList<>();

    String m_selfilepath = "";
    String responseString;
    long totalSize = 0;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE1 = 101;
    private static final int GALLERY_REQUEST_CODE1 = 201;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_GALLERY = 2;

    private Uri fileUri;

    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    private int CAMERA_PERMISSION_CODE = 25;

    boolean isFirstload = false;

    public Settings_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_activity, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    Fragment fm = new NewsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fm).addToBackStack(null).commit();

                    ((MainActivity)getActivity()).mShowStudentLayout.hideStudentList();
                    return true;
                }
                return false;
            }
        });

        ((MainActivity) getActivity()).setTitle(R.string.action_setting);

        sessionManagement = new Session_management(getActivity());

        profile = (ImageView) view.findViewById(R.id.iv_setting_profile);
        sp_lang = (Spinner) view.findViewById(R.id.sp_setting_lang);

        String en = getResources().getString(R.string.lang_english);
        String ar = getResources().getString(R.string.lang_arabic);
        String ku = getResources().getString(R.string.lang_Kurdish);

        String menu_lang[] = {en, ar, ku};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menu_lang);

        sp_lang.setAdapter(adapter);

        String getlang = LocaleHelper.getLanguage(getActivity());

        Log.e("settinglang: ", getlang);

        if (getlang.contentEquals("en")) {
            sp_lang.setSelection(adapter.getPosition("English"));
            sp_lang.setSelection(0);
        } else if (getlang.contentEquals("ar")) {
            sp_lang.setSelection(adapter.getPosition("Arabic"));
            sp_lang.setSelection(1);
        } else if (getlang.contentEquals("ckb")) {
            sp_lang.setSelection(adapter.getPosition("Kurdish"));
            sp_lang.setSelection(2);
        }

        sp_lang.setOnItemSelectedListener(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        ImageLoader.getInstance().init(config);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_login_icon)
                .showImageForEmptyUri(R.drawable.ic_login_icon)
                .showImageOnFail(R.drawable.ic_login_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(sessionManagement.getUserDetails().get(ConstValue.KEY_IMAGELINK), profile, options);

        profile.setOnClickListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (isFirstload) {
            if (i == 0) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "en");
            } else if (i == 1) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "ar");
            } else if (i == 2) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "ckb");
            }

            Intent i1 = new Intent(getActivity(), MainActivity.class);
            startActivity(i1);
            getActivity().finish();
        } else {
            if (i == 0) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "en");
            } else if (i == 1) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "ar");
            } else if (i == 2) {
                //isSettingchange = true;
                //((MainActivity) getActivity()).isChange_setting = true;
                LocaleHelper.setLocale(getActivity(), "ckb");
            }
        }

        isFirstload = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        String WriteStoragePermission = "Manifest.permission.WRITE_EXTERNAL_STORAGE";
        String CameraAcess = "Manifest.permission.CAMERA";

        //First checking if the app is already having the permission
        if (isStorageAllowed()) {
            //If permission is already having then showing the toast

            if (isCameraAllowed()) {
                //If permission is already having then showing the toast

                selectImage(GALLERY_REQUEST_CODE1, CAMERA_CAPTURE_IMAGE_REQUEST_CODE1);
            } else {
                //If the app has not the permission then asking for the permission
                requestCameraPermission();
            }

            //Existing the method with return
            return;
        } else {
            //If the app has not the permission then asking for the permission
            requestStoragePermission();
        }
        /*// this is check fro device android version is 23 or above
        if (Build.VERSION.SDK_INT >= 23) {

            // check WRITE_EXTERNAL_STORAGE permission is granted or not
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                // call function for save image
                selectImage(GALLERY_REQUEST_CODE1, CAMERA_CAPTURE_IMAGE_REQUEST_CODE1);

            } else {
                // if permission was not granted else this code for ask for grante permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        } else {
            // call function for save image

        }*/

    }

    //We are calling this method to check the permission status
    private boolean isStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private boolean isCameraAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String photoFileName = "camera_photo.jpg";
    public String videoFileName = "camera_video.mp4";
    public Uri getTempFileUri(String fileName)
    {
        String tempPath = "";
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/sadeemlight/");

        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.d("App", "failed to create directory");
            }
        }

        tempPath = dir.getAbsolutePath();
        Uri uri = Uri.fromFile(new File(tempPath + File.separator + fileName));
        return uri;
    }

    private void selectImage(final int gallery_capture_code, final int camera_capture_code) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Pictures Option");
        myAlertDialog.setMessage("Select Picture Mode");

        myAlertDialog.setPositiveButton(R.string.setting_dialog_gallary, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.picker_imagetitle)), gallery_capture_code);
            }
        });

        myAlertDialog.setNegativeButton(R.string.setting_dialog_camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempFileUri(photoFileName)); // set the image file name

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.

                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivityForResult(intent, camera_capture_code);
                }
            }
        });

        myAlertDialog.show();
    }

    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE1)
        {
            if (resultCode == getActivity().RESULT_OK)
            {
                try
                {
                    Uri takenPhotoUri = getTempFileUri(photoFileName);
                    // by this point we have the camera photo on disk
                    String image_filePath = takenPhotoUri.getPath();

                    ExifInterface ei = new ExifInterface(image_filePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap bitmap = GlobalFunction.loadImageWithShrink(image_filePath);//BitmapFactory.decodeFile(takenPhotoUri.getPath());

                    switch(orientation)
                    {
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

                    m_selfilepath = image_filePath;
                    profile.setImageBitmap(bitmap);
                    new setImage().execute();
                }
                catch (Exception e)
                {
                    GlobalFunction.outputToast(getActivity(), "Failed to capture image");
                    e.printStackTrace();
                }

            }
        }
        else if ((requestCode == GALLERY_REQUEST_CODE1))
        {
            if(resultCode == Activity.RESULT_OK)
            {
                String selectedImage = getFilePathFromUri(data);

                try
                {
                    ExifInterface ei = new ExifInterface(selectedImage);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap bitmap = GlobalFunction.loadImageWithShrink(selectedImage);//BitmapFactory.decodeFile(takenPhotoUri.getPath());

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

                    m_selfilepath = selectedImage;
                    // Set the Image in ImageView after decoding the String
                    profile.setImageBitmap(bitmap);
                    new setImage().execute();
                }
                catch (Exception e)
                {
                    //GlobalFunction.outputToast(getActivity(), "Failed to capture image");
                    e.printStackTrace();
                }

                //cursor.close();
            }

        }
    }

    public class setImage extends AsyncTask<Void, Integer, Void> {

        String getimageurl = "";
        boolean res_status = false;
        Progress_dialog pd = new Progress_dialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.showProgressbar();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            //init the http client
            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            OkHttpClient client = new OkHttpClient();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstValue.PROFILE_IMG_URL);
            httppost.setHeader("authorization", "Bearer " + access_token);

            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                File sourceFile = new File(m_selfilepath);
                if (sourceFile != null) {
                    //entity.addPart("student_image", new FileBody(sourceFile));
                    entity.addPart("file", new FileBody(sourceFile));
                }

                String studentid = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);

                // Extra parameters if you want to pass to server
                entity.addPart("student_id", new StringBody(studentid));

                totalSize = entity.getContentLength();

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                String jsonStr = EntityUtils.toString(r_entity);

                Log.e("response: ", jsonStr);

                if (jsonStr != null) {

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    getimageurl = jsonObject.getJSONObject("Data").getString("Picture");
                    res_status = jsonObject.getBoolean("Status");
                }

                Log.e("response", "do" + responseString);
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                responseString = e.toString();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismissProgress();

            Log.e("data: ", getimageurl);

            if (res_status == true)
            {

                /*isSettingchange = true;
                ((MainActivity) getActivity()).isChange_setting = true;*/

                sessionManagement.setUserDetail(ConstValue.KEY_IMAGELINK, getimageurl);

                Intent i1 = new Intent(getActivity(), MainActivity.class);
                startActivity(i1);
                getActivity().finish();

                Toast.makeText(getActivity(), "Image Upload Success.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Failed Uploading.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
