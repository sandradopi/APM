package com.example.findmyrhythm.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmyrhythm.Model.BitmapDownloaderTask;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.io.FileNotFoundException;
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



        /* TODO: esto se está ejecutando cada vez que se inicia la actividad. Lo ideal parece que
        *   sería crear una actividad de carga con el logo o algo así que se ejecutara una única
        *   vez. Es bastante común en las aplicaciones. */

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String name = currentFirebaseUser.getDisplayName();
        String email = currentFirebaseUser.getEmail();

        IOFiles.storeInfoJSON(name, email, getPackageName());

        TextView userNameView = findViewById(R.id.user_name);
//        TextView userLocationView = findViewById(R.id.user_location);
//        userLocationView.setText();
        userNameView.setText(name);


        Uri photoUrl = currentFirebaseUser.getPhotoUrl();

        for (UserInfo profile : currentFirebaseUser.getProviderData()) {
            System.out.println(profile.getProviderId());
            // check if the provider id matches "facebook.com"
            if (profile.getProviderId().equals("facebook.com")) {

                String facebookUserId = profile.getUid();

                photoUrl = Uri.parse("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");

            } else if (profile.getProviderId().equals("google.com")) {
                photoUrl = Uri.parse(photoUrl.toString().replace("s96-c", "s700-c"));
            }
        }

        IOFiles.downloadSaveBmp(photoUrl, getApplicationContext());

        ImageView imageView = findViewById(R.id.profile);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            imageView.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }

    }
}