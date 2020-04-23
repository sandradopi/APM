package com.example.findmyrhythm.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.findmyrhythm.Model.Attendee;
import com.example.findmyrhythm.Model.AttendeeDAO;
import com.example.findmyrhythm.Model.AttendeeService;
import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final AttendeeService attendeeService = new AttendeeService();
        final EventService eventService = new EventService();

        if (currentUser != null) {
            (new Thread() {
                public void run() {
                    String name = currentUser.getDisplayName();
                    String email = currentUser.getEmail();
                    String userId = currentUser.getUid();
                    ArrayList<String> eventsToAttendIds = new ArrayList<String>();
                    ArrayList<Event> eventscreated = new ArrayList<Event>();


                    SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                    String account_type = sharedPreferences.getString("account_type", null);

                    /*if (account_type == null) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }*/

                    if (account_type.equals("organizer")) {
                        PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
                        eventscreated = eventService.findEventByOrganicer(currentUser.getUid());
                        System.out.println("ORGANIZADOR"+eventscreated);

                        for (Event event : eventscreated) {
                            persistentOrganizerInfo.addEvent(getApplicationContext(),event);
                        }


                    } else {
                        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
                        eventsToAttendIds = attendeeService.findAttendeeByUser(currentUser.getUid());

                        EventService service = new EventService();
                        service.subscribeEventNotificationListener(SplashActivity.this, currentUser.getUid());

                        for (String idEvent : eventsToAttendIds) {
                            Event event = eventService.getEvent(idEvent);
                            persistentUserInfo.addEvent(getApplicationContext(),event);
                        }


                    }

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
