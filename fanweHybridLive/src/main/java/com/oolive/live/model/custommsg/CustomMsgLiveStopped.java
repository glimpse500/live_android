package com.oolive.live.model.custommsg;

import com.oolive.live.LiveConstant;

public class CustomMsgLiveStopped extends CustomMsg {

    private int room_id;

    public CustomMsgLiveStopped() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_LIVE_STOPPED);
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }
}
