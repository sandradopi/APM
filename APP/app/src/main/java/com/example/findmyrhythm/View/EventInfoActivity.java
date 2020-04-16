package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.example.findmyrhythm.Model.AttendeeService;
import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.Attendee;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.ListAdapterNext;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    String joined_text = "Apuntado";
    String join_event_text = "Apuntarse";
    TextView name, date, descripcion, ubication;
    AttendeeService attendeeService= new AttendeeService();
    Boolean Joined= false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event
        //Gson gson = new Gson();
        //final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        Event eventSelect;

        if(recommended) {
            eventSelect  = persistentUserInfo.getEventsRecommended(eventSelectId);

        } else{
             eventSelect = persistentUserInfo.getEvent(eventSelectId);
        }




        //Its joined?
        //final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());


        //Vista
        setContentView(R.layout.activity_event_info);
        final Button joinButton = (Button) findViewById(R.id.joinBtn);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(this);


        name = findViewById(R.id.eventName);
        date =  findViewById(R.id.eventDate);
        //descripcion = findViewById(R.id.eventDescContent);
        ubication = findViewById(R.id.eventLocationContent);


        name.setText(eventSelect.getName());
        date.setText("fecha");//eventSelect.getDate()
        //descripcion.setText(eventSelect);
        ubication.setText(eventSelect.getLocation());


        //If is a atendee of the event
        if (persistentUserInfo.getEvents().contains(eventSelect)) {
            joinButton.setText(joined_text);
            joinButton.setBackgroundColor(0xFF673AB7);
            Joined= true;
        }


        joinButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Joined){
                    new unSubscribe().execute();
                    joinButton.setText(join_event_text);
                    joinButton.setBackgroundColor(0xFFB3A1CE);
                    System.out.println("HOLA1");



                }else{
                    new Subscribe().execute();
                    joinButton.setText(joined_text);
                    joinButton.setBackgroundColor(0xFF673AB7);
                    System.out.println("HOLA2");

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLong = new LatLng(43.3713500, -8.3960000);
        map.addMarker(new MarkerOptions()
                .position(latLong)
                .title(""));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLong));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class unSubscribe extends AsyncTask<Void, Void, Void> {
        Gson gson = new Gson();
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        Event eventSelect  = persistentUserInfo.getEvent(eventSelectId);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            attendeeService.deleteAttendeeByEvent(eventSelect.getId());
            persistentUserInfo.deleteEvent(getApplicationContext(),eventSelect);
            Joined= false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("OUT"+persistentUserInfo.getEvents());

        }
    }

    private class Subscribe extends AsyncTask<Void, Void, Void> {
        Gson gson = new Gson();
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        Event eventSelect  = persistentUserInfo.getEventsRecommended(eventSelectId);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            attendeeService.createAttendee(currentUser.getUid(), eventSelect.getId());
            persistentUserInfo.addEvent(getApplicationContext(),eventSelect);
            Joined = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}

