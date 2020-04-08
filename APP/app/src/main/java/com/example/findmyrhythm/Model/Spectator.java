package com.example.findmyrhythm.Model;

public class Spectator extends Entity {

    private  String idEvent;
    private  String idUser;

    public Spectator(String idEvent, String idUser) {
        this.idEvent = idEvent;
        this.idUser = idUser;
    }

    public Spectator() {

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
