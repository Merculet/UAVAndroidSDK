package io.merculet.uav.sdk.http;

/**
 * Created by Tony Shen on 16/1/20.
 */
public class MWHttp {

    /**
     * 创建一个请求队列,NetworkExecutor数量为默认的数量
     *
     * @return
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(RequestQueue.DEFAULT_CORE_NUMS);
    }

    /**
     * 创建一个请求队列,NetworkExecutor数量为coreNums
     *
     * @param coreNums
     * @return
     */
    public static RequestQueue newRequestQueue(int coreNums) {
        RequestQueue queue = new RequestQueue(Math.max(0, coreNums));
        queue.start();
        return queue;
    }
}
