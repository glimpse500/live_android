package com.oolive.live.model;

import com.oolive.hybrid.model.BaseActModel;
import com.oolive.pay.model.App_monitorLiveModel;

public class App_monitorActModel extends BaseActModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private com.oolive.pay.model.App_monitorLiveModel live;

    public com.oolive.pay.model.App_monitorLiveModel getLive() {
        return live;
    }

    public void setLive(App_monitorLiveModel live) {
        this.live = live;
    }
}
