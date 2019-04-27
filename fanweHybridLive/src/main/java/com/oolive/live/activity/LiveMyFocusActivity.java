package com.oolive.live.activity;

import android.os.Bundle;

import com.oolive.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.model.App_focus_follow_ActModel;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-15 下午1:58:48 类说明
 */
public class LiveMyFocusActivity extends LiveFocusFollowBaseActivity {
    public static final String TAG = "LiveMyFocusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();
        initTitle();
    }

    private void initTitle() {
        mTitle.setMiddleTextTop("粉丝");
    }

    @Override
    protected void request(final boolean isLoadMore) {
        CommonInterface.requestMy_focus(page, mUser.getUser_id(), new AppRequestCallback<App_focus_follow_ActModel>() {
            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.getStatus() == 1) {
                    app_my_focusActModel = actModel;
                    SDViewUtil.updateAdapterByList(listModel, actModel.getList(), adapter, isLoadMore);
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
