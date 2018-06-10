package io.merculet.uav.sdk.util;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: io.merculet.uav.sdk.util.SignUtils.java
 * @author: Tony Shen
 * @date: 2018-05-07 21:11
 */
public class SignUtils {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("merculet");
    }

    public static native String generateSign(String oldString);
}
