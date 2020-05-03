package com.example.findmyrhythm.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.Model.OrganizerService;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.Model.Utils.GenericUtils;
import com.example.findmyrhythm.Model.Utils.PermissionUtils;
import com.example.findmyrhythm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
    Geocoder geocoder;
    private static final int LOCATION_PERMISSION_CODE = 7346;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_log);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        Locale spanish = new Locale("es", "ES");
        geocoder = new Geocoder(this, spanish);


        if (ContextCompat.checkSelfPermission(OrganizerLogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLastLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(OrganizerLogActivity.this, LOCATION_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        exploreMapButton.setText("Explorar en el mapa");
        exploreMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(new Intent(getApplicationContext(), SelectAddressOnMapActivity.class), 1);
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*TODO: PROBABLY IT WOULD BE WISE TO GET THE LOCATION NOT BY GETTING THE TEXT OF THE EDITTEXT
                 * BUT BY INTRODUCING THE CITY, PROVINCE, STREET ETC SEPARATED BY USING THE GOOGLE MAP APP.
                 * IT WOULD MAKE EASIER THE CHECK OF EVENTS TO USERS AFTERWARDS.
                 */

                // Check if every field are covered. If not ask for the user to cover them.
                if (GenericUtils.isEmpty(name) || GenericUtils.isEmpty(nickname) || GenericUtils.isEmpty(email) || GenericUtils.isEmpty(biography) || (organizerAddressesList.isEmpty())) {
                    Toast.makeText(OrganizerLogActivity.this, "Porfavor rellene todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validate email
                if (!GenericUtils.isValidEmail(email)) {
                    email.setError("Invalid email format");
                    return;
                }

                //TODO: Introduce into database by getting the value of every field. Check Android Service.
                createOrganizer();

                //TODO: Intent to new Activity
                Log.w(TAG, "Creación de la cuenta del organizador");
                Toast.makeText(OrganizerLogActivity.this, getString(R.string.notiCreation), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OrganizerLogActivity.this, OrganizerProfileActivity.class);
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
                            // Logic to handle location object
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                for (Address address : addresses) {
                                    organizerAddressesList.add(address);
                                    selectedAddressView.setText(GeoUtils.getAddressString(address));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Address completeAddress = data.getParcelableExtra("pickedAddress");
                Toast.makeText(getApplicationContext(),completeAddress.getSubAdminArea(),Toast.LENGTH_SHORT).show();

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
