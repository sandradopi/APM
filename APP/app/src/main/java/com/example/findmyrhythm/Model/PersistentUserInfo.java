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

public class PersistentUserInfo {

    private ArrayList<String> eventsToAttend;
    private String biography;


    public PersistentUserInfo(ArrayList<String> eventsToAttend, String biography) {
        this.eventsToAttend = eventsToAttend;
        this.biography = biography;
    }

    public ArrayList<String> getEventsToAttend() {
        return eventsToAttend;
    }

    public void setEventsToAttend(ArrayList<String> eventsToAttend) {
        this.eventsToAttend = eventsToAttend;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public static PersistentUserInfo getPersistentUserInfo(Context context) {
        Gson gson = new Gson();
        PersistentUserInfo persistentUserInfo = null;
        try {
            persistentUserInfo = gson.fromJson(new FileReader(context.getFilesDir().getPath() + "user_persistent_info.json"), PersistentUserInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return persistentUserInfo;
    }

    public static void setPersistentUserInfo(Context context, PersistentUserInfo persistentUserInfo) {
        try (Writer writer = new FileWriter(context.getFilesDir().getPath() + "user_persistent_info.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(persistentUserInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
