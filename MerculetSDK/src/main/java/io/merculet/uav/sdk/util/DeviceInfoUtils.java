package io.merculet.uav.sdk.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import io.merculet.uav.sdk.config.Constant;


/**
 * 获得设备信息
 *
 * @author Aaron.Liu
 */
public class DeviceInfoUtils {
    private final static String TAG = "DeviceInfo";
    private static final String OS = "0";   //此值代表Android，不要动
    private static final String FIRST_TAG = "0";   //此值代表首次启动
    private static final String NO_FIRST_TAG = "1";   //此值代表非首次启动
    private static String VERSION_NAME;
    private static int VERSION_CODE;

    /**
     * 填充设备信息
     *
     * @param context context
     */
    public static void initDeviceInfo(final Context context) {
        if (TextUtils.isEmpty(SPHelper.create().getString(Constant.TRACKING_FINGER_PRINTER))) {
            initFingerPrinter(context); //这种回调性质的提前初始化
            setFirstTimeAccess();

        }
    }


    public static String getFingerPrinter(final Context context) {
        if (TextUtils.isEmpty(SPHelper.create().get(Constant.TRACKING_FINGER_PRINTER))) {
            return initFingerPrinter(context);
        } else {
            return SPHelper.create().get(Constant.TRACKING_FINGER_PRINTER);
        }
    }

    /*
    * 此初始化放在UAV SDK内
    * */
    private static String initFingerPrinter(final Context context) {
        StringBuilder fa = new StringBuilder();
        fa.append(getLocal());
        fa.append(getDevice());
        fa.append(getBranding());
        if (!TextUtils.isEmpty(getMacAddress(context))) {
            fa.append(getMacAddress(context));
        } else {
            fa.append(getDeviceId(context));
        }
        fa.append(getManufacturer());
        fa.append(getScreenSize(context));
        fa.append(getCpuInfo());
        fa.append(String.valueOf((Calendar.getInstance().get(Calendar.ZONE_OFFSET) + Calendar.getInstance().get(Calendar.DST_OFFSET)) / 60000));
        String fp = String.valueOf(Math.abs(StringUtils.md5(fa.toString()).hashCode()));
        SPHelper.create().put(Constant.TRACKING_FINGER_PRINTER, fp);
        return fp;
    }

