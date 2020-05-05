package com.example.findmyrhythm.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.Model.Utils.PermissionUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.findmyrhythm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SearchEventsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText searchText;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 7346;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        searchText = findViewById(R.id.input_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GeoUtils.checkLocationEnabled(SearchEventsActivity.this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geoFire = new GeoFire(ref);

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

        if (ContextCompat.checkSelfPermission(SearchEventsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(SearchEventsActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

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

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("-------------","Listener 1");
                LatLngBounds mapLatLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                Double radius = Math.sqrt( Math.pow(mapLatLngBounds.getCenter().latitude - mapLatLngBounds.northeast.latitude, 2)
                        - Math.pow(mapLatLngBounds.getCenter().longitude - mapLatLngBounds.northeast.longitude, 2));

                EventService eventService = new EventService();

                getNearbyEvents(new GeoLocation(mapLatLngBounds.getCenter().latitude, mapLatLngBounds.getCenter().longitude), radius);

                Toast.makeText(getApplicationContext(),"IDLE",Toast.LENGTH_SHORT).show();
                // Log.e(">>>>>>>>>>>>>>>", events.toString());
            }
        });

    }

    public void getNearbyEvents(GeoLocation geoLocation, Double radius) {
        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation,radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.e("..", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                Toast.makeText(getApplicationContext(),key,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            getMyLastLocation();
        } else {
            // TODO: Permission was denied. Display an error message
            // ...
        }
    }


    private void getMyLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        } else {
                            locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(20 * 1000);
                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        return;
                                    }
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                            // Remove the request once it is received it, as we don't need continuous updates
                                            if (fusedLocationClient != null) {
                                                fusedLocationClient.removeLocationUpdates(locationCallback);
                                            }
                                        }
                                    }
                                }
                            };
                        }
                    }
                });
    }


    private void getNearbyEvents() {

        /* TODO: Buscar eventos cerca de mí o dentro de los límites del mapa.
         *   Enlaces de interés:
         * https://stackoverflow.com/questions/50631432/android-query-nearby-locations-from-firebase
         * https://stackoverflow.com/questions/43357990/query-for-nearby-locations
         * (especialmente el segundo) */

    }


    private class getEvents extends AsyncTask<String, Void, ArrayList<Event>> {

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
                HashMap<String, Object> address = event.getCompleteAddress();
                LatLng latLng = new LatLng((Double) Objects.requireNonNull(address.get("latitude")),
                        (Double) Objects.requireNonNull(address.get("longitude")));
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
