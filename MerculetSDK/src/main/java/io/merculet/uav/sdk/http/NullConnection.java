package io.merculet.uav.sdk.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tony Shen on 2016/12/6.
 */

public class NullConnection extends HttpURLConnection {

    private NullConnection(URL url) {
        super(url);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    public boolean isNull() {
        return true;
    }

    static NullConnection createNull(URL url) {
        return new NullConnection(url);
    }
}
