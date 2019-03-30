package com.oolive.live.model.custommsg;

import com.oolive.live.LiveConstant;

public class CustomMsgLight extends CustomMsg {
    private String imageName;
    private int showMsg;

    public void setShowMsg(int showMsg) {
        this.showMsg = showMsg;
    }

    public int getShowMsg() {
        return showMsg;
    }

    public CustomMsgLight() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_LIGHT);
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
