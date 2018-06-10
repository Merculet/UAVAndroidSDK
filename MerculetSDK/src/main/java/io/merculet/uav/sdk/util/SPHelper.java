package io.merculet.uav.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.config.Constant;
import io.merculet.uav.sdk.domain.UserProfile;

/**
 * Created by aaron on 14/12/25.
 */
public class SPHelper {

    private static final String AUTO_SESSION = "auto_session";
    private static volatile SPHelper defaultInstance;
    private final String SP_KEY_DEFAULT = "persistent_data";
    private final String M_TOKEN = "mw_token";
    private final String SEND_DELAY = "send_delay";
    private final String SEND_BATCH = "send_batch";
    private final String LAST_REPORT_TIME = "last_report_time";
    private final String LAST_REPORT_DB_TIME = "last_report_db_time";
    private final String MW_CHANNEL = "mw_channel";
    private final String PAGE_WITH_FRAGMENT = "page_with_fragment";
    private final String DISK_CACHE_ENABLE = "disk_cache_enable";
    private boolean DebugMode = false;
    private String TAG = "SPHelper";

    public static SPHelper create() {
        if (defaultInstance == null) {
            synchronized (SPHelper.class) {
                if (defaultInstance == null) {
                    defaultInstance = new SPHelper();
                }
            }
        }
        return defaultInstance;
    }

    private SharedPreferences getSP() {
        if (MConfiguration.get().getContext() == null) {
            return null;
        }
        try {
            return MConfiguration.get().getContext().getSharedPreferences(SP_KEY_DEFAULT, Context.MODE_PRIVATE);
        } catch (OutOfMemoryError ignored) {

        }
        return null;
    }

