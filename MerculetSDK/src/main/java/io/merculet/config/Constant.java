/**
 *
 */
package io.merculet.config;

/**
 * app所用到的常量
 *
 * @author Aaron.Liu
 */
public final class Constant {

    public static final String VERSION = "1.0.0";    //uav sdk version

    /**
     * http请求响应 *
     */
    public final static String CUSTOM_KEY = "action";

    public final static String CHINA_CARRIER_UNKNOWN = "0";
    public final static String CHINA_MOBILE = "1";
    public final static String CHINA_UNICOM = "2";
    public final static String CHINA_TELECOM = "3";
    public final static String CHINA_TIETONG = "4";

    public final static String NETWORK_WIFI = "0";
    public final static String NETWORK_2G = "1";
    public final static String NETWORK_3G = "2";
    public final static String NETWORK_4G = "3";
    public final static String NO_NETWORK = "-1";

    /**
     * ******key    start***********
     */
    //SharedPreferences Key
    public final static String M_CHANNEL = "M_CHANNEL";
    /**
     * ***********value  end**************
     */

    public static final String TRACKING_FINGER_PRINTER = "fp";         //finger printer
    public static final String TRACKING_DEVICE_ID = "device_id";         //device_id
    public static final String TRACKING_FIRST_TIME_ACCESS = "fa";//该设备首次使用sdk时间
    public static final String TRACKING_FIRST_TIME_TAG = "first_time_tag";//该设备首次使用sdk标志位

    public final static String PAGE_TRACKING_START_TIME = "pageTrackingStartTime";
    public final static String CUSTOM_START_TIME = "actionCustomStartTime";
    public final static String CUSTOM_ID = "mw_customId";
    public final static String APP_LAUNCH_START_TIME = "app_launch_start_time";

    public final static String SP_FIRST_LAUNCH = "sp_first_launch";
    public final static String SP_USER_ID = "sp_user_id";
    public final static String SP_USER_PHONE = "sp_user_phone";
    public final static String SP_USER_MD5 = "sp_user_md5";
    public final static String SP_USER_PROFILE = "sp_profile";
    public final static String SP_USER_TIMESTAMP = "sp_timestamp";


    public final static int BACK_GROUND_TIME = 600;//魅族mx5准确值是370,三星s7是550.
}
