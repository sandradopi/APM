package com.apmuei.findmyrhythm.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RatingDAO extends GenericDAO<Rating> {

    Rating rated = new Rating();
    //Boolean isRated = false;
    private static final String TAG = "RatingDAO";

    public RatingDAO() { super(Rating.class, "ratings"); }

    public ArrayList<Float> findScoreRatingsById (final String idEvent) {
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

    public ArrayList<Rating> findRatingsByEventId (final String idEvent) {
        DatabaseReference table = getTable();
        final ArrayList<Rating> ratingsByEvent = new ArrayList<Rating>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    if (rating.getEventId().contains(idEvent)) {
                        ratingsByEvent.add(rating);
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

    public ArrayList<String> findRatingsByUserId (final String idUser) {
        DatabaseReference table = getTable();
        final ArrayList<String> ratingsByUser = new ArrayList<>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    if (rating.getUserId().contains(idUser)) {
                        ratingsByUser.add(rating.getEventId());
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
        return ratingsByUser;
    }

    public ArrayList<Float> findScoreRatingsByOrganizer (final ArrayList<String> events) {
        DatabaseReference table = getTable();
        final ArrayList<Float> ratingsOrganizer = new ArrayList<>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    for (String id : events) {
                        if (rating.getEventId().contains(id)) {
                            ratingsOrganizer.add(rating.getRatingValue());
                        }
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
        return ratingsOrganizer;
    }

    public Rating isRated (final String idUser, final String idEvent) {
        DatabaseReference table = getTable();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    if (rating.getUserId().contains(idUser)) {
                        if (rating.getEventId().contains(idEvent))
                            rated = rating;
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
        return rated;
    }

}
