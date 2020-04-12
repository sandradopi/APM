package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

import java.util.ArrayList;

public class EventService {

    private static final String TAG = "EventService";

    private EventDAO eventDAO = new EventDAO();

    public Event getEvent(String eventId) {

        try {
            return eventDAO.findById(eventId);

        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "getEvent: Event not found");
            return null;
        }
    }

    public void createEvent(Event event) {

        try {
            // Insert event
            String eventId = eventDAO.insert(event);
        }
        catch (DuplicatedInstanceException e) {
            //Log.e(TAG, "Event id was already taken");
        }

    }

    public void updateEvent(Event event) {
        eventDAO.update(event);
    }

    public void deleteEvent(String eventId) {
        eventDAO.delete(eventId);
    }

    public ArrayList<Event> getRecommendedEvents(User user) {
        return eventDAO.getRecommendedEvents(user);
    }

    public ArrayList<Event> findEventByOrganicer(String idOrganicer) {
        return eventDAO.findEventByOrganicer(idOrganicer);
    }

}
