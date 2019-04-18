package com.oolive.pay;

import com.oolive.live.LiveConstant;
import com.oolive.live.activity.room.ILiveActivity;
import com.oolive.live.business.LiveBaseBusiness;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.pay.model.custommsg.CustomMsgStartPayMode;
import com.oolive.pay.model.custommsg.CustomMsgStartScenePayMode;

/**
 * Created by Administrator on 2016/12/1.
 */

public abstract class LivePayBusiness extends LiveBaseBusiness {
    public LivePayBusiness(ILiveActivity liveActivity) {
        super(liveActivity);
    }

    public void onMsgPayMode(MsgModel msg) {
        if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_START_PAY_MODE) {
            onMsgPayWillStart(msg.getCustomMsgStartPayMode());
        } else if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_START_SCENE_PAY_MODE) {
            onMsgScenePayWillStart(msg.getCustomMsgStartScenePayMode());
        }
    }

    protected void onMsgPayWillStart(CustomMsgStartPayMode customMsg) {

    }

    protected void onMsgScenePayWillStart(CustomMsgStartScenePayMode customMsg) {

    }
}
