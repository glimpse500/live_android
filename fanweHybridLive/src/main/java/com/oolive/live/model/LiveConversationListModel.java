package com.oolive.live.model;

import android.text.TextUtils;

import com.oolive.library.utils.LogUtil;
import com.oolive.live.IMHelper;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.socketio.SocketIOConversation;
import com.oolive.socketio.SocketIOConversationType;
import com.oolive.socketio.SocketIOHelper;
import com.oolive.socketio.SocketIOManager;
import com.oolive.socketio.SocketIOMessage;

/**
 * Created by Administrator on 2016/12/29.
 */

public class LiveConversationListModel extends UserModel {
    private String peer; //会话对方id
    private String peer_chatID;
    private String text; //展示文字
    private long unreadNum; //未读数量
    private long time; //时间
    private String timeFormat; //时间格式化

    public void fillValue(UserModel user) {
        if (user == null) {
            return;
        }
        String id = user.getUser_id();
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (!id.equals(getUser_id())) {
            return;
        }

        setHead_image(user.getHead_image());
        setNick_name(user.getNick_name());
        setSex(user.getSex());
        setV_icon(user.getV_icon());
        setUser_level(user.getUser_level());
    }

    public void fillValue(MsgModel msg) {
        if (msg == null) {
            return;
        }
        setPeer(msg.getConversationPeer());
        setPeerChatID(msg.getConversationPeerChatID());
        setText(msg.getCustomMsg().getConversationDesc());
        //LogUtil.i("fillValue " + msg.getUnreadNum());
        //setUnreadNum(msg.getUnreadNum());
        setTime(msg.getTimestamp()/1000);
        //setTime(msg.getTimestamp()/1000);
        LogUtil.i("setTimeFormat " + msg.getTimestampFormat());
        setTimeFormat(msg.getTimestampFormat());
        setUser_id(msg.getConversationPeer());

        if (msg.isSelf()) {
            //不能填充自己的信息
        } else {
            fillValue(msg.getCustomMsg().getSender());
        }
    }

    public void updateUnreadNumber() {
        String key = SocketIOHelper.getUserID()+ "_" + peer;
        Long unread = new Long(0);
        if (SocketIOManager.getInstance().unreadCache.keySet().contains(key)){
            unread = SocketIOManager.getInstance().unreadCache.get(key);
            LogUtil.i("fin in cache" + SocketIOManager.getInstance().unreadCache.get(key));
        }
        else{
            LogUtil.i("not in cache" + SocketIOManager.getInstance().unreadCache.get(key));
            SocketIOConversation conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.C2C,peer);
            unread = conversation.getUnreadMessageNum();
        }
        setUnreadNum(unread);
        //setUnreadNum(IMHelper.getC2CUnreadNumber(peer));
    }
    public String getPeerChatID(){
        return peer_chatID;
    }

    public void setPeerChatID(String peer_chatID)
    {
        this.peer_chatID = peer_chatID;
    }
    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(long unreadNum) {
        this.unreadNum = unreadNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
}
