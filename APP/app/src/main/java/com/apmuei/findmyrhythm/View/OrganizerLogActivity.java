package com.apmuei.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.Model.OrganizerService;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.Utils.GeoUtils;
import com.apmuei.findmyrhythm.Model.Utils.GenericUtils;
import com.apmuei.findmyrhythm.Model.Utils.PermissionUtils;
import com.apmuei.findmyrhythm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrganizerLogActivity extends AppCompatActivity {
    private static final String TAG = "OrganizerLogActivity";

    private EditText name, nickname, email, biography;
    private Button exploreMapButton;
    private TextView selectedAddressView;
    private List<Address> organizerAddressesList;
    private FloatingActionButton next;
    private FirebaseUser currentUser;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_CODE = 7346;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean canAccessLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_log);

        ActionBar actionBar = getSupportActionBar();
        Assert.assertNotNull(actionBar,"ActionBar not found");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar_empty);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        name = findViewById(R.id.orgName);
        nickname = findViewById(R.id.orgNickName);
        email = findViewById(R.id.orgEmail);
        biography = findViewById(R.id.orgBiography);
        selectedAddressView = findViewById(R.id.selected_address);
        organizerAddressesList = new ArrayList<>();
        exploreMapButton = findViewById(R.id.exploreMap);
        exploreMapButton = findViewById(R.id.exploreMap);

        next = findViewById(R.id.next);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());

        final Locale locale = Locale.getDefault(); // new Locale("es", "ES");
        geocoder = new Geocoder(this, locale);

        GeoUtils.checkLocationEnabled(OrganizerLogActivity.this);

        canAccessLocation = GeoUtils.canAccessLocation(OrganizerLogActivity.this);

        if (ContextCompat.checkSelfPermission(OrganizerLogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(OrganizerLogActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        exploreMapButton.setText(R.string.map_exlore);
        exploreMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(), SelectAddressOnMapActivity.class);

                    // Pass organizer's address in bundle if it exists
                    if (! organizerAddressesList.isEmpty()) {
                        Address organizerAddress = organizerAddressesList.get(0);
                        intent.putExtra("organizerAddress", organizerAddress);
                    }
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if every field are covered. If not ask for the user to cover them.
                if (GenericUtils.isEmpty(name) || GenericUtils.isEmpty(nickname) || GenericUtils.isEmpty(email) || GenericUtils.isEmpty(biography) || (organizerAddressesList.isEmpty())) {
                    Toast.makeText(OrganizerLogActivity.this, R.string.fields_complete, Toast.LENGTH_LONG).show();
                    return;
                }

                // Validate email
                if (!GenericUtils.isValidEmail(email)) {
                    email.setError(getString(R.string.email_format));
                    return;
                }

                //TODO: Introduce into database by getting the value of every field. Check Android Service.
                createOrganizer();

                //Log.w(TAG, "Creación de la cuenta del organizador");
                Toast.makeText(OrganizerLogActivity.this, getString(R.string.notiCreation), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OrganizerLogActivity.this, OrganizerProfileActivity.class);
                // Flags for start a new activity and clear all stack
                // This prevents organizers from going back once they have registered
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            new GeocoderAsyncTask(OrganizerLogActivity.this,
                                    location.getLatitude(), location.getLongitude()).execute();
                        }
                    }
                });
    }


    public class GeocoderAsyncTask extends AsyncTask<String, Void, Address> {
        Double latitude = null;
        Double longitude = null;
        Activity activity;

        GeocoderAsyncTask(Activity activity, double latitude, double longitude) {
            this.activity = activity;
            this. latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Address doInBackground(String... params) {
            List<Address> addresses;
            Address result = null;

            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                result = addresses.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Address result) {
            organizerAddressesList.add(result);
            selectedAddressView.setText(GeoUtils.getAddressString(result));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Address completeAddress = data.getParcelableExtra("pickedAddress");
                Toast.makeText(getApplicationContext(),completeAddress.getSubAdminArea(),Toast.LENGTH_SHORT).show();
                // Update the address to the new address selected from the map
                organizerAddressesList = new ArrayList<>();
                organizerAddressesList.add(completeAddress);
                selectedAddressView.setText(GeoUtils.getAddressString(completeAddress));
            }
        }
    }


    public void createOrganizer() {

        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("fb_name", currentUser.getDisplayName());
        editor.putString("fb_email", currentUser.getEmail());
        editor.putString("fb_id", currentUser.getUid());

        editor.putString("name", name.getText().toString());
        editor.putString("email", email.getText().toString());
        editor.putString("nickname", nickname.getText().toString());
        editor.putString("location", selectedAddressView.getText().toString());
        editor.putString("account_type", "organizer");

        editor.apply();

        PersistentOrganizerInfo persistentOrganizerInfo = new PersistentOrganizerInfo(currentUser.getUid(), name.getText().toString(),
                nickname.getText().toString(), email.getText().toString(), biography.getText().toString(),
                null, selectedAddressView.getText().toString(), new ArrayList<Event>());

        PersistentOrganizerInfo.setPersistentOrganizerInfo(getApplicationContext(), persistentOrganizerInfo);

        //TODO: Introduce into database by getting the value of every field. Check Android Service.
        new CreateOrganizerTask().execute();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    private class CreateOrganizerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            OrganizerService orgService = new OrganizerService();
            orgService.createOrganizer(currentUser.getUid(), name.getText().toString(), nickname.getText().toString(),
                    email.getText().toString(), biography.getText().toString(), null, selectedAddressView.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            IOFiles.downloadProfilePicture(currentUser, getApplicationContext());
            finish();
        }
    }


}
