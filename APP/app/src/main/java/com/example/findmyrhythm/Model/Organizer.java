package com.example.findmyrhythm.Model;

public class Organizer extends GeneralUser {

    private String rating, location;

    public Organizer(String name, String username, String email, String biography, String rating, String location) {

        super(name, username, email, biography);
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
