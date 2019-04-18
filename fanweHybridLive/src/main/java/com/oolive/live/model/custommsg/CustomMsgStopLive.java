package com.oolive.live.model.custommsg;

import com.oolive.live.LiveConstant;

public class CustomMsgStopLive extends CustomMsg {

    private String desc;
    private int room_id;

    public CustomMsgStopLive() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_STOP_LIVE);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

}
