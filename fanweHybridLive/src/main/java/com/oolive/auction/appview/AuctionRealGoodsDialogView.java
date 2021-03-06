package com.oolive.auction.appview;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.oolive.auction.activity.AuctionCreateEmptyActivity;
import com.oolive.auction.adapter.AuctionRealGoodsAdapter;
import com.oolive.auction.common.AuctionCommonInterface;
import com.oolive.auction.dialog.AuctionRealGoodsDialog;
import com.oolive.auction.model.App_shop_paigoodsActModel;
import com.oolive.auction.model.App_shop_paigoodsItemModel;
import com.oolive.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.listener.SDItemClickCallback;
import com.oolive.library.utils.SDToast;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;
import com.oolive.live.appview.BaseAppView;
import com.oolive.live.model.PageModel;
import com.oolive.live.view.pulltorefresh.IPullToRefreshViewWrapper;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 设为实物竞拍
 * Created by Administrator on 2016/10/11.
 */

public class AuctionRealGoodsDialogView extends BaseAppView {
    @ViewInject(R.id.list_auction)
    private ListView listView;

    private AuctionRealGoodsAdapter adapter;
    private List<App_shop_paigoodsItemModel> listModel;
    private PageModel pageModel = new PageModel();
    private int page = 1;

    private AuctionRealGoodsDialog auctionRealGoodsDialog;

    public AuctionRealGoodsDialogView(Context context, AuctionRealGoodsDialog dialog) {
        super(context);
        this.auctionRealGoodsDialog = dialog;
        init();
    }

    public AuctionRealGoodsDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AuctionRealGoodsDialogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setContentView(R.layout.view_auction_real_goods);
        register();
        bindAdapterPodCast();
        refreshViewer();
    }

    private void register() {
        getPullToRefreshViewWrapper().setOnRefreshCallbackWrapper(new IPullToRefreshViewWrapper.OnRefreshCallbackWrapper() {
            @Override
            public void onRefreshingFromHeader() {
                refreshViewer();
            }

            @Override
            public void onRefreshingFromFooter() {
                loadMoreViewer();
            }
        });
    }

    private void bindAdapterPodCast() {
        listModel = new ArrayList<App_shop_paigoodsItemModel>();
        adapter = new AuctionRealGoodsAdapter(listModel, getActivity());
        listView.setAdapter(adapter);
        /**
         * 设为拍品
         */
        adapter.setClickAuctionListener(new SDItemClickCallback<App_shop_paigoodsItemModel>() {
            @Override
            public void onItemClick(int position, App_shop_paigoodsItemModel item, View view) {
                int viewFlag = 1;//0--虚拟竞拍 1--实物竞拍 2--新增购物商品
                Intent intent = new Intent(getActivity(), AuctionCreateEmptyActivity.class);
                intent.putExtra(AuctionCreateEmptyActivity.EXTRA_VIEW_FLAG, viewFlag);
                intent.putExtra(AuctionCreateEmptyActivity.EXTRA_ID, item.getId());
                getActivity().startActivity(intent);
                auctionRealGoodsDialog.dismiss();
            }
        });
    }

    public void refreshViewer() {
        page = 1;
        request(false);
    }

    private void loadMoreViewer() {
        if (pageModel.getHas_next() == 1) {
            page++;
            request(true);
        } else {
            SDToast.showToast("没有更多数据了");
            getPullToRefreshViewWrapper().stopRefreshing();
        }
    }

    /**
     * 获取竞拍实物列表数据
     */
    private void request(final boolean isLoadMore) {
        AuctionCommonInterface.requestShopPaiGoods(page, new AppRequestCallback<App_shop_paigoodsActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if (actModel.getStatus() == 1) {
                    if (actModel.getList() != null) {
                        pageModel = actModel.getPage();
                        SDViewUtil.updateAdapterByList(listModel, actModel.getList(), adapter, isLoadMore);
                        getStateLayout().updateState(adapter.getCount());
                    }
                } else {
                    getStateLayout().showEmpty();
                }
            }

            @Override
            protected void onFinish(SDResponse resp) {
                super.onFinish(resp);
                getPullToRefreshViewWrapper().stopRefreshing();
            }
        });
    }
}
