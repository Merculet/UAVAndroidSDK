package io.merculet.domain;

import java.io.Serializable;

public class EventHistoryBean implements Serializable {
    private String eventName;
    private String time;

    public EventHistoryBean(String eventName, String time) {
        this.eventName = eventName;
        this.time = time;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
