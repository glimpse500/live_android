package com.fanwe.live.model.custommsg;

import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.LiveConstant.CustomMsgType;
import com.fanwe.live.common.AppRuntimeWorker;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

public class CustomMsgText extends CustomMsg {

    private String text;

    public CustomMsgText() {
        super();
        setType(CustomMsgType.MSG_TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public ByteMsg parsetoByteMsg() {
        ByteMsg bMsg = super.parsetoByteMsg();
        if (AppRuntimeWorker.getHas_dirty_words() == 1) {
            if (bMsg != null) {
                //TIMTextElem textElem = new TIMTextElem();
                bMsg.setText(text);
                //int ret = timMessage.addElement(textElem);
                LogUtil.i("CustomMsgText add TIMTextElem:" + text);
            }
        }
        return bMsg;
    }
}
