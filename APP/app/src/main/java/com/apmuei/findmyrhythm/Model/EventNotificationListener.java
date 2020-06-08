package com.apmuei.findmyrhythm.Model;

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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.EventInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EventNotificationListener implements ValueEventListener {

    private static final String TAG = "EventNotifListener";
    private static String NOTIFICATION_GROUP = "com.android.EVENT_NOTIFICATION";
    private static int GROUP_ID = -1;
    private Context mContext;
    private static EventNotificationListener instance = null;
    private User user = null;
    private boolean userLoaded = false;
    private int i = 0;
    private String lastEventId = "";

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
                   .setColor(Color.argb(0, 179, 86, 168))
                   .setContentIntent(pendingIntent)
                   .setGroup(NOTIFICATION_GROUP)
                   .setStyle(new NotificationCompat.BigTextStyle().bigText(organizer.getName() + " " + mContext.getResources().getString(R.string.notificacion)).setSummaryText(mContext.getResources().getString(R.string.summary)))
                   .setAutoCancel(true);

           NotificationCompat.Builder groupBuilder = new NotificationCompat.Builder(mContext, "myChannel")
                   .setContentTitle("Resumen")
                   .setContentText("Content Text")
                   .setSmallIcon(R.drawable.status_bar_icon)
                   .setStyle(new NotificationCompat.InboxStyle())
                   .setColor(Color.argb(0, 179, 86, 168))
                   .setGroup(NOTIFICATION_GROUP)
                   .setAutoCancel(true)
                   .setGroupSummary(true);


           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               NotificationChannel mChannel = new NotificationChannel("myChannel", "eventNotif", NotificationManager.IMPORTANCE_HIGH);
               mChannel.enableLights(true);
               mChannel.enableVibration(true);

               notificationManager.createNotificationChannel(mChannel);
           }

           notificationManager.notify(i, mBuilder.build());
            notificationManager.notify(GROUP_ID, groupBuilder.build());


       } catch (InstanceNotFoundException e) {
           Log.e(TAG, "Instance of organizer has not been found");
       }

    }

    public boolean checkValidEvent(Event event) {

        String location = null;

        while (location == null)
            location =  event.getCompleteAddress().get("province").toString();

        return  (user.getSubscribedLocations().contains(location) && user.getSubscribedGenres().contains(event.getGenre()) && !lastEventId.equals(event.getId()));
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

                        if (value != null)
                            event = value.getValue(Event.class);
                        else
                            return;
                    }


                    if (userLoaded && i > 1 && checkValidEvent(event)) {

                        lastEventId = event.getId();
                        //persistentUserInfo.addUniqueEventRecommended(mContext, event);
                        sendNotification(event);
                    }

                    lastEventId = event.getId();

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
