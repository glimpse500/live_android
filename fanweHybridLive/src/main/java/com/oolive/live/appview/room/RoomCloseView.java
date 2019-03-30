package com.oolive.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;

import com.oolive.live.R;

/**
 * 直播间关闭view
 */
public class RoomCloseView extends RoomView {
    public RoomCloseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RoomCloseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoomCloseView(Context context) {
        super(context);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.view_room_close;
    }
}