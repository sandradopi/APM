package com.example.findmyrhythm.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RatingDAO extends GenericDAO<Rating> {

    public RatingDAO() { super(Rating.class, "ratings"); }

    public ArrayList<Float> findRatingsById (final String idEvent) {
        DatabaseReference table = getTable();
        final ArrayList<Float> ratingsByEvent = new ArrayList<Float>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    if (rating.getEventId().contains(idEvent)) {
                        ratingsByEvent.add(rating.getRatingValue());
                    }
                }
                lock.countDown();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lock.countDown();
            }
        });
        // Wait for all data to be retrieved
        try {
            lock.await();
        }
        catch (InterruptedException e) {
            //Log.e(TAG, "Thread was interrupted while waiting for syncronisation with Firebase call");
        }
        return ratingsByEvent;
    }

}
