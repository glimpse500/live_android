package com.oolive.live.model.custommsg;

import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDJsonUtil;
import com.oolive.live.LiveConstant;

import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.UserModel;
import com.oolive.socketio.SocketIOHelper;
import com.oolive.socketio.SocketIOMessage;

public class LiveMsgModel extends MsgModel {

    private SocketIOMessage sMsg;
    private boolean printLog = false;

    public LiveMsgModel(SocketIOMessage sMsg) {
        super();
        if (sMsg.getTimeStamp() != null)
            this.setTimestamp(sMsg.getTimeStamp());
        setSocketIOMessage(sMsg);
    }

    @Override
    public void remove() {
        LogUtil.i("revmove");
    }

    public void setSocketIOMessage(SocketIOMessage sMsg) {
        // 解析消息
        this.sMsg = sMsg;
        this.parseCustomElem();
    }

    /**
     * 将TIMCustomElem解析成自定义消息
     */
    private void parseCustomElem() {
        if (sMsg != null) {
            CustomMsg customMsg = parseToModel(CustomMsg.class);
            if (customMsg != null) {
                int type = customMsg.getType();
                UserModel sender = customMsg.getSender();
                UserModelDao.updateLevelUp(sender);
                setConversationPeer(sMsg.getPeer());
                setConversationPeer(sMsg.getPeerChatID());
                if (sender != null) {
                    if (sender.getUser_id().equals(SocketIOHelper.getUserID())) {
                        LogUtil.i(sender.getUser_id() + "  " + SocketIOHelper.getUserID());
                        setSelf(true);
                    }
                }

                Class realCustomMsgClass = LiveConstant.mapCustomMsgClass.get(type);
                if (realCustomMsgClass == null) {
                    return;
                }
                if (ApkConstant.DEBUG && printLog) {
                    LogUtil.i("realCustomMsgClass:" + realCustomMsgClass.getName());
                }
                CustomMsg realCustomMsg = parseToModel(realCustomMsgClass);
                setCustomMsg(realCustomMsg);
            }
        }
    }



    public <T extends CustomMsg> T parseToModel(Class<T> clazz) {
        T model = null;
        String json = null;
        try {
            byte[] data = null;
            if (data == null) {
                data = sMsg.getData();
            }

            json = new String(data, LiveConstant.DEFAULT_CHARSET);
            model = SDJsonUtil.json2Object(json, clazz);

            if (ApkConstant.DEBUG && printLog) {
                LogUtil.i("parseToModel " + model.getType() + ":" + json);
            }
        } catch (Exception e) {
            if (ApkConstant.DEBUG && printLog) {
                e.printStackTrace();
                LogUtil.e("(" + getConversationPeer() + ")parse msg error:" + e.toString() + ",json:" + json);
            }
        } finally {

        }
        return model;
    }
}