    private static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        BufferedReader localBufferedReader = null;
        try {
            File file = new File(str1);
            if (!file.exists()) {
                return "";
            }

            FileInputStream inputStream = new FileInputStream(file);
            //防止inputStream为空
            if (Preconditions.isBlank(inputStream)) {
                return "";
            }
            localBufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            str2 = localBufferedReader.readLine();
            //防止str2为空
            if (Preconditions.isBlank(str2)) {
                return "";
            }
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            if (Preconditions.isNotBlank(str2)) {
                arrayOfString = str2.split("\\s+");
                if (arrayOfString.length > 1) {
                    cpuInfo[1] += arrayOfString[2];
                }
            }

            localBufferedReader.close();
        } catch (IOException ignored) {
        } finally {
            IOUtils.closeQuietly(localBufferedReader);
        }
        return Arrays.toString(cpuInfo);
    }


    public static String getOs() {
        return OS;
    }

    public static String getFirstTimeAccess() {
        return SPHelper.create().get(Constant.TRACKING_FIRST_TIME_ACCESS, Util
                .getCurrentTimeSecondStr());
    }

    private static void setFirstTimeAccess() {
        if (TextUtils.isEmpty(SPHelper.create().get(Constant.TRACKING_FIRST_TIME_ACCESS))) {
            SPHelper.create().put(Constant.TRACKING_FIRST_TIME_ACCESS, Util
                    .getCurrentTimeSecondStr());
        }
    }

    public static String getFirstTag() {

        return Preconditions.isBlank(SPHelper.create().get(Constant.TRACKING_FIRST_TIME_TAG)) ?
                FIRST_TAG : NO_FIRST_TAG;
    }

    public static void setFirstTag() {
        if (TextUtils.isEmpty(SPHelper.create().get(Constant.TRACKING_FIRST_TIME_TAG))) {
            SPHelper.create().put(Constant.TRACKING_FIRST_TIME_TAG, NO_FIRST_TAG);
        }
    }

    //    @SuppressLint("MissingPermission")
    public static String getNetGeneration(Context context) {
        if (!Util.checkPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
            return "";
        }
        String result = Constant.NO_NETWORK;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (connectivityManager != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() ==
                    NetworkInfo.State.CONNECTED) {
                result = Constant.NETWORK_WIFI;
            }
            if (networkInfo != null && networkInfo.isAvailable()) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    switch (telephonyManager.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            result = Constant.NETWORK_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            result = Constant.NETWORK_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            result = Constant.NETWORK_4G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            result = Constant.NETWORK_4G;
                            break;
                        default:
                            result = Constant.NETWORK_2G;
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return result;
    }

    /**
     * 获得Android设备唯一标识：Device_id
     *
     * @param context context
     * @return device id
     */
    @SuppressLint({"HardwareIds", "PrivateApi"})
    public static String getDeviceId(Context context) {
        //优化device id的策略，尽量减少漂移
        if (!TextUtils.isEmpty(SPHelper.create().getString(Constant.TRACKING_DEVICE_ID))) {
            return SPHelper.create().getString(Constant.TRACKING_DEVICE_ID);

        }
        if (context == null) {
            return "";
        }
        String result = "";
        try {
            if (Util.checkPermission(context, "android.permission.READ_PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                        .TELEPHONY_SERVICE);
//                获得设备IMEI标识
                String deviceId = null;
                if (tm != null) {
                    deviceId = tm.getDeviceId();
                }
                String backId = "";
                if (deviceId != null && !deviceId.contains("00000000000")) {
                    backId = deviceId;
                    backId = backId.replace("0", "");
                }

                if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(backId)) {
                    try {
                        Class c = Class.forName("android.os.SystemProperties");
                        Method get = c.getMethod("get", String.class, String.class);
                        deviceId = (String) get.invoke(c, "ro.serialno", "unknown");
                    } catch (Exception t) {
                        deviceId = null;
                    }
                }

                if (Preconditions.isNotBlank(deviceId)) {
                    result = deviceId;
                } else {
                    result = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                }
            } else {
                result = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            }
        } catch (Exception ignored) {

        }
        SPHelper.create().putString(Constant.TRACKING_DEVICE_ID, result);
        return result;
    }

    /**
     * 获得系统版本
     *
     * @return os version
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备品牌
     *
     * @return branding
     */
    public static String getBranding() {
        return Build.BRAND;
    }

    /**
     * 获得制造商
     *
     * @return manufacturer
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 设备的名字
     *
     * @return device model
     */
    public static String getDevice() {
        return Build.MODEL;
    }

    //这个可获取到类似1080*1920
    public static String getScreenSize(Context context) {

        String result = "";
        try {
            //first method
            if (Build.VERSION.SDK_INT >= 13 && Build.VERSION.SDK_INT < 17) {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context
                        .WINDOW_SERVICE);
                Display display = null;
                if (windowManager != null) {
                    display = windowManager.getDefaultDisplay();
                }
                Point size = new Point();
                if (display != null) {
                    display.getSize(size);
                }

                int screenWidth = size.x;
                int screenHeight = size.y;
                if (context.getResources().getConfiguration().orientation == Configuration
                        .ORIENTATION_PORTRAIT) {
                    result = screenWidth + "x" + screenHeight;
                } else {
                    result = screenHeight + "x" + screenWidth;
                }
            } else if (Build.VERSION.SDK_INT >= 17) {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context
                        .WINDOW_SERVICE);
                Display display = null;
                if (windowManager != null) {
                    display = windowManager.getDefaultDisplay();
                }
                Point size = new Point();

                if (display != null) {
                    display.getRealSize(size);
                }

                int screenWidth = size.x;
                int screenHeight = size.y;
                if (context.getResources().getConfiguration().orientation == Configuration
                        .ORIENTATION_PORTRAIT) {
                    result = screenWidth + "x" + screenHeight;
                } else {
                    result = screenHeight + "x" + screenWidth;
                }
            } else {
                DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
                // 竖屏
                if (context.getResources().getConfiguration().orientation == Configuration
                        .ORIENTATION_PORTRAIT) {
                    result = dm2.widthPixels + "x" + dm2.heightPixels;
                } else {// 横屏
                    result = dm2.heightPixels + "x" + dm2.widthPixels;
                }
            }
        } catch (Exception ignored) {

        }
        return result;
    }

    /**
     * 获得当前应用的版本号
     *
     * @param context context
     * @return App Version
     */
    public synchronized static String getAppVersionName(Context context) {
        if (!TextUtils.isEmpty(VERSION_NAME)) {
            return VERSION_NAME;
        }

        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException ignored) {
        }
        if (info != null) {
            VERSION_NAME = info.versionName;
            VERSION_CODE = info.versionCode;
        }
        return VERSION_NAME;
    }

    public synchronized static int getAppVersionCode(Context context) {
        if (VERSION_CODE != 0) {
            return VERSION_CODE;
        }
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException ignored) {
        }
        if (info != null) {
            VERSION_CODE = info.versionCode;
            VERSION_NAME = info.versionName;
        }
        return VERSION_CODE;
    }

   /* @SuppressLint("HardwareIds")
    public static String getIMSI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);
        String result = "";

        if (Util.checkPermission(context, "android.permission.READ_PHONE_STATE")) {
            result = manager.getSubscriberId();
        }
        return result;
    }*/

    /**
     * 获得注册运营商的名字
     *
     * @param context context
     * @return Carrier
     */
