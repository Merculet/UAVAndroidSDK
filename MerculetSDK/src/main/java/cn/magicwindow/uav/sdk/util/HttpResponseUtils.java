package cn.magicwindow.uav.sdk.util;


import cn.magicwindow.uav.sdk.domain.response.HttpResponse;

/**
 * Created by Tony Shen on 15/9/28.
 */
public class HttpResponseUtils {

    public static boolean isOK(HttpResponse httpResponse) {
        return httpResponse != null && httpResponse.isOkStatus();
    }
}
