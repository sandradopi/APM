package com.example.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.Model.Utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.findmyrhythm.R;
import com.google.android.gms.tasks.OnSuccessListener;

import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SelectAddressOnMapActivity extends FragmentActivity implements OnMapReadyCallback {
    final String TAG  = "SearchEventActivity";
    private GoogleMap mMap;
    private StringBuilder mResult;
    private EditText searchText;
    private ListView mSearchResult;
    Address selectedAddress;
    Geocoder geocoder;
    Marker marker;
    Button myLocation;
    Button submit_location;
    String provider;
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

        Locale spanish = new Locale("es", "ES");
        geocoder = new Geocoder(this, spanish);

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

        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    for (Address address : addresses) {
                        marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(GeoUtils.getAddressString(address)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        marker.showInfoWindow();
                        selectedAddress = address;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(searchText.getText().toString(), 1);
                        for (Address address : addresses) {
                            Log.e(TAG, GeoUtils.getAddressString(address));
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(GeoUtils.getAddressString(address)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            marker.showInfoWindow();
                            selectedAddress = address;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        // Send control back to organizer log activity with the new location value
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(SelectAddressOnMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getMyLastLocation();
                } else {
                    // Permission to access the location is missing. Show rationale and request permission
                    PermissionUtils.requestPermission(SelectAddressOnMapActivity.this, LOCATION_PERMISSION_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION, true);
                }
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                marker.remove();
                                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                                for (Address address : addresses) {
                                    marker.setTitle(GeoUtils.getAddressString(addresses.get(0)));
                                    marker.showInfoWindow();
                                    selectedAddress = address;
                                }
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

}