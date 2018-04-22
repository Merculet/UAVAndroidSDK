package io.merculet.domain.response;

import java.io.Serializable;

/**
 * Created by aaron on 14/11/12.
 */
public class HttpResponse implements Serializable {
    public int code;
    public String message;

    public boolean isOkStatus() {
        return code == 0;
    }
}