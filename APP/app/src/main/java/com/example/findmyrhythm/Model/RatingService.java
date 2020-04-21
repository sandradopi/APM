package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

public class RatingService {

    private RatingDAO ratingDAO = new RatingDAO();
    private static final String TAG = "RatingService";


    public Rating getRating(String ratingId) {

        try {
            return ratingDAO.findById(ratingId);

        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "getRating: Rating not found");
            return null;
        }
    }

    public String createRating(Rating rating) {

        try {
            // Insert rating
            String ratingId = ratingDAO.insert(rating);
            return ratingId;
        }
        catch (DuplicatedInstanceException e) {
            Log.e(TAG, "Rating id was already taken");
            return null;
        }

    }

    public void updateRating(Rating rating) {
        ratingDAO.update(rating);
    }

    public void deleteRating(String ratingId) {
        ratingDAO.delete(ratingId);
    }
}
