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
import java.util.List;
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


        for (Event event : locationEvents) {
            if (user.getSubscribedGenres().contains(event.getGenre())) {
                recommendedEvents.add(event);
            }
        }


//        for (String genre : user.getSubscribedGenres()) {
//
//            if (event.getGenre().equals(genre)) {
//                Log.e(TAG, event.getGenre());
//                recommendedEvents.add(event);
//                break;
//            }
//        }

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


    public ArrayList<Event> getEventsByTitle(final String token) {

        final ArrayList<Event> events = new ArrayList<>();
        ArrayList<Event> recommendedEvents = new ArrayList<>();
        final DatabaseReference table = getTable();

        final CountDownLatch lock = new CountDownLatch(1);

        // TODO: Descomentar estas partes después de hacer las pruebas
        table.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*Event event;
                Calendar eventCalendar = Calendar.getInstance();*/
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.e(TAG, ds.getValue(Event.class).getName());

                    Event event = ds.getValue(Event.class);



                    /*event = ds.getValue(Event.class);
                    eventCalendar.setTime(event.getEventDate());
                    if (eventCalendar.compareTo(currentCalendar) > 0)*/
                    if (event.getName().toLowerCase().contains(token.toLowerCase()))
                        events.add(ds.getValue(Event.class));

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

        return events;
    }


    public ArrayList<Event> findEventByOrganicer (final String idOrganicer) {
        DatabaseReference table = getTable();
        final ArrayList<Event> eventsCreated = new ArrayList<Event>();

        // Lock
        final CountDownLatch lock = new CountDownLatch(1);
        // Loop through the event Ids, getting the headers
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event event = child.getValue(Event.class);
                    // Event title contains title and event is not deleted
                    if (event.getOrganizerId().equals(idOrganicer)) {
                        eventsCreated.add(event);
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

    public void addChildEventListener (ValueEventListener listener) {
        DatabaseReference table = getTable();
        table.addValueEventListener(listener);
    }

    public void removeChildEventListener (ValueEventListener listener) {
        DatabaseReference table = getTable();
        table.removeEventListener(listener);
    }
}
