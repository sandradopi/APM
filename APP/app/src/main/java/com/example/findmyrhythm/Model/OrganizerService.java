package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

public class OrganizerService {

    private static final String TAG = "OrganizerService";
    private OrganizerDAO organizerDAO = new OrganizerDAO();

    public Organizer getOrganizer(String organizerId) throws InstanceNotFoundException {

        return organizerDAO.findById(organizerId);
    }

    public boolean createOrganizer(String id, String name, String username, String email, String biography, String rating, String location) {

        Organizer organizer = new Organizer(
                id,
                name,
                username,
                email,
                biography,
                rating,
                location
        );

        try {
            organizerDAO.insert(organizer);

        } catch (DuplicatedInstanceException e) {
            Log.e(TAG, "Tried to insert an existing organizer");
            return false;
        }

        return true;
    }

    public void updateOrganizer(Organizer organizer) {
        organizerDAO.update(organizer);
    }
}
