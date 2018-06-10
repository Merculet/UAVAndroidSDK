package io.merculet.uav.sdk.http;

public interface ResponseListener<T> {

        void onSuccess(T content);

        void onFail(Exception error);
    }
