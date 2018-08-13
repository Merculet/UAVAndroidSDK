package io.merculet.uav.sdk.domain.event;

import io.merculet.uav.sdk.RealTimeCallback;

/**
 * Created by aaron on 15/1/10.
 */
public abstract class EventPojo {

    public EventPojo() {
        init();
    }

    abstract protected void init();

    public void send() {
        EventsProxy.create().send(this);
    }

    public void sendRealTime(RealTimeCallback callback) {
        EventsProxy.create().sendRealTime(this, callback);
    }

    public void save() {
        EventsProxy.create().save(this);
    }
}
