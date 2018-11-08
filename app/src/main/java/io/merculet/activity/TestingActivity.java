package io.merculet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.magicwindow.uav.sdk.MConfiguration;
import cn.magicwindow.uav.sdk.TrackAgent;
import cn.magicwindow.uav.sdk.http.ResponseListener;
import cn.magicwindow.uav.sdk.util.SPHelper;
import io.merculet.config.RequestUtil;
import io.merculet.domain.TokenRes;
import io.merculet.example.R;

/**
 * @Description
 * @Author sean
 * @Email xiao.lu@magicwindow.cn
 * @Date 17/09/2018 7:23 PM
 * @Version
 */
public class TestingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        initView();
    }

    private void initView() {
        EditText editText = (EditText) findViewById(R.id.edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RequestUtil.user_id = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void read(View view) {
        if (SPHelper.create().isAuto()) {
            TrackAgent.currentEvent().eventRealTime("s_read",null);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void follow(View view) {
        if (SPHelper.create().isAuto()) {
            TrackAgent.currentEvent().eventRealTime("s_follow",null);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void playingContentType(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("sp_content_type", "c");
            TrackAgent.currentEvent().eventRealTime("s_playing", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void buy(View view) {
        if (SPHelper.create().isAuto()) {
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("sp_content_type", "iPad");
            properties.put("sp_amount", "100");
            TrackAgent.currentEvent().eventRealTime("s_buy", properties);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void folloWx(View view) {
        if (SPHelper.create().isAuto()) {
            TrackAgent.currentEvent().eventRealTime("s_folloWx",null);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(View view) {
        if (SPHelper.create().isAuto()) {
            TrackAgent.currentEvent().eventRealTime("s_sign_in",null);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view) {
        TrackAgent.currentEvent().cancelUserProfile();
    }

    public void AppkeyUTO(View view) {
        if (RequestUtil.user_id.isEmpty()) {
            Toast.makeText(this, "请先输入userId", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUtil.uat_name = "UTO";
        RequestUtil.initToken(
                "f4e7b731af6847ad8cf660f352f250a7",
                "85860b18dc4c4fd9badb91d9f467906a", "",
                "", new ResponseListener<TokenRes>() {
                    @Override
                    public void onSuccess(TokenRes content) {
                        MConfiguration.get().setUserId(RequestUtil.user_id);
                        String msg = "appkey: 85860b18dc4c4fd9badb91d9f467906a,  \nuat name: " + RequestUtil.uat_name;
                        ((TextView) findViewById(R.id.tv_hint)).setText(msg);
                    }

                    @Override
                    public void onFail(Exception error) {
                        Toast.makeText(TestingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });    //每次登录切换token
    }

    public void AppkeyUTP(View view) {
        if (RequestUtil.user_id.isEmpty()) {
            Toast.makeText(this, "请先输入userId", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUtil.uat_name = "UTO";
        RequestUtil.initToken(
                "f4e7b731af6847ad8cf660f352f250a7",
                "3c6a452d825d426aa46e160f2080b372", "",
                "", new ResponseListener<TokenRes>() {
                    @Override
                    public void onSuccess(TokenRes content) {
                        MConfiguration.get().setUserId(RequestUtil.user_id);
                        String msg = "appkey: 3c6a452d825d426aa46e160f2080b372,  \nuat name: " + RequestUtil.uat_name;
                        ((TextView) findViewById(R.id.tv_hint)).setText(msg);
                    }

                    @Override
                    public void onFail(Exception error) {
                        Toast.makeText(TestingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void AppkeyUTQ(View view) {
        if (RequestUtil.user_id.isEmpty()) {
            Toast.makeText(this, "请先输入userId", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUtil.uat_name = "UTO";
        RequestUtil.initToken(
                "f4e7b731af6847ad8cf660f352f250a7",
                "446b4f9ce07e4a82bed922a671a8d7eb", "",
                "", new ResponseListener<TokenRes>() {
                    @Override
                    public void onSuccess(TokenRes content) {
                        MConfiguration.get().setUserId(RequestUtil.user_id);
                        String msg = "appkey: 446b4f9ce07e4a82bed922a671a8d7eb,  \nuat name:" + RequestUtil.uat_name;
                        ((TextView) findViewById(R.id.tv_hint)).setText(msg);
                    }

                    @Override
                    public void onFail(Exception error) {
                        Toast.makeText(TestingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uav(View view) {
        if (SPHelper.create().isAuto()) {
            Intent intent = new Intent(this, UavHistoryActivity.class);
            intent.putExtra("userId",RequestUtil.user_id);
            startActivity(intent);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }

    public void uat(View view) {
        if (SPHelper.create().isAuto()) {
            Intent intent = new Intent(this, UatHistoryActivity.class);
            intent.putExtra("userId",RequestUtil.user_id);
            startActivity(intent);
        } else {
            Toast.makeText(this, "click sign in first", Toast.LENGTH_SHORT).show();
        }
    }
}
