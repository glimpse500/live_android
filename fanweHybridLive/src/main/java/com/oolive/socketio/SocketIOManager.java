package com.oolive.socketio;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.oolive.library.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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

    public long getConversationCount(SharedPreferences pref) {
        //To Do
        //SharedPreferences pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type map_type = new TypeToken<HashSet<String>>(){}.getType();
        Set<String> set = gson.fromJson(pref.getString("Conversations", ""), map_type);
        LogUtil.i("if (set == null) ");
        if (set == null)
            return 0;
        LogUtil.i("set size " + set.size());
        return set.size();
    }
    public SocketIOConversation getConversationByIndex(SharedPreferences pref,int var1) {
        Gson gson = new Gson();
        Type map_type = new TypeToken<HashSet<String>>(){}.getType();
        Set<String> set = gson.fromJson(pref.getString("Conversations", ""), map_type);
        if (set == null)
            return new SocketIOConversation();
        Type sockietIO_type = new TypeToken<SocketIOConversation>(){}.getType();
        LogUtil.i("getConversationByIndex : " + set.toArray()[var1] );
        String json2 = pref.getString( (String)set.toArray()[var1], "");
        SocketIOConversation conversation = gson.fromJson(json2, sockietIO_type);

        return conversation;
    }
    public void setRefreshListener(SocketIORefreshListener var1) {
        this.refreshListener = var1;
    }
    public void setUserStatusListener(SocketIOUserStatusListener var1) {
        this.userStatusListener = var1;
    }
    public void postOffLine(){
        LogUtil.i("onForceOffline");
        this.userStatusListener.onForceOffline();
    }

    public void setLogLevel(SocketIOLogLevel var1) {
        //TO DO
    }
    public boolean init(Context var1) {
        //TO DO
        return false;
    }

    public SocketIOConversation getConversation(SocketIOConversationType var1, String var2) {
        /*if (!SocketIOHelper.connected()) {
            LogUtil.i("not connected");
            return this.defaultConversation;
        } else if */

        if (var2 == null) {
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
