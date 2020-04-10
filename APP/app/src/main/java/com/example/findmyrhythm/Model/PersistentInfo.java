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

public class PersistentInfo extends GeneralUser {


    private ArrayList<Event> events;

    public PersistentInfo(String id, String name, String username, String email, String biography, ArrayList<Event> events) {
        super(id, name, username, email, biography);
        this.events = events;
    }

    public static PersistentInfo getPersistentInfo(Context context) {
        Gson gson = new Gson();
        PersistentInfo persistentInfo = null;
        try {
            persistentInfo = gson.fromJson(new FileReader(context.getFilesDir().getPath() + "persistent_info.json"), PersistentInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return persistentInfo;
    }


    public static void setPersistentInfo(Context context, PersistentInfo persistentInfo) {
        try (Writer writer = new FileWriter(context.getFilesDir().getPath() + "persistent_info.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(persistentInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(Context context, Event event) {
        PersistentInfo persistentInfo = getPersistentInfo(context);
        persistentInfo.events.add(event);
        setPersistentInfo(context, persistentInfo);
    }

}
