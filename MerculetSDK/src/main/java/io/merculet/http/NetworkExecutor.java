package io.merculet.http;

import android.os.Process;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

import io.merculet.MConfiguration;
import io.merculet.util.JSONUtils;
import io.merculet.util.Preconditions;
import io.merculet.util.Util;

/**
 * Created by Tony Shen on 16/1/16.
 */
final class NetworkExecutor extends Thread {

    /**
     * 请求缓存
     */
    private static Cache<String, byte[]> mReqCache = new LruHttpByteCache();
    /**
     * 结果分发器,将结果投递到主线程
     */
    private ResponseDelivery mResponseDelivery = new ResponseDelivery();
    /**
     * 网络请求队列
     */
    private BlockingQueue<Request> mRequestQueue;
    /**
     * 是否停止
     */
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<Request> queue) {
        mRequestQueue = queue;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Request request;

        while (!isStop) {
            request = null;
            try {
                request = mRequestQueue.take();

                if (request == null) {
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                if (isStop) {
                    return;
                }
                continue;
            }

            if (request.isCanceled()) {
                request.finish();
                continue;
            }

            doRequest(request);
        }
    }

    private void doRequest(Request request) {

        //如果没有权限，则不需要申请网络
        if (!Util.checkPermission(MConfiguration.getContext(), "android.permission.INTERNET")) {
            return;
        }
        byte[] responseBytes = new byte[0];

        // 重试没必要从缓存中取数据
        if (!request.isRetry() && isUseCache(request)) {
            // 从缓存中取数据
            responseBytes = mReqCache.get(getCacheKey(request));
        } else {
            if (Preconditions.isBlank(request.getUrl())) {
                return;
            }

            RestClient restClient = new RestClient(request.getUrl());
            try {

                // 从网络上获取数据
                switch (request.getHttpMethod()) {
                    case GET:
                        restClient.setRequestMethod(RestConstant.METHOD_GET);
                        responseBytes = restClient.getByte(request);
                        break;
                    case POST:
                        restClient.setRequestMethod(RestConstant.METHOD_POST);
                        responseBytes = restClient.postByte(request);
                        break;
                    default:
                        break;
                }
            } catch (RestException e) {
                if (request.getRetryNum() > 0) {
                    request.setRetryNum(request.getRetryNum() - 1);
                    request.setRetry(true);
                    doRequest(request);
                } else if (request.getRetryNum() <= 0) {
                    mResponseDelivery.deliveryError(request, e);
                }
                return;
            } catch (SocketTimeoutException e) {
                if (request.getRetryNum() > 0) {
                    request.setRetryNum(request.getRetryNum() - 1);
                    request.setRetry(true);
                    doRequest(request);
                } else if (request.getRetryNum() <= 0) {
                    mResponseDelivery.deliveryError(request, e);
                }
                return;
            } catch (IOException e) {
                if (request.getRetryNum() > 0) {
                    request.setRetryNum(request.getRetryNum() - 1);
                    request.setRetry(true);
                    doRequest(request);
                } else if (request.getRetryNum() <= 0) {
                    mResponseDelivery.deliveryError(request, e);
                }
                return;
            } catch (Exception e) {
                mResponseDelivery.deliveryError(request, e);
                return;
            } finally {
                restClient.cleanUp();
            }

            // 如果该请求需要缓存,那么请求成功则缓存到mResponseCache中
            if (request.shouldCache() && Preconditions.isNotBlank(responseBytes)) {
                mReqCache.put(getCacheKey(request), responseBytes);
            }
        }

        // 分发请求结果
        mResponseDelivery.deliveryResponse(request, responseBytes);
    }


    /**
     * 生成http缓存的key
     *
     * @param request
     * @return
     */
    private String getCacheKey(Request request) {

        String key = null;
        if (Preconditions.isNotBlank(request.getUrl())) {
            if (JSONUtils.isNotBlank(request.getBodyParams())) {
                // post请求,使用url+post body然后md5,生成key
                key = HttpUtils.md5(request.getUrl() + request.getBodyParams().toString());
            } else {
                // get请求,使用url然后md5,生成key
                key = HttpUtils.md5(request.getUrl());
            }
        }

        return key;
    }

    private boolean isUseCache(Request request) {
        return request.shouldCache() && Preconditions.isNotBlank(getCacheKey(request)) &&
                mReqCache.get(getCacheKey(request)) != null;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
