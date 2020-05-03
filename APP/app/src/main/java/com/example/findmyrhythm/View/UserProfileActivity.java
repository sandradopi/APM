package com.example.findmyrhythm.View;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
<<<<<<< HEAD
import android.net.Uri;
import android.os.Build;
=======
>>>>>>> master
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

<<<<<<< HEAD
import com.example.findmyrhythm.Model.BitmapDownloaderTask;
import com.example.findmyrhythm.Model.EndlessService;
=======
>>>>>>> master
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;

import java.io.FileNotFoundException;

public class UserProfileActivity extends UserMenuDrawerActivity {
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

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Perfil");

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String userId = preferences.getString("fb_id", null);
        String userName = preferences.getString("name", null);
        String userEmail = preferences.getString("email", null);
        TextView userNameView = findViewById(R.id.user_name);
//        TextView userLocationView = findViewById(R.id.user_location);
//        userLocationView.setText();
        userNameView.setText(userName);


        ImageView imageView = findViewById(R.id.profile);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            imageView.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }
        Intent intent = new Intent(this, EndlessService.class);
        intent.putExtra("SERVICE_STATE", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            return;
        }
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setMenuItemChecked(R.id.nav_profile);
        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*EventService service = new EventService();
        service.unSubscribeEventNotificationListener();*/
    }
}