package com.example.findmyrhythm.View;

import android.content.Intent;
import android.content.SharedPreferences;
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

        final TextView nameView = findViewById(R.id.userFullName);
        final TextView usernameView = findViewById(R.id.userNickname);
        final TextView emailView = findViewById(R.id.userEmail);
        final TextView biographyView = findViewById(R.id.userBiography);
        final TextView birthdateView = findViewById(R.id.userBirthdate);


        setMenuItemChecked(R.id.nav_settings);

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Ajustes");

        persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        selectedGenres = persistentUserInfo.getSubscribedGenres();
        selectedLocations = persistentUserInfo.getSubscribedLocations();

        nameView.setText(persistentUserInfo.getName());
        usernameView.setText(persistentUserInfo.getUsername());
        emailView.setText(persistentUserInfo.getEmail());
        biographyView.setText(persistentUserInfo.getBiography());
        birthdateView.setText(persistentUserInfo.getBirthdate());




        Button genres = findViewById(R.id.genres);
        genres.setClickable(true);
        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Géneros");
                Intent intent = new Intent(UserSettingsActivity.this, GenresSettingsActivity.class);
                intent.putStringArrayListExtra("GENRES", selectedGenres);
                startActivityForResult(intent, 1);
            }
        });

        Button locations = findViewById(R.id.locations);
        locations.setClickable(true);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Localidades");

                Intent intent = new Intent(UserSettingsActivity.this, LocationsSettingsActivity.class);
                intent.putStringArrayListExtra("LOCATIONS", selectedLocations);
                startActivityForResult(intent, 2);
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

            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", nameView.getText().toString());
            editor.commit();

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                selectedGenres = data.getStringArrayListExtra("GENRES");
            }

        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                selectedLocations = data.getStringArrayListExtra("LOCATIONS");
            }
        }
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
