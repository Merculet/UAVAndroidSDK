package io.merculet;

import android.app.Application;
import android.content.Intent;

/**
 * @Description
 * @Author luxiao418
 * @Email luxiao418@pingan.com.cn
 * @Date 09/03/2018 9:31 PM
 * @Version
 */
public class MApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //启动页初始化
        MConfiguration.init(this)
                .setLogEnable(true)
                .setPageTrackWithFragment(true)
                .setChinaEnable(true)
                .setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NTQ5MDU4MmVkZTg0MTdiODNiZTdiOTc5NTI0NGNiMyIsImlhdCI6MTUyNTk1NDYzMCwiZXhwIjoxNTI2MTI3NDMwLCJhcHAiOiIwYmI5YjlkNDA3MmQ0N2JhOGViOGFmN2M4YzQ3NmRkMyIsImV4dGVybmFsX3VzZXJfaWQiOiJ6aG91dGFvMSJ9.EGogLjHPkIvKrIUdl8iOVbsP4xtN7cc0bGmtdOOhVEU");

        Intent intent = new Intent(this, CheckExitService.class);
        getApplicationContext().startService(intent);
    }
}
