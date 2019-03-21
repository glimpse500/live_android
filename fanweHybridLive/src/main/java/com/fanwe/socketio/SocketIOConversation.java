package com.fanwe.socketio;


import java.util.ArrayList;
import java.util.List;

public class SocketIOConversation {
    SocketIOConversationType type;
    private String peer = "";
    private String identifer = "";
    private SocketIOConversation conversation;
    public SocketIOConversation(){

    }
    public SocketIOConversation(String var1) {
        this.type = SocketIOConversationType.Invalid;
        this.identifer = var1;
    }
    public SocketIOConversationType getType() {
        return this.type;
    }
    public String getPeer() {
        return this.peer;
    }
    public long getUnreadMessageNum() {
        //To Do
        long unReadMsg = 0;
        return unReadMsg;
    }
    void setPeer(String var1) {
        this.peer = var1;
    }
    void setType(SocketIOConversationType var1) {
        this.type = var1;
    }
    public List<SocketIOMessage> getLastMsgs(long var1) {
        //To Do
        ArrayList var3 = new ArrayList();
        return var3;
    }

}
