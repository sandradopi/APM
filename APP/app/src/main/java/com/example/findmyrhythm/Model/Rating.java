package com.example.findmyrhythm.Model;

public class Rating extends Entity {

    private String eventId, userId, comment;
    private float ratingValue;

    public Rating() {};

    public Rating(String eventId, String userId, String comment, float ratingValue) {
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.ratingValue = ratingValue;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }
}
