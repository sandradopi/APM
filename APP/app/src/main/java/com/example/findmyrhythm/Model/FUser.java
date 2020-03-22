package com.example.findmyrhythm.Model;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class FUser {

    public String username;
    public String email;

    public FUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

}