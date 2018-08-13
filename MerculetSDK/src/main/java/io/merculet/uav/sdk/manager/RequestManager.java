package io.merculet.uav.sdk.manager;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.RealTimeCallback;
import io.merculet.uav.sdk.config.APIConstant;
import io.merculet.uav.sdk.config.Constant;
import io.merculet.uav.sdk.db.MessageUtils;
import io.merculet.uav.sdk.db.MsgModel;
import io.merculet.uav.sdk.domain.event.EventsProxy;
import io.merculet.uav.sdk.domain.response.HttpResponse;
import io.merculet.uav.sdk.http.HttpFactory;
import io.merculet.uav.sdk.http.JsonRequest;
import io.merculet.uav.sdk.http.Request;
import io.merculet.uav.sdk.http.ResponseListener;
import io.merculet.uav.sdk.log.DebugLog;
import io.merculet.uav.sdk.util.DeviceInfoUtils;
import io.merculet.uav.sdk.util.HttpResponseUtils;
import io.merculet.uav.sdk.util.JSONUtils;
import io.merculet.uav.sdk.util.Preconditions;
import io.merculet.uav.sdk.util.SPHelper;
import io.merculet.uav.sdk.util.SignUtils;

/**
 * @Description
 * @Author lucio
 * @Email xiao.lu@magicwindow.cn
 * @Date 09/03/2018 5:52 PM
 * @Version
 */
public class RequestManager {


    public static RequestManager get() {
        return Holder.REQUEST_MANAGER;
    }

    private static class Holder {
        private static final RequestManager REQUEST_MANAGER = new RequestManager();
    }

    public synchronized void upload(String data) {
        SPHelper.create().setLastSendTime(System.currentTimeMillis());
        EventsProxy.create().clearEvents();
        DeviceInfoUtils.setFirstTag();  //设置非第一次启动tag
        request(data, "");    //退出账号上传数据时,上传失败会更新到本地
    }

    public synchronized void uploadDb() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - SPHelper.create().getLastSendDb() < 100) return;
        SPHelper.create().setLastSendTime(currentTime);
        SPHelper.create().setLastSendDb(currentTime);
        final MsgModel msgModel = MessageUtils.getEventMsgLimit(MConfiguration.get().getContext(), -1);
        if (msgModel != null && Preconditions.isNotBlank(msgModel.getDataList())) {
            int length = msgModel.getIdList().size();
            for (int pos = 0; pos < length; pos++) {
                String data = msgModel.getDataList().get(pos);
                String userId = msgModel.getUserIdList().get(pos);
                final String id = msgModel.getIdList().get(pos);
                if (Preconditions.isNotBlank(data) && SPHelper.create().getUserId().equals(userId)) { //比对数据库中userId和用户userId
                    request(data, id);
                }
            }
        }
    }

    public synchronized void uploadRealTime(String data, RealTimeCallback callback) {
        MyResponseListener listener = new MyResponseListener(data, callback);
        request(data, listener);
    }

    private void request(final String data, final String id) {
        MyResponseListener listener = new MyResponseListener(data, id);
        request(data, listener);
    }

    private void request(String data, MyResponseListener listener) {
        DebugLog.i("Track upload request: " + data);
        Request request = new JsonRequest(Request.HttpMethod.POST, APIConstant.getTrackingUrl(), listener);
        StringBuilder sb = new StringBuilder();
        final String upDataStr = data.replace("\"", "\\\"");
        sb.append("{").append("\r\n").append("\"info\":\"").append(upDataStr).append("\"").append("\r\n").append("}");
        try {
            request.addHeader("mw-sign", SignUtils.generateSign(data));
            request.addHeader("mw-token", SPHelper.create().getToken());
            request.setBodyParams(new JSONObject(sb.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }

    class MyResponseListener implements ResponseListener<JSONObject> {

        private String data;
        private String id;
        private RealTimeCallback callback;

        MyResponseListener(String data, RealTimeCallback callback) {
            this.data = data;
            this.callback = callback;
        }

        MyResponseListener(String data, String id) {
            this.data = data;
            this.id = id;
        }

        @Override
        public void onSuccess(JSONObject content) {
            try {
                HttpResponse response = JSONUtils.convertToObj(content, HttpResponse.class);
                if (HttpResponseUtils.isOK(response)) {
                    DebugLog.i("Track upload response: " + content);
                    if (Preconditions.isNotBlank(id)) { //本地上传
                        MessageUtils.deleteMsgByID(id);
                        DeviceInfoUtils.setFirstTag();  //设置非第一次启动tag
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    onError(response);
                    if (response.code == Constant.NETWORK_RESPONSE_INVALIDATE || response.code == Constant.NETWORK_RESPONSE_EXPRIED) {   //token无效/失效,广播通知用户
                        MConfiguration.get().getContext().sendBroadcast(new Intent(Constant.ACTION_TOKEN_INVALID));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(new HttpResponse(-2));
            }
        }

        @Override
        public void onFail(Exception e) {
            onError(new HttpResponse(-1));
        }

        /**
         * @param response code: -1 网络请求出错
         *                 code: -2 json解析报错
         */
        private void onError(HttpResponse response) {
            //本地数据上传失败不用存到本地:id不为空即本地
            if (callback == null && Preconditions.isBlank(id)) {
                MessageUtils.insertEventByMsg(data);
            }
            //实时传数据
            if (callback != null && Preconditions.isBlank(id)) {
                callback.onFailed(response);
            }
        }
    }
}
