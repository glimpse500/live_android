package com.oolive.socketio;
import java.util.List;

public interface SocketIORefreshListener {
    void onRefresh();

    void onRefreshConversation(List<SocketIOConversation> var1);
}

