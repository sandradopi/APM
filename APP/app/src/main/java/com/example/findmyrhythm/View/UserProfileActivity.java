package com.example.findmyrhythm.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmyrhythm.Model.BitmapDownloaderTask;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

        TextView toolbarTitle = findViewById(R.id.tvTitle);
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

        EventService service = new EventService();
        service.subscribeEventNotificationListener(UserProfileActivity.this, userId);

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

        EventService service = new EventService();
        service.unSubscribeEventNotificationListener();
    }
}