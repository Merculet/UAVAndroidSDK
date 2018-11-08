package cn.magicwindow.uav.sdk.domain.event;

import cn.magicwindow.uav.sdk.RealTimeCallback;
import cn.magicwindow.uav.sdk.domain.Device;
import cn.magicwindow.uav.sdk.domain.response.HttpResponse;
import cn.magicwindow.uav.sdk.log.DebugLog;
import cn.magicwindow.uav.sdk.manager.ScheduleManager;
import cn.magicwindow.uav.sdk.queue.BatchQueue;
import cn.magicwindow.uav.sdk.queue.QueueProcess;
import cn.magicwindow.uav.sdk.manager.RequestManager;
import cn.magicwindow.uav.sdk.util.JSONUtils;
import cn.magicwindow.uav.sdk.util.Preconditions;
import cn.magicwindow.uav.sdk.util.SPHelper;
import cn.magicwindow.uav.sdk.util.Util;

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
//        scheduleManager.start();
        compositeEvent = getEvents();
        batchQueue.add(event);
        batchQueue.setProcess(new QueueProcess<EventPojo>() {
            @Override
            public void processData(EventPojo event) {
                DebugLog.d(JSONUtils.objectToJsonString(event) + " ----->  event");
                compositeEvent.addEvent(event);
                if (compositeEvent.actions.size() >= Util.getSendBatch()) {
                    onSend();
                }
            }
        });
    }

    //直接上传数据,带回调:只上传当前事件,不传本地,失败也不缓存
    synchronized void sendRealTime(final EventPojo event) {
//        scheduleManager.start();
        CompositeEvent compositeEvent = new CompositeEvent();
        compositeEvent.external_user_id = SPHelper.create().getUserId();
        compositeEvent.addEvent(event);
        String jsonString = JSONUtils.objectToJsonString(compositeEvent);
        if (Preconditions.isNotBlank(jsonString) && SPHelper.create().isAuto()) {
            RequestManager.get().uploadRealTime(jsonString, new RealTimeCallback() {
                @Override
                public void onSuccess() {
                    DebugLog.d("upload real time");
                }

                @Override
                public void onFailed(HttpResponse httpResponse) {
                    EventsProxy.create().send(event);
                    DebugLog.d("upload real time failed" + httpResponse.message);
                }
            });
        }
    }

    public synchronized void sendDeviceInfo(RealTimeCallback callback) {
//        scheduleManager.start();
        Device device_info = new Device();
        String jsonString = JSONUtils.infoToJsonString(device_info);
        if (Preconditions.isNotBlank(jsonString) && SPHelper.create().isAuto()) {
            RequestManager.get().uploadDeviceInfo(jsonString, callback);
        }
    }

    public synchronized void onSend() {
        send();
        if (SPHelper.create().isAuto())
            RequestManager.get().uploadDb();  //启动退出和发送事件有30条才会上传本地数据
    }

    //save放在队列中是异步获取,无法直接调用onSend
    void save(EventPojo event) {
//        scheduleManager.start();
        compositeEvent = getEvents();
        compositeEvent.addEvent(event);
    }

    public void send() {
//        scheduleManager.start();
        String jsonString = getJsonString();
        if (Preconditions.isNotBlank(jsonString) && SPHelper.create().isAuto()) {
            //json传递到后台
            RequestManager.get().upload(jsonString);
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

    public void start() {
        scheduleManager.start();
    }
}
