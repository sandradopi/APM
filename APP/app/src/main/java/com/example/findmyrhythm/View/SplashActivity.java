package com.example.findmyrhythm.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            (new Thread() {
                public void run() {
                    String name = currentUser.getDisplayName();
                    String email = currentUser.getEmail();
                    String userId = currentUser.getUid();

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


                    SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                    String accountType = preferences.getString("account_type", null);

                    Intent intent;
                    if (accountType.equals("organizer")) {
                        intent = new Intent(SplashActivity.this, OrganizerProfileActivity.class);
                    } else if (accountType.equals("user")) {
                        intent = new Intent(SplashActivity.this, UserProfileActivity.class);
                    } else {
                        throw new IllegalArgumentException("Unknown user type.");
                    }

                    startActivity(intent);

                }
            }).start();

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }


    }
}
