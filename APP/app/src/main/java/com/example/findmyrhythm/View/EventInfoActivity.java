package com.example.findmyrhythm.View;

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

import com.example.findmyrhythm.Model.AttendeeService;
import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.Photo;
import com.example.findmyrhythm.Model.PhotoService;
import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    String joined_text = "Apuntado";
    String join_event_text = "Apuntarse";
    TextView name, date, descripcion, ubication, category, time, eventMaxAttendees,eventPrice;
    AttendeeService attendeeService= new AttendeeService();
    PhotoService photoService= new PhotoService();
    Boolean Joined= false;
    Photo photoEvent;


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
        Event eventSelect;
        final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        Log.e("IDACTIVITY", eventSelectId);


        if(recommended) {
            eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);


        } else{
             eventSelect = persistentUserInfo.getEvent(eventSelectId);

        }

        new getPhoto().execute();


        //Its joined?
        //final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());


        //Vista
        setContentView(R.layout.activity_event_info);
        final Button joinButton = (Button) findViewById(R.id.joinBtn);



        eventMaxAttendees = findViewById(R.id.eventCapacity);
        eventPrice = findViewById(R.id.eventCost);
        name = findViewById(R.id.eventName);
        category = findViewById(R.id.category);
        date =  findViewById(R.id.eventDate);
        time =  findViewById(R.id.eventTime);
        descripcion = findViewById(R.id.eventDescContent);
        ubication = findViewById(R.id.eventLocationContent);


        Date dateF;
        dateF = eventSelect.getEventDate();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
        date.setText(df.format(dateF));
        time.setText(df2.format(dateF));

        name.setText(eventSelect.getName());
        descripcion.setText(eventSelect.getDescription());
        ubication.setText(eventSelect.getLocation());
        category.setText(eventSelect.getGenre());
        eventMaxAttendees.setText(String.valueOf(eventSelect.getMaxAttendees())+" personas");
        eventPrice.setText(String.valueOf(eventSelect.getPrice())+"€");




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

                }else{
                    new Subscribe().execute();
                    joinButton.setText(joined_text);
                    joinButton.setBackgroundColor(0xFF673AB7);
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

    private class getPhoto extends AsyncTask<Void, Void, Void> {

        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
            if(recommended) {
              eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);

            } else{
              eventSelect = persistentUserInfo.getEvent(eventSelectId);
            }

            photoEvent = photoService.getPhoto(eventSelect.getEventImage());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            byte[] decodedString = Base64.decode(photoEvent.getEventImage(),Base64.NO_WRAP);
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            eventSelect = persistentUserInfo.getEvent(eventSelectId);
            attendeeService.deleteAttendeeByEvent(eventSelect.getId());
            persistentUserInfo.deleteEvent(getApplicationContext(),eventSelect);
            persistentUserInfo.addUniqueEventRecommended(getApplicationContext(), eventSelect);
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
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);
            attendeeService.createAttendee(currentUser.getUid(), eventSelect.getId());
            persistentUserInfo.addEvent(getApplicationContext(),eventSelect);
            persistentUserInfo.deleteRecommendedEvent(getApplicationContext(), eventSelect);
            Joined = true;



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}

