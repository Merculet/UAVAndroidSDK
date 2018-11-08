package cn.magicwindow.uav.sdk.http;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tony Shen on 16/1/19.
 */
public class RequestQueue {

    /**
     * 默认的核心数
     */
    public final static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * 线程安全的请求队列
     */
    private BlockingQueue<Request> mRequestQueue = new PriorityBlockingQueue<Request>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);
    /**
     * CPU核心数 + 1个分发线程数
     */
    private int mDispatcherNums = DEFAULT_CORE_NUMS;

    /**
     * NetworkExecutor,执行网络请求的线程
     */
    private NetworkExecutor[] mDispatchers = null;

    /**
     * @param coreNums 线程核心数
     */
    protected RequestQueue(int coreNums) {
        mDispatcherNums = coreNums;
    }

    /**
     * 启动NetworkExecutor
     */
    private void startNetworkExecutors() {
        mDispatchers = new NetworkExecutor[mDispatcherNums];
        for (int i = 0; i < mDispatcherNums; i++) {
            NetworkExecutor networkExecutor = new NetworkExecutor(mRequestQueue);
            mDispatchers[i] = networkExecutor;
            networkExecutor.start();
        }
    }

    public void start() {
        stop();
        startNetworkExecutors();
    }

    /**
     * 停止NetworkExecutor
     */
    private void stop() {
        if (mDispatchers != null && mDispatchers.length > 0) {
            for (NetworkExecutor mDispatcher : mDispatchers) {
                mDispatcher.quit();
            }
        }
    }

    /**
     * 不能重复添加请求
     *
     * @param request
     */
    void addRequest(Request request) {
        if (!mRequestQueue.contains(request)) {
            request.setSerialNumber(this.generateSerialNumber());
            mRequestQueue.add(request);
        } else {
            Log.d("RequestQueue", "### 请求队列中已经含有");
        }
    }

    public void clear() {
        mRequestQueue.clear();
    }

    public BlockingQueue<Request> getAllRequests() {
        return mRequestQueue;
    }

    /**
     * 为每个请求生成一个系列号
     *
     * @return 序列号
     */
    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
}
