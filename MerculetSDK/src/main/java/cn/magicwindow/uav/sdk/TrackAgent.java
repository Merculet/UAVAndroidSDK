//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.magicwindow.uav.sdk;

import android.app.Application;
import android.content.Context;

import java.util.Map;

import cn.magicwindow.uav.sdk.domain.UserProfile;
import cn.magicwindow.uav.sdk.manager.TrackerManager;

public class TrackAgent {

    private static TrackerInterface tracker;

    /**
     * 创建 TrackAgent 单例
     *
     * @return TrackAgent的单例
     */
    public synchronized static TrackerInterface currentEvent() {
        if (tracker == null) {
            tracker = new TrackerManager();
        }
        return tracker;
    }

    public interface TrackerInterface {

        void init(Application application);

        void onResume(Context context);

        void onResume(Context context, String pageTitle);

        void  onPause(Context context);

        void onPause(Context context, String pageTitle);

        void onPageStart(String title);

        void onPageEnd(String title);

        void event(String id, Map<String, String> properties);

        void event(String id);

        void eventRealTime(String id);

        void eventRealTime(String id, Map<String, String> properties);

        void eventStart(String id);

        void eventEnd(String id, Map<String, String> properties);

        void onKillProcess();

        void saveCacheEvent();

        void setUserProfile(UserProfile UserProfile);

        void setUserProfile(String UserProfile);

        void cancelUserProfile();

    }
}
