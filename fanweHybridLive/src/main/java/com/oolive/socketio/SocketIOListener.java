package com.oolive.socketio;

import java.util.List;

public interface SocketIOListener {
    boolean onNewMessages(List<SocketIOMessage> var1);
}

