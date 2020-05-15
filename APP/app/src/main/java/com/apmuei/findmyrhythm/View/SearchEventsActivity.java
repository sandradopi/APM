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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;


public class SearchEventsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SensorEventListener {

    private final static String TAG = "SearchEventsA";
    private SensorManager sensorManager;
    private Sensor light;
    private GoogleMap mMap;
    private EditText searchText;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 7346;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GeoFire geoFire;

    FragmentManager fragmentManager;
    SearchFiltersDialogFragment searchFiltersDialogFragment;

    private HashSet<EventMarker> eventMarkersSet = new HashSet<>();
    private HashSet<EventMarker> removedEventMarkersSet = new HashSet<>();
    private HashSet<EventMarker> newEventMarkersSet = new HashSet<>();

    // Filter values:
    private boolean showPastEvents = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        searchText = findViewById(R.id.input_search);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (light == null) {
            System.out.println("No sensor light");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geoFire = new GeoFire(ref);

        // Dialog with the search filters
        fragmentManager = getSupportFragmentManager();
        searchFiltersDialogFragment = new SearchFiltersDialogFragment();


        Toast.makeText(getApplicationContext(), getString(R.string.search_events_usage_info), Toast.LENGTH_LONG).show();

        FloatingActionButton searchFiltersFAB = findViewById(R.id.search_filters);
        searchFiltersFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSearchFiltersDialog();
            }
        });

    }


    public void showSearchFiltersDialog() {

        searchFiltersDialogFragment.show(fragmentManager, "dialog");

        // https://stackoverflow.com/questions/33866579/getting-view-of-dialogfragment
        fragmentManager.executePendingTransactions();

        final View view = searchFiltersDialogFragment.getView();
        assert view != null;
        Button applyFilters = view.findViewById(R.id.apply);
        applyFilters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Filter events by date
                CheckBox showPast = view.findViewById(R.id.checkBox_show_past_events);
                if (! showPast.isChecked()) {
                    removePastEvents();
                    showPastEvents = false;
                } else {
                    showRemovedPastEvents();
                    showPastEvents = true;
                }


                Toast.makeText(getApplicationContext(), "Filtros aplicados", Toast.LENGTH_SHORT).show();
                searchFiltersDialogFragment.dismiss();
            }
        });

        Button cancelFilters = view.findViewById(R.id.cancel);
        cancelFilters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox showPast = view.findViewById(R.id.checkBox_show_past_events);
                showPast.setChecked(showPastEvents);
                searchFiltersDialogFragment.dismiss();
            }
        });

    }

    private void showRemovedPastEvents() {
        for (Iterator<EventMarker> i = removedEventMarkersSet.iterator(); i.hasNext();) {
            EventMarker eventMarker = i.next();
            Date eventDate = eventMarker.event.getEventDate();
            Date currentDate = new Date();
            if (eventDate.before(currentDate)) {
                i.remove();
                eventMarkersSet.add(addMarkerToMap(eventMarker));
            }
        }
    }


    private void removePastEvents() {

        for (Iterator<EventMarker> i = eventMarkersSet.iterator(); i.hasNext();) {
            EventMarker eventMarker = i.next();
            Date eventDate = eventMarker.event.getEventDate();
            Date currentDate = new Date();
            if (eventDate.before(currentDate)) {
                eventMarker.marker.remove();
                i.remove();
                removedEventMarkersSet.add(eventMarker);
            }
        }
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float amountOfLight = sensorEvent.values[0];
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){

            if((amountOfLight > 50)){

                if(mMap!=null){
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json_default));
                }

            }else{

                if(mMap!=null){
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



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

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.window_info_layout, null);

                // Getting the position from the marker
                LatLng clickMarkerLatLng = args.getPosition();

                TextView title = (TextView) v.findViewById(R.id.title);
                title.setText(args.getTitle());

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        String id = (String) marker.getTag();
                        Log.e(TAG + " >>> ", id);

                        Intent intent = new Intent(SearchEventsActivity.this, EventInfoActivity.class);
                        intent.putExtra("EVENT", id);
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
                if (! searchText.getText().toString().isEmpty()) {
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

            }
        });

    }


    private class showEventMarker extends AsyncTask<String, Void, EventMarker> {

        @Override
        protected EventMarker doInBackground(String... ids) {
            String eventId = ids[0];
            EventService eventService = new EventService();
            Event event = eventService.getEvent(eventId);
            return new EventMarker(eventId, event);
        }


        @Override
        protected void onPostExecute(final EventMarker eventMarker) {
            Log.e(">>>>>>>>>>>>>>>", eventMarker.event.getName());

            eventMarkersSet.remove(eventMarker);

            if (showPastEvents) {
                // All the information is added to the EventMarker by updating it
                EventMarker updatedMarker = addMarkerToMap(eventMarker);
                eventMarkersSet.add(updatedMarker);
            } else {
                Date eventDate = eventMarker.event.getEventDate();
                Date currentDate = new Date();
                if (eventDate.before(currentDate)) {
                    removedEventMarkersSet.add(eventMarker);
                } else {
                    EventMarker updatedMarker = addMarkerToMap(eventMarker);
                    eventMarkersSet.add(updatedMarker);
                }

            }
        }

    }


    private EventMarker addMarkerToMap(EventMarker eventMarker) {
        HashMap address = eventMarker.event.getCompleteAddress();
        LatLng latLng = new LatLng((Double) Objects.requireNonNull(address.get("latitude")),
                (Double) Objects.requireNonNull(address.get("longitude")));
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(eventMarker.event.getName()));
        marker.setTag(eventMarker.id);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        eventMarker.marker = marker;
        return eventMarker;
    }


    private void showMarkersOnMap() {

        SearchEventsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Your code to run in GUI thread here
                for (EventMarker eventMarker : newEventMarkersSet) {
                    new showEventMarker().execute(eventMarker.id);
                }
                newEventMarkersSet.clear();
            }
        });
    }


    public void getNearbyEvents(GeoLocation geoLocation, Double radius) {

        /* https://stackoverflow.com/questions/50631432/android-query-nearby-locations-from-firebase
         * https://stackoverflow.com/questions/43357990/query-for-nearby-locations
         */

        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation,radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                EventMarker eventMarker = new EventMarker(key);
                if (! eventMarkersSet.contains(eventMarker)) {
                    eventMarkersSet.add(eventMarker);
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
                marker.setTag(event.getId());
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
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
