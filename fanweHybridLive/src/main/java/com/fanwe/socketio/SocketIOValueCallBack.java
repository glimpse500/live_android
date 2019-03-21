package com.fanwe.socketio;

public interface SocketIOValueCallBack<T> {
    void onError(int var1, String var2);

    void onSuccess(T var1);
}
