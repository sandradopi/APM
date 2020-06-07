package com.apmuei.findmyrhythm.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.User;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserSettingsActivity extends UserMenuDrawerActivity {
    private static final String TAG = "Ajustes Usuario";

    private static final int GENRES_RC = 1;
    private static final int LOCATIONS_RC = 2;

    ArrayList<String> selectedGenres = new ArrayList<>();
    ArrayList<String> selectedLocations = new ArrayList<>();
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

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
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
                //Log.w(TAG, "Ha clickeado en Editar Géneros");
                Intent intent = new Intent(UserSettingsActivity.this, GenresSettingsActivity.class);
                intent.putStringArrayListExtra("GENRES", selectedGenres);
                startActivityForResult(intent, GENRES_RC);
            }
        });

        Button locations = findViewById(R.id.locations);
        locations.setClickable(true);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.w(TAG, "Ha clickeado en Editar Localidades");

                Intent intent = new Intent(UserSettingsActivity.this, LocationsSettingsActivity.class);
                intent.putStringArrayListExtra("LOCATIONS", selectedLocations);
                startActivityForResult(intent, LOCATIONS_RC);
            }
        });


        FloatingActionButton savebutton = (FloatingActionButton) findViewById(R.id.save);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Log.w(TAG, "Ha clickeado en guardar ajustes");

            persistentUserInfo.updateInfo(getApplicationContext(), nameView.getText().toString(),
                    usernameView.getText().toString(), emailView.getText().toString(),
                    biographyView.getText().toString(), birthdateView.getText().toString(),
                    selectedLocations, selectedGenres);

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
            editor.putString("email", emailView.getText().toString());
            editor.putString("nickname", usernameView.getText().toString());
            editor.apply();

            Toast.makeText(UserSettingsActivity.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserSettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        setMenuItemChecked(R.id.nav_settings);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GENRES_RC) {

            if (resultCode == RESULT_OK) {
                selectedGenres = data.getStringArrayListExtra("GENRES");
            }

        } else if (requestCode == LOCATIONS_RC) {
            if (resultCode == RESULT_OK) {
                selectedLocations = data.getStringArrayListExtra("LOCATIONS");
            }
        }
    }


    //================================================================================
    // AsyncTasks
    //================================================================================

    private class UpdateInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Assert.assertNotNull(currentUser, "No FirebaseUser found");
            user.setId(currentUser.getUid());
            userService.updateUser(user);
            return null;
        }

    }


}
