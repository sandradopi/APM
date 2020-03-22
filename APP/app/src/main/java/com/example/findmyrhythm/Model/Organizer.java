package com.example.findmyrhythm.Model;

public class Organizer extends Entity {

    private String name, username, email, location, biography;

    public Organizer(String name, String username, String email, String location, String biography) {

        this.name = name;
        this.username = username;
        this.email = email;
        this.location = location;
        this.biography = biography;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
