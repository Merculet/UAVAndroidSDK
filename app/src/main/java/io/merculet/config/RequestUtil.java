package io.merculet.config;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;

import cn.magicwindow.uav.sdk.MConfiguration;
import cn.magicwindow.uav.sdk.http.HttpFactory;
import cn.magicwindow.uav.sdk.http.JsonRequest;
import cn.magicwindow.uav.sdk.http.Request;
import cn.magicwindow.uav.sdk.http.ResponseListener;
import cn.magicwindow.uav.sdk.log.DebugLog;
import cn.magicwindow.uav.sdk.util.HttpResponseUtils;
import cn.magicwindow.uav.sdk.util.JSONUtils;
import cn.magicwindow.uav.sdk.util.SPHelper;
import io.merculet.domain.TokenRes;
import io.merculet.domain.UatListResponse;
import io.merculet.domain.UavListResponse;
import io.merculet.example.BuildConfig;


/**
 * @Description
 * @Author sean
 * @Email xiao.lu@magicwindow.cn
 * @Date 01/06/2018 1:58 PM
 * @Version
 */
public class RequestUtil {

    public static String uat_name = "UTO";
    public static String user_id = "";

    private static String base_url = BuildConfig.IS_DEBUG ?
            //测试地址
            (BuildConfig.CHINA_ENABLE ? "http://score-query-cn.liaoyantech.cn" : "http://score-query.liaoyantech.cn") :
            //正式地址
            (BuildConfig.CHINA_ENABLE ? "https://openapi.merculet.cn" : "https://openapi.merculet.io");

    public static void initToken(String account_key, String app_key, String signs, String account_secret, final ResponseListener<TokenRes> listener) {

        JSONObject jsonObject = new JSONObject();
        try {
            if (account_secret.isEmpty()) {
                account_secret = "bcf6189009c345b39cd3009004f687d1";
            }
            int nonce = Math.abs(new SecureRandom().nextInt());
            long timestamp = System.currentTimeMillis();
            String secret = account_key + app_key + nonce + timestamp + user_id + account_secret;
            String sign = "";
            if (signs.isEmpty()) {
                sign = HttpUtils.encryptSHA256(secret);
            } else {
                sign = signs;
            }

            jsonObject.put("account_key", account_key);
            jsonObject.put("app_key", app_key);
            jsonObject.put("nonce", nonce);    //随机数
            jsonObject.put("timestamp", timestamp);
            jsonObject.put("user_open_id", user_id);
            jsonObject.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Request request = new JsonRequest(Request.HttpMethod.POST, base_url + "/v1/user/login", new ResponseListener<JSONObject>() {

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
                                        .setToken(token, true)   //配置token
                                        .setUserId(user_id);
                                listener.onSuccess(tokenRes);
                            }
                        } else {
                            listener.onFail(new Exception(tokenRes.message));
                            DebugLog.i("get Service Config error! ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                listener.onFail(e);
                e.printStackTrace();
            }
        });
        request.setBodyParams(jsonObject);
        request.setRetryNum(0);
        request.setConnectTimeoutMillis(3000);
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }

    public static void getUavList(final ResponseListener<UavListResponse> listener) {
        int nonce = Math.abs(new SecureRandom().nextInt());
        long timestamp = System.currentTimeMillis();
        String account_secret = "bcf6189009c345b39cd3009004f687d1";
        String from = 1 + "";
        String to = timestamp + "";
        String page_number = "1";
        String page_size = "100";
        String sign = from + nonce + page_number + page_size + timestamp + to + account_secret;
        sign = HttpUtils.encryptSHA256(sign);
        //score-query-cn.liaoyantech.cn
        UrlBuilder urlBuilder = new UrlBuilder(base_url + "/v1/user/asset/uav/history")
                .parameter("from", from)
                .parameter("nonce", nonce)
                .parameter("page_number", page_number)
                .parameter("page_size", page_size)
                .parameter("timestamp", timestamp + "")
                .parameter("to", to)
                .parameter("sign", sign);
        final Request request = new JsonRequest(Request.HttpMethod.GET, urlBuilder.buildUrl(), new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {

                try {
                    if (JSONUtils.isNotBlank(response)) {
                        DebugLog.e(response.toString());
                        UavListResponse res = JSONUtils.convertToObj(response, UavListResponse.class);
                        if (HttpResponseUtils.isOK(res)) {
                            listener.onSuccess(res);
                        } else {
                            listener.onFail(new Exception(res.message));
                            DebugLog.i("get Service Config error! ");
                        }
                    }
                } catch (Exception e) {
                    listener.onFail(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                listener.onFail(e);
                e.printStackTrace();
            }
        });
        request.setRetryNum(0);
        request.addHeader("mw-token", SPHelper.create().getToken());
        request.setConnectTimeoutMillis(3000);
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }

    public static void getUatList(final ResponseListener<UatListResponse> listener) {
        int nonce = Math.abs(new SecureRandom().nextInt());
        long timestamp = System.currentTimeMillis();
        String account_secret = "bcf6189009c345b39cd3009004f687d1";
        String from = 1 + "";
        String to = timestamp + "";
        String page_number = "1";
        String page_size = "100";
        String sign = from + nonce + page_number + page_size + timestamp + to + uat_name + account_secret;
        sign = HttpUtils.encryptSHA256(sign);
        UrlBuilder urlBuilder = new UrlBuilder(base_url + "/v1/user/asset/uat/history")
                .parameter("from", from)
                .parameter("nonce", nonce)
                .parameter("page_number", page_number)
                .parameter("page_size", page_size)
                .parameter("timestamp", timestamp + "")
                .parameter("to", to)
                .parameter("uat_name", uat_name)
                .parameter("sign", sign);
        final Request request = new JsonRequest(Request.HttpMethod.GET, urlBuilder.buildUrl(), new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {

                try {
                    if (JSONUtils.isNotBlank(response)) {
                        DebugLog.e(response.toString());
                        UatListResponse res = JSONUtils.convertToObj(response, UatListResponse.class);
                        if (HttpResponseUtils.isOK(res)) {
                            listener.onSuccess(res);
                        } else {
                            listener.onFail(new Exception(res.message));
                            DebugLog.i("get Service Config error! ");
                        }
                    }
                } catch (Exception e) {
                    listener.onFail(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                listener.onFail(e);
                e.printStackTrace();
            }
        });
        request.setRetryNum(0);
        request.addHeader("mw-token", SPHelper.create().getToken());
        request.setConnectTimeoutMillis(3000);
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }
}
