package com.fanwe.socketio;
import java.util.List;

public interface SocketIORefreshListener {
    void onRefresh();

    void onRefreshConversation(List<SocketIOConversation> var1);
}

