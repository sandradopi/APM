package com.apmuei.findmyrhythm.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apmuei.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class EventDAO extends GenericDAO<Event> {

    private String TAG = "EventDAO";
    private GeoFire geoFire;

    public EventDAO() {
        super(Event.class, "events");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geoFire = new GeoFire(ref);
    }


    @Override
    public String insert(Event entity) throws DuplicatedInstanceException {

        String eventId = super.insert(entity);

        Double latitude = (Double) (Objects.requireNonNull(entity.getCompleteAddress().get("latitude")));
        Double longitude = (Double) (Objects.requireNonNull(entity.getCompleteAddress().get("longitude")));
        geoFire.setLocation(eventId, new GeoLocation(latitude, longitude), new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });

        return eventId;

    }


    public String findEventNameById (String entityId) throws InstanceNotFoundException {

        DatabaseReference table = getTable();

        // Placeholder for the data retrieved from the DB
        final ArrayList<String> names = new ArrayList<>();

        //Lock to wait for the data
        final CountDownLatch lock = new CountDownLatch(1);

        table.child(entityId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                names.add(dataSnapshot.getValue(String.class));
                // Data retrieved, release lock
                lock.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Cancelled, release lock
                lock.countDown();
            }
        });

        try {
            lock.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "findById thread interrupted");
        }

        // Check that some data was retrieved
        if (names.get(0) == null) {
            throw new InstanceNotFoundException();
        }

        // Return entity
        return names.get(0);
    }


    @Override
    public String modify(Event entity) throws InstanceNotFoundException {
        String eventId =  super.modify(entity);

        Double latitude = (Double) (Objects.requireNonNull(entity.getCompleteAddress().get("latitude")));
        Double longitude = (Double) (Objects.requireNonNull(entity.getCompleteAddress().get("longitude")));
        geoFire.setLocation(eventId, new GeoLocation(latitude, longitude), new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });

        return  eventId;
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
            table.orderByChild("completeAddress/province").equalTo(location).addValueEventListener(new ValueEventListener() {
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
