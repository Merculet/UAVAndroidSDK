package io.merculet.uav.sdk.queue;

/**
 * Created by tony on 16/7/26.
 */
public interface QueueProcess <T> {

    void processData(T event);
}
