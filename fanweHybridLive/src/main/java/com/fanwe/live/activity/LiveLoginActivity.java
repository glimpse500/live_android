package com.fanwe.live.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.hybrid.activity.AppWebViewActivity;
import com.fanwe.hybrid.activity.BaseActivity;
import com.fanwe.hybrid.common.CommonOpenLoginSDK;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.lib.blocker.SDDurationBlocker;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.business.InitBusiness;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.event.EFirstLoginNewLevel;
import com.fanwe.live.model.App_do_updateActModel;
import com.fanwe.live.model.UserModel;
import com.sunday.eventbus.SDEventManager;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/5.
 */
public class LiveLoginActivity extends BaseActivity {

    //手机
    private LinearLayout ll_shouji;
    private ImageView iv_shouji;

    //游客
    private TextView tv_visitors;

    private TextView tv_agreement;

    private SDDurationBlocker blocker = new SDDurationBlocker(2000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsExitApp = true;
        setFullScreen(true);
        setContentView(R.layout.act_live_login);
        init();
    }

    private void init() {
        if (ApkConstant.AUTO_REGISTER) {
            clickLoginVisitors();
            return;
        }

        register();
        bindDefaultData();
        initLoginIcon();
    }

    private void register() {
        ll_shouji = find(R.id.ll_shouji);
        iv_shouji = find(R.id.iv_shouji);
        tv_visitors = find(R.id.tv_visitors);
        tv_agreement = find(R.id.tv_agreement);

        iv_shouji.setOnClickListener(this);
        tv_visitors.setOnClickListener(this);
        tv_agreement.setOnClickListener(this);

        tv_visitors.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
    }

    private void bindDefaultData() {
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null) {
            String privacy_titile = initActModel.getPrivacy_title();
            SDViewBinder.setTextView(tv_agreement, privacy_titile);
        }
    }

    private void initLoginIcon() {
        InitActModel model = InitActModelDao.query();
        if (model != null) {
            //手机
            int has_mobile_login = model.getHas_mobile_login();
            if (has_mobile_login == 1) {
                SDViewUtil.setVisible(ll_shouji);
            } else {
                SDViewUtil.setGone(ll_shouji);
            }


          int has_visitors_login = model.getHas_visitors_login();
            if (has_visitors_login == 1) {
                SDViewUtil.setVisible(tv_visitors);
          } else {
               SDViewUtil.setGone(tv_visitors);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (blocker.block()) {
            return;
        }


        if (v == iv_shouji) {
            clickLoginShouJi();
        } else if (v == tv_visitors) {
            clickLoginVisitors();
        } else if (v == tv_agreement) {
            clickAgreement();
        }
    }

    private void enableClickLogin(boolean enable) {
        iv_shouji.setClickable(enable);
        tv_visitors.setClickable(enable);
    }

    private void clickAgreement() {
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null) {
            String privacy_link = initActModel.getPrivacy_link();
            if (!TextUtils.isEmpty(privacy_link)) {
                Intent intent = new Intent(LiveLoginActivity.this, AppWebViewActivity.class);
                intent.putExtra(AppWebViewActivity.EXTRA_URL, privacy_link);
                intent.putExtra(AppWebViewActivity.EXTRA_IS_SCALE_TO_SHOW_ALL, false);
                startActivity(intent);
            }
        }
    }



    private void clickLoginShouJi() {
        Intent intent = new Intent(this, LiveMobielRegisterActivity.class);
        startActivity(intent);
    }

    private void clickLoginVisitors() {
        CommonInterface.requestLoginVisitorsLogin(new AppRequestCallback<App_do_updateActModel>() {
            @Override
            protected void onStart() {
                LogUtil.i("clickLoginVisitors onStart");
                super.onStart();
                showProgressDialog("");
            }

            @Override
            protected void onError(SDResponse resp) {
                LogUtil.i("clickLoginVisitors Error");
                LogUtil.i(resp.toString());
                super.onError(resp);
                dismissProgressDialog();
                //debug
                //startMainActivity(actModel);
            }

            @Override
            protected void onCancel(SDResponse resp) {
                LogUtil.i("clickLoginVisitors onCancel");
                super.onCancel(resp);
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(SDResponse sdResponse) {
                LogUtil.i("clickLoginVisitors onSuccess");
                LogUtil.i(sdResponse.toString());
                dismissProgressDialog();
                startMainActivity(actModel);

                if (actModel.isOk()) {
                    startMainActivity(actModel);
                }
            }
        });
    }


    private void setFirstLoginAndNewLevel(App_do_updateActModel actModel) {
        InitActModel initActModel = InitActModelDao.query();
        initActModel.setFirst_login(actModel.getFirst_login());
        initActModel.setNew_level(actModel.getNew_level());
        if (!InitActModelDao.insertOrUpdate(initActModel)) {
            SDToast.showToast("保存init信息失败");
        }
        //发送事件首次登陆升级
        EFirstLoginNewLevel event = new EFirstLoginNewLevel();
        SDEventManager.post(event);
    }

    private void startMainActivity(App_do_updateActModel actModel) {
        LogUtil.i("startMainActivity in");
        UserModel user = actModel.getUser_info();
        //InitBusiness.startMainActivity(LiveLoginActivity.this);
        LogUtil.i("user :" + user.toString());
        if (user != null) {
            if (UserModel.dealLoginSuccess(user, true)) {
                InitBusiness.startMainActivity(LiveLoginActivity.this);
            } else {
                SDToast.showToast("保存用户信息失败");
            }
        } else {
            SDToast.showToast("没有获取到用户信息");
        }

    }

    public void onEventMainThread(ERetryInitSuccess event) {
        bindDefaultData();
        initLoginIcon();
    }

    private void startBindMobileActivity(String loginType, String openid, String access_token) {
        Intent intent = new Intent(getActivity(), LiveLoginBindMobileActivity.class);
        intent.putExtra(LiveLoginBindMobileActivity.EXTRA_LOGIN_TYPE, loginType);
        intent.putExtra(LiveLoginBindMobileActivity.EXTRA_OPEN_ID, openid);
        intent.putExtra(LiveLoginBindMobileActivity.EXTRA_ACCESS_TOKEN, access_token);
        startActivity(intent);
    }


    public static final class LoginType {
        private static final String QQ_LOGIN = "qq_login";
        private static final String WX_LOGIN = "wx_login";
        private static final String SINA_LOGIN = "sina_login";
    }
}
