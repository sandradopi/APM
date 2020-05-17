package com.apmuei.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.SearchFilters;
import com.apmuei.findmyrhythm.Model.Utils.GeoUtils;
import com.apmuei.findmyrhythm.Model.Utils.PermissionUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


public class SearchEventsActivity extends FragmentActivity implements FiltersDialogInterface,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SensorEventListener {

    // Constants:
    private final static String TAG = "SearchEventsA";
    private static final int LOCATION_PERMISSION_CODE = 7346;

    // Sensors:
    private SensorManager sensorManager;
    private Sensor light;

    // GUI:
    private EditText searchEditText;

    // Map and location:
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private GeoFire geoFire;

    // Filters dialog fragment:
    FragmentManager fragmentManager;
    SearchFiltersDialogFragment searchFiltersDialogFragment;
    SearchFilters currentSearchFilters;

    // Event sets:
    private HashSet<EventMarker> eventMarkersSet = new HashSet<>();
    private HashSet<EventMarker> newEventMarkersSet = new HashSet<>();


    //================================================================================
    // Activity Lifecycle
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        searchEditText = findViewById(R.id.input_search);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (light == null) {
            System.out.println("No sensor light");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geoFire = new GeoFire(ref);

        // Dialog with the search filters
        fragmentManager = getSupportFragmentManager();
        searchFiltersDialogFragment = new SearchFiltersDialogFragment();
        searchFiltersDialogFragment.setInterface(this);
        currentSearchFilters = SearchFiltersDialogFragment.getDefaultFilters();

        Toast.makeText(getApplicationContext(), getString(R.string.search_events_usage_info), Toast.LENGTH_LONG).show();

        FloatingActionButton searchFiltersFAB = findViewById(R.id.search_filters);
        searchFiltersFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchFiltersDialogFragment.show(fragmentManager, "dialog");
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    //================================================================================
    // Light Sensor
    //================================================================================

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float amountOfLight = sensorEvent.values[0];
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {

            boolean success;

            if (amountOfLight > 50){

                if(mMap!=null){
                    success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                            R.raw.style_json_default));
                }

            } else {
                if(mMap!=null){
                    success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                            R.raw.style_json));
                }
            }
        }

    }


    //================================================================================
    // Filters Dialog
    //================================================================================

    /**
     * Interface method to enable DialogFragment->Activity communication.
     */
    @Override
    public void applyFilters(SearchFilters searchFilters) {
        if (! searchEditText.getText().toString().isEmpty()) {
            mMap.clear();

            searchFilters.setSearchText(searchEditText.getText().toString());
            updateCurrentFilters(searchFilters);

            new getEventsByTitle().execute(searchFilters);
        } else {
            // Apply filters if they are different
            if (! searchFilters.equals(currentSearchFilters)) {
                Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> finishEvent - " + searchFilters.getShowPastEvents());
                // Filter events by date
                updateCurrentFilters(searchFilters);

                for (EventMarker eventMarker : eventMarkersSet) {
                    applyFiltersToEventMarker(eventMarker, searchFilters);
                }

            }
        }

    }

    private void updateCurrentFilters(SearchFilters searchFilters) {
        searchFilters.setSearchText(searchEditText.getText().toString());
        currentSearchFilters = searchFilters;
    }

    private void updateCurrentFilters() {
        currentSearchFilters.setSearchText(searchEditText.getText().toString());
    }


    private boolean applyFiltersToEventMarker(EventMarker eventMarker, SearchFilters searchFilters) {
        boolean filtered;

        Date eventDate = eventMarker.event.getEventDate();
        Date currentDate = new Date();
        filtered = (!searchFilters.getShowPastEvents() && eventDate.before(currentDate));

        String searchString = searchEditText.getText().toString().toLowerCase();
        String eventName = eventMarker.event.getName().toLowerCase();
        filtered = filtered || (!searchString.isEmpty() && !eventName.contains(searchString));

        if (filtered) {
            eventMarker.remove();
        } else {
            eventMarker.addToMap(mMap);
        }

        return filtered;

    }


    //================================================================================
    // Map
    //================================================================================

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


        GeoUtils.checkLocationEnabled(SearchEventsActivity.this);

        if (ContextCompat.checkSelfPermission(SearchEventsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(SearchEventsActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // Check if no view has focus:
                    View view = SearchEventsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    // Remove all markers from map
                    mMap.clear();

                    updateCurrentFilters();

                    new getEventsByTitle().execute(currentSearchFilters);

                    return true;
                }
                return false;
            }
        });

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker args) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker args) {

                // Getting view from the layout file window_info_layout
                View v = getLayoutInflater().inflate(R.layout.window_info_layout, null);

                TextView title = v.findViewById(R.id.title);
                title.setText(args.getTitle());

                Event event = (Event) args.getTag();
                DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());

                TextView snippet = v.findViewById(R.id.snippet);
                assert event != null;
                snippet.setText(df.format(event.getEventDate()));

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        Event event = (Event) marker.getTag();
                        assert event != null;
                        Log.e(TAG + " >>> ", event.getId());

                        Intent intent = new Intent(SearchEventsActivity.this, EventInfoActivity.class);
                        intent.putExtra("EVENT", event.getId());
                        intent.putExtra("RECOMMENDED", false);
                        startActivity(intent);

                    }
                });
                return v;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (! searchEditText.getText().toString().isEmpty()) {
                    return;
                }

                Log.e("-------------","Listener 1");
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLngBounds mapLatLngBounds = visibleRegion.latLngBounds;

                float[] diagonalDistance = new float[1];
                double diagonalDistanceDouble;
                double radius;

                Location.distanceBetween(
                        visibleRegion.farLeft.latitude,
                        visibleRegion.farLeft.longitude,
                        visibleRegion.nearRight.latitude,
                        visibleRegion.nearRight.longitude,
                        diagonalDistance
                );

                diagonalDistanceDouble = diagonalDistance[0];
                radius = (diagonalDistanceDouble / (2.0 * 1000.0));

                double latitude = mapLatLngBounds.getCenter().latitude;
                double longitude = mapLatLngBounds.getCenter().longitude;

                // Toast.makeText(getApplicationContext(),radius+"",Toast.LENGTH_SHORT).show();
                // Toast.makeText(getApplicationContext(),latitude+ " "+longitude,Toast.LENGTH_SHORT).show();

                getNearbyEvents(new GeoLocation(latitude, longitude), radius);

                Log.e(TAG, eventMarkersSet.toString());

            }
        });

    }


    /**
     * Gets nearby events for a given location (usually user last location).
     * The library used for that is Geofire.
     *
     * https://firebaseopensource.com/projects/firebase/geofire-java/
     * https://stackoverflow.com/questions/50631432/android-query-nearby-locations-from-firebase
     * https://stackoverflow.com/questions/43357990/query-for-nearby-locations
     */
    public void getNearbyEvents(GeoLocation geoLocation, Double radius) {

        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation,radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                EventMarker eventMarker = new EventMarker(key);
                if (! newEventMarkersSet.contains(eventMarker)) {
                    newEventMarkersSet.add(eventMarker);
                    Log.d("..", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                }
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
                showMarkersOnMap();

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }


    /**
     * Gets last user location using FusedLocationClient.
     * If returned location is null, it crates a location request.
     */
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
                            mMap.setMyLocationEnabled(true);

                        } else {
                            locationRequest = LocationRequest.create();
                            locationRequest.setInterval(20 * 1000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        Log.e(TAG, "NULL LOCATION RESULT");
                                        return;
                                    }
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                            mMap.setMyLocationEnabled(true);

                                            // Remove the request once it is received it, as we don't need continuous updates
                                            if (fusedLocationClient != null) {
                                                fusedLocationClient.removeLocationUpdates(locationCallback);
                                            }
                                        }
                                    }
                                }
                            };

                            fusedLocationClient.requestLocationUpdates(locationRequest,
                                    locationCallback,
                                    Looper.getMainLooper());
                        }
                    }
                });
    }


    //================================================================================
    // Markers
    //================================================================================

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    private void showMarkersOnMap() {
        // Markers manipulation must be done in GUI thread
        SearchEventsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (EventMarker eventMarker : newEventMarkersSet) {
                    new showEventMarker().execute(eventMarker.id);
                }
                newEventMarkersSet.clear();
            }
        });
    }


    //================================================================================
    // Permissions
    //================================================================================

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


    //================================================================================
    // AsyncTasks
    //================================================================================

    private class showEventMarker extends AsyncTask<String, Void, EventMarker> {

        @Override
        protected EventMarker doInBackground(String... ids) {
            String eventId = ids[0];
            EventService eventService = new EventService();
            Event event = eventService.getEvent(eventId);
            return new EventMarker(eventId, event);
        }


        @Override
        protected void onPostExecute(EventMarker eventMarker) {
            Log.e(">>>>>>>>>>>>>>>", eventMarker.event.getName());

            applyFiltersToEventMarker(eventMarker, currentSearchFilters);
            eventMarkersSet.add(eventMarker);

        }

    }



    private class getEventsByTitle extends AsyncTask<SearchFilters, Void, ArrayList<Event>> {

        @Override
        protected ArrayList<Event> doInBackground(SearchFilters... searchFiltersArr) {
            SearchFilters searchFilters = searchFiltersArr[0];
            EventService eventService = new EventService();
            ArrayList<Event> events;

            events = eventService.getEventsByTitle(searchFilters);

            return events;
        }


        @Override
        protected void onPostExecute(final ArrayList<Event> events) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Event event : events) {
                // At this point, there are no markers in map and the results are already filtered
                EventMarker eventMarker = new EventMarker(event.getId(), event);

                eventMarker.addToMap(mMap);
                builder.include(eventMarker.marker.getPosition());
                eventMarkersSet.add(eventMarker);

            }

            try {
                LatLngBounds bounds = builder.build();
                LatLng center = bounds.getCenter();
                builder.include(new LatLng(center.latitude - 0.02f, center.longitude - 0.02f));
                builder.include(new LatLng(center.latitude + 0.02f, center.longitude + 0.02f));
                bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "No se han encontrado eventos con ese título que cumplan con los filtros de búsqueda.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "No valid events found.");
            }

        }

    }


    //================================================================================
    // Private Classes
    //================================================================================

    private class EventMarker {
        String id;
        Event event;
        Marker marker;

        EventMarker(String id, Event event) {
            this.id = id;
            this.event = event;
        }

        EventMarker(String id) {
            this.id = id;
        }

        void remove() {
            if (this.marker != null) {
                this.marker.setVisible(false);
            }
        }

        void addToMap(GoogleMap map) {
            if (marker == null) {
                HashMap address = this.event.getCompleteAddress();
                LatLng latLng = new LatLng((Double) Objects.requireNonNull(address.get("latitude")),
                        (Double) Objects.requireNonNull(address.get("longitude")));
                Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(this.event.getName()));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                marker.setTag(this.event);
                this.marker = marker;
            } else {
                this.marker.setVisible(true);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            EventMarker eventMarker = (EventMarker) obj;
            return this.id.equals(eventMarker.id);
        }
    }


}
