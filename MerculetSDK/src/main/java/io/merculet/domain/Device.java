package io.merculet.domain;

import io.merculet.MConfiguration;
import io.merculet.util.DeviceInfoUtils;

/**
 * Created by aaron on 15/1/10.
 */
public class Device {

    public String d;      //deviceId
    public String fp;      //fingerPrint
    public String os;       //0,此值代表Android
    public String osv;     //osVersion
    public String b;       //branding
    public String m;       //model
    public String mf;      //manufacturer
    public String c;       //carrier
    public String sr;      //screenResolution
    public String fa;      //该设备首次使用sdk时间
    public String n;      //如果为新设备，则为0，否则为1

    public Device() {
        this.d = DeviceInfoUtils.getDeviceId(MConfiguration.get().getContext());
        this.fp = DeviceInfoUtils.getFingerPrinter(MConfiguration.get().getContext());
        this.os = DeviceInfoUtils.getOs();
        this.osv = DeviceInfoUtils.getOSVersion();
        this.b = DeviceInfoUtils.getBranding();
        this.m = DeviceInfoUtils.getDevice();
        this.mf = DeviceInfoUtils.getManufacturer();
        this.c = DeviceInfoUtils.getCarrier(MConfiguration.get().getContext());
        this.sr = DeviceInfoUtils.getScreenSize(MConfiguration.get().getContext());
        this.fa = DeviceInfoUtils.getFirstTimeAccess();
        this.n = DeviceInfoUtils.getFirstTag();
    }
}
