package cn.magicwindow.uav.sdk.config;

import cn.magicwindow.uav.sdk.BuildConfig;

/**
 * @author aaron
 * @date 14/11/5
 */
public class APIConstant {
    //正式
    private static final String CHINA_TRACK_HOST = "https://openapi.merculet.cn";
    private static final String NON_CHINA_TRACK_HOST = "https://openapi.merculet.io";
    private static final String CHINA_TRACK_DEVICE_INFO_URL = "https://openapi.merculet.cn/v1/user/device";
    private static final String NON_CHINA_TRACK_DEVICE_INFO_URL = "https://openapi.merculet.io/v1/user/device";

    //测试
    private static final String CHINA_TRACK_HOST_TEST = "http://behaviour-tracking-cn.liaoyantech.cn"; //测试环境
    private static final String NON_CHINA_TRACK_HOST_TEST = "http://behaviour-tracking.liaoyantech.cn";
    private static final String CHINA_TRACK_DEVICE_INFO_URL_TEST = "http://score-query-cn.liaoyantech.cn/v1/user/device";
    private static final String NON_CHINA_TRACK_DEVICE_INFO_URL_TEST = "http://score-query.liaoyantech.cn/v1/user/device";

    private static final String TRACK_URL = "/v1/event/sdkBehaviour";


    public static String getTrackingUrl() {
        if (BuildConfig.IS_DEBUG) {
            if (BuildConfig.CHINA_ENABLE) {
                return APIConstant.CHINA_TRACK_HOST_TEST + TRACK_URL;
            } else {
                return APIConstant.NON_CHINA_TRACK_HOST_TEST + TRACK_URL;
            }
        } else {
            if (BuildConfig.CHINA_ENABLE) {
                return APIConstant.CHINA_TRACK_HOST + TRACK_URL;
            } else {
                return APIConstant.NON_CHINA_TRACK_HOST + TRACK_URL;
            }
        }

    }

    public static String getUploadDeviceInfoUrl() {
        if (BuildConfig.IS_DEBUG) {
            if (BuildConfig.CHINA_ENABLE) {
                return APIConstant.CHINA_TRACK_DEVICE_INFO_URL_TEST;
            } else {
                return APIConstant.NON_CHINA_TRACK_DEVICE_INFO_URL_TEST;
            }
        } else {
            if (BuildConfig.CHINA_ENABLE) {
                return APIConstant.CHINA_TRACK_DEVICE_INFO_URL;
            } else {
                return APIConstant.NON_CHINA_TRACK_DEVICE_INFO_URL;
            }
        }
    }
}
