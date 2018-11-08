package cn.magicwindow.uav.sdk.http;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.magicwindow.uav.sdk.util.JSONUtils;


/**
 * Created by Tony Shen on 16/1/16.
 */
public class Request implements Comparable<Request> {

    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    /**
     * 优先级默认设置为Normal
     */
    protected Priority mPriority = Priority.NORMAL;
    /**
     * 请求序列号
     */
    protected int mSerialNum = 0;
    /**
     * 是否取消该请求
     */
    protected boolean isCancel = false;
    /**
     * 请求Listener
     */
    protected ResponseListener responseListener;
    /**
     * 请求的url
     */
    private String stringUrl = "";
    private String safeStringUrl;
    private URL safeUrl;
    /**
     * 请求的方法
     */
    private HttpMethod mHttpMethod = HttpMethod.GET;
    /**
     * 请求的header
     */
    private Map<String, String> mHeaders = new HashMap<String, String>();
    /**
     * 请求的body参数
     */
    private JSONObject mBodyParams = new JSONObject();
    /**
     * 该请求是否应该缓存
     */
    private boolean mShouldCache = false;
    /**
     * request重试次数,0为1次。2为3次
     */
    private int retryNum = 2;
    /**
     * request是否是重试
     */
    private boolean isRetry = false;
    /**
     * request超时时间,默认是30秒
     */
    private int connctTimeout = RestConstant.DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = RestConstant.DEFAULT_READ_TIMEOUT;
//    private RequestQueue mRequestQueue;

    /**
     * @param method
     * @param url
     * @param listener
     */
    public Request(HttpMethod method, String url, ResponseListener listener) {
        mHttpMethod = method;
        stringUrl = url;
        responseListener = listener;
    }


    public URL getURL() throws MalformedURLException {
        return getSafeUrl();
    }

    // See http://stackoverflow.com/questions/3286067/url-encoding-in-android. Although the answer
    // using URI would work, using it would require both decoding and encoding each string which is
    // more complicated, slower and generates more objects than the solution below. See also issue
    // #133.
    private URL getSafeUrl() throws MalformedURLException {
        if (safeUrl == null) {
            safeUrl = new URL(getSafeStringUrl());
        }
        return safeUrl;
    }

    public String getUrl() {
        return getSafeStringUrl();
    }

    private String getSafeStringUrl() {
        if (TextUtils.isEmpty(safeStringUrl)) {
            String unsafeStringUrl = stringUrl;
            safeStringUrl = Uri.encode(unsafeStringUrl, ALLOWED_URI_CHARS);
        }
        return safeStringUrl;
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.mHttpMethod = httpMethod;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public JSONObject getBodyParams() {
        return mBodyParams;
    }

    public void setBodyParams(JSONObject bodyParams) {
        if (bodyParams != null) {
            this.mBodyParams = bodyParams;
        }
    }

    public Request setRequestQueue(RequestQueue requestQueue) {
//        mRequestQueue = requestQueue;
        return this;
    }

    //需要重载
    public byte[] getBody() {

        return mBodyParams != null ? encodeParameters(mBodyParams, RestConstant.CHARSET_UTF8) : null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(JSONObject params, String paramsEncoding) {
        if (JSONUtils.isBlank(params)) return null;

        try {
            return params.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/json";
    }

    /**
     * 该请求是否应该缓存
     *
     * @param shouldCache
     */
    public void setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }

    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }

    public int getConnectTimeoutMillis() {
        return connctTimeout;
    }

    public void setConnectTimeoutMillis(int timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("connect timeoutMillis < 0");
        }
        this.connctTimeout = timeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeout;
    }

    public void setReadTimeoutMillis(int timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeoutMillis < 0");
        }
        this.readTimeout = timeoutMillis;
    }

    public void addHeader(String name, String value) {
        mHeaders.put(name, value);
    }

    /**
     * 处理Response,该方法运行在UI线程.
     *
     * @param content
     */
    public void deliveryResponse(byte[] content) {

        if (responseListener != null) {
            responseListener.onSuccess(content);
        }
    }


    public void deliverError(Exception e) {
        if (responseListener != null) {
            responseListener.onFail(e);
        }
    }

    @Override
    public int compareTo(Request another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(anotherPriority) ? this.getSerialNumber()
                - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mShouldCache ? 1231 : 1237);
        result = prime * result + ((stringUrl == null) ? 0 : stringUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request other = (Request) obj;
        if (mHeaders == null) {
            if (other.mHeaders != null)
                return false;
        } else if (!mHeaders.equals(other.mHeaders))
            return false;
        if (mHttpMethod != other.mHttpMethod)
            return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mShouldCache != other.mShouldCache)
            return false;
        if (stringUrl == null) {
            if (other.stringUrl != null)
                return false;
        } else if (!stringUrl.equals(other.stringUrl))
            return false;
        return true;
    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     * <p>
     * <p>Also dumps all events from this request's event log; for debugging.</p>
     */
    void finish() {
//        if (mRequestQueue != null) {
//            mRequestQueue.finish(this);
//        }
        onFinish();
    }

    /**
     * clear listeners when finished
     */
    protected void onFinish() {
        responseListener = null;
    }

    /**
     * 目前只支持get、post请求
     */
    public enum HttpMethod {
        GET("GET"),
        POST("POST");

        /**
         * http request type
         */
        private String mHttpMethod = "";

        HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }


    /**
     * 优先级枚举
     *
     * @author mrsimple
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }


}
