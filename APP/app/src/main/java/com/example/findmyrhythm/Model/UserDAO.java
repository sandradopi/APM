package com.example.findmyrhythm.Model;

import java.util.ArrayList;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class, "users");
    }

}
