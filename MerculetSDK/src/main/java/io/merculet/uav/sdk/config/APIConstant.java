package io.merculet.uav.sdk.config;

import io.merculet.uav.sdk.BuildConfig;

/**
 * @author aaron
 * @date 14/11/5
 */
public class APIConstant {

    private static final String CHINA_TRACK_HOST = "https://openapi.magicwindow.cn";
    private static final String NON_CHINA_TRACK_HOST = "https://openapi.merculet.io";
    private static final String TRACK_URL = "/v1/event/sdkBehaviour";

    public static String getTrackingUrl() {
        if (BuildConfig.CHINA_ENABLE) {
            return APIConstant.CHINA_TRACK_HOST + TRACK_URL;
        } else {
            return APIConstant.NON_CHINA_TRACK_HOST + TRACK_URL;
        }
    }
}
