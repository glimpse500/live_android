package com.fanwe.socketio;

import android.content.Context;

import com.fanwe.library.utils.LogUtil;
import com.tencent.IMCoreWrapper;


import java.util.HashSet;

public class SocketIOManager {
    private String identification = "";

    private HashSet<SocketIOListener> msgListeners = new HashSet();
    public static SocketIOManager socketIOManager = null;
    private SocketIOConversation defaultConversation = new SocketIOConversation("");
    private SocketIOUserStatusListener userStatusListener = null;
    private SocketIORefreshListener refreshListener;
    public SocketIOManager(){

    }
    public static SocketIOManager getInstance() {
        if (socketIOManager == null)
            socketIOManager = new SocketIOManager();

        return socketIOManager;
    }
    public void addMessageListener(SocketIOListener var1) {
        LogUtil.i("addMessageListener");
        this.msgListeners.add(var1);
    }
    public long getConversationCount() {
        //To Do
        long tmpConversationCount = 0;
        return tmpConversationCount;
    }
    public SocketIOConversation getConversationByIndex(long var1) {
        //To Do
        return new SocketIOConversation();
    }
    public void setRefreshListener(SocketIORefreshListener var1) {
        this.refreshListener = var1;
    }
    public void setUserStatusListener(SocketIOUserStatusListener var1) {
        this.userStatusListener = var1;
    }

    public void setLogLevel(SocketIOLogLevel var1) {
        //TO DO
    }
    public boolean init(Context var1) {
        //TO DO
        return false;
    }

    public SocketIOConversation getConversation(SocketIOConversationType var1, String var2) {
        if (!IMCoreWrapper.get().isReady()) {
            return this.defaultConversation;
        } else if (var2 == null) {
            LogUtil.i("get conversation with null peer");
            return this.defaultConversation;
        } else {
            SocketIOConversation var3;
            (var3 = new SocketIOConversation(this.identification)).setPeer(var2);
            var3.setType(var1);
            return var3;
        }
    }
}
