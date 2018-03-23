package com.sadeemlight.venus_uis.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Administrator on 11/28/2015.
 */
public class GlobalFunction
{

    private static Random mRandom = new Random(1984);



    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Bitmap retVal;

        if(angle == 0) return source;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public static Bitmap transformImage(Bitmap source, float angle, boolean flipX)
    {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        int cx, cy;
        cx = source.getWidth() / 2;
        cy = source.getHeight() / 2;
        if(flipX)
        {
            //matrix.postScale(-1, 1, cx, cy);
            matrix.preScale(-1, 1);
        }
        else
        {
            matrix.preScale(1, -1);
            //matrix.postScale(-1, 1, cx, cy);
        }

        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public static Bitmap getBitmapFromAsset(Context con, String strName)
    {
        AssetManager assetManager = con.getAssets();
        InputStream istr = null;
        try
        {
            istr = assetManager.open(strName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }

    public static String getTextFromAsset(Context con, String strName)
    {
        AssetManager assetManager = con.getAssets();
        InputStream is = null;
        try
        {
            is = assetManager.open(strName);
            int size = is.available();
            byte buffer[] = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap resizeWithRatio(Bitmap src, int new_width, int new_height)
    {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, src.getWidth(), src.getHeight()), new RectF(0, 0, new_width, new_height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    public static Bitmap resizeWithoutRatio(Bitmap src, int new_width, int new_height)
    {
        Matrix m = new Matrix();

        float sx, sy;

        sx = (float)new_width / src.getWidth();
        sy = (float)new_height / src.getHeight();
        m.postScale(sx, sy);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    public static Bitmap loadImageWithShrink(String file_path)
    {
        File f= new File(file_path);

        Bitmap b = null;
        int IMAGE_MAX_SIZE = 1600;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis;
        try
        {
            fis = new FileInputStream(f);

            BitmapFactory.decodeStream(fis, null, o);
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE)
            {
                int maxwh = Math.max(o.outWidth,o.outHeight);
                while(maxwh / scale > IMAGE_MAX_SIZE)
                    scale *= 2;
            }

            //Log.d("twinklestar.containerrecog", "width: " + o.outWidth + "height: " + o.outHeight + "scale:" + scale);
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public static Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    //constrast : 0 - 10(1), brightness: -255 ~ 255(0)
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static String saveBitmap2TempFile(Bitmap bmp)
    {
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/sadeemlight/");

        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.d("App", "failed to create directory");
                return "";
            }
        }

        String file_path = dir.getAbsolutePath() + "/temp_result.jpg";
        File file = new File(file_path);
        file.delete();

        if(saveBitmap(bmp, file_path) == true)
        {
            return file_path;
        }
        else
        {
            return "";
        }
    }

    public static boolean saveBitmap(Bitmap bmp, String file_path)
    {
        File file = new File(file_path);

        try
        {
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }


    public static String getNowString4File()
    {
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        return s;
    }

    public static String getCurrentDateTime()
    {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        return s;
    }

    public static String getDuration(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        Date endTime = null;
        String hours;
        String days;
        String months;
        String duration = "";
        try {
            startTime = sdf.parse(start);
            endTime = sdf.parse(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startTime != null && endTime != null) {
            long diff = endTime.getTime() - startTime.getTime();
            int min = (int) (diff / (60 *  1000));
            int hour = (int) (diff / (60 * 60 * 1000));
            int day = (int) (diff / (60 * 60 * 1000 * 24));
            int month = (int) (diff / (60 * 60 * 1000 * 24 * 30));
            int weeks = (int) (diff / (60 * 60 * 1000 * 24 * 7));
            if (min == 0)
                //duration = "Recently";
            {
                if (startTime.getHours() < 10)
                    duration = "today, " + "0" + String.valueOf(startTime.getHours()) + ":";
                else
                    duration = "today, " + String.valueOf(startTime.getHours()) + ":";
                if (startTime.getMinutes() < 10)
                    duration = duration + "0" + String.valueOf(startTime.getMinutes());
                else
                    duration = duration + String.valueOf(startTime.getMinutes());
            }
            else if (hour == 0) {
                //duration = String.valueOf(String.valueOf(min) + "mins");
                if (startTime.getHours() < 10)
                    duration = "today, " + "0" + String.valueOf(startTime.getHours()) + ":";
                else
                    duration = "today, " + String.valueOf(startTime.getHours()) + ":";
                if (startTime.getMinutes() < 10)
                    duration = duration + "0" + String.valueOf(startTime.getMinutes());
                else
                    duration = duration + String.valueOf(startTime.getMinutes());
            }
            else
            {
                if (day == 0)
                {
                    if (hour > endTime.getHours())
                    {
                        if (startTime.getHours() < 10)
                            duration = "yesterday, " + "0" + String.valueOf(startTime.getHours()) + ":";
                        else
                            duration = "yesterday, " + String.valueOf(startTime.getHours()) + ":";
                        if (startTime.getMinutes() < 10)
                            duration = duration + "0" + String.valueOf(startTime.getMinutes());
                        else
                            duration = duration + String.valueOf(startTime.getMinutes());
                    }
                    else
                    {
                        if (startTime.getHours() < 10)
                            duration = "today, " + "0" + String.valueOf(startTime.getHours()) + ":";
                        else
                            duration = "today, " + String.valueOf(startTime.getHours()) + ":";
                        if (startTime.getMinutes() < 10)
                            duration = duration + "0" + String.valueOf(startTime.getMinutes());
                        else
                            duration = duration + String.valueOf(startTime.getMinutes());
                    }
                }
                else
                {
                    if (weeks == 0)
                    {
                        if (day == 1)
                        {
                            if (startTime.getHours() < 10)
                                duration = "yesterday, " + "0" + String.valueOf(startTime.getHours()) + ":";
                            else
                                duration = "yesterday, " + String.valueOf(startTime.getHours()) + ":";
                            if (startTime.getMinutes() < 10)
                                duration = duration + "0" + String.valueOf(startTime.getMinutes());
                            else
                                duration = duration + String.valueOf(startTime.getMinutes());
                        }
                        else {
                            duration = String.valueOf(day) + " d ago ";
                            if (startTime.getHours() < 10)
                                duration = duration + "0" + String.valueOf(startTime.getHours()) + ":";
                            else
                                duration = duration + String.valueOf(startTime.getHours()) + ":";
                            if (startTime.getMinutes() < 10)
                                duration = duration + "0" + String.valueOf(startTime.getMinutes());
                            else
                                duration = duration + String.valueOf(startTime.getMinutes());
                        }
                    }
                    else
                    {
                        duration = String.valueOf(weeks) + " w " + String.valueOf(day) + " d ago ";
                        if (startTime.getHours() < 10)
                            duration = duration + "0" + String.valueOf(startTime.getHours()) + ":";
                        else
                            duration = duration + String.valueOf(startTime.getHours()) + ":";
                        if (startTime.getMinutes() < 10)
                            duration = duration + "0" + String.valueOf(startTime.getMinutes());
                        else
                            duration = duration + String.valueOf(startTime.getMinutes());
                    }
                }
            }
        }
        return duration;
    }

    public static String getDurationUpdate(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        Date endTime = null;
        String hours;
        String days;
        String months;
        String duration = "";
        try {
            startTime = sdf.parse(start);
            endTime = sdf.parse(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startTime != null && endTime != null) {
            long diff = endTime.getTime() - startTime.getTime();
            int min = (int) (diff / (60 *  1000));
            int hour = (int) (diff / (60 * 60 * 1000));
            int day = (int) (diff / (60 * 60 * 1000 * 24));
            int month = (int) (diff / (60 * 60 * 1000 * 24 * 30));
            int weeks = (int) (diff / (60 * 60 * 1000 * 24 * 7));
            if (min == 0)
            //duration = "Recently";
            {
                duration = "few seconds";
            }
            else if (hour == 0) {
                duration = String.valueOf(min) + "m";
            }
            else
            {
                if (day == 0)
                {
                    duration = String.valueOf(hour % 24) + "h " + String.valueOf(min % 60) + "m";
                }
                else
                {
                    if (weeks == 0)
                    {
                        duration = String.valueOf(day % 7) + "d " + String.valueOf(hour % 24) + "h " + String.valueOf(min % 60) + "m";
                    }
                    else
                    {
                        duration = String.valueOf(weeks % 7) + "w " + String.valueOf(day % 7) + "d " + String.valueOf(hour % 24) + "h " + String.valueOf(min % 60) + "m";
                    }
                }
            }
        }
        return duration;
    }

    public static String getRealPathFromURI(Activity act, Uri contentURI)
    {
        Cursor cursor = act.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        }
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    static private Toast prev_toast = null;
    public static synchronized void outputToast(Context con, String text)
    {
        if(prev_toast != null) prev_toast.cancel();

        if(con != null)
        {
            prev_toast = Toast.makeText(con, text, Toast.LENGTH_SHORT);
            prev_toast.show();
        }
    }

    public static synchronized void hideToast()
    {
        if(prev_toast != null) prev_toast.cancel();
        prev_toast = null;
    }

    public static synchronized void outputToast_Test(Context con, String text)
    {
        if(con != null && true)
        {
            if(prev_toast != null) prev_toast.cancel();

            prev_toast = Toast.makeText(con, text, Toast.LENGTH_LONG);
            prev_toast.show();
        }
    }

    public static String md5_1(String s)
    {
        try {

            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(String str)
    {
        String MD5 = "";

        try{

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(str.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();

            for(int i = 0 ; i < byteData.length ; i++){

                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));

            }

            MD5 = sb.toString();



        }catch(NoSuchAlgorithmException e){

            e.printStackTrace();

            MD5 = null;

        }

        return MD5;
    }

    public static void showMessageBox(Context con, String msg, String title)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(con).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public static int getAge(String birthday)
    {
        int year, month, day;

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date formattedDate;

        if(birthday == null)
        {
            return 0;
        }

        try
        {
            formattedDate = (Date) format.parse(birthday);
            Calendar cal = Calendar.getInstance();
            cal.setTime(formattedDate);

            month = cal.get(Calendar.MONTH); // Month
            day = cal.get(Calendar.DAY_OF_MONTH);// Day of the month
            year = cal.get(Calendar.YEAR);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }


    public static Bitmap getUriBitmap(String url)
    {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public static Bitmap getSmallBitmapFromURL(String src)
    {
        try {
            System.out.printf("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            System.out.printf("Bitmap", "returned");
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 100, 100, false);//This is only if u want to set the image size.
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Exception", e.getMessage());
            return null;
        }
    }

    public static float dpFromPx(Context con, float px)
    {
        return px / con.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(Context con, float dp)
    {
        return dp * con.getResources().getDisplayMetrics().density;
    }

    public static String getTempFolderPath()
    {
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/sadeemlight/temp/");

        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.d("App", "failed to create directory");
                return "";
            }
        }

        String file_path = dir.getAbsolutePath();
        return file_path;
    }

    public static double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }





    public static final String formatValueString(long number)
    {
        final char[] magnitudes = {'K', 'M', 'G', 'T', 'P', 'E'}; // enough for long

        String ret;
        if (number >= 0)
        {
            ret = "";
        }
        else if (number <= -9200000000000000000L)
        {
            return "-9.2E";
        }
        else
        {
            ret = "-";
            number = -number;
        }
        if (number < 1000)
            return ret + number;
        for (int i = 0; ; i++)
        {
            if (number < 10000 && number % 1000 >= 100)
                return ret + (number / 1000) + '.' + ((number % 1000) / 100) + magnitudes[i];

            number /= 1000;

            if (number < 1000)
                return ret + number + magnitudes[i];
        }
    }

    public static String encodeMessage(String message) {
        message = message.replaceAll("&", ":and:");
        message = message.replaceAll("\\+", ":plus:");
        return StringEscapeUtils.escapeJava(message);
    }

    public static String decodeMessage(String message) {
        message = message.replaceAll(":and:", "&");
        message = message.replaceAll(":plus:", "+");
        return StringEscapeUtils.unescapeJava(message);
    }

    public static Point getScreenSize(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void hideSystemKeyboard(Activity act)
    {
        try
        {
            InputMethodManager imm = (InputMethodManager)act.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        {

        }
    }

    public static String getGooglemapUrl(double lat, double lnt, int zoom_level, int width, int height)
    {
        String latString = String.valueOf(lat);
        String lngString = String.valueOf(lnt);
        String szZoom = String.valueOf((int)zoom_level);
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latString + "," + lngString +
                "&zoom=" + szZoom +
                "&size=" + width + "x" + height +
                "&markers=color:blue%7Clabel:P%7C" + latString +  "," + lngString  +
                "&key=AIzaSyAt3DA9056eqXR25QwqEp56u1E9gJtrjGs";

        return url;
        // return  "https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=AIzaSyAt3DA9056eqXR25QwqEp56u1E9gJtrjGs";
    }


    public static Rect getOrgCropRect(int width, int height, int rot,
                                      Rect rotRect) {
        Rect orgRect = new Rect(rotRect);

        if (rot == Defines.ROTATE_0 )
        {
            orgRect.set(rotRect);
        }
        else if (rot == Defines.ROTATE_90)
        {
            orgRect.left = rotRect.top;
            orgRect.top = height - rotRect.right;
            orgRect.right = rotRect.bottom;
            orgRect.bottom = height - rotRect.left;
        }
        else if (rot == Defines.ROTATE_180)
        {
            orgRect.left = width - rotRect.right;
            orgRect.top = height - rotRect.bottom;
            orgRect.right = width - rotRect.left;
            orgRect.bottom = height - rotRect.top;
        }
        else if (rot == Defines.ROTATE_270)
        {
            orgRect.left = rotRect.top;
            orgRect.top = height - rotRect.right;
            orgRect.right = rotRect.bottom;
            orgRect.bottom = height - rotRect.left;
        }

        return orgRect;
    }

    public static Bitmap makeCropedGrayBitmap(byte[] data, int width, int height, int rot, Rect cropRect)
    {
        if(cropRect.left < 0 || cropRect.left >= width) return null;
        if(cropRect.right < 0 || cropRect.right >= width) return null;
        if(cropRect.top < 0 || cropRect.top >= height) return null;
        if(cropRect.bottom < 0 || cropRect.bottom >= height) return null;

        int cropwidth = cropRect.width();
        int cropheight = cropRect.height();
        int[] pixels = new int[cropwidth * cropheight];
        int grey, inputOffset, outputOffset, temp;
        byte[] yuv = data;

        if (rot == Defines.ROTATE_90)
        {// rot90, screen_rot0
            int x, y, x1, y1;
            for (y = 0; y < cropheight; y++)
            {
                y1 = cropRect.top + y;
                for (x = 0; x < cropwidth; x++)
                {
                    x1 = cropRect.left + x;
                    grey = yuv[y1 * width + x1] & 0xff;
                    pixels[x * cropheight + cropheight - y - 1] = 0xFF000000 | (grey * 0x00010101);
                }
            }

            temp = cropwidth;
            cropwidth = cropheight;
            cropheight = temp;
        }
        else if (rot == Defines.ROTATE_0)
        {// rot0, screen_rot90
            inputOffset = cropRect.top * width;
            for (int y = 0; y < cropheight; y++)
            {
                outputOffset = y * cropwidth;
                for (int x = 0; x < cropwidth; x++)
                {
                    grey = yuv[inputOffset + x + cropRect.left] & 0xff;
                    pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
                }
                inputOffset += width;
            }

        }
        else if (rot == Defines.ROTATE_180)
        {// rot0, screen_rot90
            inputOffset = cropRect.top * width;
            for (int y = 0; y < cropheight; y++)
            {
                outputOffset = (cropheight - 1 - y) * cropwidth;
                for (int x = 0; x < cropwidth; x++)
                {
                    grey = yuv[inputOffset + x + cropRect.left] & 0xff;
                    pixels[outputOffset + cropwidth - 1 - x] = 0xFF000000 | (grey * 0x00010101);
                }
                inputOffset += width;
            }

        }
        else if (rot == Defines.ROTATE_270)
        {// rot0, screen_rot90
            int x, y, x1, y1;
            for (y = 0; y < cropheight; y++)
            {
                y1 = cropRect.top + y;
                for (x = 0; x < cropwidth; x++)
                {
                    x1 = cropRect.left + x;
                    grey = yuv[y1 * width + x1] & 0xff;
                    pixels[(cropwidth - x - 1) * cropheight + y] = 0xFF000000 | (grey * 0x00010101);
                }

            }
            temp = cropwidth;
            cropwidth = cropheight;
            cropheight = temp;
        }

        Bitmap bitmap = Bitmap.createBitmap(cropwidth, cropheight,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, cropwidth, 0, 0, cropwidth, cropheight);

        pixels = null;
        yuv = null;

        return bitmap;
    }

    public static Bitmap makeBitmapFromGrayBuffer(byte[] data, int width, int height)
    {
        int[] pixels = new int[width * height];
        int grey, inputOffset, outputOffset, temp;
        byte[] yuv = data;

        inputOffset = 0;
        for (int y = 0; y < height; y++)
        {
            outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                grey = yuv[inputOffset + x ] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += width;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        pixels = null;
        yuv = null;

        return bitmap;
    }

    public static Bitmap makeBitmapFromNV21(byte[] data, int width, int height, int rot)
    {
        Bitmap bitmap;

        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 90, out);
        byte[] imageBytes = out.toByteArray();


        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return rotateImage(bitmap, rot);


    }

    public static int getByteLength(char[] str, int maxLen) {
        int i, len = 0;
        for (i = 0; i < maxLen; ++i)
        {
            if (str[i] == 0)
            {
                break;
            }
        }
        len = i;
        return len;
    }

    public static String convchar2string(char[] chstr)
    {
        int len = getByteLength(chstr, 256);
        String outStr = new String(chstr, 0, len);// ,"UTF-8");
        return outStr;
    }

    public static String getIMEI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String strIMEI = telephonyManager.getDeviceId();

        if (strIMEI == null || strIMEI.equals(""))
            strIMEI = "0";

        return strIMEI;
    }

    public static String getIMSI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String strIMSI = telephonyManager.getSubscriberId();

        if (strIMSI == null || strIMSI.equals(""))
            strIMSI = "0";

        return strIMSI;
    }


    public static String SaveRecogImage(Context con, String szFileName, Bitmap bmImage) {
        File dir = null;

        if(Environment.getExternalStorageDirectory() != null)
        {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Plaka/BlackCars");
        }
        else
        {
            return "";
            //dir = con.getDir("BlackCars", Context.MODE_PRIVATE);
        }


        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("App", "failed to create directory");
                return "";
            }
        }

        if (szFileName.isEmpty())
            szFileName = getCurTimeString() + ".jpg";

        File file = new File(dir.getAbsolutePath(), szFileName);

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bmImage.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

        return file.getAbsolutePath();
    }

    public static String g_szSavePath = "/Plaka";
    public static String writeBitmap2File(Bitmap bitmap, int mode, int[] inRect) {
        ByteArrayOutputStream baos = null;
        String _subdir = null;
        String _fname = null;
        String _path = null;
        try {
            if (mode == 0)
                _subdir = g_szSavePath + "/PreviewImages/";
            else
                _subdir = g_szSavePath + "/PictureImages/";
            File dir = new File(Environment.getExternalStorageDirectory()
                    .toString(), _subdir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                    return "";
                }
            }
            int nWidth = bitmap.getWidth();
            int nHeight = bitmap.getHeight();
            String szTag = "_" + String.valueOf(nWidth) + "_"
                    + String.valueOf(nHeight) + "_" + String.valueOf(inRect[0])
                    + "_" + String.valueOf(inRect[1]) + "_"
                    + String.valueOf(inRect[2]) + "_"
                    + String.valueOf(inRect[3]);
            _fname = getCurTimeString() + szTag + ".jpg";
            _path = dir.getAbsolutePath() + _fname;
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
            byte[] photoBytes = baos.toByteArray();
            if (photoBytes != null) {
                new FileOutputStream(new File(dir.getAbsolutePath(), _fname))
                        .write(photoBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _path;
    }

    public static Bitmap getGrayOrgBitmap(byte[] data, int width, int height)
    {
        int[] pixels = new int[width * height];
        byte[] yuv = data;
        int inputOffset = 0;

        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                // pixels[outputOffset + x] = yuv[inputOffset + x];
                int grey = yuv[inputOffset + x] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += width;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String getCurTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private static float sPixelDensity = 1;
    public static void init(Context context)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        sPixelDensity = metrics.density;
    }

    public static int dpToPixel(int dp)
    {
        return Math.round(sPixelDensity * dp);
    }

    public static SharedPreferences getSharedPreferences(Context con, String key) {
        SharedPreferences sp = con.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp;
    }

    public static double log_base(double x, double base)
    {
        return Math.log(x) / Math.log(base);
    }

    public static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            // Log exception.
            return 0;
        }
    }
}
