package com.oolive.live.model.custommsg;

import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.LiveConstant;

public class CustomMsgCreaterComeback extends CustomMsg {

    private String text = SDResourcesUtil.getString(R.string.live_creater_come_back);

    public CustomMsgCreaterComeback() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_CREATER_COME_BACK);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
