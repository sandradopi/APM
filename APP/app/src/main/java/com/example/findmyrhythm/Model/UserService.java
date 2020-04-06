package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

import java.util.ArrayList;

public class UserService {

    private static final String TAG = "UserService";
    private UserDAO userDAO = new UserDAO();

    public User getUser(String userId) throws InstanceNotFoundException {

        return userDAO.findById(userId);
    }

    public boolean createUser(String id, String name, String username, String email, String biography, String birthdate, ArrayList<String> regions, ArrayList<String> genres) {

        User user = new User(
                id,
                name,
                username,
                email,
                biography,
                birthdate,
                regions,
                genres
        );

        try {
            userDAO.insert(user);
        } catch (DuplicatedInstanceException e) {
            Log.e(TAG, "tried to insert an existing id");
            return false;
        }

        return true;
    }

    /*public boolean createUser(User user) {

        try {
            userDAO.insert(user);
        } catch (DuplicatedInstanceException e) {
            Log.e(TAG, "tried to insert an existing id");
            return false;
        }

        return true;
    }*/

    /*public boolean updateUserConfig(String id, String name, String username, String email, String biography, String birthdate, ArrayList<String> regions, ArrayList<String> genres) {

        try {
            User user = userDAO.findById(id);
            user.setName(name);
            user.setUsername(username);
            user.setEmail(email);
            user.setBiography(biography);
            user.setBirthdate(birthdate);

            user.getSubscribedGenres().clear();
            user.getSubscribedGenres().addAll(regions);

            user.getSubscribedGenres().clear();
            user.getSubscribedGenres().addAll(genres);

        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "couldn't find user");
            return  false;
        }

        return true;
    }*/

    public void updateUser(User user) {
        userDAO.update(user);
    }
}
