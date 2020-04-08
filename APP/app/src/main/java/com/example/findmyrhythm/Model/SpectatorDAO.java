package com.example.findmyrhythm.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SpectatorDAO extends GenericDAO<Spectator>{

    Spectator spectatorCheck = new Spectator();

    public SpectatorDAO() {
        super(Spectator.class, "spectators");
    }

    public Spectator findSpectatorByIds (final String idEvent, final String idUser) {
        DatabaseReference table = getTable();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);
        // Array for the headers

        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Spectator spectator = child.getValue(Spectator.class);
                    // Event title contains title and event is not deleted
                    if (spectator.getIdEvent().contains(idEvent) && spectator.getIdEvent().contains(idUser)) {
                        spectatorCheck = spectator;
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
        return spectatorCheck;
}
}