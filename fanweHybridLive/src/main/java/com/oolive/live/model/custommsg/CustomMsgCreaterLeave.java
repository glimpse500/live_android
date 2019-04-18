package com.oolive.live.model.custommsg;

import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.LiveConstant;

public class CustomMsgCreaterLeave extends CustomMsg {

    private String text = SDResourcesUtil.getString(R.string.live_creater_leave);

    public CustomMsgCreaterLeave() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_CREATER_LEAVE);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
