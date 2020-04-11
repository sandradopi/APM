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

}
