package com.example.findmyrhythm.Model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
        File f=new File(directory, "profile.png");
        bmp = BitmapFactory.decodeStream(new FileInputStream(f));

        return bmp;

    }


    private static String saveToInternalStorage(Bitmap bitmapImage, Context context) {
        // getApplicationContext()
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.png");

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


    public static void downloadProfilePicture(FirebaseUser user, Context context) {
        Uri photoUrl = user.getPhotoUrl();

        for (UserInfo profile : user.getProviderData()) {
            System.out.println(profile.getProviderId());
            // check if the provider id matches "facebook.com"
            if (profile.getProviderId().equals("facebook.com")) {

                String facebookUserId = profile.getUid();

                photoUrl = Uri.parse("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");

            } else if (profile.getProviderId().equals("google.com")) {
                photoUrl = Uri.parse(photoUrl.toString().replace("s96-c", "s700-c"));
            }
        }

        IOFiles.downloadSaveBmp(photoUrl, context);
        //setResult(RESULT_OK);
        //finish();
    }

}
