package com.oolive.hybrid.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.oolive.hybrid.app.App;
import com.oolive.hybrid.event.EExitApp;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.model.BaseActModel;
import com.fanwe.lib.statelayout.SDStateLayout;
import com.oolive.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.receiver.SDNetworkReceiver;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDOtherUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.R;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dialog.common.AppDialogProgress;
import com.oolive.live.event.EOnBackground;
import com.oolive.live.event.EOnResumeFromBackground;
import com.oolive.live.utils.LiveVideoChecker;
import com.oolive.live.view.pulltorefresh.PullToRefreshViewWrapper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import de.greenrobot.event.EventBus;

public class BaseActivity extends SDBaseActivity implements SDNetworkReceiver.SDNetworkCallback {
    /**
     * 触摸返回键是否退出App
     */
    protected boolean mIsExitApp = false;
    protected long mExitTime = 0;

    private AppDialogProgress mProgressDialog;

    private PullToRefreshViewWrapper mPullToRefreshViewWrapper;
    private SDStateLayout mStateLayout;

    @Override
    protected void init(Bundle savedInstanceState) {
        //UmengPushManager.getPushAgent().onAppStart();
        SDNetworkReceiver.addCallback(this);
        if (App.getApplication().isPushStartActivity(getClass())) {
            App.getApplication().startPushRunnable();
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        x.view().inject(this);
    }

    /**
     * 返回下拉刷新包裹对象
     *
     * @return
     */
    public final PullToRefreshViewWrapper getPullToRefreshViewWrapper() {
        if (mPullToRefreshViewWrapper == null) {
            mPullToRefreshViewWrapper = new PullToRefreshViewWrapper();
        }
        return mPullToRefreshViewWrapper;
    }

    public void setStateLayout(SDStateLayout stateLayout) {
        if (mStateLayout != stateLayout) {
            mStateLayout = stateLayout;
            if (stateLayout != null) {
                stateLayout.getEmptyView().setContentView(R.layout.view_state_empty_content);
                stateLayout.getErrorView().setContentView(R.layout.view_state_error_net);
            }
        }
    }

    public SDStateLayout getStateLayout() {
        return mStateLayout;
    }

    @Override
    public Dialog showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new AppDialogProgress(this);
        }
        mProgressDialog.setTextMsg(msg);
        mProgressDialog.show();
        return mProgressDialog;
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

    public void exitApp() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            SDToast.showToast("再按一次退出!");
        } else {
            App.getApplication().exitApp(true);
        }
        mExitTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onBackground() {
        EOnBackground event = new EOnBackground();
        EventBus.getDefault().post(event);

        CommonInterface.requestStateChangeLogout(new AppRequestCallback<BaseActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if (actModel.isOk()) {
                    LogUtil.i("requestStateChangeLogout");
                }
            }
        });
        super.onBackground();
    }

    @Override
    protected void onResumeFromBackground() {
        EOnResumeFromBackground event = new EOnResumeFromBackground();
        EventBus.getDefault().post(event);

        CommonInterface.requestStateChangeLogin(new AppRequestCallback<BaseActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if (actModel.isOk()) {
                    LogUtil.i("requestStateChangeLogin");
                }
            }
        });
        if (getClass() != InitActivity.class) {
            checkVideo();
        }
        super.onResumeFromBackground();
    }

    protected void checkVideo() {
        LiveVideoChecker checker = new LiveVideoChecker(this);
        CharSequence copyContent = SDOtherUtil.pasteText();
        checker.check(String.valueOf(copyContent));
    }

    @Override
    public void onBackPressed() {
        if (mIsExitApp) {
            exitApp();
        } else {
            super.onBackPressed();
        }
    }

    public void onEventMainThread(EExitApp event) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getApplicationContext()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        SDNetworkReceiver.removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void onNetworkChanged(SDNetworkReceiver.NetworkType type) {

    }
}
