package io.merculet;

import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Random;

import io.merculet.example.BuildConfig;
import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.http.HttpFactory;
import io.merculet.uav.sdk.http.JsonRequest;
import io.merculet.uav.sdk.http.Request;
import io.merculet.uav.sdk.http.ResponseListener;
import io.merculet.uav.sdk.log.DebugLog;
import io.merculet.uav.sdk.util.HttpResponseUtils;
import io.merculet.uav.sdk.util.JSONUtils;


/**
 * @Description
 * @Author sean
 * @Email xiao.lu@magicwindow.cn
 * @Date 01/06/2018 1:58 PM
 * @Version
 */
class RequestUtil {

    static void initToken() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account_key", BuildConfig.CHINA_ENABLE ? "85490582ede8417b83be7b9795244cb3" : "bceac1821abb409180f0de70c85c5a2c");
            jsonObject.put("app_key", BuildConfig.CHINA_ENABLE ? "0bb9b9d4072d47ba8eb8af7c8c476dd3" : "f295c2772e14444189c678eb995b453d");
            jsonObject.put("timestamp", System.currentTimeMillis());
            jsonObject.put("nonce", new SecureRandom().nextInt());    //随机数
            jsonObject.put("user_open_id", (new Random().nextInt()) + "111");
            jsonObject.put("sign", "7fLEjFPhW9AmF5cKYbilYRx52ij3ceCVXKCgUk6UfXMwAWczvTWqlAQTGHIQuG8yL5p3givh8kHMrOoAOpAbM6RNpiQ4erfMLgPtTMIPiXbtBu9LclqygZ2JmPQs9bIY");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL;
        if (BuildConfig.CHINA_ENABLE) {
            URL = "https://openapi.magicwindow.cn";
        } else {
            URL = "https://openapi.merculet.io";
        }
        final Request request = new JsonRequest(Request.HttpMethod.POST, URL + "/v1/user/login", new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {

                try {
                    if (JSONUtils.isNotBlank(response)) {
                        DebugLog.e(response.toString());
                        TokenRes tokenRes = JSONUtils.convertToObj(response, TokenRes.class);
                        if (HttpResponseUtils.isOK(tokenRes)) {
                            String token = tokenRes.data;
                            if (!TextUtils.isEmpty(token)) {
                                MConfiguration.get()
                                        .setToken(token)   //配置token
                                        .setUserId("3338f70dbe2e4044a3fffd5d709f2311");
                            }
                        } else {
                            DebugLog.i("get Service Config error! ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(MConfiguration.get().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        request.setBodyParams(jsonObject);
        request.setRetryNum(2);
        request.setConnectTimeoutMillis(3000);
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }
}
