package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.Photo;
import com.example.findmyrhythm.Model.PhotoService;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrganizerEventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    Photo photoEvent;
    PhotoService photoService= new PhotoService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event
        Gson gson = new Gson();
       // final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        Event eventSelect = persistentOrganizerInfo.getEvent(eventSelectId);
        //View
        setContentView(R.layout.activity_organizer_event_info);
        showEventInfo(eventSelect);
        new getPhoto().execute();

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




        Button editButton = findViewById(R.id.editBtn);
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
        TextView eventTime = findViewById(R.id.eventTime);
        TextView category = findViewById(R.id.category);

        eventName.setText(event.getName());
        eventMaxAttendees.setText(String.valueOf(event.getMaxAttendees())+" personas");
        eventPrice.setText(String.valueOf(event.getPrice())+"€");
        Date dateF;
        dateF = event.getEventDate();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
        eventDate.setText(df.format(dateF));
        eventTime.setText(df2.format(dateF));
        eventLocation.setText(event.getLocation());
        eventDescrip.setText(event.getDescription());
        category.setText(event.getGenre());
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

}
