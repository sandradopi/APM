package com.apmuei.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
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

    // Constants
    private final static String TAG = "SearchEventsA";
    private static final int LOCATION_PERMISSION_CODE = 7346;

    // Sensors
    private SensorManager sensorManager;
    private Sensor light;
    private int numLightSensorChanges = 0;

    // GUI
    private EditText searchEditText;

    // Map style
    boolean isMapStyleDark = false;

    // Map and location
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private GeoFire geoFire;

    // Filters dialog fragment
    FragmentManager fragmentManager;
    SearchFiltersDialogFragment searchFiltersDialogFragment;
    SearchFilters currentSearchFilters;

    // Help dialog fragment
    HelpDialogFragment helpDialogFragment;

    // Event sets
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
        Assert.assertNotNull(mapFragment,"mapFragment is null");
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geoFire = new GeoFire(ref);

        // Dialog with the search filters
        fragmentManager = getSupportFragmentManager();
        searchFiltersDialogFragment = new SearchFiltersDialogFragment(this);
        searchFiltersDialogFragment.setInterface(this);
        currentSearchFilters = SearchFiltersDialogFragment.getDefaultFilters(this);

        helpDialogFragment = new HelpDialogFragment();

        // Toast.makeText(getApplicationContext(), getString(R.string.search_events_usage_info),
        //        Toast.LENGTH_LONG).show();

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
            numLightSensorChanges++;
            boolean success = true;

            // Check value every 10 updates to do not overload activity
            if (numLightSensorChanges >= 10) {
                numLightSensorChanges = 0;

                if (mMap != null) {
                    if (amountOfLight > 50){
                        // If style is not already light, set light style
                        if (isMapStyleDark) {
                            success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                                    R.raw.style_json_default));
                            isMapStyleDark = false;
                        }

                    } else {
                        // If style is not already dark, set dark style
                        if (! isMapStyleDark) {
                            success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                                    R.raw.style_json));
                            isMapStyleDark = true;
                        }

                    }

                } else {
                    // Map is null, so style cannot be set
                    success = false;
                }

                if (! success) {
                    Log.e(TAG, "Map style could not be set.");
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

        if (! currentSearchFilters.equals(searchFilters)) {
            if (! searchFilters.getSearchText().isEmpty()) {
                hideMarkersFromMap();
                currentSearchFilters = searchFilters;
                new getEventsByTitle().execute(searchFilters);
            } else {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> finishEvent - " + searchFilters.getShowPastEvents());
                // Filter events by date
                currentSearchFilters = searchFilters;

                for (EventMarker eventMarker : eventMarkersSet) {
                    applyFiltersToEventMarker(eventMarker, searchFilters);
                }
            }
        }

    }


    @Override
    public String getSearchText() {
        return searchEditText.getText().toString();
    }


    private void applyFiltersToEventMarker(EventMarker eventMarker, SearchFilters searchFilters) {

        boolean filtered = SearchFilters.applyFiltersToEvent(eventMarker.event, searchFilters);

        if (filtered) {
            eventMarker.remove();
        } else {
            eventMarker.addToMap(mMap);
        }

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

        if (ContextCompat.checkSelfPermission(SearchEventsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(SearchEventsActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setZoomControlsEnabled(true);

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

                    String searchText = getSearchText();

                    currentSearchFilters.setSearchText(searchText);

                    // Remove all markers from map
                    hideMarkersFromMap();  // mMap.clear();

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

                EventMarker eventMarker = (EventMarker) args.getTag();
                Assert.assertNotNull(eventMarker, "EventMarker is null");
                Event event = eventMarker.event;
                DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());

                TextView snippet = v.findViewById(R.id.snippet);
                Assert.assertNotNull(event, TAG + ": Event is null");
                snippet.setText(df.format(event.getEventDate()));

                if (eventMarker.isOverlappingWithMarkers(eventMarkersSet)) {
                    ImageView imageView = v.findViewById(R.id.overlapping);
                    imageView.setVisibility(View.VISIBLE);
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        EventMarker eventMarker = (EventMarker) marker.getTag();
                        Assert.assertNotNull(eventMarker, "EventMarker is null");
                        Event event = eventMarker.event;
                        Log.d(TAG + " >>> ", event.getId());

                        Date currentDate = new Date();

                        Intent intent;
                        if (currentDate.after(event.getEventDate())) {
                            intent = new Intent(SearchEventsActivity.this, FinishedEventInfoActivity.class);
                        } else {
                            intent = new Intent(SearchEventsActivity.this, EventInfoActivity.class);
                        }

                        intent.putExtra("EVENT", event.getId());
                        intent.putExtra("RECOMMENDED", false);
                        startActivity(intent);

                    }
                });
                return v;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (! searchEditText.getText().toString().isEmpty()) {
                    return;
                }

                Log.d(TAG,"Listener 1");
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

                Log.d(TAG, radius+"");
                Log.d(TAG, latitude+ "-"+longitude);

                getNearbyEvents(new GeoLocation(latitude, longitude), radius);

                Log.d(TAG, eventMarkersSet.toString());

            }
        });


        FloatingActionButton searchFiltersFAB = findViewById(R.id.search_filters);
        searchFiltersFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchFiltersDialogFragment.show(fragmentManager, "dialog");
            }
        });

        FloatingActionButton setMyLocation = findViewById(R.id.center_on_my_location);
        setMyLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getMyLastLocation();
            }
        });

        FloatingActionButton help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                helpDialogFragment.show(fragmentManager, "dialog");
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
                if (! newEventMarkersSet.contains(eventMarker) && ! eventMarkersSet.contains(eventMarker)) {
                    newEventMarkersSet.add(eventMarker);
                    Log.d(TAG, String.format("Key %s entered the search area at [%f,%f]", key,
                            location.latitude, location.longitude));
                }
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(TAG, String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, String.format("Key %s moved within the search area to [%f,%f]", key,
                        location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "All initial data has been loaded and events have been fired!");
                showMarkersOnMap();

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(TAG, "There was an error with this query: " + error);
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

    private void hideMarkersFromMap() {
        for (EventMarker eventMarker : eventMarkersSet) {
            // Log.e(TAG, eventMarker.event.getName() +" "+eventMarker.marker);
            eventMarker.remove();
        }
    }

    //================================================================================
    // Permissions
    //================================================================================

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
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
            Log.d(">>>>>>>>>>>>>>>", eventMarker.event.getName());

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

                if (eventMarkersSet.contains(eventMarker)) {
                    eventMarker = eventMarker.getIfPresent(eventMarker, eventMarkersSet);
                } else {
                    eventMarkersSet.add(eventMarker);
                }
                eventMarker.addToMap(mMap);
                builder.include(eventMarker.marker.getPosition());
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


    public static class HelpDialogFragment extends DialogFragment {

        /** The system calls this to get the DialogFragment's layout, regardless
         of whether it's being displayed as a dialog or an embedded fragment. */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout to use as dialog or embedded fragment
            return inflater.inflate(R.layout.dialog_fragment_search_help, container, false);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Button applyFiltersButton = view.findViewById(R.id.ok);
            applyFiltersButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }

    }


    private class EventMarker {
        String id;
        Event event;
        Marker marker;
        LatLng originalPosition = null;

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
                double latitude = (double) Objects.requireNonNull(address.get("latitude"));
                double longitude = (double) (Double) Objects.requireNonNull(address.get("longitude"));
                this.originalPosition = new LatLng(latitude, longitude);

                LatLng latLng = this.originalPosition;

                Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(this.event.getName()));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                marker.setTag(this);
                this.marker = marker;
            } else {
                this.marker.setVisible(true);
            }
        }


        private boolean isOverlappingWithMarkers(HashSet<EventMarker> eventMarkersSet) {
            float[] metersDistance = new float[1];
            for (EventMarker eventMarker : eventMarkersSet) {
                if (! this.equals(eventMarker) && eventMarker.originalPosition != null) {
                    if (eventMarker.marker.isVisible()) {
                        Location.distanceBetween(
                                this.originalPosition.latitude,
                                this.originalPosition.longitude,
                                eventMarker.originalPosition.latitude,
                                eventMarker.originalPosition.longitude,
                                metersDistance);
                        if (metersDistance[0] < 20.0f) {
                            return true;
                        }
                    }
//                if (this.originalPosition.equals(eventMarker.originalPosition))
//                    return true;
                }
            }
            return false;
        }

        public EventMarker getIfPresent(EventMarker sourceEventMarker, HashSet<EventMarker> set) {
            if (set.contains(sourceEventMarker)) {
                for (EventMarker eventMarker : set) {
                    if (eventMarker.equals(sourceEventMarker))
                        return eventMarker;
                }
            }

            return null;
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
