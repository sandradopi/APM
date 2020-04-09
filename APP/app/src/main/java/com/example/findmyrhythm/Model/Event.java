package com.example.findmyrhythm.Model;

import java.time.LocalDateTime;
import java.util.Date;

public class Event extends Entity{

    private String name;
    private Date date;
    private LocalDateTime hour;
    private String location, genre, organizerId;
    private int maxAttendees, price;

    public Event() {}

    public Event(String name, Date date, LocalDateTime hour, String location, String genre, String organizerId, int maxAttendees, int price) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.location = location;
        this.genre = genre;
        this.organizerId = organizerId;
        this.maxAttendees = maxAttendees;
        this.price = price;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }
}

