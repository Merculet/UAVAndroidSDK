package io.merculet;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import io.merculet.example.BuildConfig;
import io.merculet.example.R;
import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.RealTimeCallback;
import io.merculet.uav.sdk.TrackAgent;
import io.merculet.uav.sdk.config.Constant;
import io.merculet.uav.sdk.domain.response.HttpResponse;
import io.merculet.uav.sdk.util.SPHelper;

import static io.merculet.RequestUtil.initToken;

public class MainActivity extends BaseActivity {

    private MyBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(BuildConfig.CHINA_ENABLE ? "国内版" : "国际版");
    }

    private void initEvent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_TOKEN_INVALID);
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, filter);
    }

    public void register(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("invitationCode", "2098");
            TrackAgent.currentEvent().event("register", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void charge(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("amount", "100");
            TrackAgent.currentEvent().event("charge", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(View view) {
        if (!SPHelper.create().isAuto()) {
            initToken();    //每次登录切换token
        } else {
            Toast.makeText(this, "has sign in", Toast.LENGTH_SHORT).show();
        }
    }

    public void signOut(View view) {
        TrackAgent.currentEvent().cancelUserProfile();
    }

    public void otherUser(View view) {
        MConfiguration.get().setUserId(getString(R.string.other_user_id));
    }

    public void defaultUser(View view) {
        MConfiguration.get().setUserId(getString(R.string.default_user_id));
    }

    public void invalidToken(View view) {
        MConfiguration.get().setToken(getString(R.string.invalid_token));
    }

    public void realTime(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("realTime", "实时事件");
            TrackAgent.currentEvent().eventRealTime("realTime", properties, new RealTimeCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(HttpResponse httpResponse) {
                    Toast.makeText(MainActivity.this, httpResponse.message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
