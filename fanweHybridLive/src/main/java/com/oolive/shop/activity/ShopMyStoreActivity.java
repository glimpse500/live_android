package com.oolive.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.oolive.hybrid.activity.BaseTitleActivity;
import com.oolive.library.title.SDTitleItem;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;
import com.oolive.shop.appview.ShopPodcastMyStoreView;

import org.xutils.view.annotation.ViewInject;

/**
 * 我的小店
 */
public class ShopMyStoreActivity extends BaseTitleActivity {
    @ViewInject(R.id.fl_content)
    private FrameLayout fl_content;
    private ShopPodcastMyStoreView shoppingMystoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shopping_mystore);
        initTitle();
        addAuctionShopMystoreView();
    }

    private void addAuctionShopMystoreView() {
        shoppingMystoreView = new ShopPodcastMyStoreView(ShopMyStoreActivity.this);
        replaceView(fl_content, shoppingMystoreView);
    }

    private void initTitle() {
        mTitle.setMiddleTextTop("我的小店");
        mTitle.initRightItem(1);
        mTitle.getItemRight(0).setTextBot("新增");
        SDViewUtil.setTextViewColorResId(mTitle.getItemRight(0).mTvBot, R.color.res_main_color);
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {
        super.onCLickRight_SDTitleSimple(v, index);
        Intent intent = new Intent(this, ShopAddGoodsEmptyActivity.class);
        startActivity(intent);
    }
}
