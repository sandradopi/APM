package com.example.findmyrhythm.View;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SearchOrganizerLocation extends FragmentActivity implements OnMapReadyCallback {

    Button submit;
    String provider;
    private Location newLocation;
    private GoogleMap mMap;
    private LatLng coordinates;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_organizer_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.organizerMap);
        mapFragment.getMapAsync(this);

        // Initialize the new location through the Location Manager
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria crta = new Criteria();
        provider = lm.getBestProvider(crta, true);
        newLocation = new Location(provider);

        // Retrieve the default location coordinates to set the map initial position
        Intent callerIntent = getIntent();
        Location lastLocation = callerIntent.getParcelableExtra("lastLocation");
        coordinates = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        submit = (Button) findViewById(R.id.organizer_submit);
        // Send control back to organizer log activity with the new location value
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng coordinates = marker.getPosition();
                newLocation.setLatitude(coordinates.latitude);
                newLocation.setLongitude(coordinates.longitude);
                Intent pickedLocation = new Intent();
                pickedLocation.putExtra("pickedLocation", newLocation);
                setResult(RESULT_OK, pickedLocation);
                finish();
            }
        });

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
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
    }
}
