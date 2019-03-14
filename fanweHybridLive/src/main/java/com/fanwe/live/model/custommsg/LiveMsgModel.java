package com.fanwe.live.model.custommsg;

import android.text.TextUtils;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.lib.recorder.SDMediaRecorder;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDJsonUtil;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.LiveConstant.CustomMsgType;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.UserModel;
import com.tencent.TIMCallBack;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFileElem;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.TIMVideoElem;

import java.io.File;
import java.util.List;

public class LiveMsgModel extends MsgModel {
    private boolean printLog = false;
    private ByteMsg bMsg;
    public LiveMsgModel(ByteMsg bMsg)
    {
        super();
        this.bMsg = bMsg;
    }
    public ByteMsg getByteMsg(){
        return this.bMsg;
    }

    public LiveMsgModel( boolean printLog) {
        super();
        this.printLog = printLog;
    }

    @Override
    public void remove() {
        LogUtil.i("Live Msg Model remove");
    }

    private void parseCustomElem() {
        if (bMsg != null ) {
            CustomMsg customMsg = parseToModel(CustomMsg.class);
            if (customMsg != null) {
                int type = customMsg.getType();

                UserModel sender = customMsg.getSender();
                UserModelDao.updateLevelUp(sender);

                Class realCustomMsgClass = LiveConstant.mapCustomMsgClass.get(type);
                if (realCustomMsgClass == null) {
                    return;
                }
                if (ApkConstant.DEBUG && printLog) {
                    LogUtil.i("realCustomMsgClass:" + realCustomMsgClass.getName());
                }
                CustomMsg realCustomMsg = parseToModel(realCustomMsgClass);
                setCustomMsg(realCustomMsg);
            }
        }
    }
    public <T extends CustomMsg> T parseToModel(Class<T> clazz) {
        T model = null;
        String json = null;
        try {
            byte[] data = null;
            if (data == null) {
                data = bMsg.getData();
            }
            json = new String(data, LiveConstant.DEFAULT_CHARSET);
            model = SDJsonUtil.json2Object(json, clazz);

            if (ApkConstant.DEBUG && printLog) {
                LogUtil.i("parseToModel " + model.getType() + ":" + json);
            }
        } catch (Exception e) {
            if (ApkConstant.DEBUG && printLog) {
                e.printStackTrace();
                LogUtil.e("(" + getConversationPeer() + ")parse msg error:" + e.toString() + ",json:" + json);
            }
        } finally {

        }
        return model;
    }
}
