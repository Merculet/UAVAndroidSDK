package io.merculet.config;

import io.merculet.util.SPHelper;

/**
 * @author aaron
 * @date 14/11/5
 */
public class APIConstant {

    public static final String CHINA_TRACK_HOST = "";
    public static final String NON_CHINA_TRACK_HOST = "";


    public static String getTrackingUrl() {

        if (SPHelper.create().getChinaEnable()) {

            return APIConstant.CHINA_TRACK_HOST + "/api/v1/behaviour/sdkTracking";
        } else {

            return APIConstant.NON_CHINA_TRACK_HOST + "/api/v1/behaviour/sdkTracking";
        }
    }
}
