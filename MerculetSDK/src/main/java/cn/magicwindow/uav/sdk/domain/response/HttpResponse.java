package cn.magicwindow.uav.sdk.domain.response;

import java.io.Serializable;

/**
 * Created by aaron on 14/11/12.
 */
public class HttpResponse implements Serializable {
    public int code;
    public String message;

    public HttpResponse(int code) {
        this.code = code;
    }

    public HttpResponse(int code,String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResponse() {
    }

    public boolean isOkStatus() {
        return code == 0;
    }
}
