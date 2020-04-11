package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.RatingsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

public class FinishedEventInfoActivity extends AppCompatActivity {
    private static final String TAG = "Score Event";
    TextView name, date, descripcion, ubication;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ToolBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event
        //Event
        Gson gson = new Gson();
        final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);

        //View
        setContentView(R.layout.activity_finished_event_info);

        RatingsAdapter ratingsAdapter = new RatingsAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.eventPager);
        viewPager.setAdapter(ratingsAdapter);
        TabLayout tabs = findViewById(R.id.eventTabs);
        tabs.setupWithViewPager(viewPager);

        name = findViewById(R.id.eventName);
        date =  findViewById(R.id.eventDate);
        //descripcion = findViewById(R.id.eventDescContent);
        ubication = findViewById(R.id.eventLocationContent);


        name.setText(eventSelect.getName());
        date.setText("fecha");//eventSelect.getDate()
        //descripcion.setText(eventSelect);
        ubication.setText(eventSelect.getLocation());

        // SCORES LOGIC
        RatingBar bar=(RatingBar)findViewById(R.id.pastEventScore);
        bar.setStepSize(0.5f);
        //Bundle b = getIntent().getExtras();
        float score= 8;
        //change the score out of ten to star rating out of 5
        float scores = score / 2;
        //display star rating
        bar.setRating(scores);
        bar.setClickable(true);
        bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.w(TAG, "Score event");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ScoreEventDialog dialog = new ScoreEventDialog();
                    dialog.show(fragmentManager, "tagAlerta");
                }
                return true;
            }
        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
