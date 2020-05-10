package com.apmuei.findmyrhythm.Model;

import java.util.Date;
import java.util.HashMap;

public class Event extends Entity implements Comparable<Event>{

    private String name;
    private Date eventDate;
    private String location, genre, organizerId, maxAttendees, price, description, eventImage;
    private HashMap<String, Object> completeAddress;

    public Event() {}

    public Event(String name, Date date, String location, String genre, String organizerId, String maxAttendees,
                 String price, String description, String eventImage, HashMap<String, Object> completeAddress) {
        this.name = name;
        this.eventDate = date;
        this.location = location;
        this.genre = genre;
        this.organizerId = organizerId;
        this.maxAttendees = maxAttendees;
        this.price = price;
        this.description = description;
        this.eventImage = eventImage;
        this.completeAddress = completeAddress;

    }

    public void modify(Event event){
        this.name = event.getName();
        this.eventDate = event.getEventDate();
        this.location = event.getLocation();
        this.genre = event.getGenre();
        this.organizerId = event.getOrganizerId();
        this.maxAttendees = event.getMaxAttendees();
        this.price = event.getPrice();
        this.description = event.getDescription();
        this.eventImage = event.getEventImage();
        this.completeAddress = event.getCompleteAddress();

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

    public HashMap<String, Object> getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(HashMap<String, Object> completeAddress) {
        this.completeAddress = completeAddress;
    }


    @Override
    public int compareTo(Event event) {
        return getEventDate().compareTo(event.getEventDate());
    }
}

