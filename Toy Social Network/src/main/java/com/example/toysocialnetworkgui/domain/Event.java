package com.example.toysocialnetworkgui.domain;

import java.sql.Date;
import java.time.LocalDate;

public class Event extends Entity<Long> {
    User organizer;
    String name;
    String description;
    String location;
    LocalDate date;

    public Event(User organizer, String name, String description, String location, LocalDate date) {
        this.organizer = organizer;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
