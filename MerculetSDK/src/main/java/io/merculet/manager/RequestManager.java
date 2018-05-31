package io.merculet.manager;

import org.json.JSONException;
import org.json.JSONObject;

import io.merculet.MConfiguration;
import io.merculet.config.APIConstant;
import io.merculet.db.MessageUtils;
import io.merculet.db.MsgModel;
import io.merculet.domain.event.EventsProxy;
import io.merculet.domain.response.TokenRes;
import io.merculet.http.HttpFactory;
import io.merculet.http.JsonRequest;
import io.merculet.http.Request;
import io.merculet.http.ResponseListener;
import io.merculet.log.DebugLog;
import io.merculet.util.DeviceInfoUtils;
import io.merculet.util.JSONUtils;
import io.merculet.util.Preconditions;
import io.merculet.util.SPHelper;
import io.merculet.util.SignUtils;

/**
 * @Description
 * @Author lucio
 * @Email xiao.lu@magicwindow.cn
 * @Date 09/03/2018 5:52 PM
 * @Version
 */
public class RequestManager {

    public synchronized static void upload(final String upData) {

        long currentTime = System.currentTimeMillis();
        SPHelper.create().setLastSendTime(currentTime);
        EventsProxy.create().clearEvents();
        //设置非第一次启动tag
        DeviceInfoUtils.setFirstTag();

        final String upDataStr = upData.replace("\"","\\\"");

        //todo:4-3
        Request request = new JsonRequest(Request.HttpMethod.POST, APIConstant.getTrackingUrl(), new ResponseListener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    TokenRes tokenRes = JSONUtils.convertToObj(response, TokenRes.class);
                    if (!tokenRes.isOkStatus()) {
                        if (tokenRes.code == 1) {   //token失效,重新获取
//                            initToken();
                        }
                        MessageUtils.insertMsg(MConfiguration.get().getContext(), upData, MessageUtils.TYPE_COMMON);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageUtils.insertMsg(MConfiguration.get().getContext(), upData, MessageUtils.TYPE_COMMON);
                }
                //此时说明网络是好的，可以发送数据库缓存数据
                //DB数据只要启动和关闭时上传即可。del by aaron
//                uploadMsgDB();
                DebugLog.i("Track upload content: " + response);
            }

            @Override
            public void onFail(Exception e) {
//                DebugLog.e("Track upload error: " + e);
                MessageUtils.insertMsg(MConfiguration.get().getContext(), upData, MessageUtils.TYPE_COMMON);
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\r\n").append("\"info\":\"").append(upDataStr).append("\"").append("\r\n").append("}");

        try {
            request.addHeader("mw-sign", SignUtils.generateSign(upData));
            request.addHeader("mw-token", SPHelper.create().getToken());
            request.setBodyParams(new JSONObject(sb.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpFactory.getInstance(MConfiguration.get().getContext()).addToRequestQueue(request);
    }

    public synchronized static void uploadDb() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - SPHelper.create().getLastSendDb() < 100) {
            return;
        }
        SPHelper.create().setLastSendTime(currentTime);
        SPHelper.create().setLastSendDb(currentTime);

        final MsgModel msgModel = MessageUtils.getEventMsgLimit(MConfiguration.get().getContext(), -1);

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

                    final String upDataStr = data.replace("\"","\\\"");

                    request = new JsonRequest(Request.HttpMethod.POST, APIConstant.getTrackingUrl(), new ResponseListener<JSONObject>() {

                        @Override
                        public void onSuccess(JSONObject content) {
                            try {
                                TokenRes tokenRes = JSONUtils.convertToObj(content, TokenRes.class);
                                if (tokenRes.isOkStatus()) {
                                    MessageUtils.deleteMsgByID(MConfiguration.get().getContext(), id);
                                    //设置非第一次启动tag
                                    DeviceInfoUtils.setFirstTag();
                                } else {
                                    if (tokenRes.code == 1) {   //token失效,重新获取

//                                        initToken();

                                        return;
                                    }
                                }
                                DebugLog.i("Track upload content: " + content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            //退出发送数据失败,就清空本地数据
                            if (!SPHelper.create().isAuto()) {
                                MessageUtils.deleteMsgByID(MConfiguration.get().getContext(), id);
                            }
                        }
                    });

                    StringBuilder sb = new StringBuilder();
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
            }
        }
    }
}
