package com.oolive.live.span;

import android.content.Context;

import com.fanwe.lib.span.SDImageSpan;
import com.oolive.library.utils.SDViewUtil;

/**
 * 直播间聊天列表点亮图标span
 */
public class LiveHeartSpan extends SDImageSpan {
    public LiveHeartSpan(Context context, int resourceId) {
        super(context, resourceId);
        setWidth(SDViewUtil.dp2px(15));
    }
}
