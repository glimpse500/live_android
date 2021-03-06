package com.oolive.live.view.unread;

import android.content.Context;
import android.util.AttributeSet;

import com.oolive.live.IMHelper;
import com.oolive.live.event.EIMLoginSuccess;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.socketio.SocketIOManager;

import de.greenrobot.event.EventBus;

/**
 * IM未读数量TextVview
 */
public abstract class LiveIMUnreadTextView extends LiveUnreadTextView {
    public LiveIMUnreadTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LiveIMUnreadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveIMUnreadTextView(Context context) {
        super(context);
    }

    /**
     * 收到需要刷新未读数量事件
     *
     * @param event
     */
    public final void onEventMainThread(ERefreshMsgUnReaded event) {
        onProcessIMUnread(event.model);
    }

    public final void onEventMainThread(EIMLoginSuccess event) {
        onProcessIMUnread(SocketIOManager.getInstance().getC2CTotalUnreadMessageModel());
    }

    /**
     * 处理未读数量
     *
     * @param model
     */
    protected abstract void onProcessIMUnread(TotalConversationUnreadMessageModel model);

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
