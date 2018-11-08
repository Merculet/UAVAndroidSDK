package cn.magicwindow.uav.sdk;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import cn.magicwindow.uav.sdk.domain.event.EventsProxy;
import cn.magicwindow.uav.sdk.domain.response.HttpResponse;
import cn.magicwindow.uav.sdk.log.DebugLog;
import cn.magicwindow.uav.sdk.reflect.Reflect;
import cn.magicwindow.uav.sdk.util.SPHelper;

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
        if (!SPHelper.create().getToken().isEmpty()) {
            EventsProxy.create().sendDeviceInfo(new RealTimeCallback() {
                @Override
                public void onSuccess() {
                    DebugLog.d("设备信息----->上传成功");
                }

                @Override
                public void onFailed(HttpResponse httpResponse) {
                    DebugLog.d(httpResponse.message);
                }
            });
        } else {
            DebugLog.e("token is empty");
        }
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

    public MConfiguration setToken(String token, boolean isEffective) {
        SPHelper.create().setToken(token);
        if (isEffective) {
            EventsProxy.create().sendDeviceInfo(new RealTimeCallback() {
                @Override
                public void onSuccess() {
                    DebugLog.d("设备信息----->上传成功");
                }

                @Override
                public void onFailed(HttpResponse httpResponse) {
                    DebugLog.d(httpResponse.message);
                }
            });
        }
        return this;
    }

    public MConfiguration setUserId(String userId) {
        SPHelper.create().setUserId(userId);
        return this;
    }
}
