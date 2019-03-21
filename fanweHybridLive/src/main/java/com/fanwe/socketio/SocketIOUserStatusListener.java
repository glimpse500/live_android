package com.fanwe.socketio;

public interface SocketIOUserStatusListener {
    void onForceOffline();

    void onUserSigExpired();
}
