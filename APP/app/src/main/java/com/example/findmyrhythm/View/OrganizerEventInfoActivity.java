package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class OrganizerEventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_organizer_event_info);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey("EVENT")) {
                String eventJson = extras.getString("EVENT");
                Log.e("DEBUG", eventJson);
                Gson gson = new Gson();
                event = gson.fromJson(eventJson, Event.class);

            } else if (extras.containsKey("ID")) {
                String eventId = extras.getString("ID");
                EventService eventService = new EventService();
                event = eventService.getEvent(eventId);
            }

            TextView eventName = findViewById(R.id.eventName);
            TextView eventMaxAttendees = findViewById(R.id.eventCapacity);
            TextView eventPrice = findViewById(R.id.eventCost);

            eventName.setText(event.getName());
            eventMaxAttendees.setText(String.valueOf(event.getMaxAttendees()));
            eventPrice.setText(String.valueOf(event.getPrice())+"€");

        }

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

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLong = new LatLng(43.3713500, -8.3960000);
        map.addMarker(new MarkerOptions()
                .position(latLong)
                .title("Viva Suecia"));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLong));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
