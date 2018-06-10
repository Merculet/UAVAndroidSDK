package io.merculet.uav.sdk.http;


import io.merculet.uav.sdk.util.LruCache;

/**
 * Created by Tony Shen on 16/1/16.
 */
public class LruHttpByteCache implements Cache<String, byte[]> {

    /**
     * HttpResponse缓存
     */
    private LruCache<String, byte[]> mResponseCache;

    public LruHttpByteCache() {
        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 取八分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 8;
        mResponseCache = new LruCache<String, byte[]>(cacheSize) {

            @Override
            protected int sizeOf(String key, byte[] response) {
                return response.length / 1024;
            }
        };

    }

    @Override
    public byte[] get(String key) {
        return mResponseCache.get(key);
    }

    @Override
    public void put(String key, byte[] response) {
        mResponseCache.put(key, response);
    }

    @Override
    public void remove(String key) {
        mResponseCache.remove(key);
    }
}