    public void putBoolean(String key, boolean value) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (getSP() != null) {
            value = getSP().getBoolean(key, defaultValue);

        }
        return value;
    }

    public boolean getBoolean(String key) {
        boolean value = false;
        if (getSP() != null) {
            value = getSP().getBoolean(key, false);

        }
        return value;
    }

    public void putInt(String key, int value) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        int value = defaultValue;
        if (getSP() != null) {
            value = getSP().getInt(key, defaultValue);
        }
        return value;
    }

    public int getInt(String key) {
        int value = 0;
        if (getSP() != null) {
            value = getSP().getInt(key, 0);

        }
        return value;
    }

    public void put(String key, String value) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void remove(String key) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.remove(key);
        editor.apply();
    }

    public String get(String key, String defaultValue) {
        String value = defaultValue;
        if (getSP() != null) {
            value = getSP().getString(key, defaultValue);

        }
        return value;
    }

    public String get(String key) {
        String value = "";
        if (getSP() != null) {
            value = getSP().getString(key, "");

        }
        return value;
    }

    public void putString(String key, String value) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Set<String> getSet(String key) {
        Set<String> value = new HashSet<String>();
        if (getSP() != null) {
            value = getSP().getStringSet(key, value);

        }
        return value;
    }

    public Set<String> getSet(String key, Set<String> defaultValue) {
        Set<String> value = defaultValue;
        if (getSP() != null) {
            value = getSP().getStringSet(key, defaultValue);

        }
        return value;
    }

    public void putSet(String key, Set<String> set) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public void addSet(String key, String setValue) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();

        Set<String> set = getSP().getStringSet(key, new HashSet<String>());
        set.add(setValue);
        editor.putStringSet(key, set);
        editor.apply();
    }

    public boolean contains(String key) {
        boolean value = false;
        if (getSP() != null) {
            value = getSP().contains(key);
        }
        return value;
    }

    public String getString(String key, String defaultValue) {
        String value = defaultValue;
        if (getSP() != null) {
            value = getSP().getString(key, defaultValue);

        }
        return value;
    }

    public String getString(String key) {
        String value = "";
        if (getSP() != null) {
            value = getSP().getString(key, "");

        }
        return value;
    }

    public void putLong(String key, Long value) {
        if (getSP() == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public Long getLong(String key) {
        Long value = 0L;
        if (getSP() != null) {
            value = getSP().getLong(key, 0L);

        }
        return value;
    }

    public Long getLong(String key, Long defaultValue) {
        Long value = defaultValue;
        if (getSP() != null) {
            value = getSP().getLong(key, defaultValue);

        }
        return value;
    }

    public boolean isFirstLaunch() {
        boolean isFirst = getBoolean(Constant.SP_FIRST_LAUNCH, true);
        setFirstLaunchOff();
        return isFirst;
    }

    public void setFirstLaunchOff() {
        putBoolean(Constant.SP_FIRST_LAUNCH, false);
    }

    public void setUserId(String userId) {
        putString(Constant.SP_USER_ID, userId);
    }

    /**
     * 数据满多少发送
     *
     * @return int 条数
     */
    public int getSendBatch() {
        int SEND_EVENT_BATCH = 30;
        return getInt(SEND_BATCH, SEND_EVENT_BATCH);
    }

    /**
     * 数据发送间隔
     *
     * @return int 单位 毫秒
     */
    public int getSendDelay() {
        int SEND_EVENT_DELAY_TIME = 60;
        return getInt(SEND_DELAY, SEND_EVENT_DELAY_TIME) * 1000;
    }

    /**
     * 数据上次发送时间
     *
     * @return int 单位 毫秒
     */
    public long getLastSendTime() {
        return getLong(LAST_REPORT_TIME, 0L);
    }

    public void setLastSendTime(long time) {
        putLong(LAST_REPORT_TIME, time);
    }

    /**
     * DB数据上次发送时间
     *
     * @return int 单位 毫秒
     */
    public long getLastSendDb() {
        return getLong(LAST_REPORT_DB_TIME, 0L);
    }

    public void setLastSendDb(long time) {
        putLong(LAST_REPORT_DB_TIME, time);
    }

    public String getChannel() {
        String channelMeta = Util.getMChannel();
        if (TextUtils.isEmpty(channelMeta)) {
            return get(MW_CHANNEL);
        } else {
            return channelMeta;
        }
    }

    public void setChannel(String channel) {
        if (Preconditions.isBlank(channel)) {
            return;
        }
        put(MW_CHANNEL, channel);
    }

    public boolean isPageWithFragment() {
        return getBoolean(PAGE_WITH_FRAGMENT);
    }

    public void setPageWithFragment(boolean have) {
        putBoolean(PAGE_WITH_FRAGMENT, have);
    }

    /**
     * 默认开启
     *
     * @return 是否开启disc cache
     */
    public boolean isDiskCacheEnable() {
        return getBoolean(DISK_CACHE_ENABLE, true);
    }

    public void setDiskCache(boolean enable) {
        putBoolean(DISK_CACHE_ENABLE, enable);
    }

    public boolean getDebugMode() {
        return DebugMode;
    }

    public void setLogEnable(boolean debug) {
        DebugMode = debug;
    }

    public boolean isAutoSession() {
        return Build.VERSION.SDK_INT >= 14 && getBoolean(AUTO_SESSION);
    }

    public void setAutoSession(boolean auto) {
        putBoolean(AUTO_SESSION, auto);
    }

    public String getToken() {
        return getString(M_TOKEN);
    }

    public void setToken(String mw_token) {
        putString(M_TOKEN, mw_token);
    }

    /**
     * 用户相关信息
     **/
    public String getUserId() {
        return getString(Constant.SP_USER_ID);
    }

    public boolean isAuto() {
        return !TextUtils.isEmpty(getToken());
    }

    public void setUid(UserProfile profile) {
        if (profile != null) {
            String uid = StringUtils.md5(profile.profileId + DeviceInfoUtils.getDeviceId(MConfiguration.get().getContext()));
            putString(Constant.SP_USER_MD5, uid);
        } else {
            remove(Constant.SP_USER_MD5);
        }
    }

    public void setProfile(UserProfile profile) {
        if (profile != null) {
            putString(Constant.SP_USER_PROFILE, profile.toString());
            putString(Constant.SP_USER_ID, profile.profileId);
            putString(Constant.SP_USER_PHONE, profile.phone);
            String uid = StringUtils.md5(profile.profileId + DeviceInfoUtils.getDeviceId(MConfiguration.get().getContext()));
            putString(Constant.SP_USER_MD5, uid);
        } else {
            remove(Constant.SP_USER_PROFILE);
            remove(Constant.SP_USER_PHONE);
            remove(Constant.SP_USER_MD5);
            remove(M_TOKEN);
            MConfiguration.get().userId = getUserId();
            remove(Constant.SP_USER_ID);
        }
    }
}
