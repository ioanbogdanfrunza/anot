package com.betathome.helpers;

public interface DataHandler<T> {
    void onSuccess(T data);
    void onFailure(Exception exception, String message);
}
