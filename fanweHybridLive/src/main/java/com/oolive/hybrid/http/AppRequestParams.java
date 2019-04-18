package com.oolive.hybrid.http;

import android.text.TextUtils;

import com.oolive.hybrid.map.tencent.SDTencentMapManager;
import com.fanwe.lib.cache.SDDisk;
import com.fanwe.library.adapter.http.model.SDRequestParams;
import com.oolive.library.utils.SDPackageUtil;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.common.AppDiskKey;
import com.oolive.live.common.AppRuntimeWorker;
import com.oolive.live.common.HostManager;
import com.oolive.hybrid.constant.Constant;

/**
 * Created by Administrator on 2016/3/30.
 */
public class AppRequestParams extends SDRequestParams {

    private boolean isNeedShowActInfo = true;

    public static final class RequestDataType {
        public static final int NORMAL = 0;
        public static final int AES = 1;
    }

    public AppRequestParams() {
        super();

        setUrl(HostManager.getInstance().getApiUrl());

        setRequestDataType(RequestDataType.NORMAL);

        put("screen_width", SDViewUtil.getScreenWidth());
        put("screen_height", SDViewUtil.getScreenHeight());
        put("sdk_type", Constant.DeviceType.DEVICE_ANDROID);
        put("sdk_version_name", SDPackageUtil.getVersionName());
        put("sdk_version", SDPackageUtil.getVersionCode());

        String shopping_session = SDDisk.open().getString(AppDiskKey.SHOPPING_SESSION_ID);
        if (!TextUtils.isEmpty(shopping_session)) {
            put("session_id", shopping_session);
        }

        String itype = getItypeParams();
        if (!TextUtils.isEmpty(itype)) {
            put("itype", itype);
        }

        putLocation();
    }

    public void putLocation() {
        double xpoint = SDTencentMapManager.getInstance().getLongitude();
        double ypoint = SDTencentMapManager.getInstance().getLatitude();

        if (xpoint > 0 && ypoint > 0) {
            put("xpoint", xpoint);
            put("ypoint", ypoint);
        }
    }

    public boolean isNeedShowActInfo() {
        return isNeedShowActInfo;
    }

    public void setNeedShowActInfo(boolean isNeedShowActInfo) {
        this.isNeedShowActInfo = isNeedShowActInfo;
    }

    public static String getItypeParams() {
        if (AppRuntimeWorker.getIsOpenWebviewMain()) {
            //开启O2O购物
            return "sdk";
        }
        return "";
    }
}
