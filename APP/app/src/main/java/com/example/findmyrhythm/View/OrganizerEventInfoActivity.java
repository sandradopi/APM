package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
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
import com.google.gson.Gson;

import java.util.ArrayList;

public class OrganizerEventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event
        Gson gson = new Gson();
        final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);

        //View
        setContentView(R.layout.activity_organizer_event_info);
        showEventInfo(eventSelect);

        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("EVENT")) {
                String eventJson = extras.getString("EVENT");
                Log.e("DEBUG", eventJson);
                Gson gson = new Gson();
                Event event = gson.fromJson(eventJson, Event.class);
                showEventInfo(event);

            } else if (extras.containsKey("ID")) {
                String eventId = extras.getString("ID");
                new OrganizerEventInfoActivity.getEvent().execute(eventId);
            }

        }*/

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(this);

        ImageView editButton = (ImageView) findViewById(R.id.editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Editar evento";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }


    private void showEventInfo(Event event) {
        TextView eventName = findViewById(R.id.eventName);
        TextView eventMaxAttendees = findViewById(R.id.eventCapacity);
        TextView eventPrice = findViewById(R.id.eventCost);
        TextView eventDate = findViewById(R.id.eventDate);
        TextView eventLocation = findViewById(R.id.eventLocationContent);
        TextView eventDescrip = findViewById(R.id.eventDescContent);

        eventName.setText(event.getName());
        eventMaxAttendees.setText(String.valueOf(event.getMaxAttendees()));
        eventPrice.setText(String.valueOf(event.getPrice())+"€");
        eventDate.setText("fecha");
        eventLocation.setText(event.getLocation());
        eventDescrip.setText("descripcion");
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


    /*private class getEvent extends AsyncTask<String, Void, Event> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Event doInBackground(String... ids) {
            String id = ids[0];
            EventService eventService = new EventService();
            Event event = eventService.getEvent(id);

            return event;
        }

        @Override
        protected void onPostExecute(Event event) {
            showEventInfo(event);
        }
    }*/

}
