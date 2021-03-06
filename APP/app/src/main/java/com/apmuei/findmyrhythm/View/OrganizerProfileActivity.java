package com.apmuei.findmyrhythm.View;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.RatingService;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.tabs.SectionsPagerAdapterOrg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class OrganizerProfileActivity extends OrganizerMenuDrawerActivity {
    private static final String TAG = "Perfil Organizador";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        setMenuItemChecked(R.id.nav_profile);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Perfil");

        SectionsPagerAdapterOrg sectionsPagerAdapter = new SectionsPagerAdapterOrg(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String organizerName = preferences.getString("name", null);
        String userEmail = preferences.getString("email", null);

        PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        ArrayList<Event> createdEvents = persistentOrganizerInfo.getEvents();
        ArrayList<String> eventIds = new ArrayList<String>();
        for (Event e : createdEvents) {
            eventIds.add(e.getId());
        }

        new getRatingsMediaByUser().execute(eventIds);

        TextView organizerNameView = findViewById(R.id.organizer_name);
        TextView userLocationView = findViewById(R.id.organizer_location);
        userLocationView.setText(persistentOrganizerInfo.getLocation());
        organizerNameView.setText(organizerName);

        ImageView imageView = findViewById(R.id.profile);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            imageView.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }


        FloatingActionButton addEvent = findViewById(R.id.floatingActionButton4);
        addEvent.setClickable(true);
        addEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Log.w(TAG, "Ha clickeado en añadir nuevo evento");
                Intent intent = new Intent(OrganizerProfileActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });


    }

    private class getRatingsMediaByUser extends AsyncTask<ArrayList<String>, Void, Void> {

        RatingService ratingService = new RatingService();
        Float ratingsMedia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... createdEvents) {
            ratingsMedia = ratingService.getMediaOrganizer(createdEvents[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RatingBar bar=(RatingBar)findViewById(R.id.score);
            bar.setRating(ratingsMedia);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMenuItemChecked(R.id.nav_profile);
        // Retrieve the current adapter
        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapterOrg sectionsPagerAdapter = (SectionsPagerAdapterOrg) viewPager.getAdapter();
        // If it exists, then update event tabs content. This way it maintains its previous state.
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.notifyDataSetChanged();
        } else {
            // If it does not exist, then create a new adapter with the updated content.
            SectionsPagerAdapterOrg newSectionsPagerAdapter = new SectionsPagerAdapterOrg(this, getSupportFragmentManager());
            viewPager.setAdapter(newSectionsPagerAdapter);
        }
    }
}