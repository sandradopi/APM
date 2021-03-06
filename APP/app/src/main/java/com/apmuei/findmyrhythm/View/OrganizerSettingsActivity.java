package com.apmuei.findmyrhythm.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Organizer;
import com.apmuei.findmyrhythm.Model.OrganizerService;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.Utils.GeoUtils;
import com.apmuei.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OrganizerSettingsActivity extends OrganizerMenuDrawerActivity {
    private static final String TAG = "Ajustes Organizador";
    PersistentOrganizerInfo persistentOrgInfo;
    Organizer organizer = new Organizer();
    OrganizerService orgService = new OrganizerService();
    private List<Address> organizerAddressesList;
    private TextView selectedAddressView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_settings);

        setMenuItemChecked(R.id.nav_settings);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.organizer_settings);

        persistentOrgInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        organizerAddressesList = new ArrayList<>();

        final String rating = persistentOrgInfo.getRating();

        final String name = persistentOrgInfo.getName();
        final TextView nameView = findViewById(R.id.userFullName);
        nameView.setText(name);

        final String username = persistentOrgInfo.getUsername();
        final TextView usernameView = findViewById(R.id.userNickname);
        usernameView.setText(username);

        final String email = persistentOrgInfo.getEmail();
        final TextView emailView = findViewById(R.id.userEmail);
        emailView.setText(email);

        final String biography = persistentOrgInfo.getBiography();
        final TextView biographyView = findViewById(R.id.userBiography);
        biographyView.setText(biography);

        final String location = persistentOrgInfo.getLocation();
        selectedAddressView = findViewById(R.id.selected_address);
        selectedAddressView.setText(location);

        final Button exploreMapButton = findViewById(R.id.exploreMap);
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


        FloatingActionButton savebutton = findViewById(R.id.save);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Log.w(TAG, "Ha clickeado en guardar ajustes");

                persistentOrgInfo.updateInfo(getApplicationContext(), nameView.getText().toString(), usernameView.getText().toString(), emailView.getText().toString(), biographyView.getText().toString(), selectedAddressView.getText().toString());

                organizer.setName(nameView.getText().toString());
                organizer.setUsername(usernameView.getText().toString());
                organizer.setEmail(emailView.getText().toString());
                organizer.setBiography(biographyView.getText().toString());
                organizer.setLocation(selectedAddressView.getText().toString());
                organizer.setRating(rating);

                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", nameView.getText().toString());
                editor.putString("email", emailView.getText().toString());
                editor.putString("nickname", usernameView.getText().toString());
                editor.commit();

                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.execute();
                Toast.makeText(OrganizerSettingsActivity.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrganizerSettingsActivity.this, OrganizerProfileActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        setMenuItemChecked(R.id.nav_settings);
    }


    private class UpdateInfo extends AsyncTask<Void, Void, Void> {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            organizer.setId(currentUser.getUid());
            orgService.updateOrganizer(organizer);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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

}
