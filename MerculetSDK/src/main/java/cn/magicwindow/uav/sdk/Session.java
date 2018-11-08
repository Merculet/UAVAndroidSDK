package cn.magicwindow.uav.sdk;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;

import cn.magicwindow.uav.sdk.util.DeviceInfoUtils;

/**
 * Created by Tony Shen on 16/4/18.
 */
public class Session {
    /**
     * Activity内的onResume()内调用
     *
     * @param context 上下文
     */
    public static void onResume(Context context) {
        TrackAgent.currentEvent().onResume(context);
    }

    /**
     * Activity内的onResume()内调用
     *
     * @param context   上下文
     * @param pageTitle 此页的标题
     */
    public static void onResume(Context context, String pageTitle) {
        TrackAgent.currentEvent().onResume(context, pageTitle);
    }

    /**
     * Activity内的onPause()内调用
     *
     * @param context 上下文
     */
    public static void onPause(Context context) {
        TrackAgent.currentEvent().onPause(context);
        //断网缓存数据，防止被丢
//        if (!DeviceInfoUtils.isNetworkAvailable(context)) {
//            TrackAgent.currentEvent().saveCacheEvent();
//        }
    }

    /**
     * Activity内的onPause()内调用
     *
     * @param context   上下文
     * @param pageTitle 此页的标题
     */
    public static void onPause(Context context, String pageTitle) {
        TrackAgent.currentEvent().onPause(context, pageTitle);
    }

    /**
     * 页面开始时调用，当有Fragment作为单独页面时使用。在Activity的onResume()和Fragment的setUserVisibleHint()内调用
     * <pre>
     * <code>
     * public void setUserVisibleHint(boolean isVisibleToUser) {
     *       if (isVisibleToUser) {
     *           Log.e(TAG, "HomeFragment visible");
     *           TrackAgent.currentEvent().onPageStart("主 页");
     *       } else {
     *           Log.e(TAG, "HomeFragment invisible");
     *        TrackAgent.currentEvent().onPageEnd("主 页");
     *      }
     * }
     * </code>
     * </pre>
     *
     * @param title 页面标题
     */
    public static void onPageStart(String title) {
        TrackAgent.currentEvent().onPageStart(title);
    }

    /**
     * 页面结束时调用，当有Fragment作为单独页面时使用。在Activity的onResume()和Fragment的setUserVisibleHint()内调用
     * <pre>
     * <code>
     * public void setUserVisibleHint(boolean isVisibleToUser) {
     *       if (isVisibleToUser) {
     *          Log.e(TAG, "HomeFragment visible");
     *          TrackAgent.currentEvent().onPageStart("主 页");
     *       } else {
     *          Log.e(TAG, "HomeFragment invisible");
     *          TrackAgent.currentEvent().onPageEnd("主 页");
     *       }
     * }
     * </code>
     * </pre>
     *
     * @param title 页面标题
     */
    public static void onPageEnd(String title) {
        TrackAgent.currentEvent().onPageEnd(title);
    }

    @TargetApi(14)
    public static void setAutoSession(Application application) {
        TrackAgent.currentEvent().init(application);
    }

    /**
     * 调用 Process.kill  或者 System.exit  之类的方法杀死进程，请务必在此之前调用此方法，用来保存统计数据。
     */
    public static void onKillProcess() {
        TrackAgent.currentEvent().onKillProcess();
    }
}
