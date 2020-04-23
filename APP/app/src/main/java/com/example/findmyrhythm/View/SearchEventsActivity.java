package com.example.findmyrhythm.View;

import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.findmyrhythm.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchEventsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        searchText = findViewById(R.id.input_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        // Add a marker in Sydney and move the camera

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    mMap.clear();
                    new getEvents().execute(searchText.getText().toString());

                    return true;
                }
                return false;
            }
        });

    }


    private class getEvents extends AsyncTask<String, Void, ArrayList<Event>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Event> doInBackground(String... searchTexts) {
            String searchText = searchTexts[0];
            EventService eventService = new EventService();
            ArrayList<Event> events = new ArrayList<>();

            events = eventService.getEventsByTitle(searchText);

            return events;
        }


        @Override
        protected void onPostExecute(final ArrayList<Event> events) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Event event : events) {
                HashMap<String, String> address = event.getCompleteAddress();
                LatLng latLng = new LatLng(Double.parseDouble(address.get("latitude")), Double.parseDouble(address.get("longitude")));
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(event.getName()));
                builder.include(marker.getPosition());
                LatLngBounds bounds = builder.build();
                LatLng center = bounds.getCenter();
                builder.include(new LatLng(center.latitude-0.01f,center.longitude-0.01f));
                builder.include(new LatLng(center.latitude+0.01f,center.longitude+0.01f));
                bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                marker.showInfoWindow();
            }
        }

    }




}
