package com.apmuei.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.Utils.GeoUtils;
import com.apmuei.findmyrhythm.Model.Utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SelectAddressOnMapActivity extends FragmentActivity implements OnMapReadyCallback {
    final String TAG = "SearchEventActivity";
    private GoogleMap mMap;
    private StringBuilder mResult;
    private EditText searchText;
    private ListView mSearchResult;
    private Address selectedAddress;
    private Geocoder geocoder;
    private Marker marker;
    private FloatingActionButton myLocation;
    private FloatingActionButton submit_location;
    private String provider;
    private LatLng myLocationLatLng;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 7346;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address_on_map);

        mSearchResult = findViewById(R.id.searchResult);
        searchText = findViewById(R.id.input_search);
        myLocation = findViewById(R.id.my_location);
        submit_location = findViewById(R.id.submit_location);

        Locale locale = Locale.getDefault(); // new Locale("es", "ES");
        geocoder = new Geocoder(this, locale);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Assert.assertNotNull(mapFragment, TAG+": SupportMapFragment not found");
        mapFragment.getMapAsync(this);

        // Get the organizer's location
        selectedAddress = getIntent().getParcelableExtra("organizerAddress");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Check if there is a selected location and put the marker there.
        if (selectedAddress != null) {
             new GeocoderAsyncTask(SelectAddressOnMapActivity.this,
                     selectedAddress.getLatitude(), selectedAddress.getLongitude(), "move_zoom").execute();
        } else {
            setMyLocationPermissions();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                new GeocoderAsyncTask(SelectAddressOnMapActivity.this, latLng.latitude,
                        latLng.longitude, "animate").execute();
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    new GeocoderAsyncTask(SelectAddressOnMapActivity.this,
                            searchText.getText().toString(), "move_zoom").execute();
                    return true;
                }
                return false;
            }
        });

        // Send control back to organizer log activity with the new location value
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMyLocationPermissions();
            }
        });


        // Send control back to organizer log activity with the new location value
        submit_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickedLocation = new Intent();
                pickedLocation.putExtra("pickedAddress", selectedAddress);
                setResult(RESULT_OK, pickedLocation);
                finish();
            }
        });

    }


    private void setMyLocationPermissions() {
        GeoUtils.checkLocationEnabled(SelectAddressOnMapActivity.this);

        if (ContextCompat.checkSelfPermission(SelectAddressOnMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(SelectAddressOnMapActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
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
                            new GeocoderAsyncTask(SelectAddressOnMapActivity.this,
                                    location.getLatitude(), location.getLongitude(), "move_zoom").execute();
                        } else {
                            marker = mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(0, 0)).visible(false));
                        }
                    }
                });
    }



    public class GeocoderAsyncTask extends AsyncTask<String, Void, Address> {
        Double latitude = null;
        Double longitude = null;
        String locationName = null;
        String zoom = "animate_15";
        Activity activity;

        GeocoderAsyncTask(Activity activity, double latitude, double longitude) {
            this.activity = activity;
            this. latitude = latitude;
            this.longitude = longitude;
        }

        GeocoderAsyncTask(Activity activity, String locationName) {
            this.activity = activity;
            this.locationName = locationName;
        }

        GeocoderAsyncTask(Activity activity, double latitude, double longitude, String zoom) {
            this(activity, latitude, longitude);
            this.zoom = zoom;
        }

        GeocoderAsyncTask(Activity activity, String locationName, String zoom) {
            this(activity, locationName);
            this.zoom = zoom;
        }

        @Override
        protected Address doInBackground(String... params) {
            List<Address> addresses = new ArrayList<>();
            Address result = null;

            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            try {
                if (locationName != null) {
                    //while (addresses.size() == 0 || addresses.get(0) == null)
                    addresses = geocoder.getFromLocationName(locationName, 1);
                } else {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                }
                Log.e("Addresses", "-->" + addresses);
                result = addresses.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Address result) {
            LatLng latLng;
            if (locationName != null) {
                latLng = new LatLng(result.getLatitude(), result.getLongitude());
            } else {
                latLng = new LatLng(latitude, longitude);
            }
            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
            marker.setTitle(GeoUtils.getAddressString(result));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            marker.showInfoWindow();
            switch (zoom) {
                case "animate":
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    break;
                case "animate_zoom":
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    break;
                case "move":
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    break;
                case "move_zoom":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    break;
                default:
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    break;
            }
            selectedAddress = result;
        }
    }

}
