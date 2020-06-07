package com.apmuei.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Utils.GeoUtils;
import com.apmuei.findmyrhythm.Model.Utils.PermissionUtils;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class SetLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    AutoCompleteTextView provinces;
    ArrayList<String> selectedProvinces = new ArrayList<String>();
    GridLayout locations;
    FloatingActionButton next;

    private static final int LOCATION_PERMISSION_CODE = 7448;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Google API
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Get the current locale
        final Locale locale = Locale.getDefault();
        // Initialize geocoder
        geocoder = new Geocoder(this, locale);

        provinces = (AutoCompleteTextView) findViewById(R.id.auto_province);

        next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(this);


        String selfLocation = getResources().getString(R.string.selfLocation);
        String[] countries = getResources().getStringArray(R.array.provinces_array);
        CustomAutoCompleteAdapater adapter = new CustomAutoCompleteAdapater(this, android.R.layout.simple_list_item_1, countries);

        provinces.setThreshold(0);
        provinces.setAdapter(adapter);
        provinces.setOnItemClickListener(this);

        locations = (GridLayout) findViewById(R.id.locations);

        GeoUtils.checkLocationEnabled(SetLocationActivity.this);

        // Check for permissions and call for last know location
        if (ContextCompat.checkSelfPermission(SetLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(SetLocationActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String value = (String) adapterView.getItemAtPosition(i);
        if (!selectedProvinces.contains(value)) {
            selectedProvinces.add(value);
            addProvince(value);
        }
        provinces.setText("");

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
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // First step: get an estimated address from API location coordinates.
                            Address estimatedAddress = GeoUtils.getAddressFromLocation(geocoder, location);
                            String locality = estimatedAddress.getLocality();
                            // Second step: get a more accurated address using a normalized search by location name.
                            Address address = GeoUtils.getAddressFromLocationName(geocoder, locality);
                            String userLocation = address.getSubAdminArea();
                            addProvince(userLocation);
                            // User's location is the first one by default and it will be shown in his/her profile
                            selectedProvinces.add(0, userLocation);
                        }
                    }
                });
    }

    public void addProvince(final String province) {

        final TextView text = new TextView(this);
        text.setText(province);
        LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(10, 0, 10, 25);
        text.setLayoutParams(params);
        text.setBackground(getResources().getDrawable(R.drawable.recover));
        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);

        locations.addView(text);

        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                locations.removeView(text);
                selectedProvinces.remove(province);
            }
        });
    }

    @Override
    public void onClick(View view) {
        //TODO: PASS TO THE GENRES CLASS THE ARRAYlIST OF LOCATIONS AND WHEN ALL THE INFORMATION IS KNOWN ADD THE USER IN THE DATABASE.
        Intent intent = new Intent(this, SetGenresActivity.class);
        intent.putExtra(getString(R.string.locationsListID), selectedProvinces);
        startActivity(intent);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
