package com.fanwe.socketio;


public class SocketIOMessage {
    byte[] data;
    String text;
    String json;
    String peer;
    private SocketIOConversation conversation;

    public SocketIOMessage()
    {
    }
    public SocketIOMessage(byte[] data){
        this.data= data;
    }
    public void setJson(String json){
        this.json= json;
    }
    public String getJson(){
        return this.json;
    }
    public byte[] getData() { return this.data == null ? "".getBytes() : this.data;
    }
    public void setText(String text)
    {
        this.text = text;
    }

    public void setPeer(String peer){
        this.peer = peer;
    }
    public String getPeer(){
        return this.peer;
    }
    public void setData(byte[] var1) {
        this.data = var1;
    }
    public SocketIOConversation getConversation() {
        return this.conversation;
    }

}
