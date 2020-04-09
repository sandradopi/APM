package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.example.findmyrhythm.Model.AttendeeService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.Attendee;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    Boolean id_Joined = false;
    AttendeeService attendeeService = new AttendeeService();
    UserService userService = new UserService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_event_info);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(this);

        final Chronometer chronometer = (Chronometer) findViewById(R.id.eventClock);
        final Button joinButton = (Button) findViewById(R.id.joinBtn);

           if (id_Joined) {
                joinButton.setText("Apuntado");
                joinButton.setBackgroundColor(Color.GREEN);
                chronometer.start();
           }


        joinButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(id_Joined){
                    //attendeeService.deleteAttendee(attendee);
                    joinButton.setText("Apuntarse");
                    joinButton.setBackgroundColor(0xFFB3A1CE);
                    chronometer.stop();
                    id_Joined= false;

                }else{
                    attendeeService.createAttendee("FJkjdJFIOEHb7895", "PLCfp0OsCMN3MG14kcuMZoPHnwA3");
                    joinButton.setText("Apuntado");
                    joinButton.setBackgroundColor(0xFF673AB7);
                    chronometer.start();
                    id_Joined= true;

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getJoined().execute();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private class getJoined extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("aqui1");
            try {
                User user = userService.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Attendee attendee = attendeeService.findAttendeeByIds("FJkjdJFIOEHb7895", user.getId());
                if (attendee != null){
                    id_Joined=true;
                }
                System.out.println(attendee.getIdEvent() + "\n");
                System.out.println(attendee.getIdUser() + "\n");
            } catch (InstanceNotFoundException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

