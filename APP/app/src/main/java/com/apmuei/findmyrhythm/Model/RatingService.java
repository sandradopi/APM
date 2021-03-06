package com.apmuei.findmyrhythm.Model;

import android.util.Log;

import com.apmuei.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

import java.util.ArrayList;

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

    public ArrayList<Rating> getRatingsByEvent(String eventId) {
        return ratingDAO.findRatingsByEventId(eventId);
    }

    public ArrayList<String> getRatingsByUser(String userId) {
        return ratingDAO.findRatingsByUserId(userId);
    }

    public Float getMediaByEvent (String eventId) {

        ArrayList<Float> ratings = ratingDAO.findScoreRatingsById(eventId);
        float sum = 0;
        if (!ratings.isEmpty()) {
            for (float r : ratings)
                sum += r;

            return sum / ratings.size();
        } else
            return 0f;
    }

    public Float getMediaOrganizer (ArrayList<String> events) {

        ArrayList<Float> ratings = ratingDAO.findScoreRatingsByOrganizer(events);
        float sum = 0;
        if (!ratings.isEmpty()) {
            for (float r : ratings)
                sum += r;

            return sum / ratings.size();
        } else
            return 0f;
    }


    public Rating isRated (String userId, String eventId) {

        return ratingDAO.isRated(userId, eventId);
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
