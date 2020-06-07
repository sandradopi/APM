package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.AttendeeService;
import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.Photo;
import com.apmuei.findmyrhythm.Model.PhotoService;
import com.apmuei.findmyrhythm.Model.User;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventInfoActivity extends AppCompatActivity {
    private static final String TAG = "EventInfoActivity";

    private TextView name, date, description, location, genre, time, eventMaxAttendees, eventPrice;
    private Button viewOnMap;

    private Boolean isSignedUp = false;
    private Event eventSelect;
    private String eventSelectId;
    private boolean recommended;

    private PersistentUserInfo persistentUserInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Toolbar
        ActionBar actionBar = getSupportActionBar();
        Assert.assertNotNull(actionBar, "ActionBar not found");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar_empty);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Layout
        setContentView(R.layout.activity_event_info);

        eventMaxAttendees = findViewById(R.id.eventCapacity);
        eventPrice = findViewById(R.id.eventCost);
        name = findViewById(R.id.eventName);
        genre = findViewById(R.id.category);
        date =  findViewById(R.id.eventDate);
        time =  findViewById(R.id.eventTime);
        description = findViewById(R.id.eventDescContent);
    //    description.setMovementMethod(new ScrollingMovementMethod());
        location = findViewById(R.id.eventLocationContent);
        viewOnMap = findViewById(R.id.view_on_map);

        //Event
        //Gson gson = new Gson();
        //final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);
        persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Assert.assertNotNull(extras, TAG+": No extras found");
        recommended = extras.getBoolean("RECOMMENDED");
        eventSelectId = getIntent().getStringExtra("EVENT");

        if (recommended) {
            new getEvent().execute(eventSelectId);
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
        final Button joinButton = findViewById(R.id.joinBtn);
        final String signedUpText = getString(R.string.signed_up);
        final String toSignUpText = getString(R.string.to_sign_up);

        //If is an attendee of the event
        if (persistentUserInfo.getEvents().contains(event)) {
            joinButton.setText(signedUpText);
            joinButton.setBackgroundColor(getResources().getColor(R.color.primary700));
            isSignedUp = true;
        }

        joinButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // If the user is signed up to the event
                if (isSignedUp) {
                    new unSubscribe().execute();
                    joinButton.setText(toSignUpText);
                    joinButton.setBackgroundColor(getResources().getColor(R.color.primaryBackgroundDarker2));
                } else {
                    new Subscribe().execute();
                    joinButton.setText(signedUpText);
                    joinButton.setBackgroundColor(getResources().getColor(R.color.primary700));
                }

            }
        });
    }


    private void setEventInfo(final Event event) {
        Date dateF = event.getEventDate();
        DateFormat df = new SimpleDateFormat(getString(R.string.date_pattern), java.util.Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat(getString(R.string.hour_pattern), java.util.Locale.getDefault());
        date.setText(df.format(dateF));
        time.setText(df2.format(dateF));

        name.setText(event.getName());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        genre.setText(event.getGenre());
        eventMaxAttendees.setText(event.getMaxAttendees()+ " " + getString(R.string.people));
        eventPrice.setText(event.getPrice() + getString(R.string.euro));

        viewOnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EventInfoActivity.this, EventOnMapActivity.class);
                String eventJson = (new Gson()).toJson(event);
                intent.putExtra("EVENT", eventJson);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }


    //================================================================================
    // AsyncTasks
    //================================================================================

    private class getEvent extends AsyncTask<String, Void, Event> {

        @Override
        protected Event doInBackground(String... ids) {
            String id = ids[0];
            EventService eventService = new EventService();
            return eventService.getEvent(id);
        }

        @Override
        protected void onPostExecute(final Event event) {
            eventSelect = event;
            setEventInfo(event);
            configureJoinButton(event);
            new getPhoto().execute();
        }

    }


    private class getPhoto extends AsyncTask<Void, Void, Photo> {

        @Override
        protected Photo doInBackground(Void... voids) {
            EventService eventService = new EventService();
            Event eventSelect;
            if(!recommended) {
                eventSelect = eventService.getEvent(eventSelectId);
            } else{
                eventSelect = persistentUserInfo.getEvent(eventSelectId);
            }

            if (eventSelect == null) {
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

        @Override
        protected Void doInBackground(Void... voids) {
            AttendeeService attendeeService = new AttendeeService();
            attendeeService.deleteAttendeeByEvent(eventSelect.getId());
            persistentUserInfo.deleteEvent(getApplicationContext(),eventSelect);
            isSignedUp = false;

            return null;
        }

    }


    private class Subscribe extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Assert.assertNotNull(currentUser, TAG+": FirebaseUser not found");
            AttendeeService attendeeService = new AttendeeService();
            attendeeService.createAttendee(currentUser.getUid(), eventSelect.getId());
            persistentUserInfo.addEvent(getApplicationContext(),eventSelect);
            isSignedUp = true;

            return null;
        }

    }

}

