package com.oolive.live.model.custommsg;

import android.text.TextUtils;

import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDJsonUtil;
import com.oolive.live.LiveConstant;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.UserModel;
import com.oolive.socketio.SocketIOMessage;
import com.tencent.TIMMessage;
import com.tencent.TIMCustomElem;

import java.io.Serializable;

public class CustomMsg implements ICustomMsg, Serializable {
    static final long serialVersionUID = 0;

    private int type;
    private UserModel user;
    private UserModel sender;
    private long time_stamp;
    private String deviceType;

    /**
     * 用于群组消息中指定某个用户接收消息，如果不为0而且有值，则只有指定的用户才能处理此消息
     */
    private String user_id;

    /**
     * chatSDK
     */
    private String chat_id;

    public CustomMsg() {
        type = LiveConstant.CustomMsgType.MSG_NONE;
        deviceType = "Android";
        sender = UserModelDao.query();
        if (sender == null) {
            LogUtil.i("sender is null--------------------------------------");
        }
    }

    /**
     * 是否是其他用户的消息
     *
     * @return
     */
    public boolean isOtherUserMsg() {
        return !TextUtils.isEmpty(user_id) && !"0".equals(user_id) && !user_id.equals(UserModelDao.getUserId());
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime_stamp(Long time_stamp){
        this.time_stamp = time_stamp;
    }
    public Long getTime_stamp(){
        return this.time_stamp;
    }
    public TIMMessage parsetoTIMMessage() {
        TIMMessage msg = null;
        ByteMsg bMsg = null;
        try {
            String json = SDJsonUtil.object2Json(this);
            byte[] bytes = json.getBytes(LiveConstant.DEFAULT_CHARSET);

            bMsg = new ByteMsg(bytes);
            //To do

            TIMCustomElem elemCustom = new TIMCustomElem();
            elemCustom.setData(bytes);

            msg = new TIMMessage();
            msg.addElement(elemCustom);

            LogUtil.i("send json:" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
    public SocketIOMessage parsetoSocketIOMessage(){
        SocketIOMessage sMsg = null;
        try {
            String json = SDJsonUtil.object2Json(this);
            byte[] bytes = json.getBytes(LiveConstant.DEFAULT_CHARSET);
            sMsg = new SocketIOMessage(bytes);
            sMsg.setPeer(user_id);
            sMsg.setPeerChatID(chat_id);

            sMsg.setJson(json);
            LogUtil.i("send json:" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sMsg;
    }
    public final MsgModel parseToMsgModel() {
        //TO DO
        //TIMMessage timMessage = parsetoTIMMessage();
        SocketIOMessage sMsg = parsetoSocketIOMessage();
        MsgModel msgModel = new LiveMsgModel(sMsg);
        return msgModel;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    /**
     * 返回用于会话列表中展示的内容
     *
     * @return
     */
    public String getConversationDesc() {
        return "";
    }

}
