package com.example.findmyrhythm.Model;

import java.util.ArrayList;

public class User extends Entity{

   // private String password;
    private String name, username, email, biography, birthdate;
    private ArrayList<String> subscribedLocations = new ArrayList<String>();
    private ArrayList<String> subscribedGenres = new ArrayList<String>();


    public User(String name,String username, String email, String biography, String birthdate, ArrayList<String> subscribedLocations, ArrayList<String> subscribedGenres) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.biography = biography;
        this.birthdate = birthdate;
        this.subscribedLocations = subscribedLocations;
        this.subscribedGenres = subscribedGenres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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
