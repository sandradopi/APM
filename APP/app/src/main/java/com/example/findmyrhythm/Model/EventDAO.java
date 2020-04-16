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
import java.util.Calendar;
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
        //final Calendar currentCalendar = Calendar.getInstance();
        AttendeeDAO attendeeDAO = new AttendeeDAO();

        final CountDownLatch lock = new CountDownLatch(user.getSubscribedLocations().size());

        // TODO: Descomentar estas partes después de hacer las pruebas
        for (final String location : user.getSubscribedLocations()) {
            table.orderByChild("location").equalTo(location).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Event event;
                    Calendar eventCalendar = Calendar.getInstance();*/
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.e(TAG, "element");
                        /*event = ds.getValue(Event.class);
                        eventCalendar.setTime(event.getEventDate());
                        if (eventCalendar.compareTo(currentCalendar) > 0)*/

                        locationEvents.add(ds.getValue(Event.class));
                    }
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
        for (Event event : locationEvents) {
            for (String genre : user.getSubscribedGenres()) {
                if (event.getGenre().equals(genre)) {
                    Log.e(TAG, event.getGenre());
                    recommendedEvents.add(event);
                    break;
                }
            }
        }

        Log.e(TAG+">>>>>>>>>>>>>>", recommendedEvents.toString());

        ArrayList<String> eventsConfirmed = attendeeDAO.findAttendeeByUser(user.getId());

        ArrayList<Event> finalEvents = new ArrayList<>();

        Log.e(TAG+">>>>>>>>>>>>>>Confirmed", eventsConfirmed.toString());


        for (Event event : recommendedEvents) {
            if (!eventsConfirmed.contains(event.getId())) {
                finalEvents.add(event);
            }
        }

        Log.e(TAG, locationEvents.toString());
        Log.e("FINALEVENTS", finalEvents.toString());
        return finalEvents;
    }


    public ArrayList<Event> findEventByOrganicer (final String idOrganicer) {
        DatabaseReference table = getTable();
        final ArrayList<Event> eventsCreated = new ArrayList<Event>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);
        System.out.println("ORGANIZADOR 2"+idOrganicer);
        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("ORGANIZADOR3");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event event = child.getValue(Event.class);
                    // Event title contains title and event is not deleted
                    System.out.println("ORGANIZADOR4" + idOrganicer);
                    if (event.getOrganizerId().contains(idOrganicer)) {
                        eventsCreated.add(event);
                        System.out.println("ORGANIZADOR5");
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
        return eventsCreated;
    }
}
