package cn.magicwindow.uav.sdk.http;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.magicwindow.uav.sdk.util.Preconditions;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * @author Tony Shen
 * @date 16/1/16
 */
class RestClient {

    /**
     * 默认的ConnectionFactory
     */
    private static ConnectionFactory DEFAULT = new ConnectionFactory() {
        @Override
        public HttpURLConnection create(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }

        @Override
        public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
            return (HttpURLConnection) url.openConnection(proxy);
        }
    };
    private static SSLSocketFactory TRUSTED_FACTORY;
    private static HostnameVerifier TRUSTED_VERIFIER;
    private static ConnectionFactory CONNECTION_FACTORY = DEFAULT;
    private HttpURLConnection connection = null;
    private URL url;
    private String requestMethod;
    private boolean clientIsNull = false;

    /**
     * 创建 HTTP connection wrapper
     *
     * @param url
     * @param method
     * @throws RestException
     */
    private RestClient(String url, String method)
            throws RestException {
        try {
            this.url = new URL(url);
            this.requestMethod = method;
            //支持https
            if (Preconditions.isNotBlank(url) && url.startsWith("https")) {
                //Accept all certificates
                trustAllCerts();
                //Accept all hostnames
                trustAllHosts();
            }
        } catch (MalformedURLException e) {
            throw new RestException(e);
        }
    }

    RestClient(String url)
            throws RestException {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new RestException(e);
        }
    }

    private static SSLSocketFactory getTrustedFactory()
            throws RestException {
        if (TRUSTED_FACTORY == null) {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                    // Intentionally left blank
                }

                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                    // Intentionally left blank
                }
            }};
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new SecureRandom());
                TRUSTED_FACTORY = context.getSocketFactory();
            } catch (GeneralSecurityException e) {
                IOException ioException = new IOException(
                        "Security exception configuring SSL context", e);
                throw new RestException(ioException);
            }
        }

        return TRUSTED_FACTORY;
    }

    private static HostnameVerifier getTrustedVerifier() {
        if (TRUSTED_VERIFIER == null) {
            TRUSTED_VERIFIER = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }

        return TRUSTED_VERIFIER;
    }

    /**
     * 同步发起get请求
     *
     * @param url
     * @return RestClient
     * @throws RestException
     */
    public static RestClient get(String url) throws RestException {
        return new RestClient(url, RestConstant.METHOD_GET);
    }

    /**
     * 同步发起post请求
     *
     * @param url
     * @return RestClient
     * @throws RestException
     */
    public static RestClient post(String url) throws RestException {
        return new RestClient(url, RestConstant.METHOD_POST);
    }

    void setRequestMethod(String method) {
        this.requestMethod = method;

        //支持https
        if (Preconditions.isNotBlank(url) && url.toString().startsWith("https")) {
            //Accept all certificates
            trustAllCerts();
            //Accept all hostnames
            trustAllHosts();
        }
    }

    void cleanUp() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    byte[] getByte(Request request) throws Exception {
        if (getConnection() == null || isClientIsNull()) {
            return null;
        }
        addHeaders(request.getHeaders())
                .readTimeout(request.getReadTimeoutMillis())
                .connectTimeout(request.getConnectTimeoutMillis());
        return byteBody();
    }

    byte[] postByte(Request request) throws Exception {
        if (getConnection() == null || isClientIsNull()) {
            return null;
        }
        addHeaders(request.getHeaders())
                .readTimeout(request.getReadTimeoutMillis())
                .connectTimeout(request.getConnectTimeoutMillis())
                .acceptJson()
                .contentType(request.getBodyContentType(), null);
        if (request.getBody() != null) {
            send(request.getBody());
        }
        return byteBody();
    }

    public boolean isClientIsNull() {
        return clientIsNull;
    }

    RestClient readTimeout(int timeout) {
        getConnection().setReadTimeout(timeout);
        return this;
    }

    RestClient connectTimeout(int timeout) {
        getConnection().setConnectTimeout(timeout);
        return this;
    }

    private HttpURLConnection createConnection() {
        try {
            final HttpURLConnection connection;
            connection = CONNECTION_FACTORY.create(url);
            if (connection == null) {
                return null;
            }

            connection.setRequestMethod(requestMethod);
            connection.setReadTimeout(RestConstant.DEFAULT_READ_TIMEOUT);
            connection.setConnectTimeout(RestConstant.DEFAULT_CONNECTION_TIMEOUT);
            return connection;
        } catch (IOException e) {
            throw new RestException(e);
        }
    }

    /**
     * after set connectionTimeout and readTimeout can throw java.net.SocketTimeoutException<br>
     *
     * @return
     */
    private HttpURLConnection getConnection() {
        if (connection == null) {
            connection = createConnection();
        }

        if (connection == null) {
            clientIsNull = true;
            connection = NullConnection.createNull(url);
        }
        return connection;
    }

    /**
     * 配置HTTPS连接，信任所有证书
     *
     * @return RestClient
     * @throws RestException
     */
    private void trustAllCerts() throws RestException {
        final HttpURLConnection connection = getConnection();
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection)
                    .setSSLSocketFactory(getTrustedFactory());
        }
    }

    /**
     * 配置HTTPS连接，信任所有host
     *
     * @return RestClient
     */
    private void trustAllHosts() {
        final HttpURLConnection connection = getConnection();
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection)
                    .setHostnameVerifier(getTrustedVerifier());
        }
    }

    /**
     * 返回指定charset的http response
     *
     * @return byte[]
     * @throws IOException
     */
    private byte[] byteBody() throws Exception {

        if (getConnection() == null) {
            return null;
        }
        InputStream stream;
        if (getConnection().getResponseCode() < HTTP_BAD_REQUEST) {
            stream = getConnection().getInputStream();
        } else {
            stream = getConnection().getErrorStream();
            if (stream == null) {
                stream = getConnection().getInputStream();
            }
        }
        if (stream == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        buffer.close();
        stream.close();

        return buffer.toByteArray();
    }

    /**
     * 设置{@link HttpURLConnection#setUseCaches(boolean)}的值
     *
     * @param useCaches
     * @return RestClient
     */
    public RestClient useCaches(final boolean useCaches) {
        getConnection().setUseCaches(useCaches);
        return this;
    }

    /**
     * 设置header中accept的值
     *
     * @param value
     * @return RestClient
     */
    private RestClient accept(final String value) {
        return header(RestConstant.HEADER_ACCEPT, value);
    }

    /**
     * 设置header中accept的值为application/json
     *
     * @return RestClient
     */
    RestClient acceptJson() {
        return accept(RestConstant.CONTENT_TYPE_JSON);
    }


    /**
     * 从response header中返回Content-Type的charset参数
     *
     * @return charset or null if none
     */
    public String charset() {
        return parameter(RestConstant.HEADER_CONTENT_TYPE,
                RestConstant.PARAM_CHARSET);
    }

    /**
     * 从response header中返回指定的参数值
     *
     * @param headerName
     * @param paramName
     * @return parameter value or null if missing
     */
    private String parameter(final String headerName, final String paramName) {
        return getParam(header(headerName), paramName);
    }

    /**
     * 从header中获取参数值
     *
     * @param value
     * @param paramName
     * @return parameter value or null if none
     */
    private String getParam(final String value, final String paramName) {
        if (value == null || value.length() == 0) {
            return null;
        }

        final int length = value.length();
        int start = value.indexOf(';') + 1;
        if (start == 0 || start == length) {
            return null;
        }

        int end = value.indexOf(';', start);
        if (end == -1) {
            end = length;
        }

        while (start < end) {
            int nameEnd = value.indexOf('=', start);
            if (nameEnd != -1 && nameEnd < end
                    && paramName.equals(value.substring(start, nameEnd).trim())) {
                String paramValue = value.substring(nameEnd + 1, end).trim();
                int valueLength = paramValue.length();
                if (valueLength != 0) {
                    if (valueLength > 2 && '"' == paramValue.charAt(0)
                            && '"' == paramValue.charAt(valueLength - 1)) {
                        return paramValue.substring(1, valueLength - 1);
                    } else {
                        return paramValue;
                    }
                }
            }

            start = end + 1;
            end = value.indexOf(';', start);
            if (end == -1) {
                end = length;
            }
        }

        return null;
    }

    /**
     * 获取response header中的值
     *
     * @param name
     * @return response header
     * @throws RestException
     */
    private String header(final String name) throws RestException {
//        closeOutputQuietly();
        return getConnection().getHeaderField(name);
    }

    /**
     * 设置request header中的值
     *
     * @param name
     * @param value
     * @return RestClient
     */
    private RestClient header(final String name, final String value) {
        getConnection().setRequestProperty(name, value);
        return this;
    }

    /**
     * 增加request header中的值
     *
     * @param additionalHeaders
     * @return RestClient
     */
    private RestClient addHeaders(Map<String, String> additionalHeaders) {
        if (Preconditions.isNotBlank(additionalHeaders)) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                getConnection().addRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return this;
    }


    /**
     * 将字节数组写入post body
     *
     * @param input body
     * @throws Exception ioexception
     */
    private void send(final byte[] input) throws Exception {
        if (input == null) {
            return;
        }
        DataOutputStream out = null;
        getConnection().setDoOutput(true);
        // post 请求不能使用缓存
        getConnection().setUseCaches(false);
        out = new DataOutputStream(getConnection().getOutputStream());
        out.write(input);
        out.close();
    }

    /**
     * 设置header的Content-Type
     *
     * @param value
     * @param charset
     * @return RestClient
     */
    private void contentType(final String value, final String charset) {
        if (Preconditions.isNotBlank(charset)) {
            final String separator = "; " + RestConstant.PARAM_CHARSET + '=';
            header(RestConstant.HEADER_CONTENT_TYPE, value + separator
                    + charset);
        } else {
            header(RestConstant.HEADER_CONTENT_TYPE, value);
        }
    }

}
