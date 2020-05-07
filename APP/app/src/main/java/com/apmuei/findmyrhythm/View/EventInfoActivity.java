package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.AttendeeService;
import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.Photo;
import com.apmuei.findmyrhythm.Model.PhotoService;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    String signedUpText = getString(R.string.signed_up);
    String toSignUpText = getString(R.string.to_sign_up);
    TextView name, date, description, location, genre, time, eventMaxAttendees, eventPrice;
    Boolean isSignedUp = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Vista
        setContentView(R.layout.activity_event_info);

        eventMaxAttendees = findViewById(R.id.eventCapacity);
        eventPrice = findViewById(R.id.eventCost);
        name = findViewById(R.id.eventName);
        genre = findViewById(R.id.category);
        date =  findViewById(R.id.eventDate);
        time =  findViewById(R.id.eventTime);
        description = findViewById(R.id.eventDescContent);
        location = findViewById(R.id.eventLocationContent);


        //Event
        //Gson gson = new Gson();
        //final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        Event eventSelect;
        final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        Log.e("IDACTIVITY", eventSelectId);

        if(recommended) {
            eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);
        } else {
             eventSelect = persistentUserInfo.getEvent(eventSelectId);
        }

        if (eventSelect == null) {
            new getEvent().execute(eventSelectId);
        } else {
            setEventInfo(eventSelect);
            configureJoinButton(eventSelect);
            new getPhoto().execute();
        }

    }


    private void configureJoinButton(Event event) {
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final Button joinButton = (Button) findViewById(R.id.joinBtn);
        //If is a atendee of the event
        if (persistentUserInfo.getEvents().contains(event)) {
            joinButton.setText(signedUpText);
            joinButton.setBackgroundColor(0xFF673AB7);
            isSignedUp = true;
        }

        joinButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isSignedUp){
                    new unSubscribe().execute();
                    joinButton.setText(toSignUpText);
                    joinButton.setBackgroundColor(0xFFB3A1CE);

                }else{
                    new Subscribe().execute();
                    joinButton.setText(signedUpText);
                    joinButton.setBackgroundColor(0xFF673AB7);
                }

            }
        });
    }


    private void setEventInfo(Event event) {
        Date dateF = event.getEventDate();
        DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        date.setText(df.format(dateF));
        time.setText(df2.format(dateF));

        name.setText(event.getName());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        genre.setText(event.getGenre());
        eventMaxAttendees.setText(String.valueOf(event.getMaxAttendees())+" personas");
        eventPrice.setText(String.valueOf(event.getPrice())+"€");
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


    private class getEvent extends AsyncTask<String, Void, Event> {

        @Override
        protected Event doInBackground(String... ids) {
            String id = ids[0];
            EventService eventService = new EventService();
            return eventService.getEvent(id);
        }

        @Override
        protected void onPostExecute(final Event event) {
            setEventInfo(event);
            configureJoinButton(event);
            new getPhoto().execute();
        }

    }


    private class getPhoto extends AsyncTask<Void, Void, Photo> {

        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Photo doInBackground(Void... voids) {
            Event eventSelect;
            final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
            if(recommended) {
              eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);

            } else{
              eventSelect = persistentUserInfo.getEvent(eventSelectId);
            }

            if (eventSelect == null) {
                EventService eventService = new EventService();
                eventSelect = eventService.getEvent(eventSelectId);
            }
            PhotoService photoService = new PhotoService();
            Photo photoEvent = photoService.getPhoto(eventSelect.getEventImage());
            return photoEvent;
        }

        @Override
        protected void onPostExecute(Photo photo) {

            byte[] decodedString = Base64.decode(photo.getEventImage(),Base64.NO_WRAP);
            InputStream inputStream  = new ByteArrayInputStream(decodedString);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            Bitmap imagenFinal = Bitmap.createScaledBitmap(bitmap,242,152,false);
            final ImageView imageEvent =  findViewById(R.id.imageEvent);
            imageEvent.setImageBitmap(imagenFinal);

        }
    }


    private class unSubscribe extends AsyncTask<Void, Void, Void> {
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            AttendeeService attendeeService = new AttendeeService();
            eventSelect = persistentUserInfo.getEvent(eventSelectId);
            attendeeService.deleteAttendeeByEvent(eventSelect.getId());
            persistentUserInfo.deleteEvent(getApplicationContext(),eventSelect);
            persistentUserInfo.addUniqueEventRecommended(getApplicationContext(), eventSelect);
            isSignedUp = false;
            return null;
        }

    }

    private class Subscribe extends AsyncTask<Void, Void, Void> {
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            AttendeeService attendeeService = new AttendeeService();
            eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);
            attendeeService.createAttendee(currentUser.getUid(), eventSelect.getId());
            persistentUserInfo.addEvent(getApplicationContext(),eventSelect);
            persistentUserInfo.deleteRecommendedEvent(getApplicationContext(), eventSelect);
            isSignedUp = true;

            return null;
        }

    }
}

