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

public class PersistentOrganizerInfo extends Organizer {

    private ArrayList<Event> events;

    public PersistentOrganizerInfo(String id, String name, String username, String email, String biography, String rating, String location, ArrayList<Event> events) {
        super(id, name, username, email, biography, rating, location);
        this.events = events;
    }

    public void updateInfo(Context context, String name, String username, String email, String biography, String location) {
        PersistentOrganizerInfo persistentOrgInfo = (PersistentOrganizerInfo) PersistentOrganizerInfo.getPersistentOrganizerInfo(context);
        persistentOrgInfo.setName(name);
        persistentOrgInfo.setUsername(username);
        persistentOrgInfo.setEmail(email);
        persistentOrgInfo.setBiography(biography);
        persistentOrgInfo.setLocation(location);

        PersistentOrganizerInfo.setPersistentOrganizerInfo(context, persistentOrgInfo);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Event getEvent(String id){
        //System.out.println(id);
        for (Event event:events){
            //System.out.println(event.getId());

            if(event.getId().equals(id)) return event;
        }
        return null;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public static PersistentOrganizerInfo getPersistentOrganizerInfo(Context context) {
        Gson gson = new Gson();
        PersistentOrganizerInfo persistentOrganizerInfo = null;
        try {
            persistentOrganizerInfo = gson.fromJson(new FileReader(context.getFilesDir().getPath() + "persistent_info.json"), PersistentOrganizerInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return persistentOrganizerInfo;
    }


    public static void setPersistentOrganizerInfo(Context context, PersistentOrganizerInfo persistentOrganizerInfo) {
        try (Writer writer = new FileWriter(context.getFilesDir().getPath() + "persistent_info.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(persistentOrganizerInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addEvent(Context context, Event event) {
        PersistentOrganizerInfo persistentInfo = getPersistentOrganizerInfo(context);
        events = persistentInfo.getEvents();

        if (!events.contains(event) && event!=null){
            events.add(event);

        }
        persistentInfo.setEvents(events);
        setPersistentOrganizerInfo(context, persistentInfo);
    }
    public void modifyEvent(Context context,Event eventToModiy, Event eventModified) {
        PersistentOrganizerInfo persistentInfo = getPersistentOrganizerInfo(context);
        events = persistentInfo.getEvents();

        if (events.contains(eventToModiy) && eventModified!=null){
            int index= events.indexOf(eventToModiy);
            Event tomodify = events.get(index);
            tomodify.modify(eventModified);
        }
        persistentInfo.setEvents(events);
        setPersistentOrganizerInfo(context, persistentInfo);
    }
    public void deleteEvent(Context context, Event event) {
        PersistentOrganizerInfo persistentInfo = getPersistentOrganizerInfo(context);
        events = persistentInfo.getEvents();

        if (events.contains(event) && event!=null){
            events.remove(event);
            System.out.println(events);

        }
        persistentInfo.setEvents(events);
        setPersistentOrganizerInfo(context, persistentInfo);
    }

}
