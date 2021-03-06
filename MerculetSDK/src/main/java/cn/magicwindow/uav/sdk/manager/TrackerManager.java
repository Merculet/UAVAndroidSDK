package cn.magicwindow.uav.sdk.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.magicwindow.uav.sdk.MConfiguration;
import cn.magicwindow.uav.sdk.RealTimeCallback;
import cn.magicwindow.uav.sdk.TrackAgent;
import cn.magicwindow.uav.sdk.config.Constant;
import cn.magicwindow.uav.sdk.db.MessageUtils;
import cn.magicwindow.uav.sdk.domain.UserProfile;
import cn.magicwindow.uav.sdk.domain.event.CustomEvent;
import cn.magicwindow.uav.sdk.domain.event.EventsProxy;
import cn.magicwindow.uav.sdk.util.DeviceInfoUtils;
import cn.magicwindow.uav.sdk.util.Preconditions;
import cn.magicwindow.uav.sdk.util.SPHelper;
import cn.magicwindow.uav.sdk.util.TimeMap;
import cn.magicwindow.uav.sdk.util.Util;

/**
 * @Description
 * @Author lucio
 * @Email xiao.lu@magicwindow.cn
 * @Date 08/03/2018 11:42 AM
 * @Version
 */
public class TrackerManager implements TrackAgent.TrackerInterface {

    private final static HashMap<String, String> timeMap = new TimeMap();
    private final SPHelper spHelper;
    private String PAGE_TRACKING_PRE_PAGE = "root";
    private String PAGE_TRACKING_PAGE_PATH = "root";

    public TrackerManager() {
        this.spHelper = SPHelper.create();
    }

    @Override
    public void init(Application application) {
        launchEvent();
        //开启轮询
        EventsProxy.create().start();
        MConfiguration.get().initContext(application);
        if (Build.VERSION.SDK_INT < 14) return;

        try {
            MWLifecycleCallbacks activityLifeCycleObserver = new MWLifecycleCallbacks();
            /* Set an observer for activity life cycle events. */
            application.unregisterActivityLifecycleCallbacks(activityLifeCycleObserver);
            application.registerActivityLifecycleCallbacks(activityLifeCycleObserver);
            spHelper.setAutoSession(true);
        } catch (NoSuchMethodError Ex) {
            spHelper.setAutoSession(false);
        } catch (NoClassDefFoundError error) {
            spHelper.setAutoSession(false);
        }
    }

