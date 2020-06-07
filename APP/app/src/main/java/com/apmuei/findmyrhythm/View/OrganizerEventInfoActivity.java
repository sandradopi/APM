package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.Photo;
import com.apmuei.findmyrhythm.Model.PhotoService;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrganizerEventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    Photo photoEvent;
    PhotoService photoService= new PhotoService();
    Event eventSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.info_organizer);


        //Event
        Gson gson = new Gson();
       // final Event eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        eventSelect = persistentOrganizerInfo.getEvent(eventSelectId);
        //View
        setContentView(R.layout.activity_organizer_event_info);
        showEventInfo(eventSelect);
        new getPhoto().execute();

        final Button editButton = findViewById(R.id.editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventInfoActivity.this, CreateEventActivity.class);
                intent.putExtra("EVENT",  eventSelect.getId());
                startActivity(intent);
                finish();
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
      //  eventDescrip.setMovementMethod(new ScrollingMovementMethod());
        TextView eventTime = findViewById(R.id.eventTime);
        TextView category = findViewById(R.id.category);

        eventName.setText(event.getName());
        eventMaxAttendees.setText(String.valueOf(event.getMaxAttendees()) + " " + OrganizerEventInfoActivity.this.getResources().getString(R.string.people));
        eventPrice.setText(String.valueOf(event.getPrice())+"€");
        Date dateF;
        dateF = event.getEventDate();
        DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
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
        protected Void doInBackground(Void... voids) {
            Event eventSelect;

            eventSelect = persistentUserInfo.getEvent(eventSelectId);


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
