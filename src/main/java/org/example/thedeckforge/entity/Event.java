package org.example.thedeckforge.entity;

import java.util.List;

public class Event {
    private long id;
    private String name;
    private String date;
    private String location;
    private String description;
    private List<Long> participants;
    public Event(long id, String name, String date, String location, String description, List<Long> participants) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.participants = participants;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
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
    public List<Long> getParticipants() {
        return participants;
    }
    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }
    @Override
    public String toString() {
        return "Events [id=" + id + ", name=" + name + ", date=" + date + ", location=" + location;
    }
}
