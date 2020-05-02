package com.example.findmyrhythm.Model;

import android.location.Address;

public class Organizer extends GeneralUser {

    private String rating, location;
    private Address completeAddress;

    public Organizer() {}

    public Organizer(String id, String name, String username, String email, String biography, String rating, String location) {

        super(id, name, username, email, biography);
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

    public Address getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(Address completeAddress) {
        this.completeAddress = completeAddress;
    }
}
