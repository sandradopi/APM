package com.example.findmyrhythm.Model;

public class Photo extends Entity{
    private String eventImage;

    public Photo() {}

    public Photo(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
}

