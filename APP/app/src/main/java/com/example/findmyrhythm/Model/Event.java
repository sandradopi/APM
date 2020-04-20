package com.example.findmyrhythm.Model;

import android.graphics.Bitmap;
import android.location.Address;

import java.time.LocalDateTime;
import java.util.Date;

public class Event extends Entity{

    private String name;
    private Date eventDate;
    private String location, genre, organizerId, maxAttendees, price, description, eventImage;

    public Event() {}

    public Event(String name, Date date, String location, String genre, String organizerId, String maxAttendees, String price, String description, String eventImage) {
        this.name = name;
        this.eventDate = date;
        this.location = location;
        this.genre = genre;
        this.organizerId = organizerId;
        this.maxAttendees = maxAttendees;
        this.price = price;
        this.description = description;
        this.eventImage = eventImage;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(String maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
}

