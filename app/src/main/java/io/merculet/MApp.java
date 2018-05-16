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
                .setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NTQ5MDU4MmVkZTg0MTdiODNiZTdiOTc5NTI0NGNiMyIsImlhdCI6MTUyNjQ2MTMyNywiZXhwIjoxNTI2NjM0MTI3LCJhcHAiOiIwYmI5YjlkNDA3MmQ0N2JhOGViOGFmN2M4YzQ3NmRkMyIsImV4dGVybmFsX3VzZXJfaWQiOiJ6aG91dGFvMSIsImFwcF9sb2dpbl91cmwiOiJodHRwczovL3Byb3RlZ2UuenhpbnNpZ2h0LmNvbS9qb2ludC1sb2dpbiJ9.knM8cFoTEkPcdoixrP5R-7eNe75laVG_GAMaywqMwOg");

        Intent intent = new Intent(this, CheckExitService.class);
        getApplicationContext().startService(intent);
    }
}
