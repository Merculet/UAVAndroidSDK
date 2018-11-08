package cn.magicwindow.uav.sdk.http;

/**
 * Created by Tony Shen on 16/1/16.
 */
public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);
}
