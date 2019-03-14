package com.fanwe.live.model.custommsg;

import android.text.TextUtils;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.lib.recorder.SDMediaRecorder;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDJsonUtil;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.UserModel;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFileElem;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMVideoElem;

import java.io.File;
import java.util.List;

public class ByteMsg {

    byte[] data;
    String text;
    public ByteMsg()
    {
    }
    public ByteMsg(byte[] data){
        this.data= data;
    }
    public byte[] getData() { return this.data == null ? "".getBytes() : this.data;
    }
    public void setText(String text)
    {
        this.text = text;
    }

    public void setData(byte[] var1) {
        this.data = var1;
    }


}
