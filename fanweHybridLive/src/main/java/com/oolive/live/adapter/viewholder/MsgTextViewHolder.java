package com.oolive.live.adapter.viewholder;

import android.view.View;

import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.LiveInformation;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsg;
import com.oolive.live.model.custommsg.CustomMsgText;

/**
 * 文字消息
 */
public class MsgTextViewHolder extends MsgViewHolder {
    public MsgTextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {
        appendUserInfo(customMsg.getSender());

        // 内容
        int textColor = 0;
        if (customMsg.getSender().getUser_id().equals(LiveInformation.getInstance().getCreaterId())) {
            // 主播
            textColor = SDResourcesUtil.getColor(R.color.live_msg_text_creater);
        } else {
            textColor = SDResourcesUtil.getColor(R.color.live_msg_text_viewer);
        }
        appendContent(getText(), textColor);
        setUserInfoClickListener(tv_content);
    }

    protected String getText() {
        CustomMsgText msg = (CustomMsgText) customMsg;
        return msg.getText();
    }

}
