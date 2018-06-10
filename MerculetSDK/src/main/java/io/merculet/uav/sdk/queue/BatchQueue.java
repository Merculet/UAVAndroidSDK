package io.merculet.uav.sdk.queue;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.LinkedBlockingQueue;

import io.merculet.uav.sdk.log.DebugLog;

/**
 * @Description queue
 * @Author lucio
 * @Email xiao.lu@magicwindow.cn
 * @Date 12/03/2018 11:48 PM
 * @Version 1.0.0
 */
public class BatchQueue<T> {

    // 默认队列处理长度
    private static int DEFAULT_COUNT = 30;

    // 设置队列处理长度
    private int handleLength;

    // 阻塞队列
    public LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();

    private static Handler handler = new Handler(Looper.getMainLooper());

    // 回调接口
    private QueueProcess<T> process;

    // 往队列添加数据
    public void add(T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置默认的队列处理时间和数量
     */
    public BatchQueue() {
        this(DEFAULT_COUNT);
    }

    /**
     * 可以设置队列的处理的间隔时间和处理长度
     *
     * @param handleQueueLength
     */
    public BatchQueue(int handleQueueLength) {
        this.handleLength = handleQueueLength;
        start();
    }

    private void start() {
        DataListener listener = new DataListener();
        new Thread(listener).start();
    }

    public void setProcess(QueueProcess<T> process) {
        this.process = process;
    }

    // 队列监听，当队列达到一定数量和时间后处理队列
    class DataListener implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从队列拿出并移除队列头部的元素
                    T t = queue.take(); //如果取不到数据,就阻塞线程
                    callBack(t);
                } catch (Exception e) {
                    DebugLog.i("AdTrack upload content: " + e.getMessage());
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void callBack(final T event) {
            // 处理队列
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (process != null) {
                            process.processData(event);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
