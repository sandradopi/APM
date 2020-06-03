package com.apmuei.findmyrhythm.Model;

import android.util.Log;

import com.apmuei.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

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
    

    public void updateUser(User user) {
        userDAO.update(user);
    }
}