//    @SuppressLint({"HardwareIds", "MissingPermission"})
    @SuppressLint("HardwareIds")
    public static String getCarrier(Context context) {
        String result = "";
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            if (!Util.checkPermission(context, "android.permission.READ_PHONE_STATE")) {
                result = carryByOperator(manager);
            } else {
                String imsi = "";
                if (manager != null) {
                    imsi = manager.getSubscriberId();
                }

                if (TextUtils.isEmpty(imsi)) {
                    result = carryByOperator(manager);
                }

                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                    //中国移动
                    result = Constant.CHINA_MOBILE;
                } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                    //中国联通
                    result = Constant.CHINA_UNICOM;
                } else if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
                    //中国电信
                    result = Constant.CHINA_TELECOM;
                } else if (imsi.startsWith("46020")) {
                    result = Constant.CHINA_TIETONG;
                } else {
                    result = carryByOperator(manager);
                }
            }
        } catch (Exception ignored) {

        }
        return result;

    }

    private static String carryByOperator(TelephonyManager manager) {
        String operatorString = manager.getSimOperator();

        if ("46000".equals(operatorString) || "46002".equals(operatorString) || "46007".equals
                (operatorString)) {
            //中国移动
            return Constant.CHINA_MOBILE;
        } else if ("46001".equals(operatorString) || "46006".equals(operatorString)) {
            //中国联通
            return Constant.CHINA_UNICOM;
        } else if ("46003".equals(operatorString) || "46005".equals(operatorString)) {
            //中国电信
            return Constant.CHINA_TELECOM;
        } else if ("46020".equals(operatorString)) {
            return Constant.CHINA_TIETONG;
        } else {
            return Constant.CHINA_CARRIER_UNKNOWN;
        }
    }


    /**
     * 获得本地语言和国家
     *
     * @return Language +county
     */
    public static String getLocal() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 获得网管硬件地址
     *
     * @param context context
     * @return Mac Address
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress(Context context) {
        if (!Util.checkPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
            return "";
        }
        String result = "";

        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    result = wifiInfo.getMacAddress();
                }
            }
        } catch (Exception ignored) {

        }

        // Log.i("MAC Address", "macAdd:" + result);
        return result;
    }
}
