package io.merculet.uav.sdk.http;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by Tony Shen on 16/1/16.
 */
final class ResponseDelivery implements Executor {
    /**
     * 主线程的hander
     */
     private static Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理请求结果,将其执行在UI线程
     *
     * @param request
     * @param response
     */
    void deliveryResponse(final Request request, final byte[] response) {
        execute(new Runnable() {

            @Override
            public void run() {
                if (request.isCanceled()) {
                    request.finish();
                    return;
                }
                request.deliveryResponse(response);
            }
        });
    }

    /**
     * 处理请求结果,将其执行在UI线程
     *
     * @param request
     */
    void deliveryError(final Request request, final Exception e) {
        execute(new Runnable() {

            @Override
            public void run() {
                if (request.isCanceled()) {
                    request.finish();
                    return;
                }
                request.deliverError(e);
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
