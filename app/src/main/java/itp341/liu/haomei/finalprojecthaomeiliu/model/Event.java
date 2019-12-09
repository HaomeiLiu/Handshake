package itp341.liu.haomei.finalprojecthaomeiliu.model;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String time;
    private String location;
    private String key;
    private long id;

    public Event(String title, String time, String location, String key, int participants) {
        this.title = title;
        this.time = time;
        this.location = location;
        this.key = key;
        this.participants = participants;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private int participants;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getKey() {
        return key;
    }

    public Event() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }


}
