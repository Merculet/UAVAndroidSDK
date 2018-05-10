package io.merculet.db;

import io.merculet.MConfiguration;
import io.merculet.domain.event.EventsProxy;

/**
 * @Description
 * @Author lucio
 * @Email xiao.lu@magicwindow.cn
 * @Date 09/03/2018 5:29 PM
 * @Version
 */
public class DBManager {

    public static void insertEventByMsg() {
        EventsProxy eventsProxy = EventsProxy.create();
        MessageUtils.insertMsg(MConfiguration.getContext(), eventsProxy.getJsonString(), MessageUtils.TYPE_COMMON);
        eventsProxy.clearEvents();
    }

    public static void insertEventByMsg(String upData, String type) {
        MessageUtils.insertMsg(MConfiguration.getContext(), upData, type);
    }
}
