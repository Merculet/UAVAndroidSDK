package cn.magicwindow.uav.sdk;

import cn.magicwindow.uav.sdk.domain.response.HttpResponse;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: cn.magicwindow.uav.sdk.RealTimeCallback.java
 * @author: Tony Shen
 * @date: 2018-08-02 11:28
 */
public interface RealTimeCallback {

    void onSuccess();

    void onFailed(HttpResponse httpResponse);
}
