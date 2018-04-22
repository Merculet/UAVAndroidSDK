package io.merculet.manager;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;

import io.merculet.MConfiguration;
import io.merculet.config.APIConstant;
import io.merculet.db.MessageUtils;
import io.merculet.db.MsgModel;
import io.merculet.domain.event.EventsProxy;
import io.merculet.domain.response.TokenRes;
import io.merculet.http.HttpFactory;
import io.merculet.http.HttpUtils;
import io.merculet.http.JsonRequest;
import io.merculet.http.Request;
import io.merculet.http.ResponseListener;
import io.merculet.log.DebugLog;
import io.merculet.util.DeviceInfoUtils;
import io.merculet.util.HttpResponseUtils;
import io.merculet.util.JSONUtils;
import io.merculet.util.Preconditions;
import io.merculet.util.SPHelper;
import io.merculet.util.Util;

/**
 * @Description
 * @Author lucio
 * @Date 09/03/2018 5:52 PM
 * @Version
 */
public class RequestManager {

    public synchronized static void upload(final String upData) {
        DebugLog.i("Track upload content: " + upData);
        long currentTime = System.currentTimeMillis();
        SPHelper.create().setLastSendTime(currentTime);
        EventsProxy.create().clearEvents();
        //设置非第一次启动tag
        DeviceInfoUtils.setFirstTag();
        //todo:4-3
        Request request = new JsonRequest(Request.HttpMethod.POST, APIConstant.TRACKING_URL, new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    TokenRes tokenRes = JSONUtils.convertToObj(response, TokenRes.class);
                    if (!tokenRes.isOkStatus()) {
                        if (tokenRes.code == 1) {   //token失效,重新获取
                            initToken();
                        }
                        MessageUtils.insertMsg(MConfiguration.getContext(), upData, MessageUtils.TYPE_COMMON);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageUtils.insertMsg(MConfiguration.getContext(), upData, MessageUtils.TYPE_COMMON);
                }
                //此时说明网络是好的，可以发送数据库缓存数据
                //DB数据只要启动和关闭时上传即可。del by aaron
//                uploadMsgDB();
                DebugLog.i("Track upload content: " + response);
            }

            @Override
            public void onFail(Exception e) {
//                DebugLog.e("Track upload error: " + e);
                MessageUtils.insertMsg(MConfiguration.getContext(), upData, MessageUtils.TYPE_COMMON);
            }
        });
        try {
            request.addHeader("mw-token", SPHelper.create().getToken());
            request.setBodyParams(new JSONObject(upData));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpFactory.getInstance(MConfiguration.getContext()).addToRequestQueue(request);
    }

    public synchronized static void uploadDb() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - SPHelper.create().getLastSendDb() < 100) {
            return;
        }
        SPHelper.create().setLastSendTime(currentTime);
        SPHelper.create().setLastSendDb(currentTime);

        final MsgModel msgModel = MessageUtils.getEventMsgLimit(MConfiguration.getContext(), -1);

        if (msgModel != null && Preconditions.isNotBlank(msgModel.getDataList())) {

            int length = msgModel.getIdList().size();
            String data = null;
            Request request = null;
            for (int pos = 0; pos < length; pos++) {

                //todo:4-4
                data = msgModel.getDataList().get(pos);
                final String id = msgModel.getIdList().get(pos);

                //todo: mw_appid
                if (Preconditions.isNotBlank(data)) {
                    DebugLog.i("Track upload uploadDb: " + data);
                    request = new JsonRequest(Request.HttpMethod.POST, APIConstant.TRACKING_URL, new ResponseListener<JSONObject>() {

                        @Override
                        public void onSuccess(JSONObject content) {
                            try {
                                TokenRes tokenRes = JSONUtils.convertToObj(content, TokenRes.class);
                                if (tokenRes.isOkStatus()) {
                                    MessageUtils.deleteMsgByID(MConfiguration.getContext(), id);
                                    //设置非第一次启动tag
                                    DeviceInfoUtils.setFirstTag();
                                } else {
                                    if (tokenRes.code == 1) {   //token失效,重新获取
                                        initToken();
                                    }
                                }
                                DebugLog.i("Track upload content: " + content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                        }
                    });
                    try {
                        request.addHeader("mw-token", SPHelper.create().getToken());
                        request.setBodyParams(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpFactory.getInstance(MConfiguration.getContext()).addToRequestQueue(request);
                }
            }
        }
    }

    public static void initToken() {
        SPHelper spHelper = SPHelper.create();
        String user_open_id = spHelper.getUserId();
        String app_key = Util.getMAppKey();
        if (Preconditions.isBlank(app_key)) {
            app_key = spHelper.getAppKey();
        }
        String account_key = Util.getAccountKey();
        if (Preconditions.isBlank(account_key)) {
            account_key = spHelper.getAccountKey();
        }
        String account_secret = Util.getAccountSecret();
        if (Preconditions.isBlank(account_secret)) {
            account_secret = spHelper.getAccountSecret();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            int nonce = new SecureRandom().nextInt();
            long timeMillis = System.currentTimeMillis();
            String secret = account_key + app_key + nonce + timeMillis + user_open_id + account_secret;
            String sign = HttpUtils.md5(secret);
            jsonObject.put("account_key", account_key);
            jsonObject.put("app_key", app_key);
            jsonObject.put("timestamp", timeMillis);
            jsonObject.put("nonce", nonce);    //随机数
            jsonObject.put("user_open_id", user_open_id);
            assert sign != null;
            jsonObject.put("sign", sign.toLowerCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Request request = new JsonRequest(Request.HttpMethod.POST, APIConstant.TOKEN_URL, new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (JSONUtils.isNotBlank(response)) {
                        TokenRes tokenRes = JSONUtils.convertToObj(response, TokenRes.class);
                        if (HttpResponseUtils.isOK(tokenRes)) {
                            String token = tokenRes.data;
                            if (!TextUtils.isEmpty(token)) {
                                SPHelper.create().setToken(token);
                            }
                            DebugLog.i("initToken response:" + response);
                        } else {
                            DebugLog.e(tokenRes.message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                DebugLog.e(e.getMessage());
            }
        });
        DebugLog.i("initToken request:" + jsonObject);
        request.setBodyParams(jsonObject);
        request.setRetryNum(2);
        request.setConnectTimeoutMillis(3000);
        HttpFactory.getInstance(MConfiguration.getContext()).addToRequestQueue(request);
    }
}
