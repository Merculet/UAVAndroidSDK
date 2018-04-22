package io.merculet.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.merculet.util.Preconditions;


/**
 * Created by Tony Shen on 2016/11/29.
 */

public class JsonRequest extends Request {

    /**
     * @param method
     * @param url
     * @param listener
     */
    public JsonRequest(HttpMethod method, String url, ResponseListener<JSONObject> listener) {
        super(method, url, listener);
    }

    /**
     * 处理Response,该方法运行在UI线程.
     *
     * @param content
     */
    @Override
    public void deliveryResponse(byte[] content) {
//        ResponseListener responseListener = responseListeners.get();

        if (responseListener != null) {
            /*if (responseListener.getClass().isAnonymousClass()) {
                //get outer class
                try {
                    Field field = responseListener.getClass().getDeclaredField("this$0");
                    field.setAccessible(true);
                    Object parent = field.get(responseListener);
                    if (null == parent || Util.isDestroy(parent)) {
                        return;
                    }
                } catch (NoSuchFieldException e) {
                } catch (IllegalAccessException e) {
                }
            }*/
            responseListener.onSuccess(parseJson(content));
        }
    }

    private JSONObject parseJson(byte[] data) {

        String jsonString = parseByte(data);

        if (!Preconditions.isJsonString(jsonString)) {
            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private String parseByte(byte[] data) {
        if (data == null) {
            return null;
        }
        String parsed;
        try {
            parsed = new String(data, RestConstant.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            parsed = new String(data, Charset.defaultCharset());
        }
        return parsed;
    }
}
