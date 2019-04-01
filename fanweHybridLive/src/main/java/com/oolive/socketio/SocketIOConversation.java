package com.oolive.socketio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import com.oolive.library.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SocketIOConversation {
    SocketIOConversationType type;
    private String peer = "";
    private String identifer = "";
    private ArrayList<SocketIOMessage> msglist = null;
    private int unReadNum = 0;
    //private SocketIOConversation conversation;
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
        return unReadNum;
    }
    void setPeer(String var1) {
        this.peer = var1;
    }
    void setType(SocketIOConversationType var1) {
        this.type = var1;
    }
    public List<SocketIOMessage> getLastMsgs( int cnt) {
        List subList;
        LogUtil.i("msglist.size = " + msglist.size());
        LogUtil.i("cnt = " + cnt);
        if (msglist != null){
            subList = msglist.subList(Math.max(msglist.size() - cnt, 0), msglist.size());
        }
        else
            subList = new ArrayList();
        LogUtil.i("subList  (i) " + subList.get(0).toString());
        return subList;
    }
    boolean valid() {
        return this.type != SocketIOConversationType.Invalid;
    }
    public void delete(Activity activity){
        SharedPreferences pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String key = SocketIOHelper.getUserID() + "_" +getPeer();
        LogUtil.i("conversation key  = " + key);
        Type type = new TypeToken<SocketIOConversation>(){}.getType();
        String json = pref.getString(key, "");
        SocketIOConversation conversation = gson.fromJson(json, type);
        LogUtil.i("conversation size" + conversation.msglist.size());
        if (conversation == null)
            conversation = this;
        conversation.msglist = new ArrayList<SocketIOMessage>();
        conversation.unReadNum = 0;
        SharedPreferences.Editor prefsEditor = pref.edit();
        String save_json = gson.toJson(conversation);
        prefsEditor.putString(key, save_json);
        prefsEditor.commit();
    }
    public void setMsgRead(Activity activity){
        SharedPreferences pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String key = SocketIOHelper.getUserID() + "_" +getPeer();
        LogUtil.i("conversation key  = " + key);
        Type type = new TypeToken<SocketIOConversation>(){}.getType();
        String json = pref.getString(key, "");
        SocketIOConversation conversation = gson.fromJson(json, type);
        if (conversation == null)
            conversation = this;
        conversation.unReadNum = 0;
        SharedPreferences.Editor prefsEditor = pref.edit();
        String save_json = gson.toJson(conversation);
        prefsEditor.putString(key, save_json);
        prefsEditor.commit();
    }
    public void getLocalMessage(int cnt, SocketIOMessage var2, Activity activity, SocketIOValueCallBack<List<SocketIOMessage>> callBack) {
        if (callBack != null) {
             if (!this.valid()) {
                 callBack.onError(6004, "invalid conversation. user not login or peer is null");
            } else {
                 SharedPreferences pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
                 Gson gson = new Gson();
                 String key = SocketIOHelper.getUserID() + "_" +getPeer();
                 LogUtil.i("conversation key = " + key);
                 Type type = new TypeToken<SocketIOConversation>(){}.getType();
                 String json = pref.getString(key, "");
                 SocketIOConversation conversation = gson.fromJson(json, type);
                 if (conversation == null)
                     conversation = this;
                 conversation.unReadNum = 0;

                 List<SocketIOMessage> list = conversation.msglist;
                 List<SocketIOMessage> subList = new ArrayList<SocketIOMessage>();
                 if (list != null)
                     subList = list.subList(Math.max(list.size() - cnt, 0), list.size());

                 SharedPreferences.Editor prefsEditor = pref.edit();
                 String save_json = gson.toJson(conversation);
                 prefsEditor.putString(key, save_json);
                 prefsEditor.commit();
                 /*
                 LogUtil.i("subLlist : ");
                 for (int i = 0;i < subList.size();i++){
                     LogUtil.i(subList.get(i).toString());
                 }*/
                 Collections.reverse(subList);
                 callBack.onSuccess(subList);
            }
        }
    }
    public void writeLocalMessage(SocketIOMessage sMsg, Activity activity,boolean fromSelf) {
        //SharedPreferences  mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String key = SocketIOHelper.getUserID() + "_" +getPeer();
        LogUtil.i("conversation key" + key);
        Type type = new TypeToken<SocketIOConversation>(){}.getType();
        String json = pref.getString(key, "");

        SocketIOConversation conversation = gson.fromJson(json, type);
        if (conversation == null)
            conversation = this;
        if (conversation.msglist == null)
            conversation.msglist  = new ArrayList<SocketIOMessage>();
        conversation.msglist .add(sMsg);
        if (!fromSelf)
            conversation.unReadNum +=1;
        LogUtil.i("msglist.size = " + conversation.msglist.size());
        SharedPreferences.Editor prefsEditor = pref.edit();
        String save_json = gson.toJson(conversation);
        prefsEditor.putString(key, save_json);
        prefsEditor.commit();



        Gson gson2 = new Gson();
        Type list_type = new TypeToken<List<String>>(){}.getType();
        List<String> key_list = gson2.fromJson(pref.getString("Conversations", ""), list_type);

        if (key_list == null)
            key_list = new LinkedList<String>();
        LogUtil.i("" + key_list.size() + key);
        if (!key_list.contains(key) )
            key_list.add(key);


        SharedPreferences.Editor prefsEditor2 = pref.edit();
        String set_json = gson2.toJson(key_list);
        prefsEditor2.putString("Conversations", set_json);
        prefsEditor2.commit();
        //callBack.onSuccess(list);
    }

}
