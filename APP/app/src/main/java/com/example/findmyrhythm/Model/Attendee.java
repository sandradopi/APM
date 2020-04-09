package com.example.findmyrhythm.Model;

public class Attendee extends Entity {

    private  String idEvent;
    private  String idUser;

    public Attendee(String idEvent, String idUser) {
        this.idEvent = idEvent;
        this.idUser = idUser;
    }

    public Attendee() {

    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
