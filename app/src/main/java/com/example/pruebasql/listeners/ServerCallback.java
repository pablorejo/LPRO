package com.example.pruebasql.listeners;

public interface ServerCallback {
    void onResponse(Object response);

    void onError(Exception e);
}
