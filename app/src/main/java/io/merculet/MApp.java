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
                .setPageTrackWithFragment(true);

        Intent intent = new Intent(this, CheckExitService.class);
        getApplicationContext().startService(intent);
    }
}
