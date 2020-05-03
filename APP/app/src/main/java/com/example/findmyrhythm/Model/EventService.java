package com.example.findmyrhythm.Model;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.View.UserLogActivity;

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
            String eventId = eventDAO.modify(event);

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

    public ArrayList<Event> getEventsByTitle(String token) {
        return eventDAO.getEventsByTitle(token);
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

}
