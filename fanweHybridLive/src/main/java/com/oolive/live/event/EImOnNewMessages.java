package com.oolive.live.event;

import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.socketio.SocketIOMessage;

/**
 * im消息
 *
 * @author Administrator
 * @date 2016-5-12 下午12:00:36
 */
public class EImOnNewMessages {
    public MsgModel msg;
    public SocketIOMessage sMsg;
    public String json;
    public int msgType;
}
