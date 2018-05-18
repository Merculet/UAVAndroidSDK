package io.merculet.domain.event;

import java.util.ArrayList;
import java.util.List;

import io.merculet.domain.Device;
import io.merculet.util.Preconditions;
import io.merculet.util.SPHelper;
import io.merculet.util.Util;

/**
 * Created by aaron on 15/1/10.
 */
public class CompositeEvent {

    public Device device_info;          //device
    public String external_user_id;     //用户在商户端id
    public List<EventPojo> actions;          //events;

    public CompositeEvent() {
        Device device = new Device();
        external_user_id = SPHelper.create().getUserId();
        device_info = device;
        actions = new ArrayList<>();
    }

    public void addEvent(EventPojo event) {

        actions.add(event);
    }

    public void clearEvent() {
        actions.clear();
    }
}
