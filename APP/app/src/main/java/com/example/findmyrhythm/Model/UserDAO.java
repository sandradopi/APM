package com.example.findmyrhythm.Model;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class, "users");
    }
}
