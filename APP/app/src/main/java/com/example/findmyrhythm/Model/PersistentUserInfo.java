package com.example.findmyrhythm.Model;

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

    public PersistentUserInfo(String id, String name, String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres, ArrayList<Event> events,ArrayList<Event> recommended) {
        super(id, name, username, email, biography, birthdate, subscribedLocations, subscribedGenres);
        this.events = events;
        this.recommended=recommended;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Event getEventsRecommended(String id){
        System.out.println(id);
        for (Event event:recommended){
            System.out.println(event.getId());

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

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setEventsRecommended(ArrayList<Event> events) {
        this.recommended = recommended;
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

    public void addEventRecommended(Context context, ArrayList<Event> events) {
        PersistentUserInfo persistentInfo = getPersistentUserInfo(context);
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



}
