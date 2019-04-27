package com.oolive.live.activity;

import android.content.Intent;
import android.os.Bundle;

import com.oolive.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.title.SDTitleItem;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.App_focus_follow_ActModel;
import com.oolive.live.model.UserModel;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-15 下午4:46:21 类说明
 */
public class LiveFollowActivity extends LiveFocusFollowBaseActivity {
    public static final String TAG = "LiveFollowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        initTitle();
    }

    private void initTitle() {
        mTitle.setMiddleTextTop("关注的人");
        UserModel user = mUser;
        if (user.getUser_id().equals(getIntentUserId())) {
            mTitle.initRightItem(1);
            mTitle.getItemRight(0).setImageLeft(R.drawable.ic_add_friend);
        }
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {
        Intent intent = null;
        switch (index) {
            case 0:
                intent = new Intent(this, LiveSearchUserActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void request(final boolean isLoadMore) {
        CommonInterface.requestUser_follow(page, to_user_id, new AppRequestCallback<App_focus_follow_ActModel>() {
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
