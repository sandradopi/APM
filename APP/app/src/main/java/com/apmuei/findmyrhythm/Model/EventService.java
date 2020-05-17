package com.apmuei.findmyrhythm.Model;

import android.content.Context;
import android.util.Log;

import com.apmuei.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

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

    public void modifyEvent(Event event) {

        try {
            eventDAO.modify(event);

        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "getEvent: Event not found");
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

    public ArrayList<Event> getEventsByTitle(SearchFilters searchFilters) {
        return eventDAO.getEventsByTitle(searchFilters);
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

    public void subscribeEventNotificationListener(Context context, String userId) {
        EventNotificationListener listener = EventNotificationListener.getInstance();
        listener.setContext(context);
        listener.setUser(userId);
        eventDAO.addChildEventListener(listener);

        Log.i(TAG, "NotifListener added");
    }

    public void unSubscribeEventNotificationListener() {
        EventNotificationListener listener = EventNotificationListener.getInstance();
        eventDAO.removeChildEventListener(listener);
        Log.i(TAG, "NotifListener removed");
    }


    public String findEventNameById(String id) {
        try {
            return eventDAO.findEventNameById(id);
        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "getEvent: Event not found");
        }
        return null;
    }

}
