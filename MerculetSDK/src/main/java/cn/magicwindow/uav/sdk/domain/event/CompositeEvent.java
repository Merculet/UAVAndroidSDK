package cn.magicwindow.uav.sdk.domain.event;

import java.util.ArrayList;
import java.util.List;

import cn.magicwindow.uav.sdk.domain.Os;
import cn.magicwindow.uav.sdk.util.SPHelper;

/**
 * Created by aaron on 15/1/10.
 */
public class CompositeEvent {

    public Os device_info;          //device
    public String external_user_id;     //用户在商户端id
    public List<EventPojo> actions;          //events;

    public CompositeEvent() {
        Os os = new Os();
        external_user_id = SPHelper.create().getUserId();
        device_info = os;
        actions = new ArrayList<>();
    }

    public void addEvent(EventPojo event) {
        actions.add(event);
    }

    public void clearEvent() {
        actions.clear();
    }
}
