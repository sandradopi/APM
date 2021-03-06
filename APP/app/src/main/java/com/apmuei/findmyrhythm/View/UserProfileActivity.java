package com.apmuei.findmyrhythm.View;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.Model.ListenerJob;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import com.apmuei.findmyrhythm.View.tabs.SectionsPagerAdapter;

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
        toolbarTitle.setText(R.string.profile_user);

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String userId = preferences.getString("fb_id", null);
        String userName = preferences.getString("name", null);
        String userEmail = preferences.getString("email", null);
        TextView userNameView = findViewById(R.id.user_name);
        TextView userLocationView = findViewById(R.id.user_location);
        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        // Display the first location which the user subscribed to
        userLocationView.setText(persistentUserInfo.getSubscribedLocations().get(0));
        userNameView.setText(userName);


        ImageView imageView = findViewById(R.id.profile);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            imageView.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMenuItemChecked(R.id.nav_profile);
        // Retrieve the current adapter
        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter) viewPager.getAdapter();
        // If it exists, then update event tabs content. This way it maintains its previous state.
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.notifyDataSetChanged();
        } else {
            // If it does not exist, then create a new adapter with the updated content.
            SectionsPagerAdapter newSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(newSectionsPagerAdapter);
        }

        if (!isJobServiceOn())
            ListenerJob.scheduleJob(getApplicationContext());
    }

    public boolean isJobServiceOn() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE ) ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == 123 ) {
                Log.d(TAG, "Job is scheduled!");
                return true;
            }
        }
        Log.d(TAG, "Job is not scheduled!");
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}