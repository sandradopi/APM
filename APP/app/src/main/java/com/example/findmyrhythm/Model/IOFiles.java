package com.example.findmyrhythm.Model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class IOFiles {

    public static Bitmap loadImageFromStorage(Context context) throws FileNotFoundException {
        Bitmap bmp = null;

        // getApplicationContext()
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File f=new File(directory, "profile.jpg");
        bmp = BitmapFactory.decodeStream(new FileInputStream(f));

        return bmp;

    }


    private static String saveToInternalStorage(Bitmap bitmapImage, Context context) {
        // getApplicationContext()
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }



    public static void storeInfoJSON(String name, String email, String packageName) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("name", name);
            jo.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCreateAndSaveFile("user_info.json", jo.toString(), packageName);

    }

    public static JSONObject readInfoJSON(String packageName) throws IOException {
        JSONObject jo = null;

        // Read data from file as String
        String userInfo = mReadJsonData("user_info.json", packageName);
        Log.e("DEBUG", userInfo);

        try {
            jo = new JSONObject(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo;
    }


    private static void mCreateAndSaveFile(String params, String mJsonResponse, String contextPackageName) {
        try {
            // getApplicationContext().getPackageName()
            FileWriter file = new FileWriter("/data/data/" + contextPackageName + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String mReadJsonData(String params, String packageName) throws IOException {
        String mResponse = null;
        // getPackageName()
        File f = new File("/data/data/" + packageName + "/" + params);
        FileInputStream is = new FileInputStream(f);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        mResponse = new String(buffer);

        return mResponse;
    }


    public static void downloadSaveBmp(Uri url, Context applicationContext) {
        Bitmap bmp = null;
        try {
            bmp = new BitmapDownloaderTask().execute(url.toString()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IOFiles.saveToInternalStorage(bmp, applicationContext);
    }


}
