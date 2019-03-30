package com.oolive.live.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.oolive.hybrid.activity.BaseTitleActivity;
import com.oolive.hybrid.common.CommonOpenSDK;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.listner.PayResultListner;
import com.oolive.hybrid.model.PaySdkModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.customview.SDGridLinearLayout;
import com.oolive.library.listener.SDItemClickCallback;
import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.live.R;
import com.oolive.live.adapter.LiveRechargePaymentAdapter;
import com.oolive.live.adapter.LiveRechrgeVipPaymentRuleAdapter;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.model.App_UserVipPurchaseActModel;
import com.oolive.live.model.App_payActModel;
import com.oolive.live.model.PayItemModel;
import com.oolive.live.model.PayModel;
import com.oolive.live.model.RuleItemModel;
import com.fanwei.jubaosdk.shell.OnPayResultListener;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值VIP界面
 * Created by shibx on 2010/01/16.
 */
public class LiveRechargeVipActivity extends BaseTitleActivity {

    @ViewInject(R.id.tv_user_vip_deadline)
    private TextView tv_user_vip_deadline;

    @ViewInject(R.id.ll_payment)
    private SDGridLinearLayout ll_payment;

    @ViewInject(R.id.ll_payment_rule)
    private SDGridLinearLayout ll_payment_rule;

    private LiveRechargePaymentAdapter adapterPayment;
    private List<PayItemModel> listPayment = new ArrayList<>();

    private LiveRechrgeVipPaymentRuleAdapter adapterPaymentRule;
    private List<RuleItemModel> listPaymentRule = new ArrayList<>();


    private int pay_id;
    private int rule_id;

    @Override
    protected int onCreateContentView() {
        return R.layout.act_live_recharge_vip;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mTitle.setMiddleTextTop("VIP会员");

        //支付方式
        adapterPayment = new LiveRechargePaymentAdapter(listPayment, this);
        adapterPayment.setItemClickCallback(new SDItemClickCallback<PayItemModel>() {
            @Override
            public void onItemClick(int position, PayItemModel item, View view) {
                adapterPayment.getSelectManager().performClick(item);
            }
        });
        ll_payment.setAdapter(adapterPayment);

        //支付金额
        adapterPaymentRule = new LiveRechrgeVipPaymentRuleAdapter(listPaymentRule, this);
        adapterPaymentRule.setItemClickCallback(new SDItemClickCallback<RuleItemModel>() {
            @Override
            public void onItemClick(int position, RuleItemModel item, View view) {
                rule_id = item.getId();
                clickPaymentRule(item);
            }
        });
        ll_payment_rule.setAdapter(adapterPaymentRule);
    }

    /**
     * 购买会员套餐
     *
     * @param model
     */
    private void clickPaymentRule(RuleItemModel model) {
        if (!validatePayment()) {
            return;
        }
        requestPay();
    }

    private void requestPay() {
        CommonInterface.requestPayVip(pay_id, rule_id, new AppRequestCallback<App_payActModel>() {
            @Override
            protected void onStart() {
                super.onStart();
                showProgressDialog("正在启动插件");
            }

            @Override
            protected void onFinish(SDResponse resp) {
                super.onFinish(resp);
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.isOk()) {
                    PayModel payModel = actModel.getPay();
                    if (payModel != null) {
                        PaySdkModel paySdkModel = payModel.getSdk_code();
                        if (paySdkModel != null) {
                            CommonOpenSDK.dealPayRequestSuccess(actModel, getActivity(), payResultListner, jbfPayResultListener);
                        }
                    }
                }
            }
        });
    }

    private PayResultListner payResultListner = new PayResultListner() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onDealing() {

        }

        @Override
        public void onFail() {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onNetWork() {

        }

        @Override
        public void onOther() {

        }
    };

    private OnPayResultListener jbfPayResultListener = new OnPayResultListener() {

        @Override
        public void onSuccess(Integer integer, String s, String s1) {

        }

        @Override
        public void onFailed(Integer integer, String s, String s1) {
        }
    };

    private boolean validatePayment() {
        PayItemModel payment = adapterPayment.getSelectManager().getSelectedItem();
        if (payment == null) {
            SDToast.showToast("请选择支付方式");
            return false;
        }
        pay_id = payment.getId();

        return true;
    }

    private void requestVipData() {
        CommonInterface.requestVipPurchase(new AppRequestCallback<App_UserVipPurchaseActModel>() {
            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.isOk()) {

                    if (actModel.getIs_vip() == 0) {
                        tv_user_vip_deadline.setTextColor(SDResourcesUtil.getColor(R.color.res_text_gray_m));
                    } else {
                        tv_user_vip_deadline.setTextColor(SDResourcesUtil.getColor(R.color.res_main_color));
                    }
                    SDViewBinder.setTextView(tv_user_vip_deadline, actModel.getVip_expire_time());
                    adapterPayment.updateData(actModel.getPay_list());
                    adapterPaymentRule.updateData(actModel.getRule_list());

                    int defaultPayIndex = -1;
                    List<PayItemModel> listPay = actModel.getPay_list();
                    if (listPay != null) {
                        int i = 0;
                        for (PayItemModel pay : listPay) {
                            if (pay_id == pay.getId()) {
                                defaultPayIndex = i;
                                break;
                            }
                            i++;
                        }
                        if (defaultPayIndex < 0) {
                            defaultPayIndex = 0;
                            pay_id = 0;
                        }
                    }
                    adapterPayment.getSelectManager().setSelected(defaultPayIndex, true);
                }
            }

            @Override
            protected void onFinish(SDResponse resp) {
                super.onFinish(resp);
            }
        });
    }

    @Override
    protected void onResume() {
        requestVipData();
        super.onResume();
    }
}
