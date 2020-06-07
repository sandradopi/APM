package com.apmuei.findmyrhythm.View;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.apmuei.findmyrhythm.R;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Objects;


public class EventOnMapActivity extends FragmentActivity implements OnMapReadyCallback {
    final String TAG = "EventOnMapActivity";

    Event eventSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_on_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Assert.assertNotNull(mapFragment, TAG+": SupportMapFragment not found");
        mapFragment.getMapAsync(this);

        // Get event info
        Gson gson = new Gson();
        eventSelect = gson.fromJson(getIntent().getStringExtra("EVENT"), Event.class);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Check if there is a selected location and put the marker there.
        addEventToMap(eventSelect, googleMap);

    }


    void addEventToMap(Event event, GoogleMap map) {
        // Get event address to put the marker on the map
        HashMap address = event.getCompleteAddress();
        LatLng latLng = new LatLng((Double) Objects.requireNonNull(address.get("latitude")),
                (Double) Objects.requireNonNull(address.get("longitude")));
        Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(event.getName()));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        marker.setTag(event);
        marker.showInfoWindow();
        // Center view on Event location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
