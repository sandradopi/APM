package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.FUser;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class UserLogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Creación Usuario";
    EditText name, nickname, email, biography, birthDate;
    FloatingActionButton next;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.userName);
        nickname = (EditText) findViewById(R.id.userNickname);
        email = (EditText) findViewById(R.id.userEmail);
        biography = (EditText) findViewById(R.id.userBiography);
        birthDate = (EditText) findViewById(R.id.userBirthdate);

        next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(this);

        //GET THE USER.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());

    }

    @Override
    public void onClick(View view) {

        /*TODO: Check if every field are covered.
           If not ask for the user to cover them.
           Insert the new user in the DataBase as USER With all the important information
           Create the intent to go to the next Activity
         */

//        if (isEmpty(name) || isEmpty(nickname) || isEmpty(email) || isEmpty(biography) || isEmpty(birthDate)) {
//           Toast.makeText(this, "Please cover every field shown in the screen", Toast.LENGTH_LONG).show();
//           return;
//        }

        //TODO: Introduce into database by getting the value of every field. Check Android Service.
        createUser();

        //TODO: Intent to new Activity
        Log.w(TAG, "Creación de la cuenta del usuario");
        Toast.makeText(UserLogActivity.this, getString(R.string.notiCreation),  Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }


    public void createUser() {

        DatabaseReference mDatabase;// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        String userId = currentUser.getUid();

        ArrayList<String> genres, locations;

        Bundle b = getIntent().getExtras();
        locations = b.getStringArrayList(getString(R.string.locationsListID));
        genres = b.getStringArrayList(getString(R.string.genresListID));

        User user = new User(name, nickname.getText().toString(), email, biography.getText().toString(), birthDate.getText().toString(), locations, genres);

        mDatabase.child("users2").child(userId).setValue(user);

        /* TODO: esto se está ejecutando cada vez que se inicia la actividad. Lo ideal parece que
         *   sería crear una actividad de carga con el logo o algo así que se ejecutara una única
         *   vez. Es bastante común en las aplicaciones. */

        IOFiles.storeInfoJSON(name, email, getPackageName());

        Uri photoUrl = currentUser.getPhotoUrl();

        for (UserInfo profile : currentUser.getProviderData()) {
            System.out.println(profile.getProviderId());
            // check if the provider id matches "facebook.com"
            if (profile.getProviderId().equals("facebook.com")) {

                String facebookUserId = profile.getUid();

                photoUrl = Uri.parse("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");

            } else if (profile.getProviderId().equals("google.com")) {
                photoUrl = Uri.parse(photoUrl.toString().replace("s96-c", "s700-c"));
            }
        }

        IOFiles.downloadSaveBmp(photoUrl, getApplicationContext());

    }

    private boolean isEmpty(EditText text) {

        return text.getText().toString().equals("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
