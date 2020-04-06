package com.example.findmyrhythm.Model;

public class GeneralUser extends Entity {

    private String name, username, email, biography;

    public GeneralUser() {}

    public GeneralUser(String id, String name, String username, String email, String biography) {
        setId(id);
        this.name = name;
        this.username = username;
        this.email = email;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
