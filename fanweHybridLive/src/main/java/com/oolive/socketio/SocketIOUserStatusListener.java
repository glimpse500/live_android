package com.oolive.socketio;

public interface SocketIOUserStatusListener {
    void onForceOffline();

    void onUserSigExpired();
}
