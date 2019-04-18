package com.oolive.live.model.custommsg;

import com.oolive.live.LiveConstant;

public class CustomMsgForbidSendMsg extends CustomMsg {
    private String fonts_color;
    private String desc;

    public CustomMsgForbidSendMsg() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_FORBID_SEND_MSG);
    }

    public String getFonts_color() {
        return fonts_color;
    }

    public void setFonts_color(String fonts_color) {
        this.fonts_color = fonts_color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
