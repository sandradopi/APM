package com.apmuei.findmyrhythm.Model;

import java.util.ArrayList;

public class User extends GeneralUser {

   // private String password;
    private String birthdate;
    private ArrayList<String> subscribedLocations = new ArrayList<String>();
    private ArrayList<String> subscribedGenres = new ArrayList<String>();

    public User() {}

    public User(String id, String name, String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres) {
        super(id, name, username, email, biography);
        this.birthdate = birthdate;
        this.subscribedLocations = subscribedLocations;
        this.subscribedGenres = subscribedGenres;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public ArrayList<String> getSubscribedGenres() {
        return subscribedGenres;
    }

    public void setSubscribedGenres(ArrayList<String> subscribedGenres) {
        this.subscribedGenres = subscribedGenres;
    }

    public ArrayList<String> getSubscribedLocations() {
        return subscribedLocations;
    }

    public void setSubscribedLocations(ArrayList<String> subscribedLocations) {
        this.subscribedLocations = subscribedLocations;
    }
}
