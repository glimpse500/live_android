package com.oolive.live.adapter.viewholder;

import android.view.View;

import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsg;

/**
 * 高级用户加入提示
 */
public class MsgProViewerJoinViewHolder extends MsgTextViewHolder {
    public MsgProViewerJoinViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {
        //title
        String title = SDResourcesUtil.getString(R.string.live_msg_title);
        int titleColor = SDResourcesUtil.getColor(R.color.live_msg_title);
        appendContent(title, titleColor);

        // 内容
        String text = "金光一闪，" + customMsg.getSender().getNick_name() + "加入了...";
        int textColor = SDResourcesUtil.getColor(R.color.live_msg_content);
        appendContent(text, textColor);
        setUserInfoClickListener(tv_content);
    }
}
