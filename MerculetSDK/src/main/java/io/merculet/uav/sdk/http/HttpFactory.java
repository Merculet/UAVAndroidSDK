package io.merculet.uav.sdk.http;

import android.content.Context;
import android.graphics.Bitmap;

import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.util.LruCache;

/**
 * Created by aaron on 15/6/30.
 */
public class HttpFactory {

    private static volatile HttpFactory defaultInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private HttpFactory(Context context) {
        MConfiguration.get().initContext(context);
        mRequestQueue = getRequestQueue();

    }

    /**
     * Convenience singleton for apps using a process-wide HttpClient instance.
     * 由于客户可能将MWImageView用在开启启动画面，而此时MWConfiguration有可能还未初始化，
     * 此时需要从此处初始化context.getApplicationContext()。
     * 所以必须要传context
     */
    public static HttpFactory getInstance(Context context) {
        if (defaultInstance == null) {
            synchronized (HttpFactory.class) {
                if (defaultInstance == null) {
                    defaultInstance = new HttpFactory(context.getApplicationContext());
                }
            }
        }
        return defaultInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = MWHttp.newRequestQueue();
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(Request req) {
        getRequestQueue().addRequest(req);
    }

    public ImageLoader getImageLoader() {
        if(mImageLoader ==null){

            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }

                    });
        }
        return mImageLoader;
    }

}
