package cn.magicwindow.uav.sdk.util;

import java.util.HashMap;

/**
 * 只存时间戳的hashmap
 * Created by aaron on 15/5/11.
 */
public class TimeMap extends HashMap<String, String> {

    private static final long serialVersionUID = 1629059862316753140L;

    @Override
    public String put(String key, String value) {

        return Util.isTimeStamp(value) ? super.put(key, value) : "";
    }
}
