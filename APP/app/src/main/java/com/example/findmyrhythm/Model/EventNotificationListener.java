package com.example.findmyrhythm.Model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class EventNotificationListener implements ChildEventListener {

    private static final String TAG = "EventNotifListener";
    private Context mContext;
    private static EventNotificationListener instance = null;
    private User user = null;
    private boolean userLoaded = false;

    private EventNotificationListener() {

    }

    public static EventNotificationListener getInstance() {

        if (instance == null)
            instance = new EventNotificationListener();

        return  instance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setUser(final String id) {

        //Load user data
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                UserDAO userDAO = new UserDAO();

                try {
                    user = userDAO.findById(id);
                    userLoaded = true;

                } catch (Exception e) {
                    user = null;
                    userLoaded = false;
                }
            }
        });

        t.start();

    }

    private void sendNotification(String text) {

        // Get a reference to the Notification Manager
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
