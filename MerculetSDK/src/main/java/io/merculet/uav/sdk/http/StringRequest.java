package io.merculet.uav.sdk.http;

import java.io.UnsupportedEncodingException;

/**
 * Created by Tony Shen on 2016/12/7.
 */

public class StringRequest extends Request {

    /**
     * @param method
     * @param url
     * @param listener
     */
    public StringRequest(HttpMethod method, String url, ResponseListener<String> listener) {
        super(method, url, listener);
//        responseListener = listener;
    }

    public StringRequest(HttpMethod method, String url) {
        super(method, url, null);
//        responseListener = listener;
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
            responseListener.onSuccess(parseByte(content));
        }
    }

    private String parseByte(byte[] data) {
        if(data == null){
            return null;
        }
        String parsed;
        try {
            parsed = new String(data, RestConstant.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            parsed = null;
        }
        return parsed;
    }

}
