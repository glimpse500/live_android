package com.oolive.live.model.custommsg;

import com.oolive.live.LiveConstant;

public class CustomMsgPrivateText extends CustomMsg {

    private String text = "";

    public CustomMsgPrivateText() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_PRIVATE_TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null) {
            this.text = text;
        }
    }

    @Override
    public String getConversationDesc() {
        return text;
    }

}
