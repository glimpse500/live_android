package com.oolive.hybrid.http;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.oolive.hybrid.app.App;
import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.event.EUnLogin;
import com.oolive.hybrid.model.BaseActModel;
import com.oolive.hybrid.model.BaseEncryptModel;
import com.oolive.hybrid.model.InitActModel;
import com.oolive.hybrid.utils.RetryInitWorker;
import com.fanwe.lib.dialog.ISDDialogConfirm;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.fanwe.library.adapter.http.callback.SDModelRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.common.SDActivityManager;
import com.oolive.library.utils.AESUtil;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDJsonUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.common.AppRuntimeWorker;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dialog.common.AppDialogConfirm;
import com.oolive.live.utils.LiveUtils;
import com.sunday.eventbus.SDEventManager;

public abstract class AppRequestCallback<D> extends SDModelRequestCallback<D> {
    private static final String TAG = "AppRequestCallback";

    public AppRequestParams getAppRequestParams() {
        if (getRequestParams() instanceof AppRequestParams) {
            return (AppRequestParams) getRequestParams();
        } else {
            return null;
        }
    }

    public BaseActModel getBaseActModel() {
        if (getActModel() instanceof BaseActModel) {
            return (BaseActModel) getActModel();
        } else {
            return null;
        }
    }

    /**
     * 处理返回的结果
     *
     * @param resp
     */
    private void dealResponseData(SDResponse resp) {
        //先获取已解密的字符串
        String decryptedResult = resp.getDecryptedResult();
        LogUtil.i("decryptedResult" + decryptedResult);
        if (TextUtils.isEmpty(decryptedResult)) {
            //如果已解密字符串为空，则对原始字符串进行解密
            String result = resp.getResult();
            LogUtil.i("result " + result);
            BaseEncryptModel model = SDJsonUtil.json2Object(result, BaseEncryptModel.class);
            decryptedResult = decryptData(model.getOutput());

            //解密给resp对象设置已解密字符串
            resp.setDecryptedResult(decryptedResult);
        }

        LogUtil.i(getActInfo() + "---------->" + decryptedResult);
        if (ApkConstant.DEBUG) {
            if (decryptedResult != null && decryptedResult.contains("false")) {
                SDToast.showToast(getActInfo() + " false");
            }
        }
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    private String decryptData(String data) {
        LogUtil.i("decryptData : "+ data);
        String decryptedData = AESUtil.decrypt(data, ApkConstant.getAeskeyHttp());
        if (TextUtils.isEmpty(decryptedData)) {
            //如果解密失败，尝试用打包配置的key解密，并清空已保存的key
            decryptedData = AESUtil.decrypt(data, ApkConstant.AES_KEY);
            ApkConstant.setAeskeyHttp(null); //very important
        }
        if (TextUtils.isEmpty(decryptedData)) {
            LiveUtils.updateAeskey(true, null);
            Log.e(TAG, "----------decryptData error");
        }

        return decryptedData;
    }

    @Override
    protected void onSuccessBefore(SDResponse resp) {
        LogUtil.i(resp.getResult());
        LogUtil.i(resp.toString());
        //dealResponseData(resp);

        // 调用父类方法转实体
        super.onSuccessBefore(resp);

        dealRequestParams();
        LogUtil.i("onSuccessBefore done");
    }

    private void dealRequestParams() {
        if (getAppRequestParams() == null || getBaseActModel() == null) {
            return;
        }

        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null) {
            if (getBaseActModel().getInit_version() > initActModel.getInit_version()) {
                //需要重新初始化
                RetryInitWorker.getInstance().start();
            }
        }

        if (getAppRequestParams().isNeedShowActInfo()) {
            SDToast.showToast(getBaseActModel().getError());
        }
        if (getAppRequestParams().isNeedCheckLoginState()) {
            if (getBaseActModel().getUser_login_status() == 0) {
                // 未登录
                if (ApkConstant.DEBUG) {
                    Activity activity = SDActivityManager.getInstance().getLastActivity();
                    if (activity == null) {
                        return;
                    }

                    AppDialogConfirm dialogConfirm = new AppDialogConfirm(activity);
                    dialogConfirm.setCanceledOnTouchOutside(false);
                    dialogConfirm.setCancelable(false);
                    dialogConfirm.setTextContent(getActInfo() + "未登录");
                    dialogConfirm.setTextCancel(null).setCallback(new ISDDialogConfirm.Callback() {
                        @Override
                        public void onClickCancel(View v, SDDialogBase dialog) {

                        }

                        @Override
                        public void onClickConfirm(View v, SDDialogBase dialog) {
                            dealUnLogin();
                        }
                    }).show();
                } else {
                    dealUnLogin();
                }
            }
        }
    }

    /**
     * 处理服务端返回未登录状态
     */
    private void dealUnLogin() {
        EUnLogin event = new EUnLogin();
        SDEventManager.post(event);
        if (AppRuntimeWorker.getIsOpenWebviewMain()) {
            App.getApplication().logout(false, false, true);
        } else {
            App.getApplication().logout(true);
        }
    }

    @Override
    protected void onError(SDResponse resp) {
        String errorLog = getActInfo() + String.valueOf(resp.getThrowable());

        CommonInterface.reportErrorLog(errorLog);
        Log.e(TAG, "----------onError:" + errorLog);
        if (ApkConstant.DEBUG) {
            SDToast.showToast(errorLog);
        }
    }

    @Override
    protected void onCancel(SDResponse resp) {
        LogUtil.i("onCancel:" + getActInfo());
    }

    @Override
    protected void onFinish(SDResponse resp) {

    }

    @Override
    protected <T> T parseActModel(String result, Class<T> clazz) {
        return SDJsonUtil.json2Object(result, clazz);
    }
}
