package cn.magicwindow.uav.sdk.domain;

import cn.magicwindow.uav.sdk.util.DeviceInfoUtils;

public class Os {
    public String os;

    public Os() {
        this.os = DeviceInfoUtils.getOs();
    }
}
