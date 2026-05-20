package org.example.thedeckforge.entity;

import java.util.List;

public class Event {
    private Long ownerId;
    private Long id;
    private String name;
    private String date;
    private String location;
    private String description;
    private List<String> participants;
    public Event( Long id, String name, String date, String location, String description, List<String> participants) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.participants = participants;
    }

    public Event() {

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getParticipants() {
        return participants;
    }
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    @Override
    public String toString() {
        return "Events [id=" + id + ", name=" + name + ", date=" + date + ", location=" + location;
    }
}
