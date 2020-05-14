package com.apmuei.findmyrhythm.Model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class PersistentUserInfo extends User {

    private ArrayList<Event> events;
    private ArrayList<Event> recommended;
    private ArrayList<String> ratedEvents;

    public PersistentUserInfo(String id, String name, String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres, ArrayList<Event> events, ArrayList<Event> recommended, ArrayList<String> ratedEvents) {
        super(id, name, username, email, biography, birthdate, subscribedLocations, subscribedGenres);
        this.events = events;
        this.recommended=recommended;
        this.ratedEvents = ratedEvents;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
    public ArrayList<Event> getEventsRecommended () { return recommended; }
    public ArrayList<String> getRatedEvents() { return ratedEvents; }

    public Event getEventRecommended(String id){
        System.out.println(id);
        for (Event event:recommended){
            if(event.getId().equals(id)) return event;
        }
        return null;
    }



    public Event getEvent(String id){
        System.out.println(id);
        for (Event event:events){
            System.out.println(event.getId());

            if(event.getId().equals(id)) return event;
        }
        return null;
    }

    public void setRatedEvents(ArrayList<String> ratedEvents) { this.ratedEvents = ratedEvents; }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setEventsRecommended(ArrayList<Event> events) {
        this.recommended = events;
    }

    public void updateInfo(Context context, String name, String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres) {
        PersistentUserInfo persistentUserInfo = (PersistentUserInfo) PersistentUserInfo.getPersistentUserInfo(context);
        persistentUserInfo.setName(name);
        persistentUserInfo.setUsername(username);
        persistentUserInfo.setEmail(email);
        persistentUserInfo.setBiography(biography);
        persistentUserInfo.setBirthdate(birthdate);
        persistentUserInfo.setSubscribedLocations(subscribedLocations);
        persistentUserInfo.setSubscribedGenres(subscribedGenres);

        PersistentUserInfo.setPersistentUserInfo(context, persistentUserInfo);
    }


    public static PersistentUserInfo getPersistentUserInfo(Context context) {
        Gson gson = new Gson();
        PersistentUserInfo persistentUserInfo = null;
        try {
            persistentUserInfo = gson.fromJson(new FileReader(context.getFilesDir().getPath() + "persistent_info.json"), PersistentUserInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return persistentUserInfo;
    }


    public static void setPersistentUserInfo(Context context, PersistentUserInfo persistentUserInfo) {
        try (Writer writer = new FileWriter(context.getFilesDir().getPath() + "persistent_info.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(persistentUserInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(Context context, Event event) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        recommended = persistentInfo.getEvents();

        if (!events.contains(event) && event!=null){
            events.add(event);

        }
        persistentInfo.setEvents(events);
        setPersistentUserInfo(context, persistentInfo);
    }

    public void addRatedEvent(Context context, String eventId) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        persistentInfo.getRatedEvents().add(eventId);
        setPersistentUserInfo(context, persistentInfo);
    }

    public void addEventRecommended(Context context, ArrayList<Event> events) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        persistentInfo.setEventsRecommended(events);
        setPersistentUserInfo(context, persistentInfo);
    }

    public void addUniqueEventRecommended(Context context, Event event) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        ArrayList<Event> events= persistentInfo.getEventsRecommended();
        events.add(event);
        persistentInfo.setEventsRecommended(events);
        setPersistentUserInfo(context, persistentInfo);
    }

    public void deleteEvent(Context context, Event event) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        events = persistentInfo.getEvents();

        if (events.contains(event) && event!=null){
            events.remove(event);
            System.out.println(events);

        }
        persistentInfo.setEvents(events);
        setPersistentUserInfo(context, persistentInfo);
    }

    public void deleteRecommendedEvent(Context context, Event event) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
        events = persistentInfo.getEventsRecommended();

        if (events.contains(event) && event!=null){
            events.remove(event);
            System.out.println(events);

        }
        persistentInfo.setEventsRecommended(events);
        setPersistentUserInfo(context, persistentInfo);
    }




}
