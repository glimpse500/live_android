package com.oolive.chat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.library.utils.LogUtil;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EImOnNewMessages;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.ConversationUnreadMessageModel;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.custommsg.CustomMsg;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.socketio.SocketIOConversation;
import com.oolive.socketio.SocketIOConversationType;
import com.oolive.socketio.SocketIOHelper;
import com.oolive.socketio.SocketIOManager;
import com.oolive.socketio.SocketIOMessage;
import com.sunday.eventbus.SDEventManager;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgStore {
    private static SharedPreferences pref;
    public ChatMsgStore(Activity activity){
        pref = activity.getSharedPreferences("msg_handle", Context.MODE_PRIVATE);
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
            long count = SocketIOManager.getInstance().getConversationCount(pref);
            LogUtil.i("count = " + count);
            for (int i = 0; i < count; ++i) {
                SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(pref,i);
                LogUtil.i("conversation.getType()  = " + conversation.getType());
                if (SocketIOConversationType.C2C == conversation.getType()) {
                    // 自己对自己发的消息过滤
                    LogUtil.i("conversation.getPeer()  = " + conversation.getPeer() + " : " + user.getUser_id());
                    if (!conversation.getPeer().equals(user.getUser_id())) {
                        List<SocketIOMessage> list = conversation.getLastMsgs(1);
                        LogUtil.i("list get i "+ list.get(i).getJson());
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
        long cnt = SocketIOManager.getInstance().getConversationCount(pref);
        for (int i = 0; i < cnt; ++i) {
            SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(pref,i);
            SocketIOConversationType type = conversation.getType();
            if (type == SocketIOConversationType.C2C) {
                // 自己对自己发的消息过滤
                if (!conversation.getPeer().equals(user.getUser_id())) {
                    long unreadnum = conversation.getUnreadMessageNum();
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
}
