package io.merculet;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import io.merculet.example.R;
import io.merculet.util.SPHelper;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            int nextInt = new Random().nextInt();
            String userId = StringUtils.md5(String.valueOf(nextInt));
            TrackAgent.currentEvent().setUserProfile(userId);   //设置userId
            //自定义事件
            HashMap<String, String> properties = new HashMap<>();
            TrackAgent.currentEvent().event("sign in", properties);
        } else {
            Toast.makeText(this, "has sign in", Toast.LENGTH_SHORT).show();
        }
    }

    public void signOut(View view) {
        TrackAgent.currentEvent().cancelUserProfile();
    }
}
