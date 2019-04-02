package com.oolive.socketio;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.oolive.library.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.ConversationUnreadMessageModel;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.MsgModel;
import com.sunday.eventbus.SDEventManager;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocketIOManager {
    private String identification = "";
    private HashSet<SocketIOListener> msgListeners = new HashSet();
    public static SocketIOManager socketIOManager = null;
    private static SharedPreferences pref;
    private SocketIOConversation defaultConversation = new SocketIOConversation("");
    private SocketIOUserStatusListener userStatusListener = null;
    public Map<String,Long> unreadCache = new HashMap<String,Long>();

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
        Gson gson = new Gson();
        Type map_type = new TypeToken<HashSet<String>>(){}.getType();
        Set<String> set = gson.fromJson(pref.getString("Conversations", ""), map_type);
        LogUtil.i("if (set == null) ");
        if (set == null)
            return 0;
        LogUtil.i("set size " + set.size());
        return set.size();
    }
    public SocketIOConversation getConversationByIndex(int var1) {
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

    public SocketIOConversation getConversation(SocketIOConversationType conversationType, String peer) {
        /*if (!SocketIOHelper.connected()) {
            LogUtil.i("not connected");
            return this.defaultConversation;
        } else if */

        if (peer == null) {
            LogUtil.i("get conversation with null peer");
            return this.defaultConversation;
        } else {
            SocketIOConversation conversation;
            Gson gson = new Gson();
            String key = SocketIOHelper.getUserID() + "_" +peer;
            LogUtil.i("conversation key  = " + key);
            Type type = new TypeToken<SocketIOConversation>(){}.getType();
            String json = pref.getString(key, "");
            conversation = gson.fromJson(json, type);
            if (conversation == null){
                (conversation = new SocketIOConversation(this.identification)).setPeer(peer);
                conversation.setType(conversationType);
            }
            
            return conversation;
        }
    }
    /*
    public static long getUnreadNum(String conversationKey){
        if (SocketIOManager.getInstance().unreadCache.keySet().contains(conversationKey))
            return SocketIOManager.getInstance().unreadCache.get(conversationKey);


    }*/
    public static void createSharePreFerences(Application app){
        pref = app.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
    }
    public static void postERefreshMsgUnReaded(boolean isFromSetLocalReade) {

        ERefreshMsgUnReaded event = new ERefreshMsgUnReaded();
        event.model = getC2CTotalUnreadMessageModel();
        event.isFromSetLocalReaded = isFromSetLocalReade;
        LogUtil.i("postERefreshMsgUnReaded");

        SDEventManager.post(event);
    }
    public static List<MsgModel> getC2CMsgList() {
        LogUtil.i("getC2CMsgList");
        List<MsgModel> listMsg = new ArrayList<>();
        UserModel user = UserModelDao.query();
        if (user != null) {
            long count = SocketIOManager.getInstance().getConversationCount();
            LogUtil.i("count = " + count);
            for (int i = 0; i < count; ++i) {
                SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(i);
                LogUtil.i("conversation.getType()  = " + conversation.getType());
                if (SocketIOConversationType.C2C == conversation.getType()) {
                    // 自己对自己发的消息过滤
                    LogUtil.i("conversation.getPeer()  = " + conversation.getPeer() + " : " + user.getUser_id());
                    if (!conversation.getPeer().equals(user.getUser_id())) {
                        List<SocketIOMessage> list = conversation.getLastMsgs(1);
                        LogUtil.i("========= ");
                        //LogUtil.i("list get i "+ list.get(i).getJson());
                        if (list != null && list.size() > 0) {
                            SocketIOMessage sMsg = list.get(0);

                            MsgModel msg = new LiveMsgModel(sMsg);
                            msg.setConversationPeer(conversation.getPeer());
                            if (msg.isPrivateMsg()) {
                                listMsg.add(msg);
                            }
                        }
                    }
                }
            }
        }
        return listMsg;
    }

    public static TotalConversationUnreadMessageModel getC2CTotalUnreadMessageModel() {
        TotalConversationUnreadMessageModel totalUnreadMessageModel = new TotalConversationUnreadMessageModel();
        LogUtil.i("getC2CTotalUnreadMessageModel");
        UserModel user = UserModelDao.query();
        if (user == null) {
            return totalUnreadMessageModel;
        }

        long totalUnreadNum = 0;
        long cnt = SocketIOManager.getInstance().getConversationCount();
        for (int i = 0; i < cnt; ++i) {
            SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(i);
            SocketIOConversationType type = conversation.getType();
            if (type == SocketIOConversationType.C2C) {
                // 自己对自己发的消息过滤
                LogUtil.i("getC2CTotalUnreadMessageModel " + conversation.getPeer() + " : " + user.getUser_id());

                if (!conversation.getPeer().equals(user.getUser_id())) {
                    long unreadnum = conversation.getUnreadMessageNum();
                    LogUtil.i("unreadnum " + unreadnum);
                    if (unreadnum > 0) {
                        List<SocketIOMessage> list = conversation.getLastMsgs(1);
                        if (list != null && list.size() > 0) {
                            SocketIOMessage msg = list.get(0);
                            MsgModel msgModel = new LiveMsgModel(msg);
                            if (msgModel.isPrivateMsg()) {
                                ConversationUnreadMessageModel unreadMessageModel = new ConversationUnreadMessageModel();
                                unreadMessageModel.setPeer(conversation.getPeer());
                                unreadMessageModel.setUnreadnum(unreadnum);
                                totalUnreadMessageModel.hashConver.put(conversation.getPeer(), unreadMessageModel);
                                totalUnreadNum = totalUnreadNum + unreadnum;
                            }
                        }
                    }
                }
            }
        }

        totalUnreadMessageModel.setTotalUnreadNum(totalUnreadNum);
        return totalUnreadMessageModel;
    }
    public static void setAllC2CReadMessage() {
        setAllC2CReadMessage(true);
    }

    public static void setAllC2CReadMessage(boolean isSendRefreshEvent) {
        long cnt = SocketIOManager.getInstance().getConversationCount();
        for (int i = 0; i < cnt; ++i) {
            SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(i);
            SocketIOConversationType type = conversation.getType();
            if (type == SocketIOConversationType.C2C) {
                conversation.setMsgRead(pref);
            }
        }
        if (isSendRefreshEvent) {
            postERefreshMsgUnReaded(true);
        }
    }
}

