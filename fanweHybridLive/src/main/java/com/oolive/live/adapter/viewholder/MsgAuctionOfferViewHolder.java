package com.oolive.live.adapter.viewholder;

import android.view.View;

import com.oolive.auction.model.custommsg.CustomMsgAuctionOffer;
import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsg;

/**
 * 竞拍出价
 * Created by Administrator on 2016/9/5.
 */
public class MsgAuctionOfferViewHolder extends MsgViewHolder {

    public MsgAuctionOfferViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindCustomMsg(int position, CustomMsg customMsg) {

        CustomMsgAuctionOffer msg = (CustomMsgAuctionOffer) customMsg;

        appendUserInfo(msg.getUser());

        String text = msg.getDesc();
        int textColor = SDResourcesUtil.getColor(R.color.res_second_color);
        appendContent(text, textColor);

        setUserInfoClickListener(tv_content, msg.getUser());
    }

}