    private void launchEvent() {
        if (!MConfiguration.isFirst) {
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DeviceInfoUtils.initDeviceInfo(MConfiguration.get().getContext());
                EventsProxy.create().onSend();
            }
        });
        MConfiguration.isFirst = false;
    }


    @Override
    public void onResume(Context context) {
        onResume(context, null);
    }

    @Override
    public void onResume(Context context, String pageTitle) {
        //初始化MWConfiguration
        if (!spHelper.isAutoSession()) {
            resume(context, pageTitle);
        }
    }

    @Override
    public void onPause(Context context) {
        onPause(context, null);
    }

    @Override
    public void onPause(Context context, String pageTitle) {
        if (!spHelper.isAutoSession()) {
            pause(context, pageTitle);
        }
    }

    @Override
    public void onPageStart(String title) {
        String startTime = Util.getCurrentTimeSecondStr();
        PAGE_TRACKING_PAGE_PATH = title;
        timeMap.put(Constant.PAGE_TRACKING_START_TIME, startTime);
    }

    @Override
    public void onPageEnd(String title) {
        if (Preconditions.isNotBlank(timeMap.get(Constant.PAGE_TRACKING_START_TIME))) {
//            PageTrackingEvent pageEvent = new PageTrackingEvent();
//            pageEvent.st = timeMap.get(Constant.PAGE_TRACKING_START_TIME);
//            timeMap.remove(Constant.PAGE_TRACKING_START_TIME);
//            pageEvent.et = Util.getCurrentTimeSecondStr();
//            if (Preconditions.isNotBlank(title)) title = PAGE_TRACKING_PAGE_PATH;
//            pageEvent.p = title;
//            pageEvent.pp = PAGE_TRACKING_PRE_PAGE;
//            pageEvent.send();
//            PAGE_TRACKING_PRE_PAGE = title;
        }
    }

    /**
     * 程序杀死的时候,保存到数据库
     */
    @Override
    public void onKillProcess() {
        terminateEvent();
        MessageUtils.insertEventByMsg();
    }

    @Override
    public void saveCacheEvent() {
        MessageUtils.insertEventByMsg();
    }

    @Override
    public void setUserProfile(UserProfile userProfile) {
        //提前初始化uid
        spHelper.setUid(userProfile);
        spHelper.setProfile(userProfile);
    }

    @Override
    public void setUserProfile(String userId) {
        setUserProfile(new UserProfile(userId));
    }

    @Override
    public void cancelUserProfile() {
        EventsProxy.create().onSend();  //退出登录上传数据
        spHelper.setProfile(null);
    }

    private void pause(final Context context, String pageTitle) {
        //初始化MWConfiguration
        MConfiguration.get().initContext(context);
        if (!spHelper.isPageWithFragment()) {
            if (Preconditions.isBlank(pageTitle)) {
                pageTitle = context.getClass().getName();
            }
            onPageEnd(pageTitle);
        }

        android.os.Handler handler = new Util.MyHandler(context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAppExit()) {
                    onTerminate();
                }
            }
        }, Constant.BACK_GROUND_TIME);
    }

    @Override
    public void event(String id) {
        event(id, null);
    }


    @Override
    public void event(String id, Map<String, String> properties) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(SPHelper.create().getUserId())) return;
        CustomEvent custom = createEvent(id, properties);
        custom.send();
    }

    @Override
    public void eventRealTime(String id) {
        eventRealTime(id, null);
    }

    @Override
    public void eventRealTime(String id, Map<String, String> properties) {
        if (TextUtils.isEmpty(id)) return;
        CustomEvent custom = createEvent(id, properties);
        custom.sendRealTime();
    }

    private CustomEvent createEvent(String id, Map<String, String> properties) {
        if (properties == null) properties = new HashMap<>();
        properties.put(Constant.SP_USER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        CustomEvent custom = new CustomEvent();
        custom.action_params = properties;
        custom.action = id;
        return custom;
    }

    @Override
    public void eventStart(String id) {
        timeMap.put(getCustomTimeKey(Constant.CUSTOM_START_TIME, id), Util.getCurrentTimeSecondStr());
    }

    @Override
    public void eventEnd(String id, Map<String, String> properties) {
        if (Preconditions.isNotBlanks(id, properties)) {
            if (Preconditions.isNotBlank(getCustomTimeKey(Constant.CUSTOM_START_TIME, id))) {
                CustomEvent custom = new CustomEvent();
                properties.put(Constant.CUSTOM_KEY, id);
                custom.action_params = properties;
                custom.send();
                timeMap.remove(getCustomTimeKey(Constant.CUSTOM_START_TIME, properties.get(Constant.CUSTOM_ID)));
            }
        }
    }

    private void resume(Context context, String pageTitle) {
        //初始化MWConfiguration
        MConfiguration.get().initContext(context);
        onStart();
        if (!spHelper.isPageWithFragment()) {
            if (Preconditions.isBlank(pageTitle)) {
                pageTitle = context.getClass().getName();
            }
            onPageStart(pageTitle);
        }
    }

    private void onStart() {
        if (Preconditions.isBlank(timeMap.get(Constant.APP_LAUNCH_START_TIME))) {
            timeMap.put(Constant.APP_LAUNCH_START_TIME, Util.getCurrentTimeSecondStr());
        }
    }

    private String getCustomTimeKey(String partOne, String partTwo) {
        return partOne + partTwo;
    }

    private boolean isAppExit() {
        if (MConfiguration.get().getContext() == null) {
            return true;
        }

        String packageName = MConfiguration.get().getContext().getPackageName();
        if (Util.checkPermission(MConfiguration.get().getContext(), "android.permission.GET_TASKS")) {
            ActivityManager am = (ActivityManager) MConfiguration.get().getContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null && Preconditions.isNotBlank(am.getRunningTasks(1)) && am.getRunningTasks(1).get(0) != null && am.getRunningTasks(1).get(0).topActivity != null) {
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                return !packageName.equals(cn.getPackageName());
            } else {
                return !isAppOnForeground();
            }
        } else {
            return !isAppOnForeground();
        }
    }

    private boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device
        String packageName = MConfiguration.get().getContext().getPackageName();
        ActivityManager activityManager = (ActivityManager) MConfiguration.get().getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (Preconditions.isBlank(appProcesses)) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * app进入后台的时候上传
     */
    private void onTerminate() {
        terminateEvent();
        EventsProxy.create().onSend();
    }

    public synchronized void terminateEvent() {
//        if (Preconditions.isNotBlank(timeMap.get(Constant.APP_LAUNCH_START_TIME))) {
//            AppLaunchEvent event = new AppLaunchEvent();
//            event.st = timeMap.get(Constant.APP_LAUNCH_START_TIME);
//            timeMap.remove(Constant.APP_LAUNCH_START_TIME);
//            event.et = Util.getCurrentTimeSecondStr();
//            event.save();
//        }
    }

    @TargetApi(14)
    private class MWLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            resume(activity, null);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            pause(activity, null);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
