package com.oolive.live.span;

import android.view.View;

import com.oolive.library.utils.SDViewUtil;

/**
 * 直播间聊天列表礼物图标span
 */
public class LiveMsgGiftSpan extends SDNetImageSpan {

    public LiveMsgGiftSpan(View view) {
        super(view);
        setWidth(SDViewUtil.dp2px(15));
    }
}
