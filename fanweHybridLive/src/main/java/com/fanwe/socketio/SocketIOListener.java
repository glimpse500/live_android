package com.fanwe.socketio;

import java.util.List;

public interface SocketIOListener {
    boolean onNewMessages(List<SocketIOMessage> var1);
}

