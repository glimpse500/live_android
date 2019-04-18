package com.oolive.live.activity.room;

import com.oolive.live.ILiveInfo;

public interface ILiveActivity extends ILiveInfo {

    /**
     * 打开直播间输入框
     *
     * @param content
     */
    void openSendMsg(String content);

}
