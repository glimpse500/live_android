package com.oolive.auction.appview.room;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oolive.auction.activity.AuctionGoodsDetailActivity;
import com.oolive.auction.event.EDoPaiSuccess;
import com.oolive.auction.model.App_pai_user_get_videoActModel;
import com.oolive.auction.model.PaiUserGoodsDetailDataInfoModel;
import com.oolive.auction.model.custommsg.CustomMsgAuctionOffer;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.live.LiveConstant;
import com.oolive.live.R;
import com.oolive.live.appview.room.RoomView;
import com.oolive.live.model.custommsg.MsgModel;

/**
 * Created by yhz on 2016/8/18.
 */
public class RoomAuctionInfoView extends RoomView {
    public RoomAuctionInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RoomAuctionInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoomAuctionInfoView(Context context) {
        super(context);
        init();
    }

    private LinearLayout ll_top_price;
    private TextView tv_top_price;//当前最高价

    private App_pai_user_get_videoActModel actModel;

    private void init() {
        setContentView(R.layout.view_room_auction_info);
        initView();
        initListener();
    }

    private void initView() {
        ll_top_price = (LinearLayout) findViewById(R.id.ll_top_price);
        tv_top_price = (TextView) findViewById(R.id.tv_top_price);
    }

    private void initListener() {
        ll_top_price.setOnClickListener(this);
    }

    public void bindAuctionDetailInfo(App_pai_user_get_videoActModel actModel) {
        this.actModel = actModel;
        PaiUserGoodsDetailDataInfoModel info = actModel.getDataInfo();
        if (info != null) {
            SDViewBinder.setTextView(tv_top_price, Integer.toString(info.getLast_pai_diamonds()));
        }
    }

    @Override
    public void onMsgAuction(MsgModel msg) {
        super.onMsgAuction(msg);
        if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_AUCTION_OFFER) {
            CustomMsgAuctionOffer customMsgAuctionOffer = msg.getCustomMsgAuctionOffer();
            SDViewBinder.setTextView(tv_top_price, Integer.toString(customMsgAuctionOffer.getPai_diamonds()));
        }
    }

    public void onEventMainThread(EDoPaiSuccess event) {
        String s_top_price = tv_top_price.getText().toString();
        int i_top_price = Integer.valueOf(s_top_price);
        if (event.last_pai_diamonds > i_top_price) {
            SDViewBinder.setTextView(tv_top_price, Integer.toString(event.last_pai_diamonds));
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ll_top_price) {
            clickLastPaiDiamons();
        }
    }

    private void clickLastPaiDiamons() {
        PaiUserGoodsDetailDataInfoModel model = actModel.getDataInfo();
        if (model == null) {
            return;
        }
        int id = model.getId();
        boolean isCreater = getLiveActivity().isCreater();
        if (id > 0) {
            Intent intent = new Intent(getActivity(), AuctionGoodsDetailActivity.class);
            intent.putExtra(AuctionGoodsDetailActivity.EXTRA_IS_ANCHOR, isCreater);
            intent.putExtra(AuctionGoodsDetailActivity.EXTRA_ID, String.valueOf(id));
            intent.putExtra(AuctionGoodsDetailActivity.EXTRA_IS_SMALL_SCREEN, 1);
            getActivity().startActivity(intent);
        }
    }
}
