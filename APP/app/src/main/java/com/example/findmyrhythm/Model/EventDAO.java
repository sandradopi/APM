package com.example.findmyrhythm.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class EventDAO extends GenericDAO<Event> {

    private String TAG = "EventDAO";

    public EventDAO() {
        super(Event.class, "events");
    }

    public ArrayList<Event> getRecommendedEvents(User user) {


        final ArrayList<Event> locationEvents = new ArrayList<>();
        ArrayList<Event> recommendedEvents = new ArrayList<>();
        final DatabaseReference table = getTable();
        System.out.println("*************************************************************************************\n");
        final CountDownLatch lock = new CountDownLatch(user.getSubscribedLocations().size());

        for (final String location : user.getSubscribedLocations()) {
            table.orderByChild("location").equalTo(location).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                        locationEvents.add(ds.getValue(Event.class));

                    lock.countDown();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    lock.countDown();
                }
            });

        }

        try {
            lock.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted Exception");
        }

        //TODO: CHECK WITH CONTAINS
        for (Event event : locationEvents)
            for (String genre : user.getSubscribedGenres())
                if (event.getGenre().equals(genre)) {
                    recommendedEvents.add(event);
                    break;
                }


        System.out.println(recommendedEvents.get(0).getLocation());
        System.out.println(recommendedEvents.get(1).getLocation());
        System.out.println(recommendedEvents.size());

        return recommendedEvents;


    }
}
