package com.oolive.live.business;

import com.oolive.live.activity.room.ILiveActivity;

/**
 * Created by Administrator on 2017/6/3.
 */

public abstract class LiveBaseBusiness extends BaseBusiness {
    private ILiveActivity mLiveActivity;

    public LiveBaseBusiness(ILiveActivity liveActivity) {
        mLiveActivity = liveActivity;
    }

    public ILiveActivity getLiveActivity() {
        return mLiveActivity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
