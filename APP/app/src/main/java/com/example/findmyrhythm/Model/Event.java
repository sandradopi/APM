package com.example.findmyrhythm.Model;

import java.time.LocalDateTime;
import java.util.Date;

public class Event extends Entity{

    private String name;
    private Date date;
    private LocalDateTime hour;
    private String location, organizerId, price;

    public Event(String name, Date date, LocalDateTime hour, String location, String organizerId, String price) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.location = location;
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
}

