package com.oolive.shop.adapter.viewholder;

import android.view.View;

import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.adapter.viewholder.MsgViewHolder;
import com.oolive.live.model.custommsg.CustomMsg;
import com.oolive.shop.model.custommsg.CustomMsgShopBuySuc;

/**
 * Created by Administrator on 2016/12/5.
 */

public class MsgAuctionShopBuySucViewHolder extends MsgViewHolder {
    public MsgAuctionShopBuySucViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {
        CustomMsgShopBuySuc msg = (CustomMsgShopBuySuc) customMsg;
        appendUserInfo(msg.getUser());
        String text = msg.getDesc();
        int textColor = SDResourcesUtil.getColor(R.color.res_second_color);
        appendContent(text, textColor);
        setUserInfoClickListener(tv_content, msg.getUser());
    }
}
