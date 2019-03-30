package com.oolive.auction.model;

import com.oolive.hybrid.model.BaseActModel;
import com.oolive.library.utils.SDToast;

/**
 * Created by Administrator on 2016/8/23.
 */
public class App_pai_user_get_videoActModel extends BaseActModel {
    private PaiUserGetVideoDataModel data;

    public PaiUserGetVideoDataModel getData() {
        return data;
    }

    public void setData(PaiUserGetVideoDataModel data) {
        this.data = data;
    }

    public PaiUserGoodsDetailDataInfoModel getDataInfo() {
        if (data != null && data.getInfo() != null) {
            return data.getInfo();
        }
        if (data == null) {
            SDToast.showToast("data为空");
        }
        if (data.getInfo() == null) {
            SDToast.showToast("data里面info为空");
        }
        return null;
    }
}
