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

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserSettingsActivity extends UserMenuDrawerActivity {
    private static final String TAG = "Ajustes Usuario";
    ArrayList<String> selectedGenres = new ArrayList<String>();
    ArrayList<String> selectedLocations = new ArrayList<String>();
    PersistentUserInfo persistentUserInfo;
    UserService userService = new UserService();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        setMenuItemChecked(R.id.nav_settings);

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Ajustes");

        persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        Bundle b = getIntent().getExtras();
        if (b != null) {
            Gson gson = new Gson();
            persistentUserInfo = gson.fromJson(getIntent().getStringExtra("INFO"), PersistentUserInfo.class);
        }

        selectedGenres = persistentUserInfo.getSubscribedGenres();
        selectedLocations = persistentUserInfo.getSubscribedLocations();

        final String name = persistentUserInfo.getName();
        final TextView nameView = findViewById(R.id.userFullName);
        nameView.setText(name);

        final String username = persistentUserInfo.getUsername();
        final TextView usernameView = findViewById(R.id.userNickname);
        usernameView.setText(username);

        final String email = persistentUserInfo.getEmail();
        final TextView emailView = findViewById(R.id.userEmail);
        emailView.setText(email);

        final String biography = persistentUserInfo.getBiography();
        final TextView biographyView = findViewById(R.id.userBiography);
        biographyView.setText(biography);

        final String birthdate = persistentUserInfo.getBirthdate();
        final TextView birthdateView = findViewById(R.id.userBirthdate);
        birthdateView.setText(birthdate);



        Button genres = findViewById(R.id.genres);
        genres.setClickable(true);
        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Géneros");
                persistentUserInfo.setName(nameView.getText().toString());
                persistentUserInfo.setUsername(usernameView.getText().toString());
                persistentUserInfo.setEmail(emailView.getText().toString());
                persistentUserInfo.setBiography(biographyView.getText().toString());
                persistentUserInfo.setBirthdate(birthdateView.getText().toString());
                Intent intent = new Intent(UserSettingsActivity.this, GenresSettingsActivity.class);
                String infoJson = (new Gson()).toJson(persistentUserInfo);
                intent.putExtra("INFO", infoJson);
                startActivity(intent);
            }
        });

        Button locations = findViewById(R.id.locations);
        locations.setClickable(true);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Localidades");
                persistentUserInfo.setName(nameView.getText().toString());
                persistentUserInfo.setUsername(usernameView.getText().toString());
                persistentUserInfo.setEmail(emailView.getText().toString());
                persistentUserInfo.setBiography(biographyView.getText().toString());
                persistentUserInfo.setBirthdate(birthdateView.getText().toString());
                Intent intent = new Intent(UserSettingsActivity.this, LocationsSettingsActivity.class);
                String infoJson = (new Gson()).toJson(persistentUserInfo);
                intent.putExtra("INFO", infoJson);
                startActivity(intent);
            }
        });


        FloatingActionButton savebutton = (FloatingActionButton) findViewById(R.id.save);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.w(TAG, "Ha clickeado en guardar ajustes");
            persistentUserInfo.updateInfo(getApplicationContext(), nameView.getText().toString(), usernameView.getText().toString(), emailView.getText().toString(), biographyView.getText().toString(), birthdateView.getText().toString(), selectedLocations, selectedGenres);
            user.setName(nameView.getText().toString());
            user.setUsername(usernameView.getText().toString());
            user.setEmail(emailView.getText().toString());
            user.setBiography(biographyView.getText().toString());
            user.setBirthdate(birthdateView.getText().toString());
            user.setSubscribedGenres(selectedGenres);
            user.setSubscribedLocations(selectedLocations);
            UpdateInfo updateInfo = new UpdateInfo();
            updateInfo.execute();
            Toast.makeText(UserSettingsActivity.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserSettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }
        });

        Switch notification = findViewById(R.id.switch1);
        notification.setClickable(true);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Log.w(TAG, "Ha activado las notificaciones");
                Toast.makeText(UserSettingsActivity.this, getString(R.string.noti),  Toast.LENGTH_SHORT).show();
            }else{
                Log.w(TAG, "Ha desactivado las notificaciones");
                Toast.makeText(UserSettingsActivity.this, getString(R.string.desnoti),  Toast.LENGTH_SHORT).show();
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
            user.setId(currentUser.getUid());
            userService.updateUser(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}
