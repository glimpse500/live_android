package com.oolive.live.adapter.viewholder.privatechat;

import android.view.View;
import android.widget.TextView;

import com.oolive.library.utils.SDViewBinder;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsg;
import com.oolive.live.model.custommsg.CustomMsgPrivateGift;
import com.oolive.live.utils.GlideUtil;

/**
 * Created by Administrator on 2016/8/30.
 */
public class MsgGiftRightViewHolder extends MsgGiftLeftViewHolder {
    public TextView tv_score;

    public MsgGiftRightViewHolder(View itemView) {
        super(itemView);
        tv_score = find(R.id.tv_score);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {
        CustomMsgPrivateGift msg = (CustomMsgPrivateGift) customMsg;

        // 图片
        GlideUtil.load(msg.getProp_icon()).into(iv_gift);
        SDViewBinder.setTextView(tv_msg, msg.getFrom_msg());
        SDViewBinder.setTextView(tv_score, msg.getFrom_score());
    }
}
