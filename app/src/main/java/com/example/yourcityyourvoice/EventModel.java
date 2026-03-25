package com.example.yourcityyourvoice;

public class EventModel {
    private String key;
    private String title;
    private String organizer;
    private String description;
    private String location;
    private String imageUrl;
    private long timestamp;
    private String time;
    private String createdBy; // Email of the event creator

    public EventModel() {}

    public EventModel(String key, String title, long timestamp, String organizer,
                      String description, String imageUrl, String location,
                      String time, String createdBy) {
        this.key = key;
        this.title = title;
        this.timestamp = timestamp;
        this.organizer = organizer;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.time = time;
        this.createdBy = createdBy;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
