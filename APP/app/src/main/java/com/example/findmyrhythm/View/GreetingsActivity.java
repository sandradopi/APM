package com.example.findmyrhythm.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URL;
import java.util.concurrent.ExecutionException;

public class GreetingsActivity extends AppCompatActivity implements View.OnClickListener {

    //Widgets
    Button orgLogin, userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        orgLogin = (Button) findViewById(R.id.orgLogin);
        orgLogin.setOnClickListener(this);

        userLogin = (Button) findViewById(R.id.userLogin);
        userLogin.setOnClickListener(this);



        //##########################################################################################
        // TODO: Aquí habría quizás que poner un spiner o algún elemento visual de carga.
        // Pantalla de carga solo con el logotipo, pues esta alternativa de haceer la carga
        // en los greetings solo vale para nuevos usuarios


        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String name = currentFirebaseUser.getDisplayName();
        String email = currentFirebaseUser.getEmail();
        Uri photoUrl = currentFirebaseUser.getPhotoUrl();

        IOFiles.storeInfoJSON(name, email, getPackageName());
        IOFiles.readInfoJSON(getPackageName());


        Bitmap bmp = null;
        try {
            bmp = new GreetingsActivity.BitmapDownloaderTask().execute(photoUrl.toString()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IOFiles.saveToInternalStorage(bmp, getApplicationContext());

        // ImageView imageView = findViewById(R.id.test_image);

        Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());

        // imageView.setImageBitmap(bmp2);

    }


    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url = null;
            Bitmap image = null;
            try {
                url = new URL(urls[0]);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }
    }


    @Override
    public void onClick(View v) {

        if (v == orgLogin) {

            //TODO: Intent to the desired Activity. MAYBE PASS A VARIABLE DEFINING THE USER AS ORGANIZATION
            Intent locationIntent = new Intent(this, OrganizerLogActivity.class);
            startActivity(locationIntent);

        } else {

            //TODO: Intent to the desired Activity. MAYBE PASS A VARIABLE DEFINING THE USER AS USER.
            Intent locationIntent = new Intent(this, SetLocationActivity.class);
            startActivity(locationIntent);
        }
    }
}
