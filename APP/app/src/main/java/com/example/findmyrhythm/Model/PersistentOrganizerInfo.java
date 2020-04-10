package com.example.findmyrhythm.Model;

import java.util.ArrayList;

public class PersistentOrganizerInfo extends PersistentInfo {

    private String rating, location;

    public PersistentOrganizerInfo(String id, String name, String username, String email, String biography, ArrayList<Event> events, String rating, String location) {
        super(id, name, username, email, biography, events);
        this.rating = rating;
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
