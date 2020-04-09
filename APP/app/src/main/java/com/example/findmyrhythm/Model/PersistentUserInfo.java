package com.example.findmyrhythm.Model;

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
}
