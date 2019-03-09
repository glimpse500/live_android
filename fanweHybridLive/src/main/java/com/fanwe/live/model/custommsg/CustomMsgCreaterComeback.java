package com.fanwe.live.model.custommsg;

import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.live.LiveConstant.CustomMsgType;
import com.fanwe.live.R;

public class CustomMsgCreaterComeback extends CustomMsg {

    private String text = SDResourcesUtil.getString(R.string.live_creater_come_back);

    public CustomMsgCreaterComeback() {
        super();
        setType(CustomMsgType.MSG_CREATER_COME_BACK);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
