package com.example.findmyrhythm;

import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    private String name;
    private Date date;
    private LocalDateTime hour;
    private String ubication;

    public Event(String name, Date date, LocalDateTime hour, String ubication) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.ubication = ubication;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }
}

