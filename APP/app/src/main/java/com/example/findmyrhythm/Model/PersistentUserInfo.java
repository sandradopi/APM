package com.example.findmyrhythm.Model;

import android.content.Context;

import java.util.ArrayList;

public class PersistentUserInfo extends PersistentInfo {

    private String birthdate;
    private ArrayList<String> subscribedLocations = new ArrayList<String>();
    private ArrayList<String> subscribedGenres = new ArrayList<String>();

    public PersistentUserInfo(String id, String name, String username, String email, String biography, ArrayList<Event> events, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres) {
        super(id, name, username, email, biography, events);
        this.birthdate = birthdate;
        this.subscribedLocations = subscribedLocations;
        this.subscribedGenres = subscribedGenres;
    }

    public ArrayList<String> getSubscribedLocations() {
        return subscribedLocations;
    }

    public void setSubscribedLocations(ArrayList<String> subscribedLocations) {
        this.subscribedLocations = subscribedLocations;
    }

    public ArrayList<String> getSubscribedGenres() {
        return subscribedGenres;
    }

    public void setSubscribedGenres(ArrayList<String> subscribedGenres) {
        this.subscribedGenres = subscribedGenres;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }



    public void updateInfo(Context context, String name, String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres) {
        PersistentUserInfo persistentUserInfo = (PersistentUserInfo) PersistentUserInfo.getPersistentInfo(context);
        persistentUserInfo.setName(name);
        persistentUserInfo.setUsername(username);
        persistentUserInfo.setBiography(biography);
        persistentUserInfo.setBirthdate(birthdate);
        persistentUserInfo.setSubscribedLocations(subscribedLocations);
        persistentUserInfo.setSubscribedGenres(subscribedGenres);

        PersistentUserInfo.setPersistentInfo(context, persistentUserInfo);
    }

}
