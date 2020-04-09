package com.example.findmyrhythm.Model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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


    public static PersistentUserInfo getPersistentUserInfo(Context context) {
        Gson gson = new Gson();
        PersistentUserInfo persistentUserInfo = null;
        try {
            persistentUserInfo = gson.fromJson(new FileReader(context.getFilesDir().getPath() + "user_persistent_info.json"), PersistentUserInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return persistentUserInfo;
    }

    public static void setPersistentUserInfo(Context context, PersistentUserInfo persistentUserInfo) {
        try (Writer writer = new FileWriter(context.getFilesDir().getPath() + "user_persistent_info.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(persistentUserInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
