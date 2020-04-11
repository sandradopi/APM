package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

import java.util.ArrayList;

public class AttendeeService {

    private static final String TAG = "AttendeeService";
    private AttendeeDAO attendeeDAO = new AttendeeDAO();


    public Attendee getAttendee(String attendeeId) throws InstanceNotFoundException {

        return attendeeDAO.findById(attendeeId);
    }

    public boolean createAttendee(String idUser, String idEvent) {

        Attendee attendee = new Attendee(
                idUser,
                idEvent
        );

        try {
            attendeeDAO.insert(attendee);
        } catch (DuplicatedInstanceException e) {
            Log.e(TAG, "tried to insert an existing id");
            return false;
        }

        return true;
    }

    public void updateAttendee(Attendee attendee) {
        attendeeDAO.update(attendee);
    }
    public void deleteAttendee(Attendee attendee) {
        attendeeDAO.delete(attendee.getId());
    }

    public Attendee findAttendeeByIds(String idEvent, String idUser) {
        return attendeeDAO.findAttendeeByIds(idEvent,idUser );
    }

    public ArrayList<String> findAttendeeByUser(String idUser) {
        return attendeeDAO.findAttendeeByUser(idUser);
    }

    public void deleteAttendeeByEvent(String idEvent) {
        attendeeDAO.deleteAttendeeByEvent(idEvent);
    }


}
