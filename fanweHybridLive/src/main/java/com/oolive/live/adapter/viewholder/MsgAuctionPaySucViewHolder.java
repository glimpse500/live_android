package com.oolive.live.adapter.viewholder;

import android.view.View;

import com.oolive.auction.model.custommsg.CustomMsgAuctionPaySuccess;
import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsg;

/**
 * 支付成功
 * Created by Administrator on 2016/9/6.
 */
public class MsgAuctionPaySucViewHolder extends MsgViewHolder {

    public MsgAuctionPaySucViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {
        CustomMsgAuctionPaySuccess msg = (CustomMsgAuctionPaySuccess) customMsg;
        appendUserInfo(msg.getUser());
        String text = msg.getDesc();
        int textColor = SDResourcesUtil.getColor(R.color.res_second_color);
        appendContent(text, textColor);
        setUserInfoClickListener(tv_content, msg.getUser());
    }

}
