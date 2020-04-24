package com.example.findmyrhythm.View;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;
import com.example.findmyrhythm.View.tabs.SectionsPagerAdapterOrg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.FileNotFoundException;


public class OrganizerProfileActivity extends OrganizerMenuDrawerActivity {
    private static final String TAG = "Perfil Organizador";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        setMenuItemChecked(R.id.nav_profile);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);



        SectionsPagerAdapterOrg sectionsPagerAdapter = new SectionsPagerAdapterOrg(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String organizerName = preferences.getString("name", null);
        String userEmail = preferences.getString("email", null);
        TextView organizerNameView = findViewById(R.id.organizer_name);
//        TextView userLocationView = findViewById(R.id.user_location);
//        userLocationView.setText();
        organizerNameView.setText(organizerName);

        ImageView imageView = findViewById(R.id.profile);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            imageView.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }



        ImageView infoButton = findViewById(R.id.info);
        infoButton.setClickable(true);
        infoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en el dialogo de la información del local");
                FragmentManager fragmentManager = getSupportFragmentManager();
                OrganizerInfoDialog dialogo = new OrganizerInfoDialog();
                dialogo.show(fragmentManager, "tagAlerta");

            }
        });

        FloatingActionButton addEvent = findViewById(R.id.floatingActionButton4);
        addEvent.setClickable(true);
        addEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en añadir nuevo evento");
                Intent intent = new Intent(OrganizerProfileActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("account_type", "organizer");
        editor.commit(); // or apply

        setMenuItemChecked(R.id.nav_profile);
        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapterOrg sectionsPagerAdapter = new SectionsPagerAdapterOrg(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

    }
}