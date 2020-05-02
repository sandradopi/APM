package com.example.findmyrhythm.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.EventInfoActivity;
import com.example.findmyrhythm.View.OrganizerProfileActivity;
import com.example.findmyrhythm.View.UserProfileActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class EventNotificationListener implements ValueEventListener {

    private static final String TAG = "EventNotifListener";
    private Context mContext;
    private static EventNotificationListener instance = null;
    private User user = null;
    private boolean userLoaded = false;
    private int i = 0;

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

    private void sendNotification(Event event) {

        // Get a reference to the Notification Manager
       /* NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(mContext, UserProfileActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(mContext, 1201, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.logo_white)
                .setContentTitle("Find my Rhythm")
                .setContentText("Tienes un nuevo evento figura! Su Nombre es: " + event.getName())
                .setAutoCancel(true)
                .setContentIntent(pIntent);

        mNotificationManager.notify(001, mBuilder.build());*/

       OrganizerService service = new OrganizerService();
       try {
           Organizer organizer = service.getOrganizer(event.getOrganizerId());

           Intent resultIntent = new Intent(mContext, EventInfoActivity.class);
           resultIntent.putExtra("EVENT", event.getId());
           resultIntent.putExtra("RECOMMENDED", true);
           PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1902, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

           NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
           NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "myChannel")
                   .setSmallIcon(R.drawable.status_bar_icon)
                   .setContentTitle(event.getName())
                   .setDefaults(NotificationCompat.DEFAULT_ALL)
                   .setContentText(organizer.getName() + " ha creado un evento que te puede interesar!")
                   .setColor(Color.argb(0, 179, 86, 168))
                   .setContentIntent(pendingIntent)
                   .setAutoCancel(true);


           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               NotificationChannel mChannel = new NotificationChannel("myChannel", "eventNotif", NotificationManager.IMPORTANCE_HIGH);
               mChannel.enableLights(true);
               mChannel.enableVibration(true);

               notificationManager.createNotificationChannel(mChannel);
           }


           notificationManager.notify(0, mBuilder.build());

       } catch (InstanceNotFoundException e) {
           Log.e(TAG, "Instance of organizer has not been found");
       }



    }

    public boolean checkValidEvent(Event event) {

        return  (user.getSubscribedLocations().contains(event.getLocation()) && user.getSubscribedGenres().contains(event.getGenre()));
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        final DataSnapshot snapshot = dataSnapshot;
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(mContext);
        i++;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Iterable<DataSnapshot> iterable = snapshot.getChildren();
                    final Event event;

                    if (iterable instanceof Collection) {
                        Collection<DataSnapshot>  collection = (Collection<DataSnapshot>) iterable;
                        List<DataSnapshot> list = (List<DataSnapshot>) collection;
                        event = list.get(list.size() - 1).getValue(Event.class);

                    } else {

                        Iterator<DataSnapshot> i = iterable.iterator();
                        DataSnapshot value = null;

                        if (i.hasNext())
                            do {
                                value = i.next();
                            } while (i.hasNext());


                        event = value.getValue(Event.class);
                    }


                    if (userLoaded && i > 1 && checkValidEvent(event)) {

                        persistentUserInfo.addUniqueEventRecommended(mContext, event);
                        sendNotification(event);
                    }

                } catch (NullPointerException e ) {
                    Log.e(TAG, "There has been a null pointer exception when calculating the notification");
                }

            }
        });

        t.start();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
