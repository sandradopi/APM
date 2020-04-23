package com.example.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.Model.OrganizerService;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.Locale;

public class OrganizerLogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Creación Organizador";
    private EditText name, nickname, email, biography;
    private Button location, exploreMapButton;
    private Address completeAddress;
    private FloatingActionButton next;
    private FirebaseUser currentUser;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;
    private GeoUtils geoUtils;
    private static final int LOCATION_PERMISSION_CODE = 7346;
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private Boolean locationPermissionGranted = false;
    private Boolean useMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_log);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.orgName);
        nickname = (EditText) findViewById(R.id.orgNickName);
        email = (EditText) findViewById(R.id.orgEmail);
        biography = (EditText) findViewById(R.id.orgBiography);
        location = (Button) findViewById(R.id.currentLocation);
        exploreMapButton = (Button) findViewById(R.id.exploreMap);

        next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());

        useMyLocation = true;
        exploreMapButton.setText("Explorar en el mapa");
        exploreMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //startActivityForResult(new Intent(getApplicationContext(), SearchOrganizerLocation.class).putExtra("lastLocation", lastLocation), 1);
                    startActivityForResult(new Intent(getApplicationContext(), SearchEventsActivity.class), 1);
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        });
        // Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else if (useMyLocation) {
            getLastLocation();
            geoUtils = new GeoUtils(this, Locale.getDefault());
            completeAddress = geoUtils.getAddressFromLocation(lastLocation);
            location.setText(completeAddress.getLocality());
        }
    }

    // Methods for requesting permissions
    // Return the current state of the permissions needed
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionsRequest() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
    }

    private void requestPermissions() {

        String[] permissions = {ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startLocationPermissionsRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_CODE) {

            // If grantResults lenght is > 0 is that something was granted
            if (grantResults.length > 0) {
                for (int item : grantResults)
                    if (item == PackageManager.PERMISSION_DENIED) {
                        return;
                    }

                if (useMyLocation) {
                    getLastLocation();
                    GeoUtils geoUtils = new GeoUtils(this, Locale.getDefault());
                    completeAddress = geoUtils.getAddressFromLocation(lastLocation);
                    location.setText(completeAddress.getLocality());
                }
            }
        }
    }

    // Method for obtaining the last know location of the device
    private void getLastLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria crta = new Criteria();
        String provider = lm.getBestProvider(crta, true);
        try {
            lastLocation = lm.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            Log.w(TAG, e.toString());
        }

        /* fusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.getException());
                }
            }
        }); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                useMyLocation = false;
                Address completeAddress = data.getParcelableExtra("pickedAddress");
//                geoUtils = new GeoUtils(this, Locale.getDefault());
//                completeAddress = geoUtils.getAddressFromLocation(newLocation);
                location.setText(completeAddress.getLocality());
            }
        }
    }

    @Override
    public void onClick(View view) {

        /*TODO: Check if every field are covered.
         * If not ask for the user to cover them.
         * Insert the new user in the DataBase as ORGANIZATION with all the important information
         * Create the intent to go to the next Activity
         */

        /*TODO: PROBABLY IT WOULD BE WISE TO GET THE LOCATION NOT BY GETTING THE TEXT OF THE EDITTEXT
         * BUT BY INTRODUCING THE CITY, PROVINCE, STREET ETC SEPARATED BY USING THE GOOGLE MAP APP.
         * IT WOULD MAKE EASIER THE CHECK OF EVENTS TO USERS AFTERWARDS.
         */

//        if (isEmpty(name) || isEmpty(nickname) || isEmpty(email) || isEmpty(biography) || isEmpty(location)) {
//            Toast.makeText(this, "Porfavor rellene todos los campos", Toast.LENGTH_LONG).show();
//            return;
//        }

        //TODO: Introduce into database by getting the value of every field. Check Android Service.
        createOrganizer();

        //TODO: Intent to new Activity
        Log.w(TAG, "Creación de la cuenta del organizador");
        Toast.makeText(OrganizerLogActivity.this, getString(R.string.notiCreation), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, OrganizerProfileActivity.class);
        startActivity(intent);
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
        editor.putString("location", location.getText().toString());
        editor.putString("account_type", "organizer");

        editor.commit(); // or apply

        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Organizer organizer = new Organizer(name.getText().toString(), nickname.getText().toString(), email.getText().toString(), location.getText().toString(), biography.getText().toString());

        mDatabase.child("organizers").child(currentUser.getUid()).setValue(organizer);*/

        PersistentOrganizerInfo persistentOrganizerInfo = new PersistentOrganizerInfo(currentUser.getUid(), name.getText().toString(),
                nickname.getText().toString(), email.getText().toString(), biography.getText().toString(),
                null, location.getText().toString(), new ArrayList<Event>());

        PersistentOrganizerInfo.setPersistentOrganizerInfo(getApplicationContext(), persistentOrganizerInfo);

        //TODO: Introduce into database by getting the value of every field. Check Android Service.
        new CreateOrganizerTask().execute();


    }

    private boolean isEmpty(EditText text) {

        return text.getText().toString().equals("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private class CreateOrganizerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            OrganizerService orgService = new OrganizerService();
            orgService.createOrganizer(currentUser.getUid(), name.getText().toString(), nickname.getText().toString(),
                    email.getText().toString(), biography.getText().toString(), null, location.getText().toString());
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
