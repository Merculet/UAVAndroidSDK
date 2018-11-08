package cn.magicwindow.uav.sdk.http;

public interface ResponseListener<T> {

        void onSuccess(T content);

        void onFail(Exception error);
    }
