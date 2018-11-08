package cn.magicwindow.uav.sdk.domain.event;

import cn.magicwindow.uav.sdk.RealTimeCallback;

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

    public void sendRealTime() {
        EventsProxy.create().sendRealTime(this);
    }

    public void save() {
        EventsProxy.create().save(this);
    }
}
