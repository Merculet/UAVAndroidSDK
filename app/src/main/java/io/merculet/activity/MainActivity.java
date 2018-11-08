package io.merculet.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.magicwindow.uav.sdk.MConfiguration;
import cn.magicwindow.uav.sdk.RealTimeCallback;
import cn.magicwindow.uav.sdk.TrackAgent;
import cn.magicwindow.uav.sdk.config.Constant;
import cn.magicwindow.uav.sdk.domain.response.HttpResponse;
import cn.magicwindow.uav.sdk.http.ResponseListener;
import cn.magicwindow.uav.sdk.util.SPHelper;
import io.merculet.config.MyBroadcastReceiver;
import io.merculet.config.RequestUtil;
import io.merculet.domain.EventDataBean;
import io.merculet.domain.EventHistoryBean;
import io.merculet.domain.TokenRes;
import io.merculet.example.BuildConfig;
import io.merculet.example.R;

import static io.merculet.config.RequestUtil.initToken;


public class MainActivity extends BaseActivity {

    private MyBroadcastReceiver receiver;
    private CheckBox cb;
    private EditText key1, key2, key3;
    private EditText value1, value2, value3;
    private EditText event_id, userId;
    private EditText appKey, accountKey, accountSecret;
    private EventDataBean datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        datas = new EventDataBean();
        datas.datas = new ArrayList<>();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(BuildConfig.CHINA_ENABLE ? "国内版" : "国际版");
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        ll.setFocusable(true);
        ll.requestFocus();
        cb = (CheckBox) findViewById(R.id.cb);
        cb.setSelected(false);
        key1 = (EditText) findViewById(R.id.key1);
        key2 = (EditText) findViewById(R.id.key2);
        key3 = (EditText) findViewById(R.id.key3);
        value1 = (EditText) findViewById(R.id.value1);
        value2 = (EditText) findViewById(R.id.value2);
        value3 = (EditText) findViewById(R.id.value3);
        event_id = (EditText) findViewById(R.id.event_id);
        userId = (EditText) findViewById(R.id.et_uid);
        appKey = (EditText) findViewById(R.id.appkey);
        accountKey = (EditText) findViewById(R.id.accountKey);
        accountSecret = (EditText) findViewById(R.id.accountSecret);
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
            sendEvent("register", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void charge(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("amount", "100");
            sendEvent("charge", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadData(View view) {
        String id = event_id.getText().toString().trim();
        String args1 = key1.getText().toString().trim();
        String args2 = key2.getText().toString().trim();
        String args3 = key3.getText().toString().trim();
        String data1 = value1.getText().toString().trim();
        String data2 = value2.getText().toString().trim();
        String data3 = value3.getText().toString().trim();
//        if (args1.isEmpty() && args2.isEmpty() && args3.isEmpty()) {
//            Toast.makeText(this, "键不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (id.isEmpty()) {
            Toast.makeText(this, "事件id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<>();
            if (!args1.isEmpty()) {
                properties.put(args1, data1);
            }
            if (!args2.isEmpty()) {
                properties.put(args2, data2);
            }
            if (!args3.isEmpty()) {
                properties.put(args3, data3);
            }

            sendEvent(id, properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(View view) {
        String app_key = appKey.getText().toString().trim();
        String account_key = accountKey.getText().toString().trim();
        String account_secret = accountSecret.getText().toString().trim();
        String uid = userId.getText().toString().trim();

        if (app_key.isEmpty() || account_secret.isEmpty() || account_key.isEmpty()) {
            Toast.makeText(this, "缺少信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uid.isEmpty()) {
            Toast.makeText(this, "用户id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!SPHelper.create().isAuto()) {
            RequestUtil.user_id = uid;
            initToken(account_key, app_key,
                    "", account_secret,
                    new ResponseListener<TokenRes>() {
                        @Override
                        public void onSuccess(TokenRes content) {
                            TrackAgent.currentEvent().event("sign in");
                        }

                        @Override
                        public void onFail(Exception error) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });    //每次登录切换token
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
        MConfiguration.get().setToken(getString(R.string.invalid_token), false);
    }


    public void testing(View view) {
        startActivity(new Intent(this, TestingActivity.class));
    }


    /**
     * 实时发送事件历史
     *
     * @param view
     */
    public void history(View view) {
        Intent intent = new Intent(this, EventHistoryActivity.class);
        intent.putExtra("datas", datas);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    /**
     * 发送事件
     *
     * @param id         事件id
     * @param properties 参数
     */
    private void sendEvent(final String id, HashMap<String, String> properties) {
        boolean cb_status = cb.isChecked();
        if (cb_status) {
            TrackAgent.currentEvent().eventRealTime(id, properties);
            Date date = new Date();
            //获取日期/时间格式器
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String time = dateFormat.format(date);
            datas.datas.add(new EventHistoryBean(id, time));
        } else {
            TrackAgent.currentEvent().event(id, properties);
        }
    }

}
