package io.merculet.domain.event;

import io.merculet.manager.ScheduleManager;
import io.merculet.queue.BatchQueue;
import io.merculet.queue.QueueProcess;
import io.merculet.manager.RequestManager;
import io.merculet.util.JSONUtils;
import io.merculet.util.Preconditions;
import io.merculet.util.SPHelper;
import io.merculet.util.Util;

/**
 * Created by aaron on 15/1/10.
 */
public class EventsProxy {

    private static volatile EventsProxy defaultInstance;
    private final ScheduleManager scheduleManager;
    private CompositeEvent compositeEvent;
    private BatchQueue<EventPojo> batchQueue;

    private EventsProxy() {
        this.batchQueue = new BatchQueue<>(SPHelper.create().getSendBatch());
        this.compositeEvent = new CompositeEvent();
        this.scheduleManager = new ScheduleManager(this);
    }

    public static EventsProxy create() {
        if (defaultInstance == null) {
            synchronized (EventsProxy.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventsProxy();
                }
            }
        }
        return defaultInstance;
    }

    void send(EventPojo event) {
        scheduleManager.start();
        compositeEvent = getEvents();
        batchQueue.add(event);
        batchQueue.setProcess(new QueueProcess<EventPojo>() {
            @Override
            public void processData(EventPojo event) {
                compositeEvent.addEvent(event);
                if (compositeEvent.actions.size() >= Util.getSendBatch()) {
                    onSend();
                }
            }
        });
    }

    public synchronized void onSend() {
        send();
        if (SPHelper.create().isAuto())
            RequestManager.uploadDb();  //启动退出和发送事件有30条才会上传本地数据
    }

    //save放在队列中是异步获取,无法直接调用onSend
    void save(EventPojo event) {
        scheduleManager.start();
        compositeEvent = getEvents();
        compositeEvent.addEvent(event);
    }

    public void send() {
        scheduleManager.start();
        String jsonString = getJsonString();
        if (Preconditions.isNotBlank(jsonString) && SPHelper.create().isAuto()) {
            //json传递到后台
            RequestManager.upload(jsonString);
        }
    }

    public void clearEvents() {
        if (compositeEvent != null) {
            compositeEvent.clearEvent();
        }
    }

    private CompositeEvent getEvents() {
        if (compositeEvent != null) {
            compositeEvent.external_user_id = SPHelper.create().getUserId();
            return compositeEvent;
        } else {
            return new CompositeEvent();
        }
    }

    public String getJsonString() {
        return getEvents().actions.size() > 0 ? JSONUtils.objectToJsonString(getEvents()) : "";
    }
}
