package io.merculet.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.magicwindow.uav.sdk.config.Constant;


public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constant.ACTION_TOKEN_INVALID.equals(intent.getAction())) {
            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }
}
