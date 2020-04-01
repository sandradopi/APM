package com.example.findmyrhythm.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.findmyrhythm.Model.BitmapDownloaderTask;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;

public class UserProfileActivity extends MenuDrawerActivity {
    private static final String TAG = "Perfil Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_user_profile);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setMenuItemChecked(R.id.nav_profile);

        ImageView editButton = findViewById(R.id.edit);
        editButton.setClickable(true);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en ajustes del usuario");
                Intent intent = new Intent(UserProfileActivity.this, UserSettingsActivity.class);
                startActivity(intent);
            }
        });


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
            bmp = new BitmapDownloaderTask().execute(photoUrl.toString()).get();
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
}