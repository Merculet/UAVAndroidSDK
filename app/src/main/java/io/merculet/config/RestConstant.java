/**
 *
 */
package io.merculet.config;

/**
 * @author Tony Shen
 */
public class RestConstant {

    /**
     * 'UTF-8' charset name
     */
    public static final String CHARSET_UTF8 = "UTF-8";


    /**
     * 'application/json' content type header value
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * 'gzip' encoding header value
     */
    public static final String ENCODING_GZIP = "gzip";

    /**
     * 'Accept' header name
     */
    public static final String HEADER_ACCEPT = "Accept";


    /**
     * 'Accept-Encoding' header name
     */
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * 'Content-Encoding' header name
     */
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

    /**
     * 'Content-Length' header name
     */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * 'Content-Type' header name
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * 'GET' request method
     */
    public static final String METHOD_GET = "GET";


    /**
     * 'POST' request method
     */
    public static final String METHOD_POST = "POST";

    /**
     * 'charset' header value parameter
     */
    public static final String PARAM_CHARSET = "charset";

    public static final String BOUNDARY = "00content0boundary00";

    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary="
            + BOUNDARY;

    public static final String CRLF = "\r\n";
    public static final int SUCCESS = 200;
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public final static int DEFAULT_READ_TIMEOUT = 10000;
    public final static int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static int DEFAULT_RETRY_NUM = 2;
}
