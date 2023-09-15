package com.example.loginstart;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class Event {
    public String title;
    public String host;
    public String eventDescription;
    public String location;
    public String date;
    public String startTime;
    public String endTime;
    public int capacity;
    public boolean inviteOnly;
    public HashMap<String, Object> wills;
    public HashMap<String, Object> maybes;
    public HashMap<String, Object> wonts;
    public HashMap<String, Object> enemies;

    public Event(String title, String host, String eventDescription, String location, String date, String startTime, String endTime, int capacity, boolean inviteOnly) {
        this.title = title;
        this.host = host;
        this.eventDescription = eventDescription;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.inviteOnly = inviteOnly;
    }

    public String toString() {
        System.out.println(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        return "Event@" + Integer.toHexString(hashCode());
    }

    public String getTitle() {
        return title;
    }

    public String getHost() {
        return host;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setHost(String newHost) {
        host = newHost;
    }

    public void setEventDescription(String newEventDescription) {
        eventDescription = newEventDescription;
    }

    public void setLocation(String newLocation) {
        location = newLocation;
    }

    public void setWills(HashMap<String, Object> wills) {
        this.wills = wills;
    }

    public void setMaybes(HashMap<String, Object>  maybes) {
        this.maybes = maybes;
    }

    public void setWonts(HashMap<String, Object> wonts) {
        this.wonts = wonts;
    }

    public void setEnemies(HashMap<String, Object> enemies) {
        this.enemies = enemies;
    }
}
