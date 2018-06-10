package io.merculet.uav.sdk;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import io.merculet.uav.sdk.reflect.Reflect;
import io.merculet.uav.sdk.util.SPHelper;

public class MConfiguration {
    private volatile static Context context;
    public static volatile boolean isFirst = true;
    public String userId;

    private MConfiguration() {
    }

    public static MConfiguration get() {
        return MConfiguration.Holder.CONFIGURATION;
    }

    private static class Holder {
        private static final MConfiguration CONFIGURATION = new MConfiguration();
    }

    /**
     * 初始化sdk
     */
    public synchronized MConfiguration init(Application context) {
        initContext(context);
        Session.setAutoSession(context);
        return this;
    }

    private synchronized void initDefaultValue(Context context) {
        if (context != null) {
            MConfiguration.context = context.getApplicationContext();
        } else if (Build.VERSION.SDK_INT >= 14) {
            MConfiguration.context = Reflect.on("android.app.ActivityThread").call("currentApplication").get();
        }
        SPHelper spHelper = SPHelper.create();
        spHelper.setLogEnable(true);
        spHelper.setPageWithFragment(false);
    }

    public void initContext(Context context) {
        if (context != null && MConfiguration.context == null) {
            initDefaultValue(context);
        }
    }

    public Context getContext() {
        if (context == null && Build.VERSION.SDK_INT >= 14) {
            context = Reflect.on("android.app.ActivityThread").call("currentApplication").get();
        }
        return context;
    }

    public MConfiguration setLogEnable(boolean enable) {
        SPHelper.create().setLogEnable(enable);
        return this;
    }

    public MConfiguration setChannel(String channel) {
        SPHelper.create().setChannel(channel);
        return this;
    }

    public MConfiguration setPageTrackWithFragment(boolean have) {
        SPHelper.create().setPageWithFragment(have);
        return this;
    }

    public MConfiguration diskCache(boolean enable) {
        SPHelper.create().setDiskCache(enable);
        return this;
    }

    public MConfiguration setToken(String token) {
        SPHelper.create().setToken(token);
        return this;
    }

    public MConfiguration setUserId(String userId) {
        SPHelper.create().setUserId(userId);
        return this;
    }
}
