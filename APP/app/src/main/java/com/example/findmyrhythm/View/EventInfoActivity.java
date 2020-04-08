package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.example.findmyrhythm.Model.Spectator;
import com.example.findmyrhythm.Model.SpectatorService;
import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    Boolean id_Joined = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_event_info);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(this);

        final Chronometer chronometer = (Chronometer) findViewById(R.id.eventClock);
        final Button joinButton = (Button) findViewById(R.id.joinBtn);

        final SpectatorService spectatorService = new SpectatorService();

        final Spectator spectator = spectatorService.findSpectatorByIds("FJkjdJFIOEHb7895", "PLCfp0OsCMN3MG14kcuMZoPHnwA3");

        if (spectator != null){
            id_Joined=true;
        }

           if (id_Joined) {
                joinButton.setText("Apuntado");
                joinButton.setBackgroundColor(Color.GREEN);
                chronometer.start();
            }


        joinButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(id_Joined){
                    spectatorService.deleteSpectator(spectator);
                    joinButton.setText("Apuntarse");
                    joinButton.setBackgroundColor(Color.BLUE);
                    chronometer.stop();
                    id_Joined= false;

                }else{
                    spectatorService.createSpectator("FJkjdJFIOEHb7895", "PLCfp0OsCMN3MG14kcuMZoPHnwA3");
                    joinButton.setText("Apuntado");
                    joinButton.setBackgroundColor(Color.GREEN);
                    chronometer.start();
                    id_Joined= true;

                }

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
