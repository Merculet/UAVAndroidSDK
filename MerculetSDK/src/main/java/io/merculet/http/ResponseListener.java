package io.merculet.http;

public interface ResponseListener<T> {

        void onSuccess(T content);

        void onFail(Exception error);
    }