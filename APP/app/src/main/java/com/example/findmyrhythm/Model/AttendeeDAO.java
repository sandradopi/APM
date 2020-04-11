package com.example.findmyrhythm.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class AttendeeDAO extends GenericDAO<Attendee>{

    Attendee attendeeCheck = new Attendee();

    public AttendeeDAO() {
        super(Attendee.class, "attendees");
    }

    public Attendee findAttendeeByIds (final String idEvent, final String idUser) {
        DatabaseReference table = getTable();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);
        // Array for the headers

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Attendee attendee = child.getValue(Attendee.class);
                    // Event title contains title and event is not deleted
                    if (attendee.getIdEvent().contains(idEvent) && attendee.getIdUser().contains(idUser)) {
                        attendeeCheck = attendee;
                        break;
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
        return attendeeCheck;
}

    public ArrayList<String> findAttendeeByUser (final String idUser) {
        DatabaseReference table = getTable();
        final ArrayList<String> eventsToAttendId = new ArrayList<String>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Attendee attendee = child.getValue(Attendee.class);
                    // Event title contains title and event is not deleted
                    if (attendee.getIdUser().contains(idUser)) {
                        eventsToAttendId.add(attendee.getIdEvent());
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
        return eventsToAttendId;
    }
}