package io.merculet.uav.sdk;

import io.merculet.uav.sdk.domain.response.HttpResponse;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: io.merculet.uav.sdk.RealTimeCallback.java
 * @author: Tony Shen
 * @date: 2018-08-02 11:28
 */
public interface RealTimeCallback {

    void onSuccess();

    void onFailed(HttpResponse httpResponse);
}
