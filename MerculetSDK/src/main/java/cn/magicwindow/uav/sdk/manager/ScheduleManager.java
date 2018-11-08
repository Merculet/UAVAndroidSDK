package cn.magicwindow.uav.sdk.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;

import cn.magicwindow.uav.sdk.domain.event.EventsProxy;
import cn.magicwindow.uav.sdk.util.SPHelper;

public class ScheduleManager {
    private static final String LOG_TAG;
    private static final int MSG_TIMEOUT = 1;
    private EventsProxy eventsProxy;
    private long mCardiacCycle;
    private long mDefaultCycle;
    private static final ThreadLocal<Handler> sPrivateHandler;
    private boolean isLoop;

    public ScheduleManager(EventsProxy eventsProxy) {
        this.eventsProxy = eventsProxy;
    }

    public void start() {
        long aCardiacCycle = SPHelper.create().getSendDelay();
        this.mDefaultCycle = aCardiacCycle;
        this.mCardiacCycle = aCardiacCycle;
        this.checkDateChanging();
        this.stop();
        if (!isLoop) {
            this.loop();
        }
    }

    public void stop() {
        ((Handler) sPrivateHandler.get()).removeMessages(1);
        isLoop = false;
    }

    private void loop() {
        isLoop = true;
        Message msg = ((Handler) sPrivateHandler.get()).obtainMessage(1, this);
        ((Handler) sPrivateHandler.get()).sendMessageDelayed(msg, this.mCardiacCycle);
    }

    public Boolean isLoop() {
        return isLoop;
    }

    public void onTimeOut() {
        this.eventsProxy.send();
    }

    private void checkDateChanging() {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        if (hour == 23) {
            int cycle = 61 - minute;
            long timeSchedule = (long) cycle * 60000L;
            if (timeSchedule < this.mCardiacCycle) {
                this.mCardiacCycle = timeSchedule;
            }
        } else if (this.mCardiacCycle != this.mDefaultCycle) {
            this.mCardiacCycle = this.mDefaultCycle;
        }

    }

    static {
        LOG_TAG = ScheduleManager.class.getSimpleName();
        sPrivateHandler = new ThreadLocal<Handler>() {
            protected Handler initialValue() {
                return new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1:
                                ScheduleManager schedule = (ScheduleManager) msg.obj;
                                if (schedule != null) {
                                    schedule.onTimeOut();
                                    schedule.checkDateChanging();
                                    schedule.loop();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };
            }
        };
    }
}
