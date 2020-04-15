package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.Organizer;
import com.example.findmyrhythm.Model.OrganizerService;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrganizerSettingsActivity extends OrganizerMenuDrawerActivity {
    private static final String TAG = "Ajustes Organizador";
    PersistentOrganizerInfo persistentOrgInfo;
    Organizer organizer = new Organizer();
    OrganizerService orgService = new OrganizerService();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_settings);

        setMenuItemChecked(R.id.nav_settings);

        persistentOrgInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());

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
        final TextView locationView = findViewById(R.id.userLocation);
        locationView.setText(location);

        FloatingActionButton savebutton = findViewById(R.id.save);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en guardar ajustes");
                persistentOrgInfo.updateInfo(getApplicationContext(), nameView.getText().toString(), usernameView.getText().toString(), emailView.getText().toString(), biographyView.getText().toString(), locationView.getText().toString());
                organizer.setName(nameView.getText().toString());
                organizer.setUsername(usernameView.getText().toString());
                organizer.setEmail(emailView.getText().toString());
                organizer.setBiography(biographyView.getText().toString());
                organizer.setLocation(locationView.getText().toString());
                organizer.setRating(rating);
                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.execute();
                Toast.makeText(OrganizerSettingsActivity.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrganizerSettingsActivity.this, OrganizerProfileActivity.class);
                startActivity(intent);
            }
        });

        Switch notification = findViewById(R.id.switch1);
        notification.setClickable(true);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.w(TAG, "Ha activado las notificaciones");
                    Toast.makeText(OrganizerSettingsActivity.this, getString(R.string.noti),  Toast.LENGTH_SHORT).show();
                }else{
                    Log.w(TAG, "Ha desactivado las notificaciones");
                    Toast.makeText(OrganizerSettingsActivity.this, getString(R.string.desnoti),  Toast.LENGTH_SHORT).show();
                }
            }
        });


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

}
