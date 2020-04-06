package com.example.findmyrhythm.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventDAO extends GenericDAO<Event> {

    private String TAG = "EventDAO";

    public EventDAO() {
        super(Event.class, "events");
    }

    public ArrayList<Event> getRecommendedEvents() {

        ArrayList<Event> recommendedEvents = new ArrayList<>();
        DatabaseReference table = getTable();

        table.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*for (String location : user.getSubscribedLocations())
                    for (String genre : user.getSubscribedGenres())*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Event event = ds.getValue(Event.class);
                            System.out.println(event);
                        }

                        System.out.println("*****************************************************\n");
                        System.out.println(dataSnapshot.getValue(Event.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*for (String location : user.getSubscribedLocations())
            for (String genre : user.getSubscribedGenres()) {


            }*/
        return null;


    }
}
